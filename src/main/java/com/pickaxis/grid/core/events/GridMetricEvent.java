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

import com.google.common.base.Preconditions;
import com.pickaxis.grid.core.GridPlugin;
import lombok.Getter;
import org.bukkit.event.HandlerList;

/**
 * Reports a metric in Grid for DataMine to collect.
 */
@Getter
public class GridMetricEvent extends GridEvent
{
    @Getter
    public enum MetricType
    {
        COUNT_DELTA( Long.class ),
        COUNT_INCREMENT( null ),
        COUNT_DECREMENT( null ),
        GAUGE_DOUBLE( Double.class ),
        GAUGE_LONG( Long.class ),
        HISTOGRAM_DOUBLE( Double.class ),
        HISTOGRAM_LONG( Long.class ),
        EXECUTION_TIME( Long.class );
        
        private final Class<? extends Number> valueType;
        
        MetricType( Class<? extends Number> valueType )
        {
            this.valueType = valueType;
        }
    }
    
    @Getter
    private static final HandlerList handlerList = new HandlerList();
    
    private final MetricType type;
    
    private final String key;
    
    private final Number value;
    
    private final String[] tags;
    
    public GridMetricEvent( MetricType type, String key, Number value, String... tags )
    {
        Preconditions.checkNotNull( type );
        Preconditions.checkNotNull( key );
        if( type.getValueType() != null )
        {
            Preconditions.checkNotNull( value );
        }
        
        this.type = type;
        this.key = GridPlugin.getInstance().getConfig().getString( "metrics.prefix", "grid." ) + key;
        this.value = value;
        this.tags = tags;
    }
    
    public GridMetricEvent( MetricType type, String key, String... tags )
    {
        this( type, key, null, tags );
    }
    
    @Override
    public HandlerList getHandlers()
    {
        return GridMetricEvent.getHandlerList();
    }
}
