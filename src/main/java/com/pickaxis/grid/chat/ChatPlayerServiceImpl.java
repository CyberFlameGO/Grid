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

import com.pickaxis.grid.chat.mq.messages.ChatMessage;
import com.pickaxis.grid.chat.senders.PlayerMessageSender;
import com.pickaxis.grid.chat.senders.PlayerMessageSenderImpl;
import com.pickaxis.grid.chat.targets.ChatChannel;
import com.pickaxis.grid.chat.targets.ChatTarget;
import com.pickaxis.grid.chat.targets.PrivateMessageTarget;
import com.pickaxis.grid.chat.targets.PrivateMessageTargetImpl;
import com.pickaxis.grid.core.GridPlugin;
import com.pickaxis.grid.core.data.tables.ChatChannelParticipants;
import com.pickaxis.grid.core.data.tables.ChatChannelsJoined;
import com.pickaxis.grid.core.data.tables.ChatPlayersFocus;
import com.pickaxis.grid.core.data.tables.Players;
import com.pickaxis.grid.core.data.tables.records.ChatChannelsJoinedRecord;
import com.pickaxis.grid.core.data.tables.records.ChatPlayersFocusRecord;
import com.pickaxis.grid.core.player.AbstractPlayerService;
import com.pickaxis.grid.core.player.GridPlayer;
import com.pickaxis.grid.core.util.LangUtil;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.exception.DataAccessException;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.jooq.types.UNumber;
import org.jooq.types.UShort;
import org.kitteh.vanish.VanishPlugin;

/**
 * Lets players chat on the Grid!
 */
@Getter
@Setter( AccessLevel.PRIVATE )
public class ChatPlayerServiceImpl extends AbstractPlayerService implements ChatPlayerService
{
    private ChatTarget focus;
    
    @Getter( AccessLevel.PRIVATE )
    private final Collection<ChatChannel> channelsJoined;
    
    @Setter
    private PrivateMessageTarget lastInboundWhisperTarget;
    
    private BaseComponent componentName;
    
    private PlayerMessageSender messageSender;
    
    public ChatPlayerServiceImpl( final GridPlayer player )
    {
        super( player );
        
        this.channelsJoined = new HashSet<>();
    }
    
    @Override
    public ChatManager getChatManager()
    {
        return GridPlugin.getInstance().getManager( ChatManager.class );
    }
    
    @Override
    public void onAsyncPostLogin()
    {
        this.setComponentName( new NameBuilder( this ).buildComponentName() );
        PlayerMessageSender ms = new PlayerMessageSenderImpl( this.getPlayer() );
        this.setMessageSender( ms );
        ms.setName( this.getPlayer().getName() );
        ms.setComponentName( this.getComponentName() );
        ms.setDisplayName( TextComponent.toLegacyText( this.getComponentName() ) );

        // Removed
    }
    
    @Override
    public void onLogout()
    {
        for( ChatChannel channel : new HashSet<>( this.getChannelsJoined() ) )
        {
            this.leaveChannel( channel, false );
        }
        this.getChannelsJoined().clear();
    }
    
    @Override
    @SuppressWarnings( "element-type-mismatch" )
    public void setFocus( final ChatTarget target )
    {
        if( target instanceof ChatChannel )
        {
            if( !this.getChannelsJoined().contains( target ) )
            {
                // TODO: Create a more specific exception for this.
                throw new RuntimeException( "The player isn't in this channel." );
            }
            
            this.getPlayer().sendMessage( LangUtil.formatString( "chat.focus-changed.channel", "&eNow speaking in %s", target.getName() ) );
        }
        else if( target instanceof PrivateMessageTarget )
        {
            this.getPlayer().sendMessage( LangUtil.formatString( "chat.focus-changed.player", "&dNow talking to &f%s&d.", target.getName() ) );
        }
        
        this.focus = target;
    }
    
    // Removed
    
    @Override
    public Collection<ChatChannel> getChannels()
    {
        return Collections.unmodifiableCollection( this.getChannelsJoined() );
    }
    
    @Override
    public BaseComponent[] getChannelsJoinedMessage()
    {
        BaseComponent[] out = new BaseComponent[ ( this.getChannels().size() * 2 ) + 1 ];
        
        out[0] = LangUtil.toTextComponent( LangUtil.getString( "chat.channels-joined.header", "&eYou're in the following channels: " ) );
        
        int index = 1;
        for( ChatChannel ch : this.getChannels() )
        {
            out[ index ] = ch.getTag();
            out[ index + 1 ] = LangUtil.toTextComponent( LangUtil.getString( "chat.channels-joined.separator", " " ) );
            
            index += 2;
        }
        
        return out;
    }
}
