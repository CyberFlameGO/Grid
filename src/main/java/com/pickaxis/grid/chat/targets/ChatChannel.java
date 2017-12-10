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

package com.pickaxis.grid.chat.targets;

import com.pickaxis.grid.chat.targets.types.ChannelType;
import com.pickaxis.grid.core.data.tables.records.ChatChannelsRecord;
import java.util.Collection;
import java.util.Map;
import net.md_5.bungee.api.chat.BaseComponent;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.types.UInteger;

/**
 * A ChatTarget with participant lists.
 */
public interface ChatChannel extends ChatTarget
{
    /**
     * Gets the MQ routing key for this channel.
     * 
     * @return The MQ routing key for the channel.
     */
    String getRoutingKey();
    
    /**
     * Gets the channel type.
     * 
     * @return The channel type
     */
    ChannelType getType();
    
    /**
     * Gets the database row for this channel.
     * 
     * @return The channel's database row
     */
    ChatChannelsRecord getDbRow();
    
    /**
     * Gets the database ID for this channel.
     * 
     * @return The channel's ID
     */
    UInteger getId();
    
    /**
     * Gets channel_id, meta_id, and meta_secondary_id fields for
     * a database query on the specified table.
     * 
     * @param table The table being queried
     * @return A map of the channel-specific fields
     */
    Map<? extends Field<?>, ?> getFields( Table table );
    
    /**
     * Gets the conditions for a database select statement on
     * the specified table.
     * 
     * @param table The table being queried
     * @return The conditions for this channel
     */
    Condition getConditions( Table table );
    
    /**
     * Refreshes the channel's participant lists.  Should always
     * be run asynchronously.
     */
    void refreshParticipants();
    
    /**
     * Gets whether it's time to update the participant list from the database.
     * 
     * @return Whether the participant list should be refreshed.
     */
    boolean readyRefreshParticipants();
    
    /**
     * Gets the (in)visible participants list.  Should always
     * be run asynchronously.
     * 
     * @param visible Will return the visible participants list if true, or invisible participants list if false.
     * @return The selected participants list.
     */
    Collection<String> getParticipants( boolean visible );
    
    /**
     * Gets the visible participants list.
     * 
     * @return The visible participants list.
     */
    Collection<String> getParticipants();
    
    /**
     * Gets the channel's BaseComponent prefix.
     * 
     * @return The channel's prefix.
     */
    BaseComponent getTag();
}
