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
import com.pickaxis.grid.chat.mq.messages.PrivateMessage;
import com.pickaxis.grid.chat.senders.MessageSender;
import java.util.UUID;
import lombok.Getter;

/**
 * A ChatTarget for private messages.
 */
@Getter
public class PrivateMessageTargetImpl implements PrivateMessageTarget
{
    private final UUID uuid;
    
    private final String name;
    
    public PrivateMessageTargetImpl( UUID uuid, String name )
    {
        this.uuid = uuid;
        this.name = name;
    }
    
    @Override
    public void dispatchChatMessage( MessageSender sender, MessageType type, String message )
    {
        new PrivateMessage().setSender( sender )
                            .setType( MessageType.PMINBOUND )
                            .setMessage( message )
                            .setPlayerUuid( this.getUuid() )
                            .send();
    }
}
