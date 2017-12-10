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

package com.pickaxis.grid.chat.targets;

import com.google.common.base.Preconditions;
import com.pickaxis.grid.chat.targets.types.ChannelType;
import com.pickaxis.grid.core.data.tables.ChatChannelParticipants;
import com.pickaxis.grid.core.data.tables.ChatChannelsJoined;
import com.pickaxis.grid.core.data.tables.records.ChatChannelsRecord;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.types.UInteger;
import org.jooq.types.UNumber;
import org.jooq.types.UShort;

/**
 * A chat channel with meta IDs.
 */
@Getter
public class MetaChatChannelImpl extends AbstractChatChannel implements MetaChatChannel
{
    private final ChatChannel parentChannel;
    
    private final UInteger meta;
    
    private final UShort metaSecondary;
    
    private final String routingKey;
    
    public MetaChatChannelImpl( ChatChannel parentChannel, UInteger meta, UShort metaSecondary )
    {
        Preconditions.checkArgument( parentChannel instanceof ChatChannelImpl );
        
        this.parentChannel = parentChannel;
        this.meta = meta;
        this.metaSecondary = metaSecondary;
        
        String routingKey = this.getParentChannel().getId().toString() + "." + this.getMeta().toString();
        if( this.getMetaSecondary() instanceof UNumber )
        {
            routingKey += "." + this.getMetaSecondary();
        }
        this.routingKey = routingKey;
    }
    
    public MetaChatChannelImpl( ChatChannelImpl parentChannel, UInteger meta )
    {
        this( parentChannel, meta, null );
    }
    
    @Override
    public BaseComponent getTag()
    {
        return this.getParentChannel().getTag();
    }
    
    @Override
    public String getName()
    {
        return this.getParentChannel().getName();
    }
    
    @Override
    public ChannelType getType()
    {
        return this.getParentChannel().getType();
    }
    
    @Override
    public ChatChannelsRecord getDbRow()
    {
        return this.getParentChannel().getDbRow();
    }
    
    @Override
    public UInteger getId()
    {
        return this.getDbRow().getId();
    }
    
    @Override
    @SuppressWarnings( { "rawtypes", "unchecked" } )
    public Map<? extends Field<?>, ?> getFields( Table table )
    {
        Map<Field<? extends UNumber>, UNumber> values = new HashMap<>();
        
        values.put( (Field<UInteger>) table.field( "channel_id" ), this.getParentChannel().getId() );
        values.put( (Field<UInteger>) table.field( "meta_id" ), this.getMeta() );
        
        if( table.equals( ChatChannelsJoined.CHAT_CHANNELS_JOINED ) || table.equals( ChatChannelParticipants.CHAT_CHANNEL_PARTICIPANTS ) )
        {
            values.put( (Field<UShort>) table.field( "meta_secondary_id" ), this.getMetaSecondary() instanceof UNumber ? this.getMetaSecondary() : UShort.valueOf( 0 ) );
        }
        else
        {
            values.put( (Field<UShort>) table.field( "meta_secondary_id" ), this.getMetaSecondary() );
        }
        
        return values;
    }
    
    @Override
    @SuppressWarnings( { "rawtypes", "unchecked" } )
    public Condition getConditions( Table table )
    {
        Condition condition = ( (Field<UInteger>) table.field( "channel_id" ) ).eq( this.getParentChannel().getId() )
                                .and( ( (Field<UInteger>) table.field( "meta_id" ) ).eq( this.getMeta() ) );
        
        if( table.equals( ChatChannelsJoined.CHAT_CHANNELS_JOINED ) || table.equals( ChatChannelParticipants.CHAT_CHANNEL_PARTICIPANTS ) )
        {
            condition.and( ( (Field<UShort>) table.field( "meta_secondary_id" ) ).eq( this.getMetaSecondary() instanceof UNumber ? this.getMetaSecondary() : UShort.valueOf( 0 ) ) );
        }
        else
        {
            condition.and( ( (Field<UShort>) table.field( "meta_secondary_id" ) ).eq( this.getMetaSecondary() ) );
        }
        
        return condition;
    }
}
