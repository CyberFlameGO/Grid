/* 
 * Grid
 * Copyright (c) 2017 PickAxis, All Rights Resered.
 * 
 * NOTICE:  All information contained herein is, and remains the property of PickAxis.  The intellectual
 * and technical concepts contained herein are proprietary to PickAxis and may be covered by U.S. and
 * Foreign Patents, patents in process, and are protected by trade secret or copyright law.  Dissemination
 * of this information or reproduction of this material is strictly forbidden unless prior written permission
 * is obtained from PickAxis.  Use of this source code or any derivative of it is strictly forbidden.
 */

package com.pickaxis.grid.chat;

import com.pickaxis.grid.core.GridPlugin;
import com.pickaxis.grid.core.data.tables.ChatFlair;
import com.pickaxis.grid.core.data.tables.ChatFlairOwned;
import com.pickaxis.grid.core.data.tables.records.ChatFlairRecord;
import com.pickaxis.grid.core.util.LangUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.types.UByte;

/**
 * Builds a player's display name.
 */
@Getter( AccessLevel.PRIVATE )
@Setter( AccessLevel.PRIVATE )
class NameBuilder
{
    private final ChatPlayerService cps;
    
    private TextComponent primary;
    
    private final List<BaseComponent> secondaryPrefixes;
    
    private final List<BaseComponent> secondarySuffixes;
    
    NameBuilder( ChatPlayerService cps )
    {
        this.cps = cps;
        this.secondaryPrefixes = new ArrayList<>();
        this.secondarySuffixes = new ArrayList<>();
    }
    
    BaseComponent buildComponentName()
    {
        boolean hasPrimary = false;
        
        // Fetch individual non-permission-based flair.
        Result<Record> query = GridPlugin.getInstance().getDb().getContext().select()
                                                                            .from( ChatFlairOwned.CHAT_FLAIR_OWNED )
                                                                            .join( ChatFlair.CHAT_FLAIR )
                                                                            .on( ChatFlairOwned.CHAT_FLAIR_OWNED.FLAIR_ID.eq( ChatFlair.CHAT_FLAIR.ID) )
                                                                            .where( ChatFlairOwned.CHAT_FLAIR_OWNED.PLAYER_ID.eq( this.getCps().getPlayer().getId() ) )
                                                                            .and( ChatFlairOwned.CHAT_FLAIR_OWNED.DISPLAY.ne( UByte.valueOf( 0 ) ) )
                                                                            .fetch();
        
        // Process non-permission-based flair.
        for( Record row : query )
        {
            boolean willBePrimary = false;
            
            if( row.getValue( ChatFlairOwned.CHAT_FLAIR_OWNED.DISPLAY ) == UByte.valueOf( 2 ) && !hasPrimary )
            {
                willBePrimary = true;
                hasPrimary = true;
            }
            
            this.applyFlair( row, willBePrimary );
        }
        
        // Process permission-based flair.
        for( ChatFlairRecord row : this.getCps().getChatManager().getPermissionFlair() )
        {
            if( !this.getCps().getPlayer().hasPermission( row.getValue( ChatFlair.CHAT_FLAIR.PERMISSION ) ) )
            {
                continue;
            }
            
            this.applyFlair( row, !hasPrimary );
            
            if( !hasPrimary )
            {
                hasPrimary = true;
            }
        }
        
        // Put it all together.
        if( this.getSecondaryPrefixes().isEmpty() && this.getSecondarySuffixes().isEmpty() )
        {
            return this.getPrimary();
        }
        
        List<BaseComponent> bcList = new ArrayList<>();
        
        for( BaseComponent cur : this.getSecondaryPrefixes() )
        {
            bcList.add( cur );
        }
        
        bcList.add( this.getPrimary() );
        
        for( BaseComponent cur : this.getSecondarySuffixes() )
        {
            bcList.add( cur );
        }
        
        BaseComponent bc = new TextComponent( "" );
        bc.setExtra( bcList );
        
        return bc;
    }
    
    private void applyFlair( Record row, boolean primary )
    {
        if( primary )
        {
            TextComponent flair = LangUtil.toTextComponent( ( row.getValue( ChatFlair.CHAT_FLAIR.PREFIX_PRIMARY ) == null ? "" : row.getValue( ChatFlair.CHAT_FLAIR.PREFIX_PRIMARY ) ) + 
                                                            this.getCps().getPlayer().getName() + 
                                                            ( row.getValue( ChatFlair.CHAT_FLAIR.SUFFIX_PRIMARY ) == null ? "" : row.getValue( ChatFlair.CHAT_FLAIR.SUFFIX_PRIMARY ) ) + 
                                                            LangUtil.getString( "chat.flair.reset", "&f" ) );
            
            this.processEvents( row, flair );
            
            if( this.getPrimary() != null )
            {
                throw new IllegalStateException( "A primary prefix/suffix has already been set." );
            }
            
            this.setPrimary( flair );
        }
        else
        {
            TextComponent flairPrefix = LangUtil.toTextComponent( row.getValue( ChatFlair.CHAT_FLAIR.PREFIX_SECONDARY ) );
            TextComponent flairSuffix = LangUtil.toTextComponent( row.getValue( ChatFlair.CHAT_FLAIR.SUFFIX_SECONDARY ) );
            
            this.processEvents( row, flairPrefix, flairSuffix );
            
            if( row.getValue( ChatFlair.CHAT_FLAIR.PREFIX_SECONDARY ) instanceof String )
            {
                this.getSecondaryPrefixes().add( flairPrefix );
            }
            
            if( row.getValue( ChatFlair.CHAT_FLAIR.SUFFIX_SECONDARY ) instanceof String )
            {
                this.getSecondarySuffixes().add( flairSuffix );
            }
        }
    }
    
    private void processEvents( Record row, TextComponent... flair )
    {
        boolean canHaveData = Arrays.asList( row.fields() ).contains( ChatFlairOwned.CHAT_FLAIR_OWNED.DATA );
        
        if( row.getValue( ChatFlair.CHAT_FLAIR.HOVER ) instanceof String )
        {
            String hoverText = row.getValue( ChatFlair.CHAT_FLAIR.HOVER );
            if( canHaveData && row.getValue( ChatFlairOwned.CHAT_FLAIR_OWNED.DATA ) instanceof String )
            {
                hoverText = String.format( hoverText, this.getCps().getPlayer().getName(), row.getValue( ChatFlairOwned.CHAT_FLAIR_OWNED.DATA ) );
            }
            
            HoverEvent hoverEvent = new HoverEvent( HoverEvent.Action.SHOW_TEXT, LangUtil.toBaseComponentArray( hoverText ) );
            
            for( TextComponent c : flair )
            {
                c.setHoverEvent( hoverEvent );
            }
        }
        
        if( row.getValue( ChatFlair.CHAT_FLAIR.CLICK ) instanceof String )
        {
            String clickText = row.getValue( ChatFlair.CHAT_FLAIR.CLICK );
            if( canHaveData && row.getValue( ChatFlairOwned.CHAT_FLAIR_OWNED.DATA ) instanceof String )
            {
                clickText = String.format( clickText, this.getCps().getPlayer().getName(), row.getValue( ChatFlairOwned.CHAT_FLAIR_OWNED.DATA ) );
            }
            
            ClickEvent clickEvent = new ClickEvent( ClickEvent.Action.valueOf( row.getValue( ChatFlair.CHAT_FLAIR.CLICK_TYPE ) ), clickText );
            
            for( TextComponent c : flair )
            {
                c.setClickEvent( clickEvent );
            }
        }
    }
}
