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

import com.pickaxis.grid.core.GridManager;
import com.pickaxis.grid.core.mq.messages.GridMessage;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

/**
 * Message Queue Manager
 */
public interface MQManager extends GridManager
{
    /**
     * Whether this instance is connected to the MQ server.
     * 
     * @return True if this instance is connected to the MQ server.
     */
    boolean isConnected();
    
    /**
     * Gets the serializer used to serialize a GridMessage to JSON.
     * 
     * @return The serializer used to serialize a GridMessage to JSON.
     */
    JSONSerializer getJsonSerializer();
    
    /**
     * Gets the deserializer used for JSON GridMessages.
     * 
     * @return The deserializer used for JSON GridMessages.
     */
    JSONDeserializer<GridMessage> getJsonDeserializer();
    
    /**
     * Cancel a consumer.
     *
     * @param channel The channel the consumer is running on.
     * @param queue The queue to stop consuming.
     * @param tagSuffix A suffix for the consumer tag.
     */
    void cancelConsumer( String channel, String queue, String tagSuffix ) throws IllegalArgumentException;
    
    /**
     * Cancel a consumer.
     *
     * @param channel The channel the consumer is running on.
     * @param queue The queue to stop consuming.
     */
    void cancelConsumer( String channel, String queue ) throws IllegalArgumentException;
    
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
    String consume( String channel, String queue, boolean autoAck, Consumer consumer, String tagSuffix );
    
    /**
     * Consume a queue.
     *
     * @param channel The channel to run the consumer on.
     * @param queue The queue to be consumed.
     * @param autoAck Whether messages should be auto-acknowledged when they arrive.
     * @param consumer The consumer object.
     * @return The consumer tag.
     */
    String consume( String channel, String queue, boolean autoAck, Consumer consumer );
    
    /**
     * Consume a queue.
     *
     * @param channel The channel to run the consumer on.
     * @param queue The queue to be consumed.
     * @param consumer The consumer object.
     * @return The consumer tag.
     */
    String consume( String channel, String queue, Consumer consumer );
    
    /**
     * Generates a consumer tag with a suffix.
     *
     * @param queue The queue name.
     * @param tagSuffix The suffix for the consumer tag.
     * @return A consumer tag.
     */
    String generateConsumerTag( String queue, String tagSuffix );
    
    /**
     * Gets a channel identified by a specific name.  Automatically
     * creates the channel, if necessary.
     *
     * @param name The name of the channel.
     * @return The requested Channel.
     */
    Channel getChannel( String name );
    
    /**
     * Gets a consumer identified by a specific name.
     *
     * @param name The name of the consumer.
     * @return The requested consumer, if it exists.
     */
    DefaultConsumer getConsumer( String name );
}
