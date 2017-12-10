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

package com.pickaxis.grid.core;

import org.bukkit.plugin.Plugin;

/**
 * Coordinates registration, initialization, getting, and
 * shutdown of GridManagers.
 */
public interface ManagerCoordinator
{
    /**
     * Gets a manager.
     * 
     * @param <T> The type of the manager to get.
     * @param cls The interface of the manager to get.
     * @return The requested manager.
     */
    <T extends GridManager> T get( Class<T> cls );
    
    /**
     * Initializes a group of managers for the specified plugin.
     * 
     * @param async False to initialize the synchronous group, true to initialize the asynchronous group.
     * @param plugin The plugin to initialize managers for.
     */
    void initializeManagers( boolean async, Plugin plugin );
    
    /**
     * Registers a manager.
     * 
     * @param priority The manager's priority.  Lower get loaded earlier, above ManagerCoordinatorImpl.ASYNC_THRESHOLD will be initialized asynchronously.
     * @param intf The manager's interface.
     * @param impl The manager's implementation.
     * @param plugin The plugin the manager belongs to.
     */
    void registerManager( Integer priority, Class<? extends GridManager> intf, Class<? extends GridManager> impl, Plugin plugin );
    
    /**
     * Registers a manager.
     * 
     * @param priority The manager's priority.  Lower get loaded earlier, above ManagerCoordinatorImpl.ASYNC_THRESHOLD will be initialized asynchronously.
     * @param impl The manager's implementation.
     * @param plugin The plugin the manager belongs to.
     */
    void registerManager( Integer priority, Class<? extends GridManager> impl, Plugin plugin );
    
    /**
     * Shuts down all managers for a plugin.
     * 
     * @param plugin The plugin to shutdown managers for.
     */
    void shutdownManagers( Plugin plugin );
}
