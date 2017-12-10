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

import com.pickaxis.grid.chat.targets.populators.MetaPopulator;
import org.bukkit.plugin.Plugin;
import org.jooq.Field;
import org.jooq.types.UByte;
import org.jooq.types.UNumber;

/**
 * Represents a type of channel available in Grid Chat.
 */
public interface ChannelType
{
    /**
     * The name of the type, for internal reference only.
     * 
     * @return The name of the type
     */
    String getName();
    
    /**
     * The database ID of the type.
     * 
     * @return The ID of the type
     */
    UByte getId();
    
    /**
     * The MetaPopulator for the type.
     * 
     * @return The MetaPopulator for the type
     */
    MetaPopulator getMetaPopulator();
    
    /**
     * The primary meta field of the type, for internal reference
     * only.  A UNumber type up to UInteger.
     * 
     * @return The field used for the primary meta ID of the type
     */
    Field<UNumber> getMetaPrimary();
    
    /**
     * The secondary meta field of the type, for internal reference
     * only.  A UNumber type up to UShort.
     * 
     * @return The field used for the secondary meta ID of the type
     */
    Field<UNumber> getMetaSecondary();
    
    /**
     * Whether the type makes use of a primary meta field.
     * 
     * @return Whether the type makes use of a primary meta field
     */
    boolean hasMeta();
    
    /**
     * Whether the field makes use of a secondary meta field.
     * 
     * @return Whether the field makes use of a secondary meta field
     */
    boolean hasMetaSecondary();
    
    /**
     * The plugin that registered the ChannelType.
     * 
     * @return The related plugin
     */
    Plugin getPlugin();
}
