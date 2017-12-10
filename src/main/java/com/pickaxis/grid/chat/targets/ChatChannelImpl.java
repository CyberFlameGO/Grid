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

package com.pickaxis.grid.chat.targets;

import com.pickaxis.grid.chat.ChatManager;
import com.pickaxis.grid.chat.targets.types.ChannelType;
import com.pickaxis.grid.chat.targets.types.ChannelTypeRegistry;
import com.pickaxis.grid.core.GridPlugin;
import com.pickaxis.grid.core.data.tables.ChatChannelParticipants;
import com.pickaxis.grid.core.data.tables.ChatChannels;
import com.pickaxis.grid.core.data.tables.ChatChannelsJoined;
import com.pickaxis.grid.core.data.tables.records.ChatChannelsRecord;
import com.pickaxis.grid.core.util.LangUtil;
import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.ChatColor;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.types.UInteger;
import org.jooq.types.UNumber;
import org.jooq.types.UShort;

/**
 * A chat channel.
 */
@Getter
@Setter( AccessLevel.PRIVATE )
public class ChatChannelImpl extends AbstractChatChannel
{
    private final UInteger id;
    
    private final String routingKey;
    
    private ChatChannelsRecord dbRow;
    
    private ChannelType type;
    
    private BaseComponent tag;
    
    private String name;
    
    public ChatChannelImpl( ChatChannelsRecord dbRow, ChannelTypeRegistry channelTypeRegistry )
    {
        this.id = dbRow.getId();
        this.routingKey = dbRow.getId().toString();
        this.setDbRow( dbRow );
        this.setType( channelTypeRegistry.get( this.getDbRow().getType() ) );
        
        if( this.getDbRow().getQuick() instanceof String )
        {    
            this.setTag( LangUtil.toTextComponent( LangUtil.formatString( "chat.channeltag.quick", "%s[%s. %s] ", ChatColor.getByChar( this.getDbRow().getColor() ), this.getDbRow().getQuick(), this.getDbRow().getName() ) ) );
        }
        else
        {
            this.setTag( LangUtil.toTextComponent( LangUtil.formatString( "chat.channeltag.noquick", "%s[%s] ", ChatColor.getByChar( this.getDbRow().getColor() ), this.getDbRow().getName() ) ) );
        }
        this.getTag().setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, LangUtil.toBaseComponentArray( LangUtil.getString( "chat.options.channel.hover", "Click to show channel options." ) ) ) );
        this.getTag().setClickEvent( new ClickEvent( ClickEvent.Action.RUN_COMMAND, String.format( "/chat options channel %s", this.getDbRow().getSlug() ) ) );
        
        this.setName(  this.getTag().toLegacyText() );
    }
    
    public ChatChannelImpl( UInteger id, ChannelTypeRegistry channelTypeRegistry )
    {
        this( GridPlugin.getInstance().getDb().getContext().selectFrom( ChatChannels.CHAT_CHANNELS )
                                                           .where( ChatChannels.CHAT_CHANNELS.ID.eq( id ) )
                                                           .fetchOne(), 
              channelTypeRegistry );
    }
    
    @Override
    @SuppressWarnings( { "rawtypes", "unchecked" } )
    public Map<? extends Field<?>, ?> getFields( Table table )
    {
        Map<Field<? extends UNumber>, UNumber> values = new HashMap<>();
        
        values.put( (Field<UInteger>) table.field( "channel_id" ), this.getId() );
        
        if( table.equals( ChatChannelsJoined.CHAT_CHANNELS_JOINED ) || table.equals( ChatChannelParticipants.CHAT_CHANNEL_PARTICIPANTS ) )
        {
            values.put( (Field<UInteger>) table.field( "meta_id" ), UInteger.valueOf( 0 ) );
            values.put( (Field<UShort>) table.field( "meta_secondary_id" ), UShort.valueOf( 0 ) );
        }
        else
        {
            values.put( (Field<UInteger>) table.field( "meta_id" ), null );
            values.put( (Field<UShort>) table.field( "meta_secondary_id" ), null );
        }
        
        return values;
    }
    
    @Override
    @SuppressWarnings( { "rawtypes", "unchecked" } )
    public Condition getConditions( Table table )
    {
        Condition condition = ( (Field<UInteger>) table.field( "channel_id" ) ).eq( this.getId() );
        
        if( table.equals( ChatChannelsJoined.CHAT_CHANNELS_JOINED ) || table.equals( ChatChannelParticipants.CHAT_CHANNEL_PARTICIPANTS ) )
        {
            condition.and( ( (Field<UInteger>) table.field( "meta_id" ) ).eq( UInteger.valueOf( 0 ) ) )
                     .and( ( (Field<UShort>) table.field( "meta_secondary_id" ) ).eq( UShort.valueOf( 0 ) ) );
        }
        else
        {
            condition.and( ( (Field<UInteger>) table.field( "meta_id" ) ).isNull() )
                     .and( ( (Field<UShort>) table.field( "meta_secondary_id" ) ).isNull() );
        }
        
        return condition;
    }
}
