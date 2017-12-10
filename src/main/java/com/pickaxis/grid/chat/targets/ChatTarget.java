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
import com.pickaxis.grid.chat.senders.MessageSender;

/**
 * An object that chat messages can be sent to.
 */
public interface ChatTarget
{
    /**
     * Dispatches a chat message to the target, logging the message
     * to the database and sending the MQ message.
     *
     * @param sender The sender
     * @param type The type of message
     * @param message The message
     */
    void dispatchChatMessage( MessageSender sender, MessageType type, String message );
    
    /**
     * Gets the target's name/tag.
     * 
     * @return The name of the chat target.
     */
    String getName();
}
