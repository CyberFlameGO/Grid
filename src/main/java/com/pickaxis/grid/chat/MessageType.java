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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.jooq.types.UByte;

/**
 * Representation of message types, as stored in the database
 * and used in MQ messages.
 */
@Getter
public enum MessageType
{
    JOIN( 1, "{TAG}{SENDER} has joined the channel." ),
    LEAVE( 2, "{TAG}{SENDER} has left the channel." ),
    SYSTEM( 3, "{TAG}{MESSAGE}" ),
    MESSAGE( 4, "{TAG}{SENDER}: {MESSAGE}" ),
    ACTION( 5, "{TAG}! {SENDER} has {ACTION} {TARGET}." ), // TODO: Replace with individual messages for each action?
    EMOTE( 6, "{TAG}* {SENDER} {MESSAGE}" ),
    ONLINE( 7, "{TAG}{SENDER}&e has come online." ),
    OFFLINE( 8, "{TAG}{SENDER}&e has gone offline." ),
    PMINBOUND( 9, "&d[{SENDER}&d] whispers: {MESSAGE}" ),
    PMOUTBOUND( 10, "&dTo [{SENDER}&d]: {MESSAGE}" ),
    PMSTATUS( 11, "&d[{SENDER}&d] is {STATUS}&d: {MESSAGE}" ), // TODO: Needs third arg.
    CHANGETEAM( 14, "{TAG}{SENDER} switched to {MESSAGE}." ),
    SWITCHSERVER( 16, "{TAG}{SENDER} has switched to {MESSAGE}." );
    // Remember to add new types to the get method switch!
    
    private final Integer code;
    
    private final UByte dbCode;
    
    private final Object[] formattingArray;
   
    MessageType( int code, String format )
    {
        this.code = code;
        this.dbCode = UByte.valueOf( code );
        
        List<String> matches = new ArrayList<>();
        Matcher m = Pattern.compile( "(\\{[A-Z]+\\}|[^\\{\\}]+)" ).matcher( LangUtil.getString( "chat.format." + this.name().toLowerCase(), format ) );
        while( m.find() )
        {
            matches.add( m.group() );
        }
        this.formattingArray = matches.toArray();
        
        for( int i = 0; i < this.formattingArray.length; i++ )
        {
            if( ( (String) this.formattingArray[i] ).length() > 2 )
            {
                String sub = ( (String) this.formattingArray[i] ).substring( 1, ( (String) this.formattingArray[i] ).length() - 1 );
                for( Replacement r : Replacement.values() )
                {
                    if( sub.equals( r.name() ) )
                    {
                        this.formattingArray[i] = r;
                    }
                }
            }
            
            if( this.formattingArray[i] instanceof String )
            {
                this.formattingArray[i] = new TextComponent( TextComponent.fromLegacyText( (String) this.formattingArray[i] ) );
            }
        }
    }
    
    /**
     * Gets a copy of the formatting array.  You should NOT
     * modify the BaseComponent elements of the array, but
     * you can replace them if needed.  Modifying the elements
     * will modify them in all future uses of this format, as
     * only a reference is passed, not a copy.
     * 
     * @return A copy of the formatting arary.
     */
    public Object[] getFormattingArray()
    {
        return Arrays.copyOf( this.formattingArray, this.formattingArray.length );
    }
    
    /**
     * Formats a message using the specified replacements.
     * 
     * @param replacements The replacements to use.
     * @return The formatted message (as a BaseComponent[]).
     */
    @SuppressWarnings( "element-type-mismatch" )
    public BaseComponent[] formatMessage( Map<Replacement, Object> replacements )
    {
        Object[] formatArray = this.getFormattingArray();
        for( int i = 0; i < formatArray.length; i++ )
        {
            if( formatArray[i] instanceof Replacement )
            {
                if( !replacements.containsKey( formatArray[i] ) )
                {
                    replacements.put( (Replacement) formatArray[i], "" );
                }
                
                Object r = replacements.get( formatArray[i] );
                
                if( r instanceof BaseComponent )
                {
                    formatArray[i] = r;
                }
                else if( r instanceof String )
                {
                    formatArray[i] = new TextComponent( TextComponent.fromLegacyText( (String) r ) );
                }
            }
        }
        return Arrays.copyOf( formatArray, formatArray.length, BaseComponent[].class );
    }
    
    public static MessageType get( int code )
    {
        switch( code )
        {
            case 1: return JOIN;
            case 2: return LEAVE;
            case 3: return SYSTEM;
            case 4: return MESSAGE;
            case 5: return ACTION;
            case 6: return EMOTE;
            case 7: return ONLINE;
            case 8: return OFFLINE;
            case 9: return PMINBOUND;
            case 10: return PMOUTBOUND;
            case 11: return PMSTATUS;
            case 14: return CHANGETEAM;
            case 16: return SWITCHSERVER;
            default: return null;
        }
    }
}
