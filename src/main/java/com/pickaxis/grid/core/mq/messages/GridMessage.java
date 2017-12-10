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
import com.pickaxis.grid.core.mq.MessageParameter;
import com.pickaxis.grid.core.mq.MessageParameters;
import com.pickaxis.grid.core.mq.flexjson.UNumberFactory;
import com.pickaxis.grid.core.server.GridServer;
import com.pickaxis.grid.core.server.ServerDataManager;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Envelope;
import flexjson.JSON;
import java.io.Serializable;
import java.util.Arrays;
import lombok.Getter;
import lombok.Setter;
import org.jooq.types.UShort;

/**
 * Base message class.
 */
public abstract class GridMessage implements Serializable
{
    @Getter
    @Setter
    @JSON( objectFactory = UNumberFactory.class )
    protected UShort originServerId;
    
    public GridMessage()
    {
        this.setOriginServerId( GridPlugin.getInstance().getSdm().getLocalServer().getDbRow().getId() );
    }
    
    @JSON( include = false )
    public boolean isAsync()
    {
        return this.getClass().isAnnotationPresent( MessageParameters.class ) ? Arrays.asList( this.getClass().getAnnotation( MessageParameters.class ).value() ).contains( MessageParameter.ASYNC ) : false;
    }
    
    @JSON( include = false )
    public boolean isDurable()
    {
        return this.getClass().isAnnotationPresent( MessageParameters.class ) ? Arrays.asList( this.getClass().getAnnotation( MessageParameters.class ).value() ).contains( MessageParameter.DURABLE ) : false;
    }
    
    public GridServer getOriginServer()
    {
        return GridPlugin.getInstance().getManager( ServerDataManager.class ).getServer( this.getOriginServerId() );
    }
    
    public abstract void send();
    
    public void execute()
    {
        // Must be implemented if neither of the following execute methods are implemented.
    }
    
    public void execute( String consumerTag )
    {
        this.execute();
    }
    
    public void execute( String consumerTag, Envelope envelope, BasicProperties properties )
    {
        this.execute( consumerTag );
    }
}
