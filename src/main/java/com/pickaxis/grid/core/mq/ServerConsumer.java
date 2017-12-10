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

import com.pickaxis.grid.core.GridPlugin;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import java.io.IOException;
import java.util.logging.Level;

/**
 * Handles incoming messages on the server channel.
 */
public class ServerConsumer extends DefaultConsumer
{
    public ServerConsumer( Channel channel )
    {
        super( channel );
    }
    
    @Override
    public void handleDelivery( String consumerTag, Envelope envelope, BasicProperties properties, byte[] body )
    {
        if( GridPlugin.getInstance().isEnabled() )
        {
            new MessageDeserializeTask( consumerTag, envelope, properties, body ).runTaskAsynchronously( GridPlugin.getInstance() );
        }
        else
        {
            try
            {
                this.getChannel().basicNack( envelope.getDeliveryTag(), false, true );
            }
            catch( IOException ex )
            {
                GridPlugin.getInstance().getLogger().log( Level.WARNING, "Couldn't nack MQ message: " + envelope.getDeliveryTag(), ex );
            }
        }
    }
}
