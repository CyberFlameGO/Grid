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

package com.pickaxis.grid.chat.targets.populators;

import com.pickaxis.grid.chat.ChatPlayerService;
import com.pickaxis.grid.chat.targets.ChatChannel;

/**
 * Populates MetaChatChannels with the appropriate meta IDs.
 */
public interface MetaPopulator
{
    /**
     * Obtains a MetaChatChannel for a ChatChannel with bindings
     * for a specific player on the local server.
     * 
     * @param channel The parent channel
     * @param player The player that will be using the channel
     * @return The requested MetaChatChannel
     */
    ChatChannel populate( ChatChannel channel, ChatPlayerService player );
    
    /**
     * Gets a slug for a channel, using its meta ID(s).
     * 
     * @param channel The channel to get the slug for
     * @return The channel's slug
     */
    String getSlug( ChatChannel channel );
}
