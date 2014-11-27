/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.embeddedrailroad.eri.layoutio;

import java.util.Formatter;
import java.util.Locale;

/**
 *
 * @author brian
 */
public abstract class AbstractNodeMessageManager implements NodeMessageManager
{
    @Override
    public String prettyFormat( NodeMessage mesg, int options )
    {
        // see also: http://www.w3schools.com/tags/tag_td.asp
        // see also: http://www.w3schools.com/tags/att_td_valign.asp


        StringBuilder  sb = new StringBuilder( 500 );
        //  See java.util.Formatter for examples.
        // Send all output to the Appendable object sb
        Formatter  formatter = new Formatter( sb, Locale.getDefault() );

        final byte[]   bytes = mesg.getAllBytes();

        int   bytes_per_line = Integer.MAX_VALUE;
        if( (options & PKT_FMT_8_BYTE_DUMP) != 0 )
            bytes_per_line = 8;
        else
        if( (options & PKT_FMT_16_BYTE_DUMP) != 0 )
            bytes_per_line = 16;

        if( (options & PKT_FMT_HTML_TABLE_ROW) != 0 )
        {
            //  <tr> <td td valign="top" align="right"> OFFSET </td> <td align="left">BYTE ..</td> </tr> EOLN
            int  offset = 0;
            int  cntr;

            while( offset < bytes.length )
            {
                if( (options & PKT_FMT_HTML_5) != 0 )
                    sb.append( "  <tr> <td style=\"vertical-align:top; text-align:right\">" );
                else
                    sb.append( "  <tr> <td align=\"top\" align=\"right\">" );
                formatter.format( "%02X", offset );

                if( (options & PKT_FMT_HTML_5) != 0 )
                    sb.append(  "</td> <td style=\"text-align:left\">" );
                else
                    sb.append(  "</td> <td align=\"left\">" );

                for( cntr = 0 ; cntr < bytes_per_line && offset < bytes.length ; ++cntr, ++offset )
                {
                    formatter.format( " %02X", bytes[ offset ] );
                }

                sb.append( "</td> </tr>" );
                sb.append( System.lineSeparator() );
            }

        }
        else
        {
            int  offset = 0;
            int  cntr;

            while( offset < bytes.length )
            {
                formatter.format( "%02X:", offset );

                for( cntr = 0 ; cntr < bytes_per_line && offset < bytes.length ; ++cntr, ++offset )
                {
                    formatter.format( " %02X", bytes[ offset ] );
                }

                sb.append( System.lineSeparator() );
            }

        }

        return sb.toString();
    }

}
