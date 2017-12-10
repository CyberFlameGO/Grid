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

package com.pickaxis.grid.chat.senders;

import com.pickaxis.grid.core.player.GridPlayer;
import com.pickaxis.grid.core.server.GridServer;
import java.util.UUID;
import net.md_5.bungee.api.chat.BaseComponent;
import org.jooq.types.UInteger;
import org.jooq.types.UShort;

/**
 * A player that's sending a message.
 */
public interface PlayerMessageSender extends MessageSender
{
    /**
     * Sets the player's unformatted name.
     * 
     * @param name The new unformatted name.
     * @return this PlayerMessageSender.
     */
    PlayerMessageSender setName( String name );
    
    /**
     * Sets the player's display name.
     * 
     * @param displayName The new display name.
     * @return This PlayerMessageSender.
     */
    PlayerMessageSender setDisplayName( String displayName );
    
    /**
     * Sets the player's component name.
     * 
     * @param componentName The new component name.
     * @return This PlayerMessageSender.
     */
    PlayerMessageSender setComponentName( BaseComponent componentName );
    
    /**
     * Gets the player's UUID.
     * 
     * @return The player's UUID.
     */
    UUID getUuid();
    
    /**
     * Gets the player's Grid ID.
     * 
     * @return The player's ID.
     */
    UInteger getId();
    
    /**
     * Gets the ID of the server the player is on.
     * 
     * @return The ID of the server the player is on.
     */
    UShort getServerId();
    
    /**
     * Gets the associated GridPlayer, if there is one.
     *
     * @return The associated GridPlayer.
     */
    GridPlayer getPlayer();
    
    /**
     * Gets the server the player is on.
     * 
     * @return The server the player is on.
     */
    GridServer getServer();
}
