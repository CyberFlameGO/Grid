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
import com.pickaxis.grid.core.mq.flexjson.UUIDFactory;
import com.pickaxis.grid.core.mq.flexjson.UUIDTransformer;
import com.pickaxis.grid.core.player.GridPlayer;
import com.rabbitmq.client.AMQP.BasicProperties;
import flexjson.JSON;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.SerializationUtils;

/**
 * Abstract class for player messages, which should include a
 * player's UUID.
 */
@Getter
@Setter
public abstract class GridPlayerMessage extends GridMessage
{
    @JSON( transformer = UUIDTransformer.class,
           objectFactory = UUIDFactory.class )
    protected UUID playerUuid;
    
    public GridPlayer getPlayer()
    {
        return GridPlugin.getInstance().getPlayerManager().getPlayer( this.getPlayerUuid() );
    }
    
    @Override
    public void send()
    {
        this.send( false );
    }
    
    protected void send( boolean toProxy )
    {
        BasicProperties properties = new BasicProperties().builder().contentType( "application/octet-stream" ).build();
        
        String exchange = MQConstants.EXCHANGE_PLAYERS;
        if( toProxy )
        {
            exchange = MQConstants.EXCHANGE_PLAYERS_PROXY;
        }
        
        try
        {
            GridPlugin.getInstance().getMq().getChannel( MQConstants.CHANNEL_PLAYERS ).basicPublish( exchange, this.getPlayerUuid().toString(), properties, SerializationUtils.serialize( this ) );
        }
        catch( IOException ex )
        {
            GridPlugin.getInstance().getLogger().log( Level.WARNING, "Couldn't publish " + this.toString(), ex );
        }
    }
}
