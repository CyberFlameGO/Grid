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

import com.google.common.base.Preconditions;
import com.pickaxis.grid.core.GridPlugin;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Allows for registration of player services.
 */
public class PlayerServiceRegistryImpl implements PlayerServiceRegistry
{
    @Getter( AccessLevel.PRIVATE )
    private final Map<Plugin, Map<Class<? extends PlayerService>, Class<? extends PlayerService>>> servicesMap;
    
    @Getter
    @Setter( AccessLevel.PRIVATE )
    private Map<Class<? extends PlayerService>, Class<? extends PlayerService>> services;
    
    PlayerServiceRegistryImpl()
    {
        this.servicesMap = new HashMap<>();
        this.services = new HashMap<>();
    }
    
    @Override
    public void registerService( final Class<? extends PlayerService> intf, Class<? extends PlayerService> impl, Plugin plugin )
    {
        Preconditions.checkArgument( intf.isAssignableFrom( impl ), "%s does not implement %s", impl.getCanonicalName(), intf.getCanonicalName() );
        
        if( !this.getServicesMap().containsKey( plugin ) )
        {
            this.getServicesMap().put( plugin, new LinkedHashMap<Class<? extends PlayerService>, Class<? extends PlayerService>>() );
        }
        
        this.getServicesMap().get( plugin ).put( intf, impl );
        
        for( GridPlayer player : GridPlugin.getInstance().getPlayerManager().getAllOnline() )
        {
            try
            {
                PlayerService service = impl.getConstructor( GridPlayer.class ).newInstance( player );
                ( (GridPlayerImpl) player ).getServices().put( intf, service );
                // TODO: Run this asynchronously, then run the onLogin after it.
                service.onAsyncPreLogin();
                service.onLogin();
                service.onJoin();
            }
            catch( NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex )
            {
                throw new IllegalArgumentException( "A PlayerService must have a constructor that accepts only a GridPlayer object.", ex );
            }
        }
        
        this.refreshServices();
        
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                for( GridPlayer player : GridPlugin.getInstance().getPlayerManager().getAllOnline() )
                {
                    player.getService( intf ).onAsyncPostLogin();
                }
            }
        }.runTaskAsynchronously( GridPlugin.getInstance() );
    }
    
    @Override
    public void registerService( Class<? extends PlayerService> impl, Plugin plugin )
    {
        this.registerService( impl, impl, plugin );
    }
    
    @Override
    public void registerService( Class<? extends PlayerService> intf, Class<? extends PlayerService> impl )
    {
        this.registerService( intf, impl, GridPlugin.getInstance() );
    }
    
    @Override
    public void registerService( Class<? extends PlayerService> impl )
    {
        this.registerService( impl, impl );
    }
    
    @Override
    public void unregisterService( Class<? extends PlayerService> intf, Plugin plugin )
    {
        Preconditions.checkArgument( this.getServicesMap().containsKey( plugin ), "The specified plugin doesn't have any registered player services." );
        Preconditions.checkArgument( this.getServicesMap().get( plugin ).containsKey( intf ), "The specified player service isn't registered." );
        
        for( GridPlayer player : GridPlugin.getInstance().getPlayerManager().getAllOnline() )
        {
            GridPlayerImpl playerImpl = (GridPlayerImpl) player;
            if( !playerImpl.getServices().containsKey( intf ) )
            {
                continue;
            }
            playerImpl.getServices().get( intf ).onLogout();
            playerImpl.getServices().remove( intf );
        }
        
        this.getServicesMap().get( plugin ).remove( intf );
        
        this.refreshServices();
    }
    
    @Override
    public void unregisterService( Class<? extends PlayerService> intf )
    {
        this.unregisterService( intf, GridPlugin.getInstance() );
    }
    
    @Override
    public void unregisterServices( Plugin plugin )
    {
        if( !this.getServicesMap().containsKey( plugin ) )
        {
            return;
        }
        
        for( Entry<Class<? extends PlayerService>, Class<? extends PlayerService>> service : this.getServicesMap().get( plugin ).entrySet() )
        {
            for( GridPlayer player : GridPlugin.getInstance().getPlayerManager().getAllOnline() )
            {
                GridPlayerImpl playerImpl = (GridPlayerImpl) player;
                if( !playerImpl.getServices().containsKey( service.getKey() ) )
                {
                    continue;
                }
                playerImpl.getServices().get( service.getKey() ).onLogout();
                playerImpl.getServices().remove( service.getKey() );
            }
        }
        
        this.getServicesMap().remove( plugin );
        
        this.refreshServices();
    }
    
    /**
     * Refreshes the Map stored for getServices().
     */
    private void refreshServices()
    {
        Map<Class<? extends PlayerService>, Class<? extends PlayerService>> services = new HashMap<>();
        
        for( Map<Class<? extends PlayerService>, Class<? extends PlayerService>> pluginServices : this.getServicesMap().values() )
        {
            services.putAll( pluginServices );
        }
        
        this.setServices( Collections.unmodifiableMap( services ) );
    }
}
