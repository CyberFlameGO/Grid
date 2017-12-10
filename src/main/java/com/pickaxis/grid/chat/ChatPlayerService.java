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

import com.pickaxis.grid.chat.targets.ChatTarget;
import com.pickaxis.grid.chat.targets.PrivateMessageTarget;
import com.pickaxis.grid.core.player.PlayerService;
import net.md_5.bungee.api.chat.BaseComponent;

/**
 * Do we really want to let players talk?
 */
public interface ChatPlayerService extends PlayerService, Chatter
{
    /**
     * Sets the player's ChatTarget focus.
     * 
     * @param focus The new ChatTarget to focus
     */
    void setFocus( ChatTarget focus );
    
    /**
     * Gets the player's current focus.
     * 
     * @return The channel the player has focused
     */
    ChatTarget getFocus();
    
    /**
     * Gets the player's BaseComponent name.
     * 
     * @return The player's BaseComponent name.
     */
    BaseComponent getComponentName();
    
    /**
     * Gets the ChatManagerImpl.
     * 
     * @return The ChatManagerImpl instance
     */
    ChatManager getChatManager();
    
    /**
     * Gets the PrivateMessageTarget from the last inbound PM.
     * 
     * @return The PrivateMessageTarget from the last PM received
     */
    PrivateMessageTarget getLastInboundWhisperTarget();
    
    /**
     * Sets the most recent inbound PM's PrivateMessageTarget.
     * 
     * @param lastInboundWhisperTarget The new target.
     * @return This ChatPlayerService object.
     */
    ChatPlayerService setLastInboundWhisperTarget( PrivateMessageTarget lastInboundWhisperTarget );
    
    /**
     * Gets a formatted message to show all of the channels the
     * player is currently in.
     * 
     * @return A formatted message showing the channels the player is in.
     */
    BaseComponent[] getChannelsJoinedMessage();
}
