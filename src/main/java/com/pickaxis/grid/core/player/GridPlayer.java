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

import com.pickaxis.grid.core.data.tables.records.PlayersRecord;
import com.pickaxis.grid.core.db.Fetchable;
import com.pickaxis.grid.core.server.GridServer;
import java.util.UUID;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;
import org.jooq.Result;
import org.jooq.types.UInteger;

/**
 * Grid player wrapper.
 */
public interface GridPlayer extends Fetchable
{
    /**
     * Gets the player's UUID.
     * 
     * @return The player's UUID.
     */
    UUID getUuid();
    
    /**
     * Gets the player's IP address.
     * 
     * @return The player's IP address.
     */
    GridIPAddress getIp();
    
    /**
     * Gets the associated Bukkit Player object.
     * 
     * @return The associated Bukkit Player object.
     */
    Player getPlayer();
    
    /**
     * Gets the player's unformatted name.
     * 
     * @return The player's unformatted name.
     */
    String getName();
    
    /**
     * Sets the player's unformatted name.  This method should only be
     * used by Grid Core, or when creating a GridPlayer instance for
     * an offline player.
     * 
     * @param name The player's name.
     * @return This GridPlayer object.
     */
    GridPlayer setName( String name );
    
    // Removed
    
    /**
     * Gets the timestamp when the player logged in.
     * 
     * @return The timestamp when the player logged in.
     */
    Integer getLoginTime();
    
    /**
     * Gets the server the player is on.
     * 
     * @return The server the player is on.
     */
    GridServer getServer();
    
    // Removed
    
    /**
     * Gets the player's database row, or fetches it if necessary.
     *
     * @return The player's database row.
     */
    PlayersRecord getDbRow();
    
    /**
     * Shortcut to get a player's database ID.
     *
     * @return The player's database row ID.
     */
    UInteger getId();
    
    /**
     * Gets the MQ queue name for the player.
     *
     * @return The player's MQ queue name.
     */
    String getQueueName();
    
    /**
     * Retrieves a service related to this player.
     *
     * @param <C> The PlayerService to be obtained.
     * @param key The PlayerService class.
     * @return The PlayerService class for this player.
     */
    <C extends PlayerService> C getService( Class<C> key );
    
    /**
     * Shortcut for getPlayer().hasPermission( permission ).
     *
     * @param permission The permission to check for.
     * @return Whether the player has the permission.
     */
    boolean hasPermission( String permission );
    
    /**
     * Checks if the player is visible with VanishNoPacket.
     *
     * true: Player visible to everyone or VanishNoPacket is disabled.
     * false: Player is vanished.
     *
     * @return True if the player is visible to everyone.
     */
    boolean isVisible();
    
    /**
     * Reload the player's database row.
     *
     * @param delay Ticks to delay the reload by.
     */
    void refreshDbRow( int delay );
    
    /**
     * Reload the player's database row.
     */
    void refreshDbRow();
    
    /**
     * Shortcut for getPlayer().sendMessage( message ).
     *
     * @param message The message to send to the player.
     */
    void sendMessage( String message );
    
    /**
     * Sends a player a BaseComponent message, automatically utilizing the
     * proper underlying method for the implementation Grid is running on.
     *
     * @param components The message to send to the player.
     */
    void sendMessage( BaseComponent... components );
    
    /**
     * Sets an IP address for this player and associates it
     * with them in the database.  This method should always
     * be run asynchronously.
     *
     * @param ip The IP address to associate.
     * @return This GridPlayer object.
     */
    GridPlayer setIp( GridIPAddress ip );
    
    /**
     * Queue an asynchronous update of the player's database row.
     *
     * @deprecated This method is unused in Grid Core.
     */
    @Deprecated
    void updateDbRow();
    
    /**
     * Send the player to another server in the network.
     * 
     * @param dest The server to send the player to.
     */
    void sendToServer( GridServer dest );
}
