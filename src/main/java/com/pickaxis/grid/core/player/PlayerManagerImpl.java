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

package com.pickaxis.grid.core.player;

import com.pickaxis.grid.core.GridPlugin;
import com.pickaxis.grid.core.data.tables.PlayTime;
import com.pickaxis.grid.core.data.tables.Players;
import com.pickaxis.grid.core.data.tables.records.PlayersRecord;
import com.pickaxis.grid.core.util.NameFetcher;
import com.pickaxis.grid.core.util.UUIDFetcher;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import lombok.AccessLevel;
import lombok.Getter;
import net.jodah.expiringmap.ExpiringMap;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;
import org.jooq.Result;
import org.jooq.types.UInteger;

/**
 * Grid player manager.
 */
@Getter( AccessLevel.PRIVATE )
public class PlayerManagerImpl implements PlayerManager
{
    // Removed
    
    private final Map<Player, GridPlayer> playerMap;
    
    private final Map<UInteger, GridPlayer> idMap;
    
    private final Map<UUID, GridPlayer> uuidMap;
    
    @Getter
    private final PlayerServiceRegistry serviceRegistry;
    
    public PlayerManagerImpl()
    {
        // Removed
        
        this.playerMap = new HashMap<>();
        this.idMap = new HashMap<>();
        
        // Removed
        
        this.serviceRegistry = new PlayerServiceRegistryImpl();
    }
    
    /**
     * Get the GridPlayer object associated with a player.
     * 
     * @param player The player to get the GridPlayer object for.
     * @return The player's associated GridPlayer object.
     */
    @Override
    public GridPlayer getPlayer( Player player )
    {
        if( this.getPlayerMap().containsKey( player ) )
        {
            return this.getPlayerMap().get( player );
        }
        
        GridPlayer gp = new GridPlayerImpl( player );
        this.getPlayerMap().put( player, gp );
        this.getIdMap().put( gp.getId(), gp );
        return gp;
    }
    
    /**
     * Get the GridPlayer object associated with a UUID.
     * 
     * @param uuid The UUID to get the GridPlayer object for.
     * @return The UUID's associated GridPlayer object.
     */
    @Override
    public GridPlayer getPlayer( UUID uuid )
    {
        // Fetch a GridPlayer that is already logged in by UUID
        Player p;
        p = GridPlugin.getInstance().getServer().getPlayer( uuid );
        if( p instanceof Player )
        {
            return this.getPlayer( p );
        }
        
        // Fetch a GridPlayer that isn't logged in (or is in pre-login phase)
        if( this.getUuidMap().containsKey( uuid ) )
        {
            return this.getUuidMap().get( uuid );
        }
        
        GridPlayer gp = new GridPlayerImpl( uuid );
        try
        {
            this.getUuidMap().put( uuid, gp );
        }
        catch( Exception ex )
        {
            GridPlugin.getInstance().getLogger().log( Level.WARNING, "Caught exception while atetmpting to store GridPlayer in UUID cache map.", ex );
        }
        
        return gp;
    }
    
    /**
     * Gets a player by their database ID.  This method should
     * be executed asynchronously if possible when attempting
     * to get a known offline player.
     * 
     * @param id The player's database ID
     * @return The requested GridPlayer
     */
    @Override
    public GridPlayer getPlayer( UInteger id ) throws IllegalArgumentException
    {
        if( this.getIdMap().containsKey( id ) )
        {
            return this.getIdMap().get( id );
        }
        
        PlayersRecord dbRow = GridPlugin.getInstance().getDb().getContext().selectFrom( Players.PLAYERS )
                                                                           .where( Players.PLAYERS.ID.eq( id ) )
                                                                           .fetchOne();
        if( !( dbRow instanceof PlayersRecord ) )
        {
            throw new IllegalArgumentException( "Invalid ID: No player returned." );
        }
        GridPlayer gp = this.getPlayer( UUID.fromString( dbRow.getUuid() ) );
        ( (GridPlayerImpl) gp ).setDbRow( dbRow );
        return gp;
    }
    
