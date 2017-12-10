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

/**
 * Interface for player services.
 */
public interface PlayerService
{
    /**
     * Gets the player that the instance is associated with.
     * 
     * @return The GridPlayer this PlayerService instance is related to.
     */
    GridPlayer getPlayer();
    
    /**
     * Executed before the player fully logs in.
     */
    void onAsyncPreLogin();
    
    /**
     * Executed when the player logs in, or when the service is registered.
     */
    void onLogin();
    
    /**
     * Executed asynchronously when the player logs in, or when the service is registered.
     * 
     * This is not guaranteed to run expeditiously, and can take a few moments, especially
     * if a service is registered (or a plugin is reloaded) while there is a large number
     * of players on the server, or if there is a large number of services registered.
     */
    void onAsyncPostLogin();
    
    /**
     * Executed when the player is in the world, or when the service is registered.
     */
    void onJoin();
    
    /**
     * Executed when information about the player is refreshed.
     */
    void onRefresh();
    
    /**
     * Executed when the player logs out, or when the service is unregistered.
     */
    void onLogout();
}
