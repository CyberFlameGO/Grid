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

import com.pickaxis.grid.chat.targets.ChatChannel;
import com.pickaxis.grid.chat.targets.MetaChatChannel;
import com.pickaxis.grid.chat.targets.types.ChannelTypeRegistry;
import com.pickaxis.grid.core.GridManager;
import com.pickaxis.grid.core.data.tables.records.ChatFlairRecord;
import java.util.Collection;
import java.util.List;
import org.jooq.types.UInteger;
import org.jooq.types.UShort;

/**
 * Manages chat... but mainly the channels.
 */
public interface ChatManager extends GridManager
{
    /**
     * Gets an unmodifiable collection of all autojoin channels.
     *
     * @return All autojoin channels.
     */
    Collection<ChatChannel> getAutojoins();
    
    /**
     * Gets an unmodifiable list of all permission-based chat flair.
     * 
     * @return All permission-based chat flair.
     */
    List<ChatFlairRecord> getPermissionFlair();
    
    /**
     * Gets the ChannelTypeRegistry.
     * 
     * @return The ChannelTypeRegistry
     */
    ChannelTypeRegistry getChannelTypeRegistry();
    
    /**
     * Gets a channel by its ID.  Should be run asynchronously.
     *
     * @param id The channel's ID
     * @return The requested ChatChannelImpl
     */
    ChatChannel getChannel( UInteger id );
    
    /**
     * Gets a channel by its ID prefixed with #, shortcut, or slug.
     * Should be run asynchronously.
     *
     * @param search The search string
     * @return The requested ChatChannelImpl
     */
    ChatChannel getChannel( String search );
    
    /**
     * Gets a chat channel with meta.
     *
     * @param id The parent chat channel ID
     * @param meta The primary meta ID
     * @param metaSecondary The secondary meta ID, or null
     * @return The requested MetaChatChannelImpl
     */
    MetaChatChannel getChannel( UInteger id, UInteger meta, UShort metaSecondary );
    
    /**
     * Gets a channel by its shortcut.  Should be run asynchronously.
     *
     * @param shortcut The channel's shortcut
     * @return The requested ChatChannelImpl
     */
    ChatChannel getChannelByShortcut( String shortcut );
    
    /**
     * Gets a channel by its slug.  Should be run asynchronously.
     *
     * @param slug The channel's slug
     * @return The requested ChatChannelImpl
     */
    ChatChannel getChannelBySlug( String slug );
    
    /**
     * Loads all persistent channels.
     */
    void refreshChannels();
    
    /**
     * Binds a channel to this server's queue.
     * 
     * @param routingKey The channel's routing key
     */
    void bindChannel( String routingKey );
    
    /**
     * Unbinds channels no longer have players in them on this server.
     */
    void cleanupBoundChannels();
}
