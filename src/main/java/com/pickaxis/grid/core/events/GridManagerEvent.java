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

package com.pickaxis.grid.core.events;

import com.pickaxis.grid.core.GridManager;
import lombok.Getter;
import org.bukkit.event.HandlerList;

/**
 * Fired when the state of a GridManager changes.
 * 
 * @param <T> The GridManager that was initialized.
 */
public abstract class GridManagerEvent<T extends GridManager> extends GridEvent
{
    @Getter
    private static final HandlerList handlerList = new HandlerList();
    
    @Getter
    protected final T manager;
    
    public GridManagerEvent( T manager )
    {
        this.manager = manager;
    }
    
    /**
     * Gets the class of the manager that was initialized.
     *
     * @return The class of the initialized manager.
     */
    public Class<? extends GridManager> getManagerClass()
    {
        return this.getManager().getClass();
    }
    
    @Override
    public HandlerList getHandlers()
    {
        return GridManagerInitializedEvent.getHandlerList();
    }
}
