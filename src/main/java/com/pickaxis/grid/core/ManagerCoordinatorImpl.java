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

import com.google.common.base.Preconditions;
import com.pickaxis.grid.core.events.GridManagerInitializedEvent;
import com.pickaxis.grid.core.events.GridManagerPreShutdownEvent;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * Coordinates registration, initialization, getting, and
 * shutdown of GridManagers.
 */
@Getter( AccessLevel.PRIVATE )
public class ManagerCoordinatorImpl implements ManagerCoordinator
{
    public static final int ASYNC_THRESHOLD = 32000;
    
    private final Map<Class<? extends GridManager>, GridManager> managers;
    
    private final Map<Plugin, SortedMap<Integer, Map<Class<? extends GridManager>, Class<? extends GridManager>>>> managerPriorities;
    
    private final Map<Plugin, SortedMap<Integer, Map<Class<? extends GridManager>, Class<? extends GridManager>>>> reverseManagerPriorities;
    
    public ManagerCoordinatorImpl()
    {
        this.managers = new HashMap<>();
        this.managerPriorities = new HashMap<>();
        this.reverseManagerPriorities = new HashMap<>();
    }
    
    @Override
    public void registerManager( Integer priority, Class<? extends GridManager> intf, Class<? extends GridManager> impl, Plugin plugin )
    {
        Preconditions.checkArgument( intf.isAssignableFrom( impl ), "%s does not implement %s", impl.getCanonicalName(), intf.getCanonicalName() );
        
        // Manager Priorities
        if( !this.getManagerPriorities().containsKey( plugin ) )
        {
            this.getManagerPriorities().put( plugin, new TreeMap<Integer, Map<Class<? extends GridManager>, Class<? extends GridManager>>>() );
        }
        Map<Integer, Map<Class<? extends GridManager>, Class<? extends GridManager>>> mgrsPl = this.getManagerPriorities().get( plugin );
        
        if( !mgrsPl.containsKey( priority ) )
        {
            mgrsPl.put( priority, new LinkedHashMap<Class<? extends GridManager>, Class<? extends GridManager>>() );
        }
        Map<Class<? extends GridManager>, Class<? extends GridManager>> mgrsPlPri = mgrsPl.get( priority );
        
        // Reverse Manager Priorities
        if( !this.getReverseManagerPriorities().containsKey( plugin ) )
        {
            this.getReverseManagerPriorities().put( plugin, new TreeMap<Integer, Map<Class<? extends GridManager>, Class<? extends GridManager>>>( Collections.reverseOrder() ) );
        }
        Map<Integer, Map<Class<? extends GridManager>, Class<? extends GridManager>>> revMgrsPl = this.getReverseManagerPriorities().get( plugin );
        
        if( !revMgrsPl.containsKey( priority ) )
        {
            revMgrsPl.put( priority, mgrsPlPri );
        }
        
        // Add
        mgrsPlPri.put( intf, impl );
    }
    
    @Override
    public void registerManager( Integer priority, Class<? extends GridManager> impl, Plugin plugin )
    {
        this.registerManager( priority, impl, impl, plugin );
    }
    
    @Override
    public void initializeManagers( boolean async, Plugin plugin )
    {
        if( !this.getManagerPriorities().containsKey( plugin ) )
        {
            GridPlugin.getInstance().debug( String.format( "No managers registered for %s", plugin.getName() ) );
            return;
        }
        
        GridPlugin.getInstance().debug( String.format( "Initializing %s managers for %s", async ? "async" : "sync", plugin.getName() ) );
        for( Entry<Integer, Map<Class<? extends GridManager>, Class<? extends GridManager>>> mgrsPl : async ? this.getManagerPriorities().get( plugin ).tailMap( ManagerCoordinatorImpl.ASYNC_THRESHOLD ).entrySet() : this.getManagerPriorities().get( plugin ).headMap( ManagerCoordinatorImpl.ASYNC_THRESHOLD ).entrySet() )
        {
            GridPlugin.getInstance().debug( String.format( "Initializing managers for %s at priority %s", plugin.getName(), mgrsPl.getKey() ) );
            for( Entry<Class<? extends GridManager>, Class<? extends GridManager>> mgr : mgrsPl.getValue().entrySet() )
            {
                GridPlugin.getInstance().debug( String.format( "Initializing manager: %s", mgr.getValue().getCanonicalName() ) );
                try
                {
                    this.getManagers().put( mgr.getKey(), mgr.getValue().newInstance() );
                    Bukkit.getPluginManager().callEvent( new GridManagerInitializedEvent( this.getManagers().get( mgr.getKey() ) ) );
                }
                catch( InstantiationException | IllegalAccessException ex )
                {
                    GridPlugin.getInstance().getLogger().log( Level.SEVERE, String.format( "Couldn't initialize manager: %s (Interface: %s)", mgr.getValue().getCanonicalName(), mgr.getKey().getCanonicalName() ), ex );
                }
            }
        }
    }
    
    @Override
    public <T extends GridManager> T get( Class<T> cls )
    {
        return cls.cast( this.getManagers().get( cls ) );
    }
    
    @Override
    public void shutdownManagers( Plugin plugin )
    {
        GridPlugin.getInstance().debug( String.format( "Shutting down managers for $s", plugin.getName() ) );
        for( Entry<Integer, Map<Class<? extends GridManager>, Class<? extends GridManager>>> mgrsPl : this.getReverseManagerPriorities().get( plugin ).entrySet() )
        {
            GridPlugin.getInstance().debug( String.format( "Shutting down managers for %s at priority %s", plugin.getName(), mgrsPl.getKey() ) );
            for( Entry<Class<? extends GridManager>, Class<? extends GridManager>> mgr : mgrsPl.getValue().entrySet() )
            {
                GridPlugin.getInstance().debug( String.format( "Shutting down manager: %s", mgr.getValue().getCanonicalName() ) );
                try
                {
                    Bukkit.getPluginManager().callEvent( new GridManagerPreShutdownEvent( this.getManagers().get( mgr.getKey() ) ) );
                    this.getManagers().remove( mgr.getKey() ).shutdown();
                }
                catch( Exception ex )
                {
                    GridPlugin.getInstance().getLogger().log( Level.SEVERE, String.format( "Couldn't shut down manager: %s (Interface: %s)", mgr.getValue().getCanonicalName(), mgr.getKey().getCanonicalName() ), ex );
                }
            }
        }
    }
}
