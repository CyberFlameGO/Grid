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

package com.pickaxis.grid.chat;

import com.pickaxis.grid.core.util.LangUtil;
import lombok.Getter;
import org.jooq.types.UByte;

/**
 * Representation of channel action types, as stored in the database.
 */
@Getter
public enum ChannelActionType
{
    MUTE( 1, null, "muted", "unmuted" ),
    BAN( 2, null, "banned", "unbanned" ),
    INVITE( 3, null, "invited", "uninvited" ),
    VOICE( 4, "+", "voiced", "devoiced" ),
    MOD( 5, "%", "granted mod status to", "revoked mod status from" );
    
    private final Integer code;
    
    private final UByte dbCode;
    
    private final String prefix;
    
    private final String message;
    
    private final String messageUndo;
    
    ChannelActionType( int code, String prefix, String message, String messageUndo )
    {
        this.code = code;
        this.dbCode = UByte.valueOf( code );
        this.prefix = prefix;
        this.message = LangUtil.getString( "chat.action." + this.name().toLowerCase() + ".message", message );
        this.messageUndo = LangUtil.getString( "chat.action." + this.name().toLowerCase() + ".message-undo", messageUndo );
    }
    
    public static ChannelActionType get( int code )
    {
        return ChannelActionType.values()[ code - 1 ];
    }
}
