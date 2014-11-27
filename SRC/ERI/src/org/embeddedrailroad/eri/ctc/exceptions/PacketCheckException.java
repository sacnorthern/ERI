/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.embeddedrailroad.eri.ctc.exceptions;

/**
 *   Thrown when a received packet has an invalid check on it.
 *   Not all packets have a checksum or CRC, so it might not be thrown...
 * @author brian
 */
public class PacketCheckException extends java.lang.RuntimeException
{
    /**
     * Constructs an <code>PacketCheckException</code> with no
     * detail message.
     */
    public PacketCheckException() {
        super();
    }

    /**
     * Constructs an <code>PacketCheckException</code> with the
     * specified detail message.
     *
     * @param   s   the detail message.
     */
    public PacketCheckException(String s) {
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
    public PacketCheckException(String message, Throwable cause) {
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
    public PacketCheckException(Throwable cause) {
        super(cause);
    }

    private static final long serialVersionUID = 9324701248L;

}
