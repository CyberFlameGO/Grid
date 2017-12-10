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

package com.pickaxis.grid.chat.mq.messages;

import com.pickaxis.grid.chat.ChatManager;
import com.pickaxis.grid.chat.ChatPlayerService;
import com.pickaxis.grid.chat.MessageType;
import com.pickaxis.grid.chat.Replacement;
import com.pickaxis.grid.chat.senders.MessageSender;
import com.pickaxis.grid.chat.senders.PlayerMessageSender;
import com.pickaxis.grid.chat.targets.ChatChannel;
import com.pickaxis.grid.chat.targets.MetaChatChannel;
import com.pickaxis.grid.core.GridPlugin;
import com.pickaxis.grid.core.data.tables.ChatLogs;
import com.pickaxis.grid.core.mq.MQConstants;
import com.pickaxis.grid.core.mq.MessageParameter;
import com.pickaxis.grid.core.mq.MessageParameters;
import com.pickaxis.grid.core.mq.flexjson.UNumberFactory;
import com.pickaxis.grid.core.mq.messages.GridMessage;
import com.pickaxis.grid.core.player.GridPlayer;
import com.pickaxis.grid.core.server.ServerDataManager;
import com.pickaxis.grid.core.util.DateUtil;
import com.rabbitmq.client.AMQP.BasicProperties;
import flexjson.JSON;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.apache.commons.lang.SerializationUtils;
import org.jooq.exception.DataAccessException;
import org.jooq.types.UInteger;
import org.jooq.types.ULong;
import org.jooq.types.UShort;

/**
 * A message in a message.
 */
@Getter
@Setter
@ToString
@MessageParameters( { MessageParameter.ASYNC } )
public class ChatMessage extends GridMessage
{
    @JSON( include = false )
    private transient ChatChannel channel;
    
    @JSON( objectFactory = UNumberFactory.class )
    private UInteger channelId;
    
    @JSON( objectFactory = UNumberFactory.class )
    private UInteger metaId;
    
    @JSON( objectFactory = UNumberFactory.class )
    private UShort metaSecondaryId;
    
    private MessageSender sender;
    
    private MessageType type;
    
    private String message;
    
    @JSON( objectFactory = UNumberFactory.class )
    private ULong messageId;
    
    private transient BaseComponent[] display;
    
    @Getter( AccessLevel.PRIVATE )
    @Setter( AccessLevel.PRIVATE )
    @JSON( include = false )
    private String displayJson;
    
    private ChatManager getChatManager()
    {
        return GridPlugin.getInstance().getManager( ChatManager.class );
    }
    
    private void logMessage()
    {
        if( this.getSender() instanceof PlayerMessageSender )
        {
            try
            {
                ULong messageId = GridPlugin.getInstance().getDb().getContext().insertInto( ChatLogs.CHAT_LOGS )
                                                                               .set( ChatLogs.CHAT_LOGS.TIME, DateUtil.getUnixTimeUnsigned() )
                                                                               .set( ChatLogs.CHAT_LOGS.PLAYER_ID, this.getSender() instanceof PlayerMessageSender ? ( (PlayerMessageSender) this.getSender() ).getPlayer().getId() : null )
                                                                               .set( this.getChannel().getFields( ChatLogs.CHAT_LOGS ) )
                                                                               .set( ChatLogs.CHAT_LOGS.SERVER_ID, GridPlugin.getInstance().getManager( ServerDataManager.class ).getLocalServer().getId() )
                                                                               .set( ChatLogs.CHAT_LOGS.TYPE, this.getType().getDbCode() )
                                                                               .set( ChatLogs.CHAT_LOGS.MESSAGE, this.getMessage() )
                                                                               .returning( ChatLogs.CHAT_LOGS.ID )
                                                                               .fetchOne()
                                                                               .getId();
            
                this.setMessageId( messageId );
            }
            catch( DataAccessException ex )
            {
                GridPlugin.getInstance().getLogger().log( Level.SEVERE, "Exception while logging chat message: ", ex );
            }
        }
    }
    
    public ChatMessage setDisplay( BaseComponent[] display )
    {
        this.display = display;
        this.setDisplayJson( ComponentSerializer.toString( display ) );
        
        return this;
    }
    
    public BaseComponent[] getDisplay()
    {
        if( this.display != null )
        {
            return this.display;
        }
        
        if( this.getDisplayJson() != null )
        {
            this.display = ComponentSerializer.parse( this.getDisplayJson() );
            return this.display;
        }
        
        return null;
    }
    
    private void buildDisplay()
    {
        Map<Replacement, Object> replacements = new HashMap<>();
        replacements.put( Replacement.TAG, this.getChannel().getTag() );
        replacements.put( Replacement.SENDER, this.getSender().getComponentName( this.getMessageId() ) );
        replacements.put( Replacement.MESSAGE, this.getMessage() );
        
        this.setDisplay( this.getType().formatMessage( replacements ) );
    }
    
    @Override
    public void send()
    {
        this.logMessage();
        this.sendWithoutLogging();
    }
    
    public void sendWithoutLogging()
    {
        if( this.display == null )
        {
            this.buildDisplay();
        }
        
        BasicProperties properties = new BasicProperties().builder().contentType( "application/octet-stream" ).build();
        
        try
        {
            GridPlugin.getInstance().getMq().getChannel( MQConstants.CHANNEL_PLAYERS ).basicPublish( MQConstants.EXCHANGE_CHAT_CHANNELS, this.getChannel().getRoutingKey(), properties, SerializationUtils.serialize( this ) );
        }
        catch( IOException ex )
        {
            GridPlugin.getInstance().getLogger().log( Level.WARNING, "Couldn't publish " + this.toString(), ex );
        }
    }
    
    public ChatChannel getChannel()
    {
        if( this.channel != null )
        {
            return this.channel;
        }
        
        if( this.getMetaId() != null )
        {
            this.channel = this.getChatManager().getChannel( this.getChannelId(), this.getMetaId(), this.getMetaSecondaryId() );
            return this.channel;
        }
        
        this.channel = this.getChatManager().getChannel( this.getChannelId() );
        return this.channel;
    }
    
    public ChatMessage setChannel( ChatChannel channel )
    {
        this.channel = channel;
        this.setChannelId( channel.getId() );
        
        if( channel instanceof MetaChatChannel )
        {
            MetaChatChannel mChannel = (MetaChatChannel) channel;
            
            this.setMetaId( mChannel.getMeta() );
            this.setMetaSecondaryId( mChannel.getMetaSecondary() );
        }
        
        return this;
    }
    
    @Override
    public void execute()
    {
        if( this.getChannel().getDbRow().getVerbose().intValue() == 0 && ( this.getType() == MessageType.JOIN || this.getType() == MessageType.LEAVE ) )
        {
            return;
        }
        
        for( GridPlayer gp : GridPlugin.getInstance().getPlayerManager().getAllOnline() )
        {
            if( gp.getService( ChatPlayerService.class ).getChannels().contains( this.getChannel() ) )
            {
                gp.sendMessage( this.getDisplay() );
            }
        }
    }
}