    /**
     * Get the GridPlayer object associated with a name or UUID, in
     * String format.  If no GridPlayer can be found, but the name
     * or UUID is valid, one will be created.  If the name or UUID
     * is not valid, null will be returned.  This is the most
     * expensive way of fetching a GridPlayer object, and should
     * be executed asynchronously if at all possible.
     * 
     * @param search The name or UUID of the player
     * @return The requested GridPlayer
     */
    @Override
    public GridPlayer getPlayer( String search ) throws IllegalArgumentException
    {
        // Get player by ID
        if( search.startsWith( "#" ) )
        {
            return this.getPlayer( UInteger.valueOf( search.substring( 1 ) ) );
        }
        
        // Get player by name
        if( search.length() <= 16 )
        {
            Player p;
            p = GridPlugin.getInstance().getServer().getPlayerExact( search );
            if( p instanceof Player )
            {
                return this.getPlayer( p );
            }
            
            Result<PlayersRecord> dbRow = GridPlugin.getInstance().getDb().getContext().selectFrom( Players.PLAYERS )
                                                                                       .where( Players.PLAYERS.NAME.equalIgnoreCase( search ) )
                                                                                       .fetch();
            
            GridPlayer gp;
            
            // Single result, player already exists in Grid database with no ambiguity.
            if( dbRow.size() == 1 )
            {
                gp = this.getPlayer( UUID.fromString( dbRow.get( 0 ).getUuid() ) );
                ( (GridPlayerImpl) gp ).setDbRow( dbRow.get( 0 ) );
                return gp;
            }
            
            // No results, add player to Grid database.
            if( dbRow.isEmpty() )
            {
                try
                {
                    UUID uuid = UUIDFetcher.getUUIDOf( search );
                    gp = GridPlugin.getInstance().getPlayerManager().getPlayer( uuid );
                    gp.setName( NameFetcher.getNameOf( uuid ) );
                }
                catch( Exception ex )
                {
                    throw new IllegalArgumentException( "Invalid name or error fetching UUID.", ex );
                }
                
                if( !( gp instanceof GridPlayer ) )
                {
                    throw new IllegalArgumentException( "Invalid player." );
                }
                
                return gp;
            }
            
            // Multiple results, find player who most recently played with the name.
            // if( dbRow.size() > 1 )
            UInteger selectedId = GridPlugin.getInstance().getDb().getContext().select( PlayTime.PLAY_TIME.PLAYER_ID )
                                                                               .from( PlayTime.PLAY_TIME )
                                                                               .where( PlayTime.PLAY_TIME.PLAYER_ID.in( dbRow.getValues( Players.PLAYERS.ID ) ) )
                                                                               .orderBy( PlayTime.PLAY_TIME.LOGIN_TIME.desc() )
                                                                               .fetchOne()
                                                                               .getValue( PlayTime.PLAY_TIME.PLAYER_ID );
            
            for( PlayersRecord pRow : dbRow )
            {
                if( pRow.getId().equals( selectedId ) )
                {
                    gp = GridPlugin.getInstance().getPlayerManager().getPlayer( UUID.fromString( pRow.getUuid() ) );
                    ( (GridPlayerImpl) gp ).setDbRow( pRow );
                    return gp;
                }
            }
            
            throw new IllegalArgumentException( "Player not found." );
        }
        
        // Get player by UUID
        // if( search.length() > 16 )
        UUID uuid;
        String name;
        
        try
        {
            uuid = UUID.fromString( search );
            name = NameFetcher.getNameOf( uuid );
        }
        catch( Exception ex )
        {
            throw new IllegalArgumentException( "Invalid UUID or error fetching name.", ex );
        }
        
        if( name instanceof String )
        {
            GridPlayer gp = this.getPlayer( uuid );
            gp.setName( name );
            return gp;
        }
        else
        {
            throw new IllegalArgumentException( "Invalid player." );
        }
    }
    
    /**
     * Gets all online GridPlayers.
     * 
     * @return A collection of all online GridPlayers.
     */
    @Override
    public Collection<GridPlayer> getAllOnline()
    {
        return this.getPlayerMap().values();
    }
    
    /**
     * Checks if a specific player is online using their Grid ID.
     * 
     * @param id
     * @return Whether the player is online on this server.
     */
    @Override
    public boolean isPlayerOnline( UInteger id )
    {
        return this.getIdMap().containsKey( id );
    }
    
    // Removed
    
    /**
     * Removes a player who has logged out from memory.
     * 
     * @param player The player to be removed.
     * @param async Whether database queries should be run asynchronously.
     */
    @Override
    public void logout( Player player, boolean async )
    {
        GridPlayer gp = this.getPlayer( player );
        
        ( (GridPlayerImpl) gp ).logout( async );
        
        // Remove player from maps.
        this.getPlayerMap().remove( player );
        this.getIdMap().remove( gp.getId() );
    }
    
    /**
     * Removes a player who has logged out from memory.
     * 
     * @param player The player to be removed.
     */
    @Override
    public void logout( Player player )
    {
        this.logout( player, true );
    }
    
    /**
     * Logs all players into Grid.
     */
    @Override
    public void loginAll()
    {
        for( Player player : GridPlugin.getInstance().getServer().getOnlinePlayers() )
        {
            this.getPlayer( player ).setIp( new GridIPAddressImpl( player.getAddress().getAddress() ) );
        }
    }
    
    /**
     * Logs all players out of Grid.
     */
    @Override
    public void logoutAll()
    {
        for( Player player : GridPlugin.getInstance().getServer().getOnlinePlayers() )
        {
            this.logout( player, false );
        }
    }
    
    /**
     * Called when the plugin is disabled.
     */
    @Override
    public void shutdown()
    {
        this.logoutAll();
    }
}
