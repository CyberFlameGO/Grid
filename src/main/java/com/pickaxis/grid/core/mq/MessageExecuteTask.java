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

import com.pickaxis.grid.core.mq.messages.GridMessage;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Envelope;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Execute a GridMessage
 */
@Getter( AccessLevel.PRIVATE )
public class MessageExecuteTask extends BukkitRunnable
{
    private final GridMessage message;
    
    private final String consumerTag;
    
    private final Envelope envelope;
    
    private final BasicProperties properties;
    
    public MessageExecuteTask( GridMessage message, String consumerTag, Envelope envelope, BasicProperties properties )
    {
        this.message = message;
        this.consumerTag = consumerTag;
        this.envelope = envelope;
        this.properties = properties;
    }
    
    @Override
    public void run()
    {
        this.getMessage().execute( this.getConsumerTag(), this.getEnvelope(), this.getProperties() );
    }
}
