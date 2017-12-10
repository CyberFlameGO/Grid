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

package com.pickaxis.grid.chat.targets.types;

import org.jooq.types.UByte;

/**
 * Handles registration of ChannelTypes.
 */
public interface ChannelTypeRegistry
{
    /**
     * Gets a channel type by its database ID.
     * 
     * @param id The type's database ID
     * @return The type with the specified database ID
     */
    ChannelType get( UByte id );
    
    /**
     * Registers a ChannelType.
     * 
     * @param type The ChannelType to register
     */
    void registerType( ChannelType type );
}
