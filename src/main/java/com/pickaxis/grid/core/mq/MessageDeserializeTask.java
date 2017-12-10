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
import com.pickaxis.grid.core.mq.messages.GridMessage;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Envelope;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.logging.Level;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Deserialize a GridMessage, then execute it.
 */
@Getter( AccessLevel.PRIVATE )
public class MessageDeserializeTask extends BukkitRunnable
{
    private final String consumerTag;
    
    private final Envelope envelope;
    
    private final BasicProperties properties;
    
    private final byte[] data;
    
    public MessageDeserializeTask( String consumerTag, Envelope envelope, BasicProperties properties, byte[] data )
    {
        this.consumerTag = consumerTag;
        this.envelope = envelope;
        this.properties = properties;
        this.data = data;
    }
    
    @Override
    public void run()
    {
        GridMessage message;
        
        try
        {
            switch( properties.getContentType() )
            {
                case "application/json":
                    ClassLoader cl = Thread.currentThread().getContextClassLoader();
                    Thread.currentThread().setContextClassLoader( this.getClass().getClassLoader() );
                    message = GridPlugin.getInstance().getMq().getJsonDeserializer().deserialize( new String( this.getData() ) );
                    Thread.currentThread().setContextClassLoader( cl );
                    break;
                case "application/octet-stream":
                default:
                    try( ByteArrayInputStream bis = new ByteArrayInputStream( this.getData() );
                         ObjectInput in = new ObjectInputStream( bis ) )
                    {
                        message = (GridMessage) in.readObject();
                    }
                    break;
            }
        }
        catch( InvalidClassException ex )
        {
            GridPlugin.getInstance().getLogger().log( Level.WARNING, "Received a message from a different version of Grid: {0}", ex.getMessage() );
            return;
        }
        catch( IOException | ClassNotFoundException ex )
        {
            GridPlugin.getInstance().getLogger().log( Level.WARNING, "Failed to deserialize a GridMessage.", ex );
            return;
        }
        
        Bukkit.getPluginManager().callEvent( new GridMessageReceivedEvent( message ) );
        
        if( message.isAsync() )
        {
            message.execute( this.getConsumerTag(), this.getEnvelope(), this.getProperties() );
        }
        else
        {
            new MessageExecuteTask( message, this.getConsumerTag(), this.getEnvelope(), this.getProperties() ).runTask( GridPlugin.getInstance() );
        }
    }
}
