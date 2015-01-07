/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.crunchynoodles.util.exceptions;

/**
 *  Thrown when the (property) key is not supported or unknown by sub-system.
 *
 * @author brian
 */
public class UnsupportedKeyException
    extends java.lang.RuntimeException
{

    /**
     * Constructs a <code>UnsupportedKeyException</code> with no
     * detail message.
     */
    public UnsupportedKeyException()
    {
        super();
    }

    /**
     * Constructs a <code>UnsupportedKeyException</code> with the
     * specified detail message.
     *
     * @param   s   the detail message.
     */
    public UnsupportedKeyException(String s)
    {
        super(s);
    }

    /***
     *  Construction run-time exception with a canned "unsupported key" message.
     *
     * @param dupKey Key string that is unsupported.
     * @param dummy  ignored
     */
    public UnsupportedKeyException( String dupKey, Boolean dummy )
    {
        super( "Rejecting unsupported key \"" + dupKey + "\"" );
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
    public UnsupportedKeyException(String message, Throwable cause)
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
    public UnsupportedKeyException(Throwable cause)
    {
        super(cause);
    }

    private static final long serialVersionUID = 58230600110817L;
}
