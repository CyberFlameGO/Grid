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

package com.pickaxis.grid.chat.senders;

import com.pickaxis.grid.core.GridPlugin;
import com.pickaxis.grid.core.mq.flexjson.UNumberFactory;
import com.pickaxis.grid.core.mq.flexjson.UUIDFactory;
import com.pickaxis.grid.core.mq.flexjson.UUIDTransformer;
import com.pickaxis.grid.core.player.GridPlayer;
import com.pickaxis.grid.core.server.GridServer;
import com.pickaxis.grid.core.util.LangUtil;
import flexjson.JSON;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.jooq.types.UInteger;
import org.jooq.types.ULong;
import org.jooq.types.UShort;

/**
 * A player that's sending a message.
 */
@Getter
@Setter( AccessLevel.PRIVATE )
@NoArgsConstructor
public class PlayerMessageSenderImpl implements PlayerMessageSender
{
    @Setter
    private String name;
    
    @Setter
    private String displayName;
    
    private transient BaseComponent componentName;
    
    @Getter( AccessLevel.PRIVATE )
    @Setter( AccessLevel.PRIVATE )
    private String componentNameJson;
    
    @JSON( transformer = UUIDTransformer.class,
           objectFactory = UUIDFactory.class )
    private UUID uuid;
    
    @JSON( objectFactory = UNumberFactory.class )
    private UInteger id;
    
    @JSON( objectFactory = UNumberFactory.class )
    private UShort serverId;
    
    public PlayerMessageSenderImpl( GridPlayer player )
    {
        this.setName( player.getName() );
        this.setUuid( player.getUuid() );
        this.setId( player.getId() );
        this.setServerId( player.getServer().getId() );
    }
    
    @Override
    public GridPlayer getPlayer()
    {
        return GridPlugin.getInstance().getPlayerManager().getPlayer( this.getId() );
    }
    
    @Override
    public PlayerMessageSender setComponentName( BaseComponent componentName )
    {
        this.componentName = componentName;
        this.setComponentNameJson( ComponentSerializer.toString( componentName ) );
        
        return this;
    }
    
    @Override
    public BaseComponent getComponentName()
    {
        if( this.componentName instanceof BaseComponent )
        {
            return this.componentName;
        }
        
        if( this.getComponentNameJson() instanceof String )
        {
            this.componentName = new TextComponent( ComponentSerializer.parse( this.getComponentNameJson() ) );
            return this.componentName;
        }
        
        return null;
    }
    
    private TextComponent duplicateComponent( BaseComponent old )
    {
        TextComponent c = new TextComponent();
        
        c.setColor( old.getColorRaw() );
        c.setBold( old.isBoldRaw() );
        c.setItalic( old.isItalicRaw() );
        c.setUnderlined( old.isUnderlinedRaw() );
        c.setStrikethrough( old.isStrikethroughRaw() );
        c.setObfuscated( old.isObfuscatedRaw() );
        c.setClickEvent( old.getClickEvent() );
        c.setHoverEvent( old.getHoverEvent() );
        
        if( old.getExtra() != null )
        {
            for( BaseComponent oec : old.getExtra() )
            {
                c.addExtra( this.duplicateComponent( oec ) );
            }
        }
        
        if( old instanceof TextComponent )
        {
            c.setText( ( (TextComponent) old ).getText() );
        }
        
        return c;
    }
    
    @Override
    public BaseComponent getComponentName( ULong messageId )
    {
        if( messageId == null )
        {
            return this.getComponentName();
        }
        
        // TODO: This can be replaced with the following once we update past git-Spigot-550ebac-7019900.
        // this.getComponentName().duplicate();
        BaseComponent name = this.duplicateComponent( this.getComponentName() );
        
        HoverEvent hover = new HoverEvent( HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText( LangUtil.getString( "chat.options.player.hover", "Click to show options." ) ) );
        ClickEvent click = new ClickEvent( ClickEvent.Action.RUN_COMMAND, String.format( "/chat options message %d", messageId.longValue() ) );
        
        if( name.getClickEvent() == null && name.getHoverEvent() == null )
        {
            name.setClickEvent( click );
            name.setHoverEvent( hover );
        }
        
        for( BaseComponent c : name.getExtra() )
        {
            if( c.getClickEvent() == null && c.getHoverEvent() == null )
            {
                c.setClickEvent( click );
                c.setHoverEvent( hover );
            }
        }
        
        return name;
    }
    
    @Override
    public GridServer getServer()
    {
        return GridPlugin.getInstance().getSdm().getServer( this.getServerId() );
    }
}
