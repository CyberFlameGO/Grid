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

package com.pickaxis.grid.chat;

import com.pickaxis.grid.chat.commands.ChannelCommands;
import com.pickaxis.grid.chat.commands.ChatCommands;
import com.pickaxis.grid.chat.targets.ChatChannel;
import com.pickaxis.grid.chat.targets.ChatChannelImpl;
import com.pickaxis.grid.chat.targets.MetaChatChannel;
import com.pickaxis.grid.chat.targets.MetaChatChannelImpl;
import com.pickaxis.grid.chat.targets.types.ChannelTypeRegistry;
import com.pickaxis.grid.chat.targets.types.ChannelTypeRegistryImpl;
import com.pickaxis.grid.core.GridPlugin;
import com.pickaxis.grid.core.command.CommandManager;
import com.pickaxis.grid.core.data.tables.ChatChannelParticipants;
import com.pickaxis.grid.core.data.tables.ChatChannels;
import com.pickaxis.grid.core.data.tables.ChatFlair;
import com.pickaxis.grid.core.data.tables.records.ChatChannelsRecord;
import com.pickaxis.grid.core.data.tables.records.ChatFlairRecord;
import com.pickaxis.grid.core.exceptions.NoSearchResultsException;
import com.pickaxis.grid.core.mq.MQConstants;
import com.pickaxis.grid.core.player.GridPlayer;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import lombok.AccessLevel;
import lombok.Getter;
import net.jodah.expiringmap.ExpiringMap;
import net.jodah.expiringmap.ExpiringMap.ExpirationPolicy;
import org.bukkit.scheduler.BukkitRunnable;
import org.jooq.Field;
import org.jooq.Result;
import org.jooq.exception.DataAccessException;
import org.jooq.types.UInteger;
import org.jooq.types.UNumber;
import org.jooq.types.UShort;

/**
 * Manages chat... but mainly the channels.
 */
@Getter( AccessLevel.PRIVATE )
public class ChatManagerImpl implements ChatManager
{
    @Getter
    private final ChannelTypeRegistry channelTypeRegistry;
    
    private final Map<UInteger, ChatChannel> idMap;
    
    private final Map<String, UInteger> slugMap;
    
    private final Map<String, UInteger> shortcutMap;
    
    private final Map<String, MetaChatChannel> metaMap;
    
    private final Collection<ChatChannel> autojoinChannels;
    
    private final Collection<String> boundChannels;
    
    private final Result<ChatFlairRecord> flair;
    
    public ChatManagerImpl()
    {
        this.channelTypeRegistry = new ChannelTypeRegistryImpl();
        
        // TODO: Optimize these better, possibly using Google Commons Cache objects.
        this.idMap = new HashMap<>();
        // Removed
        this.metaMap = new HashMap<>();
        this.autojoinChannels = new HashSet<>();
        this.boundChannels = Collections.newSetFromMap( new ConcurrentHashMap<String, Boolean>( GridPlugin.getInstance().getConfig().getInt( "chat.internals.boundchannels.init-capacity", 64 ),
                                                                                                0.75f,
                                                                                                GridPlugin.getInstance().getConfig().getInt( "chat.internals.boundchannels.concurrency-level", 6 ) ) );
        
        this.flair = GridPlugin.getInstance().getDb().getContext().selectFrom( ChatFlair.CHAT_FLAIR )
                                                                  .where( ChatFlair.CHAT_FLAIR.TYPE.in( ChatFlairType.STAFF.getDbCode(),
                                                                                                        ChatFlairType.STORE.getDbCode() ) )
                                                                  .orderBy( ChatFlair.CHAT_FLAIR.PRIORITY.desc() )
                                                                  .fetch();
        
        this.refreshChannels();
        
        this.clearLocalParticipants();
        
        // PlayerManager gets registered before ChatManager.
        GridPlugin.getInstance().getPlayerManager().getServiceRegistry().registerService( ChatPlayerService.class, ChatPlayerServiceImpl.class );
        
        // CommandManager gets registered before ChatManager.
        GridPlugin.getInstance().getManager( CommandManager.class ).register( new ChannelCommands(),
                                                                              new ChatCommands() );
        
        GridPlugin.getInstance().getServer().getPluginManager().registerEvents( new ChatListener(), GridPlugin.getInstance() );
        
        // Removed
    }
    
