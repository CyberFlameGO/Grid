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

package com.pickaxis.grid.core.mq;

import com.pickaxis.grid.core.events.GridEvent;
import com.pickaxis.grid.core.mq.messages.GridMessage;
import lombok.Getter;
import org.bukkit.event.HandlerList;

/**
 * Fired when a Grid MQ message has been received.
 * 
 * @param <T> The GridMessage that was initialized.
 */
public class GridMessageReceivedEvent<T extends GridMessage> extends GridEvent
{
    @Getter
    private static final HandlerList handlerList = new HandlerList();
    
    @Getter
    private final T message;
    
    public GridMessageReceivedEvent( T message )
    {
        this.message = message;
    }
    
    /**
     * Gets the class of the message that was received.
     * 
     * @return The class of the message.
     */
    public Class<? extends GridMessage> getMessageClass()
    {
        return this.getMessage().getClass();
    }
    
    @Override
    public HandlerList getHandlers()
    {
        return GridMessageReceivedEvent.getHandlerList();
    }
}
