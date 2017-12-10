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
import com.pickaxis.grid.core.data.tables.Servers;
import com.pickaxis.grid.core.data.tables.ServersGroupMembers;
import com.pickaxis.grid.core.data.tables.records.ServersRecord;
import com.pickaxis.grid.core.util.ProcessUtil;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Level;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.types.UInteger;
import org.jooq.types.UShort;
import org.redisson.api.RMap;

/**
 * Maintains records of all servers in the network, as well
 * as the ID of the currently running server.
 */
@Getter
@Setter( AccessLevel.PRIVATE )
public class ServerDataManagerImpl implements ServerDataManager
{
    private GridServer localServer;
    
    private GridServer globalServer;
    
    @Getter( AccessLevel.PRIVATE )
    private final Map<UShort, GridServer> idServerMap;
    
    @Getter( AccessLevel.PRIVATE )
    private Map<String, UShort> slugIdMap;
    
    @Getter( AccessLevel.PACKAGE )
    RMap<UShort, GridServer> onlineIdServerMap;
    
    private Collection<UShort> applicableServerIds;
    
    private Collection<String> applicableServerSlugs;
    
    public ServerDataManagerImpl()
    {
        // Register BungeeCord plugin messaging channel
        Bukkit.getMessenger().registerOutgoingPluginChannel( GridPlugin.getInstance(), "BungeeCord" );
        
        // Create map
        this.idServerMap = new HashMap<>();
        
        // Load all servers
        this.refresh();
        
        // Set global server
        this.setGlobalServer( this.getServer( GridPlugin.getInstance().getConfig().getInt( "managers.ServerData.globalServerId", 1 ) ) );
        
        // Removed
    }
    
    @Override
    public final void refresh( boolean recalculate )
    {
        // Get all servers from database
        Result<ServersRecord> serversQuery = GridPlugin.getInstance().getDb().getContext().selectFrom( Servers.SERVERS )
                                                                                          .fetch();
        
        // Update servers in map
        for( ServersRecord server : serversQuery )
        {
            if( this.getIdServerMap().containsKey( server.getId() ) )
            {
                ( (GridServerImpl) this.getIdServerMap().get( server.getId() ) ).setDbRow( server );
            }
            else
            {
                this.getIdServerMap().put( server.getId(), new GridServerImpl( server ) );
            }
        }
        
        if( recalculate && this.getLocalServer() != null )
        {
            this.refreshApplicableServerIds();
            this.refreshApplicableServerSlugs();
        }
    }
    
    @Override
    public final void refresh()
    {
        this.refresh( true );
    }
    
    @Override
    public void refreshApplicableServerIds()
    {
        Collection<UShort> ids = new HashSet<>();
        ids.add( this.getLocalServer().getDbRow().getId() );
        
        // Get server groups.
        Result<Record1<UShort>> idsQuery = GridPlugin.getInstance().getDb().getContext().select( ServersGroupMembers.SERVERS_GROUP_MEMBERS.GROUP_ID )
                                                                                        .from( ServersGroupMembers.SERVERS_GROUP_MEMBERS )
                                                                                        .where( ServersGroupMembers.SERVERS_GROUP_MEMBERS.MEMBER_ID.eq( this.getLocalServer().getDbRow().getId() ) )
                                                                                        .fetch();
        for( Record1<UShort> id : idsQuery )
        {
            ids.add( id.value1() );
        }
        
        // Get global server(s).
        Result<Record1<UShort>> idsQueryGlobal = GridPlugin.getInstance().getDb().getContext().select( Servers.SERVERS.ID )
                                                                                              .from( Servers.SERVERS )
                                                                                              .where( Servers.SERVERS.TYPE.eq( ServerType.GLOBAL.getDbCode() ) )
                                                                                              .fetch();
        
        for( Record1<UShort> id : idsQueryGlobal )
        {
            ids.add( id.value1() );
        }
        
        this.setApplicableServerIds( Collections.unmodifiableCollection( ids ) );
        
        GridPlugin.getInstance().debug( "Applicable server IDs: " + ids.toString() );
    }
    
    @Override
    public void refreshApplicableServerSlugs()
    {
        Collection<String> slugs = new HashSet<>();
        slugs.add( this.getLocalServer().getDbRow().getSlug() );
        for( GridServer server : this.getIdServerMap().values() )
        {
            if( this.getApplicableServerIds().contains( server.getDbRow().getId() ) )
            {
                slugs.add( server.getDbRow().getSlug() );
            }
        }
        this.setApplicableServerSlugs( Collections.unmodifiableCollection( slugs ) );
    }
    
    @Override
    public Collection<UShort> getApplicableServerIds()
    {
        return Collections.unmodifiableCollection( this.applicableServerIds );
    }
    
    @Override
    public Collection<String> getApplicableServerSlugs()
    {
        return Collections.unmodifiableCollection( this.applicableServerSlugs );
    }
    
    @Override
    public void updateLocalServer()
    {
        this.getLocalServer().getDbRow().update();
    }
    
    @Override
    public GridServer getServer( UShort id )
    {
        return this.getIdServerMap().get( id );
    }
    
    @Override
    public GridServer getServer( int id )
    {
        return this.getServer( UShort.valueOf( id ) );
    }
    
    @Override
    public GridServer getServer( String slug )
    {
        slug = slug.toLowerCase();
        switch( slug )
        {
            case "local":
            case "@":
                return this.getLocalServer();
            case "*":
                slug = "global";
                break;
        }
        
        for( GridServer server : this.getAllServers() )
        {
            if( server.getDbRow().getSlug().equals( slug ) )
            {
                return server;
            }
        }
        
        return null;
    }
    
    @Override
    public Collection<GridServer> getAllServers()
    {
        return this.getIdServerMap().values();
    }
    
    @Override
    public void shutdown()
    {
        // Stop updating database player count
        this.getPlayerCountUpdateTask().cancel();
        
        // Zero out database values
        this.getLocalServer().getDbRow().setIsOnline( (byte) 0 );
        this.getLocalServer().getDbRow().setPid( null );
        this.getLocalServer().getDbRow().setPlayersOnline( UShort.valueOf( 0 ) );
        this.getLocalServer().getDbRow().setPlayersHidden( UShort.valueOf( 0 ) );
        this.getLocalServer().getDbRow().update();
        
        // Unregister BungeeCord plugin messaging channel
        Bukkit.getMessenger().unregisterOutgoingPluginChannel( GridPlugin.getInstance(), "BungeeCord" );
    }
}
