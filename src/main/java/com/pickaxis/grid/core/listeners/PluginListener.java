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

package com.pickaxis.grid.core.listeners;

import com.pickaxis.grid.coins.CoinCommands;
import com.pickaxis.grid.core.GridPlugin;
import com.pickaxis.grid.core.command.CommandManager;
import com.pickaxis.grid.core.events.GridManagerInitializedEvent;
import com.pickaxis.grid.core.server.ServerCommands;
import com.pickaxis.grid.core.server.ServerDataManager;
import com.pickaxis.grid.tokens.TokenCommands;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Plugin event listener.
 */
public class PluginListener implements Listener
{
    @EventHandler( priority = EventPriority.MONITOR )
    public void onPluginDisableEvent( final PluginDisableEvent event )
    {
        if( event.getPlugin().getDescription().getDepend().contains( "Grid" ) )
        {
            GridPlugin.getInstance().getPlayerManager().getServiceRegistry().unregisterServices( event.getPlugin() );
            GridPlugin.getInstance().getManagerCoordinator().shutdownManagers( event.getPlugin() );
        }
    }
    
    @EventHandler( priority = EventPriority.MONITOR )
    public void onPluginEnableEvent( final PluginEnableEvent event )
    {
        if( event.getPlugin().getDescription().getDepend().contains( "Grid" ) )
        {
            try
            {
                Properties settings = new Properties();
                settings.load( event.getPlugin().getResource( "grid.properties" ) );
                
                if( settings.contains( "managers.autoinitialize" ) && !Boolean.parseBoolean( settings.get( "managers.autoinitialize" ).toString() ) )
                {
                    return;
                }
            }
            catch( IOException | NullPointerException ex )
            {
                GridPlugin.getInstance().debug( String.format( "%s doesn't have a grid.properties file.", event.getPlugin().getName() ) );
            }
            
            GridPlugin.getInstance().getManagerCoordinator().initializeManagers( false, event.getPlugin() );
            
            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    GridPlugin.getInstance().getManagerCoordinator().initializeManagers( true, event.getPlugin() );
                }
            }.runTaskAsynchronously( event.getPlugin() );
        }
    }
    
    // Removed
    
    @EventHandler( priority = EventPriority.MONITOR )
    public void onGridManagerInitializedEvent( final GridManagerInitializedEvent event )
    {
        // TODO: Move these cases to somewhere more suitable (better organization).
        if( event.getManager() instanceof ServerDataManager )
        {
            // Removed
        }
        else if( event.getManager() instanceof CommandManager )
        {
            // Removed
        }
    }
}
