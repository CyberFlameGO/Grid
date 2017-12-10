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

package com.pickaxis.grid.core.player;

import lombok.Getter;

/**
 * Base player service, allowing for optional implementation
 * of interface methods.
 */
public abstract class AbstractPlayerService implements PlayerService
{
    @Getter
    private final GridPlayer player;
    
    public AbstractPlayerService( final GridPlayer player )
    {
        this.player = player;
    }
    
    @Override
    public void onAsyncPreLogin()
    {
        // Empty method to allow optional implementation in actual services.
    }
    
    @Override
    public void onLogin()
    {
        // Empty method to allow optional implementation in actual services.
    }
    
    @Override
    public void onAsyncPostLogin()
    {
        // Empty method to allow optional implementation in actual services.
    }
    
    @Override
    public void onJoin()
    {
        // Empty method to allow optional implementation in actual services.
    }
    
    @Override
    public void onRefresh()
    {
        // Empty method to allow optional implementation in actual services.
    }
    
    @Override
    public void onLogout()
    {
        // Empty method to allow optional implementation in actual services.
    }
}
