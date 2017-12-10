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

import com.pickaxis.grid.core.GridManager;
import java.util.Collection;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.jooq.types.UInteger;

/**
 * Grid player manager.
 */
public interface PlayerManager extends GridManager
{
    /**
     * Gets the PlayerServiceRegistry.
     * 
     * @return The PlayerServiceRegistry.
     */
    PlayerServiceRegistry getServiceRegistry();
    
    /**
     * Moves a player from from the temporary map to the
     * permanent map after their login is complete.
     *
     * @param player
     */
    void finishLogin( Player player );
    
    /**
     * Gets all online GridPlayers.
     *
     * @return A collection of all online GridPlayers.
     */
    Collection<GridPlayer> getAllOnline();
    
    /**
     * Get the GridPlayer object associated with a player.
     *
     * @param player The player to get the GridPlayer object for.
     * @return The player's associated GridPlayer object.
     */
    GridPlayer getPlayer( Player player );
    
    /**
     * Get the GridPlayer object associated with a UUID.
     *
     * @param uuid The UUID to get the GridPlayer object for.
     * @return The UUID's associated GridPlayer object.
     */
    GridPlayer getPlayer( UUID uuid );
    
    /**
     * Gets a player by their database ID.  This method should
     * be executed asynchronously if possible when attempting
     * to get a known offline player.
     *
     * @param id The player's database ID
     * @return The requested GridPlayer
     */
    GridPlayer getPlayer( UInteger id ) throws IllegalArgumentException;
    
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
    GridPlayer getPlayer( String search ) throws IllegalArgumentException;
    
    /**
     * Checks if a specific player is online using their Grid ID.
     *
     * @param id
     * @return Whether the player is online on this server.
     */
    boolean isPlayerOnline( UInteger id );
    
    /**
     * Logs all players into the Grid.
     */
    void loginAll();
    
    /**
     * Removes a player who has logged out from memory.
     *
     * @param player The player to be removed.
     * @param async Whether database queries should be run asynchronously.
     */
    void logout( Player player, boolean async );
    
    /**
     * Removes a player who has logged out from memory.
     *
     * @param player The player to be removed.
     */
    void logout( Player player );
    
    /**
     * Logs all players out of the Grid.
     */
    void logoutAll();
}
