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

import com.pickaxis.grid.chat.MessageType;
import com.pickaxis.grid.chat.mq.messages.ChatMessage;
import com.pickaxis.grid.chat.senders.MessageSender;
import com.pickaxis.grid.chat.senders.PlayerMessageSender;
import com.pickaxis.grid.core.GridPlugin;
import com.pickaxis.grid.core.data.tables.ChatChannelParticipants;
import com.pickaxis.grid.core.data.tables.Players;
import com.pickaxis.grid.core.player.GridPlayer;
import com.pickaxis.grid.core.util.DateUtil;
import com.pickaxis.grid.core.util.LangUtil;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jooq.Record;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.types.UByte;

/**
 * Parent object for chat channels with and without meta.
 */
@Getter( AccessLevel.PRIVATE )
@Setter( AccessLevel.PRIVATE )
public abstract class AbstractChatChannel implements ChatChannel
{
    private final List<String> participantsVisible;
    
    private final List<String> participantsInvisible;
    
    private int participantsUpdated;
    
    AbstractChatChannel()
    {
        this.participantsVisible = new LinkedList<>();
        this.participantsInvisible = new LinkedList<>();
        this.setParticipantsUpdated( 0 );
    }
    
    @Override
    public final void refreshParticipants()
    {
        // Removed
    }
    
    @Override
    public final boolean readyRefreshParticipants()
    {
        return this.getParticipantsUpdated() < DateUtil.getUnixTime() + GridPlugin.getInstance().getConfig().getInt( "chat.participant-refresh", 30 );
    }
    
    @Override
    public Collection<String> getParticipants( boolean visible )
    {
        if( this.readyRefreshParticipants() )
        {
            this.refreshParticipants();
        }
        
        return Collections.unmodifiableList( visible ? this.getParticipantsVisible() : this.getParticipantsInvisible() );
    }
    
    @Override
    public Collection<String> getParticipants()
    {
        return this.getParticipants( true );
    }
    
    @Override
    public void dispatchChatMessage( MessageSender sender, MessageType type, String message )
    {
        // Check permission.
        if( sender instanceof PlayerMessageSender && this.getDbRow().getPermission() instanceof String )
        {
            GridPlayer player = ( (PlayerMessageSender) sender ).getPlayer();
            if( !player.hasPermission( this.getDbRow().getPermission() + "." + type.name().toLowerCase() ) )
            {
                player.sendMessage( this.getName() + LangUtil.getString( "chat.speak.nopermission", "You don't have permission to speak in this channel." ) );
                return;
            }
        }
        
        // TODO: Check if channel is moderated and user is voiced.
        
        new ChatMessage().setChannel( this )
                         .setSender( sender )
                         .setType( type )
                         .setMessage( message )
                         .send();
    }
    
    @Override
    public boolean equals( Object o )
    {
        return o instanceof ChatChannel && this.getRoutingKey().equals( ( (ChatChannel) o ).getRoutingKey() );
    }

    @Override
    public int hashCode()
    {
        return this.getRoutingKey().hashCode();
    }
}
