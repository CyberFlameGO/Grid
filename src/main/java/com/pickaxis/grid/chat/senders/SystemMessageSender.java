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

package com.pickaxis.grid.chat.senders;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.jooq.types.ULong;

/**
 * Something that's sending a chat message.
 */
@Getter
@Setter
@NoArgsConstructor
public class SystemMessageSender implements MessageSender
{
    private String name;
    
    private String displayName;
    
    public SystemMessageSender( String name )
    {
        this.setDisplayName( ChatColor.translateAlternateColorCodes( '&', name ) );
        this.setName( ChatColor.stripColor( this.getDisplayName() ) );
    }
    
    @Override
    public BaseComponent getComponentName()
    {
        return new TextComponent( TextComponent.fromLegacyText( this.getDisplayName() ) );
    }
    
    @Override
    public BaseComponent getComponentName( ULong messageId )
    {
        return this.getComponentName();
    }
}
