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
 * Always returns the original channel.
 */
public class DefaultMetaPopulator implements MetaPopulator
{
    @Override
    public ChatChannel populate( ChatChannel channel, ChatPlayerService player )
    {
        return channel;
    }
    
    @Override
    public String getSlug( ChatChannel channel )
    {
        return channel.getDbRow().getSlug();
    }
}
