/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.embeddedrailroad.eri.ctc.exceptions;

/**
 *
 * @author brian
 */
public class UnknownCtcBlockException extends java.lang.RuntimeException
{
    /**
     * Constructs an <code>UnknownCtcBlockException</code> with no
     * detail message.
     */
    public UnknownCtcBlockException() {
        super();
    }

    /**
     * Constructs an <code>UnknownCtcBlockException</code> with the
     * specified detail message.
     *
     * @param   s   the detail message.
     */
    public UnknownCtcBlockException(String s) {
        super(s);
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
     *         {@link Throwable#getCause()} method).  (A {@code null} value
     *         is permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     * @since 1.5
     */
    public UnknownCtcBlockException(String message, Throwable cause) {
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
     *         {@link Throwable#getCause()} method).  (A {@code null} value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     * @since  1.5
     */
    public UnknownCtcBlockException(Throwable cause) {
        super(cause);
    }

    private static final long serialVersionUID = 723402340782383L;

}
