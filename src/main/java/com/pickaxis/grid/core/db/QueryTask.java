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

import com.pickaxis.grid.core.GridPlugin;
import java.util.logging.Level;
import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;
import org.jooq.Query;
import org.jooq.exception.DataAccessException;

/**
 * General async database query executor task.
 */
public class QueryTask extends BukkitRunnable
{
    @Getter
    private final Query query;
    
    public QueryTask( final Query query )
    {
        this.query = query;
    }
    
    @Override
    public void run()
    {
        try
        {
            this.getQuery().execute();
        }
        catch( DataAccessException ex )
        {
            GridPlugin.getInstance().getLogger().log( Level.WARNING, "Exception occurred while running query: " + this.getQuery().getSQL(), ex );
        }
    }
}
