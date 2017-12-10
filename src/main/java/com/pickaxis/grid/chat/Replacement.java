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

/**
 * Replacements variables in messages.
 */
public enum Replacement
{
    TAG,
    SLUG, //irc
    SENDER,
    SENDER_UUID, //irc
    SENDER_ID, //irc
    MESSAGE,
    STATUS,
    ACTION,
    TARGET,
    SERVER_SLUG, //irc
    RECIPIENT; //irc
}
