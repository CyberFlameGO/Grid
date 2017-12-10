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

import java.util.Map;
import org.jooq.Record3;
import org.jooq.exception.DataAccessException;
import org.jooq.types.UShort;

/**
 * PlayerService for storing arbitrary data in the database.
 * 
 * You must obtain a data key before using the methods in this class.
 * Failure to do so will result in complete and utter failure.  And
 * probably some exceptions, too.
 * 
 * @deprecated This was a bad idea.
 */
@Deprecated
public interface DataPlayerService extends PlayerService
{
    /**
     * Fetches extra player data that isn't stored with the main player
     * row.  This method should always be executed asynchronously.
     */
    void fetchData();
    
    /**
     * Gets the data rows associated with the player applicable
     * to this server, or fetches them if necessary.
     *
     * @return The player's persistent extra data.
     */
    Map<String, Record3<String, UShort, String>> getData();
    
    /**
     * Get the value of the specified key for the player.
     *
     * @param key The key to get the value of.
     * @return The value of the specified key.
     */
    String getData( String key );
    
    /**
     * Get the server ID associated with the specified key for the player.
     *
     * @param key The key to get the associated server ID of.
     * @return The associated server ID.
     */
    UShort getDataServerId( String key );
    
    /**
     * Checks to see if the player has data for the specified key.
     *
     * @param key The key to check.
     * @return Whether data is present for the key.
     */
    boolean hasData( String key );
    
    /**
     * Sets a data value for a player on the specified server ID.  This
     * method should always be executed asynchronously.
     *
     * @param key The key to identify the data by.
     * @param value The value of the data for the player on the specified server.
     * @param serverId The server ID to set the value for.
     * @throws DataAccessException If the specified key isn't present in the database.
     */
    void setData( String key, String value, UShort serverId ) throws DataAccessException;
    
    /**
     * Sets a data value for a player, using the local server ID.  This
     * method should always be executed asynchronously.
     *
     * @param key The key to identify the data by.
     * @param value The value of the data for the player on the specified server.
     * @throws DataAccessException If the specified key isn't present in the database.
     */
    void setData( String key, String value ) throws DataAccessException;
}
