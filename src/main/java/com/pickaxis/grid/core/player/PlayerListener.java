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

import com.pickaxis.grid.core.GridPlugin;
import com.pickaxis.grid.core.events.GridInitializedEvent;
import com.pickaxis.grid.core.util.LangUtil;
import java.util.logging.Level;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jooq.types.UInteger;

/**
 * Player event listener.
 */
public class PlayerListener implements Listener
{
    @EventHandler( priority = EventPriority.HIGHEST )
    public void onAsyncPlayerPreLoginEvent( AsyncPlayerPreLoginEvent event )
    {
        if( !GridPlugin.getInstance().isGridInitialized() )
        {
            event.disallow( AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, LangUtil.getString( "lang.action.initwait", "This server hasn't finished initializing yet.  Try again in a minute." ) );
            return;
        }
        
        GridPlayer gridPlayer = GridPlugin.getInstance().getPlayerManager().getPlayer( event.getUniqueId() );
        gridPlayer.setName( event.getName() );
        
        GridIPAddress gridIp = new GridIPAddressImpl( event.getAddress() );
        gridIp.fetchDbRow();
        gridPlayer.setIp( gridIp );
        
        // TODO: Check permission-based whitelist access here.  Can we even check permissions this early?
        
        // Removed
    }
    
    @EventHandler( priority = EventPriority.MONITOR )
    public void onPlayerLoginEvent( PlayerLoginEvent event )
    {
        if( !GridPlugin.getInstance().isGridInitialized() )
        {
            event.disallow( PlayerLoginEvent.Result.KICK_WHITELIST, LangUtil.getString( "lang.action.initwait", "This server hasn't finished initializing yet.  Try again in a minute." ) );
            return;
        }
        
        if( event.getResult() == PlayerLoginEvent.Result.ALLOWED )
        {
            GridPlugin.getInstance().getPlayerManager().finishLogin( event.getPlayer() );
        }
    }
    
    @EventHandler( priority = EventPriority.MONITOR )
    public void onPlayerJoinEvent( PlayerJoinEvent event )
    {
        ( (GridPlayerImpl) GridPlugin.getInstance().getPlayerManager().getPlayer( event.getPlayer() ) ).finishJoin();
    }
    
    @EventHandler( priority = EventPriority.MONITOR )
    public void onPlayerDisconnectEvent( PlayerQuitEvent event )
    {
        GridPlugin.getInstance().getPlayerManager().logout( event.getPlayer() );
    }
    
    @EventHandler
    public void onGridInitialized( GridInitializedEvent event )
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                GridPlugin.getInstance().getPlayerManager().getServiceRegistry().registerService( DataPlayerService.class, DataPlayerServiceImpl.class );
                GridPlugin.getInstance().getPlayerManager().loginAll();
            }
        }.runTaskLaterAsynchronously( GridPlugin.getInstance(), 1 );
    }
}
