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

import com.pickaxis.grid.chat.targets.populators.DefaultMetaPopulator;
import com.pickaxis.grid.chat.targets.populators.MetaPopulator;
import com.pickaxis.grid.core.GridPlugin;
import java.util.logging.Level;
import lombok.Getter;
import org.bukkit.plugin.Plugin;
import org.jooq.Field;
import org.jooq.types.UByte;
import org.jooq.types.UNumber;

@Getter
public class ChannelTypeImpl implements ChannelType
{
    @Getter
    private final static MetaPopulator defaultMetaPopulator = new DefaultMetaPopulator();
    
    private final String name;
    
    private final UByte id;
    
    private final MetaPopulator metaPopulator;
    
    private final Field<UNumber> metaPrimary;
    
    private final Field<UNumber> metaSecondary;
    
    private final Plugin plugin;
    
    private static MetaPopulator getMetaPopulatorFromClass( String name, Class<? extends MetaPopulator> metaPopulatorCls )
    {
        MetaPopulator metaPopulator = ChannelTypeImpl.getDefaultMetaPopulator();
        if( metaPopulatorCls instanceof Class )
        {
            try
            {
                metaPopulator = metaPopulatorCls.newInstance();
            }
            catch( InstantiationException | IllegalAccessException ex )
            {
                GridPlugin.getInstance().getLogger().log( Level.SEVERE, "Exception while instantiating MetaPopulator in ChannelType." + name, ex );
            }
        }
        return metaPopulator;
    }
    
    public ChannelTypeImpl( String name, UByte id, MetaPopulator metaPopulator, Field metaPrimary, Field metaSecondary, Plugin plugin )
    {
        this.name = name;
        this.id = id;
        this.metaPopulator = metaPopulator;
        this.metaPrimary = metaPrimary;
        this.metaSecondary = metaSecondary;
        this.plugin = plugin;
    }
    
    public ChannelTypeImpl( String name, UByte id, Class<? extends MetaPopulator> metaPopulatorCls, Field metaPrimary, Field metaSecondary, Plugin plugin )
    {
        this( name, id, ChannelTypeImpl.getMetaPopulatorFromClass( name, metaPopulatorCls ), metaPrimary, metaSecondary, plugin );
    }
    
    ChannelTypeImpl( String name, UByte id, Class<? extends MetaPopulator> metaPopulatorCls, Field metaPrimary, Field metaSecondary )
    {
        this( name, id, metaPopulatorCls, metaPrimary, metaSecondary, GridPlugin.getInstance() );
    }
    
    ChannelTypeImpl( String name, UByte id, Class<? extends MetaPopulator> metaPopulatorCls, Field metaPrimary )
    {
        this( name, id, metaPopulatorCls, metaPrimary, null, GridPlugin.getInstance() );
    }
    
    ChannelTypeImpl( String name, UByte id )
    {
        this( name, id, (Class<? extends MetaPopulator>) null, null, null, GridPlugin.getInstance() );
    }
    
    @Override
    public boolean hasMeta()
    {
        return this.getMetaPrimary() instanceof Field;
    }
    
    @Override
    public boolean hasMetaSecondary()
    {
        return this.getMetaSecondary() instanceof Field;
    }
}
