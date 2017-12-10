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
import com.pickaxis.grid.chat.targets.MetaChatChannel;
import com.pickaxis.grid.core.GridPlugin;
import org.jooq.types.UInteger;
import org.jooq.types.UShort;

/**
 * Gets the MetaChatChannel for a ChannelType.SERVER.
 */
public class ServerMetaPopulator extends AbstractMetaPopulator
{
    @Override
    public ChatChannel populate( ChatChannel channel, ChatPlayerService player )
    {
        return this.getChatManager().getChannel( channel.getId(), UInteger.valueOf( GridPlugin.getInstance().getSdm().getLocalServer().getId().intValue() ), null );
    }
    
    @Override
    public String getSlug( ChatChannel channel )
    {
        return channel.getDbRow().getSlug() + "-" + GridPlugin.getInstance().getSdm().getServer( UShort.valueOf( ( (MetaChatChannel) channel ).getMeta().intValue() ) ).getDbRow().getSlug();
    }
}