    @Override
    public Collection<ChatChannel> getAutojoins()
    {
        return Collections.unmodifiableCollection( this.getAutojoinChannels() );
    }
    
    @Override
    public List<ChatFlairRecord> getPermissionFlair()
    {
        return Collections.unmodifiableList( this.getFlair() );
    }
    
    @Override
    public ChatChannel getChannel( UInteger id )
    {
        if( !this.getIdMap().containsKey( id ) )
        {
            ChatChannelImpl channel = new ChatChannelImpl( id, this.getChannelTypeRegistry() );
            
            this.getIdMap().put( id, channel );
            
            if( channel.getDbRow().getSlug() instanceof String && !this.getSlugMap().containsKey( channel.getDbRow().getSlug() ) )
            {
                this.getSlugMap().put( channel.getDbRow().getSlug(), id );
            }
            
            if( channel.getDbRow().getShortcut() instanceof String && !this.getShortcutMap().containsKey( channel.getDbRow().getShortcut() ) )
            {
                this.getShortcutMap().put( channel.getDbRow().getShortcut(), id );
            }
        }
        
        return this.getIdMap().get( id );
    }
    
    @Override
    public ChatChannel getChannelBySlug( String slug )
    {
        if( !this.getSlugMap().containsKey( slug ) )
        {
            this.loadChannel( ChatChannels.CHAT_CHANNELS.SLUG, slug );
        }
        
        return this.getChannel( this.getSlugMap().get( slug ) );
    }
    
    @Override
    public ChatChannel getChannelByShortcut( String shortcut )
    {
        if( !this.getShortcutMap().containsKey( shortcut) )
        {
            this.loadChannel( ChatChannels.CHAT_CHANNELS.SHORTCUT, shortcut );
        }
        
        return this.getChannel( this.getShortcutMap().get( shortcut ) );
    }
    
    @Override
    public ChatChannel getChannel( String search )
    {
        if( search.startsWith( "#" ) )
        {
            return this.getChannel( UInteger.valueOf( search.substring( 1 ) ) );
        }
        
        if( this.getShortcutMap().containsKey( search ) )
        {
            return this.getChannel( this.getShortcutMap().get( search ) );
        }
        
        if( this.getSlugMap().containsKey( search ) )
        {
            return this.getChannel( this.getSlugMap().get( search ) );
        }
        
        return null;
    }
    
    /**
     * Searches for a channel and adds it to the channel maps if found.
     * 
     * @param field The field to use in the search.
     * @param search The search string.
     */
    private void loadChannel( Field field, String search )
    {
        ChatChannelsRecord dbRow = GridPlugin.getInstance().getDb().getContext().selectFrom( ChatChannels.CHAT_CHANNELS )
                                                                                .where( field.eq( search.toLowerCase() ) )
                                                                                .fetchOne();
        
        if( !( dbRow instanceof ChatChannelsRecord ) )
        {
            throw new NoSearchResultsException( search, ChatChannels.CHAT_CHANNELS );
        }
        
        this.addChannelToMaps( new ChatChannelImpl( dbRow, this.getChannelTypeRegistry() ) );
    }
    
    /**
     * Adds the channel to the ID, slug, and shortcut maps.
     * 
     * @param channel The channel to add.
     */
    private void addChannelToMaps( ChatChannelImpl channel )
    {
        this.getIdMap().put( channel.getId(), channel );
        
        if( channel.getDbRow().getSlug() instanceof String )
        {
            this.getSlugMap().put( channel.getDbRow().getSlug(), channel.getId() );
        }

        if( channel.getDbRow().getShortcut() instanceof String )
        {
            this.getShortcutMap().put( channel.getDbRow().getShortcut(), channel.getId() );
        }
    }
    
