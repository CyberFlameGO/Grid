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

import com.pickaxis.grid.core.data.tables.records.ServersRecord;
import org.jooq.types.UShort;

/**
 * Grid representation of a local or remote server in the network.
 */
public interface GridServer
{
    /**
     * Gets the server's database row.
     * 
     * @return The server's database row.
     */
    ServersRecord getDbRow();
    
    /**
     * Shortcut to get the server's database ID.
     *
     * @return The server's database row ID.
     */
    UShort getId();
    
    /**
     * Shortcut to get the server's name
     *
     * @return The server's name.
     */
    String getName();
    
    /**
     * Shortcut to get the server's slug.
     *
     * @return The server's slug.
     */
    String getSlug();
    
    /**
     * Gets the type of server this is.
     *
     * @return The server's ServerType.
     */
    ServerType getType();
    
    /**
     * Determines whether this GridServer instance is the local server.
     *
     * @return Whether this instance is the local server.
     */
    boolean isLocalServer();
    
    /**
     * Gets the level of visibility for the server.
     * 
     * @return The server's visibility.
     */
    ServerVisibility getVisibility();
}
