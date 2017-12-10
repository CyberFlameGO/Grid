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

import com.pickaxis.grid.chat.senders.MessageSender;
import com.pickaxis.grid.chat.targets.ChatChannel;
import java.util.Collection;

/**
 * Someone who can send and receive messages.
 */
public interface Chatter
{
    /**
     * Gets the player's MessageSender.
     * 
     * @return The player's MessageSender
     */
    MessageSender getMessageSender();
    
    /**
     * Joins a channel.
     * 
     * @param channel The channel to join
     * @param persist Whether joining the channel should persist across sessions
     */
    void joinChannel( final ChatChannel channel, boolean persist );
    
    /**
     * Joins a channel.
     *
     * @param channel The channel to join
     */
    void joinChannel( final ChatChannel channel );
    
    /**
     * Leaves a channel.
     * 
     * @param channel The channel to leave
     * @param persist Whether leaving the channel should persist across sessions
     */
    void leaveChannel( final ChatChannel channel, boolean persist );
    
    /**
     * Leaves a channel.
     *
     * @param channel The channel to leave
     */
    void leaveChannel( final ChatChannel channel );
    
    /**
     * Gets an unmodifiable collection of channels the player is in.
     * 
     * @return Channels the player is in
     */
    Collection<ChatChannel> getChannels();
}
