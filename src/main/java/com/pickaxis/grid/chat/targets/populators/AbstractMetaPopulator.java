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

import com.pickaxis.grid.chat.ChatManager;
import com.pickaxis.grid.core.GridPlugin;

/**
 * Abstract MetaPopulator that provides a shortcut to
 * get the ChatManager.
 */
public abstract class AbstractMetaPopulator implements MetaPopulator
{
    protected ChatManager getChatManager()
    {
        return GridPlugin.getInstance().getManager( ChatManager.class );
    }
}
