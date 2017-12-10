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

package com.pickaxis.grid.chat.commands;

import com.pickaxis.grid.chat.ChatManager;
import com.pickaxis.grid.chat.ChatPlayerService;
import com.pickaxis.grid.chat.targets.ChatChannel;
import com.pickaxis.grid.core.GridPlugin;
import com.pickaxis.grid.core.command.CommandHandler;
import com.pickaxis.grid.core.command.CommandListener;
import java.util.Map;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Commands for personal channel management (joining, leaving, etc).
 */
public class ChannelCommands implements CommandListener
{
    /**
     * /channels
     * 
     * @param sender
     * @param args 
     */
    @CommandHandler( value = "channels",
                     description = "Lists the channels you're in.",
                     async = true,
                     playerOnly = true )
    public void channelsPresent( CommandSender sender, Map<String, String> args )
    {
        ChatPlayerService cps = GridPlugin.getInstance().getPlayerManager().getPlayer( (Player) sender ).getService( ChatPlayerService.class );
        
        cps.getPlayer().sendMessage( cps.getChannelsJoinedMessage() );
    }
}
