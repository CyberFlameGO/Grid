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

import java.io.Serializable;
import net.md_5.bungee.api.chat.BaseComponent;
import org.jooq.types.ULong;

/**
 * Someone (or something!) that's sending a message.
 */
public interface MessageSender extends Serializable
{
    /**
     * Gets the message sender's unformatted name.
     * 
     * @return The sender's unformatted name.
     */
    String getName();
    
    /**
     * Gets the message sender's formatted display name.
     * 
     * @return The sender's display name.
     */
    String getDisplayName();
    
    /**
     * Gets the message sender's chat component display name.
     * 
     * @return The sender's component name.
     */
    BaseComponent getComponentName();
    
    /**
     * Gets the message sender's chat component display name, with
     * and embedded message ID for options.
     * 
     * @param messageId The message ID to embed.
     * @return The sender's component name.
     */
    BaseComponent getComponentName( ULong messageId );
}
