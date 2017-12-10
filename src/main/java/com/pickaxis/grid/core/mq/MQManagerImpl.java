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
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.ShutdownSignalException;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.jodah.lyra.ConnectionOptions;
import net.jodah.lyra.Connections;
import net.jodah.lyra.config.Config;
import net.jodah.lyra.config.RecoveryPolicies;
import net.jodah.lyra.config.RecoveryPolicy;
import net.jodah.lyra.config.RetryPolicies;
import net.jodah.lyra.config.RetryPolicy;
import net.md_5.bungee.api.chat.BaseComponent;
import org.jooq.types.UNumber;

/**
 * Message Queue Manager
 */
@Getter( AccessLevel.PRIVATE )
@Setter( AccessLevel.PRIVATE )
public class MQManagerImpl implements MQManager
{
    private MQCredentials credentials;
    
    private ConnectionOptions connectionOptions;
    
    private RecoveryPolicy recoveryPolicy;
    
    private RetryPolicy retryPolicy;
    
    private LyraListener lyraListener;
    
    private Config config;
    
    private Connection connection;
    
    @Getter
    private boolean connected;
    
    private final Map<String, Channel> channelMap;
    
    private final Map<String, DefaultConsumer> consumerMap;
    
    @Getter
    private final JSONSerializer jsonSerializer;
    
    @Getter
    private final JSONDeserializer<GridMessage> jsonDeserializer;
    
    public MQManagerImpl()
    {
        this.channelMap = new HashMap<>();
        this.consumerMap = new HashMap<>();
        
        // Removed
        
        this.setRecoveryPolicy( RecoveryPolicies.recoverAlways() );
        this.setRetryPolicy( RetryPolicies.retryAlways() );
        this.setLyraListener( new LyraListener() );
        
        // Removed
        
        try
        {
            this.setConnection( Connections.create( this.getConnectionOptions(), this.getConfig() ) );
            this.setConnected( true );
        }
        catch( IOException ex )
        {
            this.setConnected( false );
            GridPlugin.getInstance().getLogger().log( Level.SEVERE, "Could not connect to message queue server.", ex );
        }
        
        // Removed
    }
    
    /**
     * Gets a channel identified by a specific name.  Automatically
     * creates the channel, if necessary.
     * 
     * @param name The name of the channel.
     * @return The requested Channel.
     */
    @Override
    public Channel getChannel( String name )
    {
        if( !this.getChannelMap().containsKey( name ) )
        {
            try
            {
                this.getChannelMap().put( name, this.getConnection().createChannel() );
            }
            catch( IOException ex )
            {
                throw new RuntimeException( "Could not create Channel: " + name, ex );
            }
        }
        return this.getChannelMap().get( name );
    }
    
    /**
     * Gets a consumer identified by a specific name.
     * 
     * @param name The name of the consumer.
     * @return The requested consumer, if it exists.
     */
    @Override
    public DefaultConsumer getConsumer( String name )
    {
        return this.getConsumerMap().get( name );
    }
    
    /**
     * Generates a consumer tag with a suffix.
     * 
     * @param queue The queue name.
     * @param tagSuffix The suffix for the consumer tag.
     * @return A consumer tag.
     */
    @Override
    public final String generateConsumerTag( String queue, String tagSuffix )
    {
        // Removed
    }
    
    /**
     * Consume a queue.
     * 
     * @param channel The channel to run the consumer on.
     * @param queue The queue to be consumed.
     * @param autoAck Whether messages should be auto-acknowledged when they arrive.
     * @param consumer The consumer object.
     * @param tagSuffix A suffix for the consumer tag.
     * @return The consumer tag.
     */
    @Override
    public String consume( String channel, String queue, boolean autoAck, Consumer consumer, String tagSuffix )
    {
        try
        {
            return this.getChannel( channel ).basicConsume( queue, autoAck, this.generateConsumerTag( queue, tagSuffix ), consumer );
        }
        catch( IOException ex )
        {
            throw new RuntimeException( "Could not begin consuming: " + channel + "/" + queue, ex );
        }
    }
    
    /**
     * Consume a queue.
     * 
     * @param channel The channel to run the consumer on.
     * @param queue The queue to be consumed.
     * @param autoAck Whether messages should be auto-acknowledged when they arrive.
     * @param consumer The consumer object.
     * @return The consumer tag.
     */
    @Override
    public String consume( String channel, String queue, boolean autoAck, Consumer consumer )
    {
        return this.consume( channel, queue, autoAck, consumer, null );
    }
    
    /**
     * Consume a queue.
     * 
     * @param channel The channel to run the consumer on.
     * @param queue The queue to be consumed.
     * @param consumer The consumer object.
     * @return The consumer tag.
     */
    @Override
    public String consume( String channel, String queue, Consumer consumer )
    {
        return this.consume( channel, queue, true, consumer );
    }
    
    /**
     * Cancel a consumer.
     * 
     * @param channel The channel the consumer is running on.
     * @param queue The queue to stop consuming.
     * @param tagSuffix A suffix for the consumer tag.
     */
    @Override
    public void cancelConsumer( String channel, String queue, String tagSuffix ) throws IllegalArgumentException
    {
        String tag = this.generateConsumerTag( queue, tagSuffix );
        try
        {
            this.getChannel( channel ).basicCancel( tag );
        }
        catch( IOException ex )
        {
            throw new IllegalArgumentException( "Could not cancel consumer: " + channel + "/" + tag, ex );
        }
    }
    
    /**
     * Cancel a consumer.
     * 
     * @param channel The channel the consumer is running on.
     * @param queue The queue to stop consuming.
     */
    @Override
    public void cancelConsumer( String channel, String queue ) throws IllegalArgumentException
    {
        this.cancelConsumer( channel, queue, null );
    }
    
    /**
     * Gracefully disconnect from the message queue server.
     */
    @Override
    public void shutdown()
    {
        try
        {
            this.getConnection().close( GridPlugin.getInstance().getConfig().getInt( "messagequeue.closetimeout", 5 ) );
        }
        catch( ShutdownSignalException ex )
        {
            GridPlugin.getInstance().getLogger().log( Level.INFO, "Message queue disconnected." );
        }
        catch( IOException ex )
        {
            throw new RuntimeException( "Couldn't succesfully close message queue connection.", ex );
        }
    }
}
