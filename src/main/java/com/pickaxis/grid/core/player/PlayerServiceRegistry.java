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
import org.bukkit.plugin.Plugin;

/**
 * Allows for registration of player services.
 */
public interface PlayerServiceRegistry
{
    /**
     * Gets a map of all registered services and their implementations.
     * 
     * @return A map of services and their implementations.
     */
    Map<Class<? extends PlayerService>, Class<? extends PlayerService>> getServices();
    
    /**
     * Registers a player service.
     *
     * @param intf The service's interface.
     * @param impl The service's implementation.
     * @param plugin The plugin the service is a part of.
     */
    void registerService( final Class<? extends PlayerService> intf, Class<? extends PlayerService> impl, Plugin plugin );
    
    /**
     * Registers a player service.
     *
     * @deprecated Services should have an interface.
     * @param impl The service's implementation.
     * @param plugin The plugin the service is a part of.
     */
    @Deprecated
    void registerService( Class<? extends PlayerService> impl, Plugin plugin );
    
    /**
     * Registers a player service for Grid Core.
     *
     * @param intf The service's interface.
     * @param impl The service's implementation.
     */
    void registerService( Class<? extends PlayerService> intf, Class<? extends PlayerService> impl );
    
    /**
     * Registers a player service for Grid Core.
     *
     * @deprecated Services should have an interface.
     * @param impl The service's implementation.
     */
    @Deprecated
    void registerService( Class<? extends PlayerService> impl );
    
    /**
     * Unregisters a player service.
     *
     * @param intf The service's interface.
     * @param plugin The plugin the service is a part of.
     */
    void unregisterService( Class<? extends PlayerService> intf, Plugin plugin );
    
    /**
     * Unregisters a player service for Grid Core.
     *
     * @param intf The service's interface.
     */
    void unregisterService( Class<? extends PlayerService> intf );
    
    /**
     * Unregisters all of a plugin's player services.
     *
     * @param plugin The plugin that is being unregistered.
     */
    void unregisterServices( Plugin plugin );
}
