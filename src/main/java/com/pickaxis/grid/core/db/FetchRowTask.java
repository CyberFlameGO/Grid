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

package com.pickaxis.grid.core.db;

import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Fetch a row from the database.
 */
public class FetchRowTask extends BukkitRunnable
{
    @Getter( AccessLevel.PRIVATE )
    private final Fetchable fetchable;
    
    public FetchRowTask( Fetchable fetchable )
    {
        this.fetchable = fetchable;
    }
    
    @Override
    public void run()
    {
        this.getFetchable().fetchDbRow();
    }
}
