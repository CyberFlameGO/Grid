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

package com.pickaxis.grid.core.server;

import com.pickaxis.grid.core.GridPlugin;
import com.pickaxis.grid.core.data.tables.records.ServersRecord;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jooq.types.UShort;

/**
 * Grid representation of a local or remote server in the network.
 */
public class GridServerImpl implements GridServer
{
    @Getter
    @Setter( AccessLevel.PACKAGE )
    private ServersRecord dbRow;
    
    public GridServerImpl( ServersRecord dbRow )
    {
        this.setDbRow( dbRow );
    }
    
    @Override
    public boolean isLocalServer()
    {
        return GridPlugin.getInstance().getSdm().getLocalServer().equals( this );
    }
    
    @Override
    public UShort getId()
    {
        return this.getDbRow().getId();
    }
    
    @Override
    public String getSlug()
    {
        return this.getDbRow().getSlug();
    }
    
    @Override
    public String getName()
    {
        return this.getDbRow().getName();
    }
    
    @Override
    public ServerType getType()
    {
        return ServerType.get( this.getDbRow().getType().intValue() );
    }
    
    @Override
    public ServerVisibility getVisibility()
    {
        if( this.getDbRow().getEnabled() == null || this.getDbRow().getEnabled().equals( (byte) 0 )  )
        {
            return ServerVisibility.HIDDEN;
        }
        
        if( this.getDbRow().getIsOnline() == null || this.getDbRow().getIsOnline().equals( (byte) 0 ) )
        {
            return ServerVisibility.OFFLINE;
        }
        
        switch( this.getType() )
        {
            case SERVICE:
            case GLOBAL:
            case PROXY:
            case WORKER:
                return ServerVisibility.HIDDEN;
            case TEST:
                return ServerVisibility.STAFF;
            default:
                return ServerVisibility.PUBLIC;
        }
    }
}
