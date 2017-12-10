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

package com.pickaxis.grid.core.mq.messages;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

/**
 * Broadcast a message to certain or all servers in the network.
 */
@Getter
@Setter
public class ServerBroadcastMessage extends GridServerMessage
{
    private String message;
    
    private String permission;
    
    @Override
    public void execute()
    {
        if( this.getPermission() instanceof String && this.getPermission().length() > 0 )
        {
            Bukkit.broadcast( this.getMessage(), this.getPermission() );
        }
        else
        {
            Bukkit.broadcastMessage( this.getMessage() );
        }
    }
}
