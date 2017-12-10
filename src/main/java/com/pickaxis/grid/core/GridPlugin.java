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

import com.pickaxis.grid.chat.ChatManager;
import com.pickaxis.grid.chat.ChatManagerImpl;
import com.pickaxis.grid.coins.CoinPackageManager;
import com.pickaxis.grid.coins.CoinPackageManagerImpl;
import com.pickaxis.grid.core.command.CommandManager;
import com.pickaxis.grid.core.command.CommandManagerImpl;
import com.pickaxis.grid.core.db.DBManager;
import com.pickaxis.grid.core.db.DBManagerImpl;
import com.pickaxis.grid.core.db.redis.RedisManager;
import com.pickaxis.grid.core.db.redis.RedisManagerImpl;
import com.pickaxis.grid.core.events.GridInitializedEvent;
import com.pickaxis.grid.core.listeners.ListenerManager;
import com.pickaxis.grid.core.logging.LoggerManager;
import com.pickaxis.grid.core.mq.MQManager;
import com.pickaxis.grid.core.mq.MQManagerImpl;
import com.pickaxis.grid.core.player.PlayerManager;
import com.pickaxis.grid.core.player.PlayerManagerImpl;
import com.pickaxis.grid.core.rewards.RewardManager;
import com.pickaxis.grid.core.rewards.RewardManagerImpl;
import com.pickaxis.grid.core.server.ServerDataManager;
import com.pickaxis.grid.core.server.ServerDataManagerImpl;
import com.pickaxis.grid.permissions.PermissionManager;
import com.pickaxis.grid.permissions.PermissionManagerImpl;
import com.pickaxis.grid.tokens.FlightManager;
import com.pickaxis.grid.tokens.TokenRewardManager;
import com.pickaxis.grid.tokens.TokenRewardManagerImpl;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Main Grid class.
 */
@Getter
@Setter( AccessLevel.PRIVATE )
public class GridPlugin extends JavaPlugin
{
    @Getter
    @Setter( AccessLevel.PRIVATE )
    private static GridPlugin instance;
    
    private boolean debug;
    
    private Properties buildInfo;
    
    private ManagerCoordinator managerCoordinator;
    
    private boolean gridInitialized;
    
    /**
     * Initialization tasks to run when plugin is enabled.
     */
    @Override
    public void onEnable()
    {
        // Set singleton instance.
        GridPlugin.setInstance( this );
        
        // Set debug variable from config.
        this.setDebug( this.getConfig().getBoolean( "debug", false ) );
        
        // Populate build info.
        this.setBuildInfo( new Properties() );
        try
        {
            this.getBuildInfo().load( this.getClass().getClassLoader().getResourceAsStream( "git.properties" ) );
        }
        catch( IOException | NullPointerException ex )
        {
            this.getLogger().log( Level.WARNING, "Couldn't load build info.", ex );
        }
        
        // Create ManagerCoordinator.
        this.setManagerCoordinator( new ManagerCoordinatorImpl() );
        
        // Register managers.
        this.getManagerCoordinator().registerManager( 2000, RewardManager.class, RewardManagerImpl.class, this );
        this.getManagerCoordinator().registerManager( 4000, ListenerManager.class, this );
        this.getManagerCoordinator().registerManager( 32000, DBManager.class, DBManagerImpl.class, this );
        this.getManagerCoordinator().registerManager( 33000, ServerDataManager.class, ServerDataManagerImpl.class, this );
        this.getManagerCoordinator().registerManager( 33500, LoggerManager.class, this );
        this.getManagerCoordinator().registerManager( 33750, RedisManager.class, RedisManagerImpl.class, this );
        this.getManagerCoordinator().registerManager( 34000, MQManager.class, MQManagerImpl.class, this );
        this.getManagerCoordinator().registerManager( 35000, PlayerManager.class, PlayerManagerImpl.class, this );
        this.getManagerCoordinator().registerManager( 36500, PermissionManager.class, PermissionManagerImpl.class, this );
        this.getManagerCoordinator().registerManager( 37000, TokenRewardManager.class, TokenRewardManagerImpl.class, this );
        this.getManagerCoordinator().registerManager( 38000, FlightManager.class, this );
        this.getManagerCoordinator().registerManager( 39000, CoinPackageManager.class, CoinPackageManagerImpl.class, this );
        this.getManagerCoordinator().registerManager( 40000, CommandManager.class, CommandManagerImpl.class, this );
        this.getManagerCoordinator().registerManager( 41000, ChatManager.class, ChatManagerImpl.class, this );
        
        // Initialize managers.
        this.getManagerCoordinator().initializeManagers( false, this );
        
        // Removed
    }
    
    /**
     * Shutdown tasks to run when the plugin is disabled.
     */
    @Override
    public void onDisable()
    {
        // Shutdown managers.
        this.getManagerCoordinator().shutdownManagers( this );
        
        // Unset singleton instance.
        GridPlugin.setInstance( null );
    }
    
    public <T extends GridManager> T getManager( Class<T> cls )
    {
        return this.getManagerCoordinator().get( cls );
    }
    
    public DBManager getDb()
    {
        return this.getManager( DBManager.class );
    }
    
    public MQManager getMq()
    {
        return this.getManager( MQManager.class );
    }
    
    public ServerDataManager getSdm()
    {
        return this.getManager( ServerDataManager.class );
    }
    
    public PlayerManager getPlayerManager()
    {
        return this.getManager( PlayerManager.class );
    }
    
    /**
     * Log a debug message if debugging is enabled.
     * 
     * @param message The message to log.
     */
    public void debug( String message )
    {
        if( this.isDebug() )
        {
            this.getLogger().log( Level.INFO, message );
        }
    }
}
