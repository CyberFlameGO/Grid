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

import com.pickaxis.grid.core.GridManager;
import java.util.Collection;
import org.jooq.types.UShort;

/**
 * Maintains records of all servers in the network, as well
 * as the ID of the currently running server.
 */
public interface ServerDataManager extends GridManager
{
    /**
     * Gets the local (running) server.
     * 
     * @return The GridServer for this instance.
     */
    GridServer getLocalServer();
    
    /**
     * Gets the global server.
     * 
     * @return The global server.
     */
    GridServer getGlobalServer();
    
    /**
     * Gets an unmodifiable collection of all applicable server IDs.
     * This is primarily for use in database queries.
     * 
     * @return A collection of server IDs applicable to this server.
     */
    Collection<UShort> getApplicableServerIds();
    
    /**
     * Gets an unmodifiable collection of all applicable server slugs.
     * 
     * @return A collection of server slugs applicable to this server.
     */
    Collection<String> getApplicableServerSlugs();
    
    /**
     * Gets a collection of all servers in the network.
     *
     * @return A collection of all GridServers in the network.
     */
    Collection<GridServer> getAllServers();
    
    /**
     * Get a GridServer based on its ID.
     *
     * @param id The requested server's ID.
     * @return The related GridServer object.
     */
    GridServer getServer( UShort id );
    
    /**
     * Get a GridServer based on its ID.
     *
     * @param id The requested server's ID.
     * @return The related GridServer object.
     */
    GridServer getServer( int id );
    
    /**
     * Gets a grid server based on its slug.
     *
     * @param slug The requested server's slug.
     * @return The related GridServer object.
     */
    GridServer getServer( String slug );
    
    /**
     * Fetch all servers from the database.
     * 
     * @param recalculate Whether the applicable server IDs and slugs should be recalculated.
     */
    void refresh( boolean recalculate );
    
    /**
     * Fetch all servers from the database.
     */
    void refresh();
    
    /**
     * Populate applicable server IDs, for use in "WHERE `server_id` IN( ... )" queries.
     */
    void refreshApplicableServerIds();
    
    /**
     * Populate applicable server slugs, for use in message queuing.
     */
    void refreshApplicableServerSlugs();
    
    /**
     * Update the running server's database row.
     */
    void updateLocalServer();
}
