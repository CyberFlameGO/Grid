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

import com.pickaxis.grid.chat.targets.ChatChannel;
import com.pickaxis.grid.chat.targets.ChatTarget;
import com.pickaxis.grid.chat.targets.PrivateMessageTarget;
import com.pickaxis.grid.chat.targets.PrivateMessageTargetImpl;
import com.pickaxis.grid.core.GridPlugin;
import com.pickaxis.grid.core.data.tables.ChatChannelParticipants;
import com.pickaxis.grid.core.data.tables.records.ChatChannelParticipantsRecord;
import com.pickaxis.grid.core.db.QueryTask;
import com.pickaxis.grid.core.util.LangUtil;
import com.pickaxis.grid.core.util.UUIDFetcher;
import java.util.Collection;
import java.util.HashSet;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.jooq.UpdateConditionStep;
import org.jooq.types.UByte;

/**
 * Chat message listener.
 */
@Getter( AccessLevel.PRIVATE )
public class ChatListener implements Listener
{
    private final Collection<String> whisperCommands;
    
    private final Collection<String> replyCommands;
    
    public ChatListener()
    {
        this.whisperCommands = new HashSet<>();
        this.replyCommands = new HashSet<>();
        
        this.getWhisperCommands().add( "whisper" );
        this.getWhisperCommands().add( "message" );
        this.getWhisperCommands().add( "msg" );
        this.getWhisperCommands().add( "tell" );
        this.getWhisperCommands().add( "pm" );
        
        this.getReplyCommands().add( "reply" );
    }
    
    private void whisper( ChatPlayerService cps, String input )
    {
        String[] message = input.split( " ", 2 );
        
        PrivateMessageTarget target;
        try
        {
            target = new PrivateMessageTargetImpl( UUIDFetcher.getUUIDOf( message[0] ), message[0] );
        }
        catch( Exception ex )
        {
            cps.getPlayer().sendMessage( LangUtil.formatString( "player.name-lookup-no-uuid", "No player with the name %s exists.", message[0] ) );
            return;
        }
        
        if( message.length == 1 )
        {
            cps.setFocus( target );
            return;
        }
        
        target.dispatchChatMessage( cps.getMessageSender(), MessageType.PMINBOUND, message[1] );
    }
    
    @EventHandler( priority = EventPriority.MONITOR, ignoreCancelled = true )
    public void onPlayerCommandPreprocess( PlayerCommandPreprocessEvent event )
    {
        ChatPlayerService cps = GridPlugin.getInstance().getPlayerManager().getPlayer( event.getPlayer() ).getService( ChatPlayerService.class );
        
        String[] command = event.getMessage().substring( 1 ).split( " ", 2 );
        
        if( this.getWhisperCommands().contains( command[0].toLowerCase() ) )
        {
            event.setCancelled( true );
            
            if( command.length == 1 )
            {
                cps.getPlayer().sendMessage( LangUtil.formatString( "chat.whisper.no-player", "&cYou need to specify a player to message: /%s <player> [message]", command[0] ) );
                return;
            }
            
            this.whisper( cps, command[1] );
            return;
        }
        
        if( this.getReplyCommands().contains( command[0].toLowerCase() ) )
        {
            event.setCancelled( true );
            
            if( cps.getLastInboundWhisperTarget() == null )
            {
                cps.getPlayer().sendMessage( LangUtil.getString( "chat.whisper.reply.no-target", "&cYou haven't received any whispers yet this session." ) );
                return;
            }
            
            if( command.length == 1 )
            {
                cps.setFocus( cps.getLastInboundWhisperTarget() );
                return;
            }
            
            cps.getLastInboundWhisperTarget().dispatchChatMessage( cps.getMessageSender(), MessageType.PMINBOUND, command[1] );
            return;
        }
        
        ChatChannel selected = null;
        for( ChatChannel channel : cps.getChannels() )
        {
            if( channel.getDbRow().getShortcut() instanceof String && channel.getDbRow().getShortcut().equals( command[0].toLowerCase() ) )
            {
                selected = channel;
                break;
            }
        }
        
        if( !( selected instanceof ChatChannel ) )
        {
            return;
        }
        event.setCancelled( true );
        
        if( command.length == 1 )
        {
            cps.setFocus( selected );
        }
        else
        {
            selected.dispatchChatMessage( cps.getMessageSender(), MessageType.MESSAGE, command[1] );
        }
    }
    
    @EventHandler( priority = EventPriority.MONITOR, ignoreCancelled = true )
    public void onAsyncPlayerChat( AsyncPlayerChatEvent event )
    {
        ChatPlayerService cps = GridPlugin.getInstance().getPlayerManager().getPlayer( event.getPlayer() ).getService( ChatPlayerService.class );
        
        event.setCancelled( true );
        
        if( !( cps.getFocus() instanceof ChatTarget ) )
        {
            event.getPlayer().sendMessage( LangUtil.getString( "chat.nofocus", "&cYou don't have a chat focus." ) );
            return;
        }
        
        cps.getFocus().dispatchChatMessage( cps.getMessageSender(), MessageType.MESSAGE, event.getMessage() );
    }
}
