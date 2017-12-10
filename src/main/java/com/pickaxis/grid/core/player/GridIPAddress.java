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

import com.pickaxis.grid.core.data.tables.records.IpAddressesRecord;
import com.pickaxis.grid.core.db.Fetchable;
import java.net.InetAddress;
import org.jooq.Result;
import org.jooq.types.ULong;

/**
 * Grid IP address wrapper.
 */
public interface GridIPAddress extends Fetchable
{
    /**
     * Gets the associated InetAddress.
     * 
     * @return The associated InetAddress.
     */
    InetAddress getAddress();
    
    /**
     * Gets the IP version (4 or 6).
     * 
     * @return The IP version.
     */
    int getIpVersion();
    
    // Removed
    
    /**
     * Get or queue a fetch of the player's database row.
     *
     * @return The player's database row.
     */
    IpAddressesRecord getDbRow();
    
    /**
     * Shortcut for this.getDbRow().getId();
     *
     * @return The IP's database ID.
     */
    ULong getId();
}
