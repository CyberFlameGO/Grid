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

import lombok.Getter;
import org.jooq.types.UByte;

/**
 * Representation of chat flair types, as stored in the database.
 */
@Getter
public enum ChatFlairType
{
    STAFF( 1, true ),
    STORE( 2, true ),
    FLAIR( 3, false );
    
    private final int code;
    
    private final UByte dbCode;
    
    private final boolean permissionBased;
    
    ChatFlairType( int code, boolean permissionBased )
    {
        this.code = code;
        this.dbCode = UByte.valueOf( code );
        this.permissionBased = permissionBased;
    }
    
    public ChatFlairType get( int code )
    {
        return ChatFlairType.values()[ code - 1 ];
    }
}
