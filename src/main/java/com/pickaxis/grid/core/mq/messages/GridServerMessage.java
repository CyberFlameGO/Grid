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

package com.pickaxis.grid.core.mq.messages;

import com.pickaxis.grid.core.GridPlugin;
import com.pickaxis.grid.core.mq.MQConstants;
import com.pickaxis.grid.core.mq.flexjson.UNumberFactory;
import com.pickaxis.grid.core.server.GridServer;
import com.pickaxis.grid.core.server.ServerDataManager;
import com.pickaxis.grid.core.server.ServerType;
import com.rabbitmq.client.AMQP.BasicProperties;
import flexjson.JSON;
import java.io.IOException;
import java.util.logging.Level;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.SerializationUtils;
import org.jooq.types.UShort;

/**
 * Abstract class for server messages, which should include an
 * origin and destination server ID.  The destination server
 * ID can be used to determine if the message was sent to a
 * group of servers.
 */
@Getter
@Setter
public abstract class GridServerMessage extends GridMessage
{
    @JSON( objectFactory = UNumberFactory.class )
    protected UShort destinationServerId;
    
    public GridServer getDestinationServer()
    {
        return GridPlugin.getInstance().getManager( ServerDataManager.class ).getServer( this.getDestinationServerId() );
    }
    
    @Override
    public void send()
    {
        this.send( this.isDurable() );
    }
    
    protected void send( boolean durable )
    {
        String exchange= null;
        String routingKey = null;
        
        GridServer server = GridPlugin.getInstance().getSdm().getServer( this.getDestinationServerId() );
        
        switch( ServerType.get( server.getDbRow().getType().intValue() ) )
        {
            case GLOBAL:
                exchange = MQConstants.EXCHANGE_SERVERS_ALL;
                routingKey = "";
                break;
            case PROXY:
            case HUB:
            case NORMAL:
            case MINIGAME:
            case TEST:
            case WORKER:
                exchange = MQConstants.EXCHANGE_SERVERS_INDIVIDUAL;
                routingKey = server.getDbRow().getSlug();
                break;
            case GROUP:
                exchange = MQConstants.EXCHANGE_SERVERS_GROUPS;
                routingKey = server.getDbRow().getSlug();
                break;
        }
        
        if( !( exchange instanceof String ) || !( routingKey instanceof String ) )
        {
            throw new RuntimeException( "Couldn't find an appropriate exchange and/or routing key." );
        }
        
        if( durable )
        {
            exchange += MQConstants.DURABLE;
        }
        
        this.send( exchange, routingKey );
    }
    
    protected void send( String exchange, String routingKey )
    {
        BasicProperties properties = new BasicProperties().builder().contentType( "application/octet-stream" ).build();
        try
        {
            GridPlugin.getInstance().getMq().getChannel( MQConstants.CHANNEL_SERVER ).basicPublish( exchange, routingKey, properties, SerializationUtils.serialize( this ) );
        }
        catch( IOException ex )
        {
            GridPlugin.getInstance().getLogger().log( Level.WARNING, "Couldn't publish " + this.toString(), ex );
        }
    }
}
