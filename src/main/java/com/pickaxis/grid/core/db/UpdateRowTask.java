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
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;
import org.jooq.UpdatableRecord;
import org.jooq.exception.DataAccessException;

/**
 * Asynchronously insert/update a database row.
 */
public class UpdateRowTask extends BukkitRunnable
{
    @Getter( AccessLevel.PRIVATE )
    private final UpdatableRecord dbRow;
    
    public UpdateRowTask( UpdatableRecord dbRow )
    {
        this.dbRow = dbRow;
    }
    
    @Override
    public void run()
    {
        try
        {
            this.getDbRow().store();
        }
        catch( DataAccessException ex )
        {
            GridPlugin.getInstance().getLogger().log( Level.WARNING, "Exception occurred while updating database row: " + this.getDbRow().toString(), ex );
        }
    }
}
