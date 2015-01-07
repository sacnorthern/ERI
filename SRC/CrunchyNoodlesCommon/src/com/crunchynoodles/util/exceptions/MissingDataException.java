/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.crunchynoodles.util.exceptions;

import org.w3c.dom.Element;

/**
 *  Thrown when an XML element should have CDATA but none is available.
 *
 * @author brian
 */
public class MissingDataException
    extends java.lang.RuntimeException
{
    /**
     * Constructs a <code>MissingDataException</code> with no
     * detail message.
     */
    public MissingDataException()
    {
        super();
    }

    /**
     * Constructs a <code>MissingDataException</code> with the
     * specified detail message.
     *
     * @param   s   the detail message.
     */
    public MissingDataException(String s)
    {
        super(s);
    }

    /***
     *  Construction run-time exception with a canned "missing data" message.
     *
     * @param dupKey Key string that is duplicate
     * @param dummy  ignored
     */
    public MissingDataException( Element element )
    {
        super( "Element \"" + element.getNodeName() + "\" has no CDATA." );
    }

    /**
     * Constructs a new exception with the specified detail message and
     * cause.
     *
     * <p>Note that the detail message associated with <code>cause</code> is
     * <i>not</i> automatically incorporated in this exception's detail
     * message.
     *
     * @param  message the detail message (which is saved for later retrieval
     *         by the {@link Throwable#getMessage()} method).
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link Throwable#getCause()} method).  (A <tt>null</tt> value
     *         is permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     */
    public MissingDataException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified cause and a detail
     * message of <tt>(cause==null ? null : cause.toString())</tt> (which
     * typically contains the class and detail message of <tt>cause</tt>).
     * This constructor is useful for exceptions that are little more than
     * wrappers for other throwables (for example, {@link
     * java.security.PrivilegedActionException}).
     *
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link Throwable#getCause()} method).  (A <tt>null</tt> value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     */
    public MissingDataException(Throwable cause)
    {
        super(cause);
    }

    private static final long serialVersionUID = -40230673092348397L;
}