    @Override
    public final void refreshChannels()
    {
        Result<ChatChannelsRecord> query = GridPlugin.getInstance().getDb().getContext().selectFrom( ChatChannels.CHAT_CHANNELS )
                                                                                        .where( ChatChannels.CHAT_CHANNELS.KEEP_LOADED.eq( (byte) 1 ) )
                                                                                        .and( ChatChannels.CHAT_CHANNELS.SERVER_ID.in( GridPlugin.getInstance().getSdm().getApplicableServerIds() ) )
                                                                                        .fetch();
        
        for( ChatChannelsRecord dbRow : query )
        {
            this.addChannelToMaps( new ChatChannelImpl( dbRow, this.getChannelTypeRegistry() ) );
            
            if( dbRow.getAutojoin() == (byte) 1 )
            {
                this.getAutojoinChannels().add( this.getChannel( dbRow.getId() ) );
            }
        }
    }
    
    @Override
    public MetaChatChannel getChannel( UInteger id, UInteger meta, UShort metaSecondary )
    {
        String key = id.toString() + "." + meta.toString();
        if( metaSecondary instanceof UNumber )
        {
            key += "." + metaSecondary.toString();
        }
        
        if( !this.getMetaMap().containsKey( key ) )
        {
            this.getMetaMap().put( key, new MetaChatChannelImpl( this.getChannel( id ), meta, metaSecondary ) );
        }
        
        return this.getMetaMap().get( key );
    }
    
    @Override
    public void bindChannel( String routingKey )
    {
        if( this.getBoundChannels().contains( routingKey ) )
        {
            return;
        }
        
        try
        {
            GridPlugin.getInstance().getMq().getChannel( MQConstants.CHANNEL_SERVER ).queueBind( MQConstants.QUEUE_SERVER, MQConstants.EXCHANGE_CHAT_CHANNELS, routingKey );
            this.getBoundChannels().add( routingKey );
        }
        catch( IOException ex )
        {
            GridPlugin.getInstance().getLogger().log( Level.WARNING, "Failed to bind to chat channel with routing key: " + routingKey, ex );
        }
    }
    
    /**
     * Unbinds a channel from this server's queue.
     * 
     * @param routingKey The channel's routing key
     */
    private void unbindChannel( String routingKey )
    {
        if( !this.getBoundChannels().contains( routingKey ) )
        {
            return;
        }
        
        try
        {
            GridPlugin.getInstance().getMq().getChannel( MQConstants.CHANNEL_SERVER ).queueUnbind( MQConstants.QUEUE_SERVER, MQConstants.EXCHANGE_CHAT_CHANNELS, routingKey );
            this.getBoundChannels().remove( routingKey );
        }
        catch( IOException ex )
        {
            GridPlugin.getInstance().getLogger().log( Level.WARNING, "Failed to unbind from chat channel with routing key: " + routingKey, ex );
        }
    }
    
    @Override
    public void cleanupBoundChannels()
    {
        for( String routingKey : this.getBoundChannels() )
        {
            mainloop:
            {
                for( GridPlayer gp : GridPlugin.getInstance().getPlayerManager().getAllOnline() )
                {
                    for( ChatChannel ch : gp.getService( ChatPlayerService.class ).getChannels() )
                    {
                        if( ch.getRoutingKey().equals( routingKey ) )
                        {
                            break mainloop;
                        }
                    }
                }
                this.unbindChannel( routingKey );
            }
        }
    }
    
    /**
     * Removes all participants on this server from the database
     * table.  Used when starting up or shutting down to ensure
     * that ghosts aren't left in channels.
     */
    private void clearLocalParticipants()
    {
        try
        {
            GridPlugin.getInstance().getDb().getContext().delete( ChatChannelParticipants.CHAT_CHANNEL_PARTICIPANTS )
                                                         .where( ChatChannelParticipants.CHAT_CHANNEL_PARTICIPANTS.SERVER_ID.eq( GridPlugin.getInstance().getSdm().getLocalServer().getId() ) )
                                                         .execute();
        }
        catch( DataAccessException ex )
        {
            GridPlugin.getInstance().getLogger().log( Level.SEVERE, "Exception while clearing local chat participants: ", ex );
        }
    }
    
    @Override
    public void shutdown()
    {
        GridPlugin.getInstance().getPlayerManager().getServiceRegistry().unregisterService( ChatPlayerService.class );
        
        this.clearLocalParticipants();
    }
}
