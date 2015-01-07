/***  Java Commons and Niceties Library from CrunchyNoodles.com
 ***  Copyright (C) 2014 in USA by Brian Witt , bwitt@value.net
 ***
 ***  Licensed under the Apache License, Version 2.0 ( the "License" ) ;
 ***  you may not use this file except in compliance with the License.
 ***  You may obtain a copy of the License at:
 ***        http://www.apache.org/licenses/LICENSE-2.0
 ***
 ***  Unless required by applicable law or agreed to in writing, software
 ***  distributed under the License is distributed on an "AS IS" BASIS,
 ***  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ***  See the License for the specific languatge governing permissions and
 ***  limitations under the License.
 ***/

package com.crunchynoodles.util.exceptions;

/**
 *  Thrown when the (property) key is a duplicate, and duplicates are not permitted.
 *
 * @author brian
 */
public class DuplicateKeyException
    extends java.lang.RuntimeException
{
    /**
     * Constructs a <code>DuplicateKeyException</code> with no
     * detail message.
     */
    public DuplicateKeyException()
    {
        super();
    }

    /**
     * Constructs a <code>DuplicateKeyException</code> with the
     * specified detail message.
     *
     * @param   s   the detail message.
     */
    public DuplicateKeyException(String s)
    {
        super(s);
    }

    /***
     *  Construction run-time exception with a canned "rejected key" message.
     *
     * @param dupKey Key string that is duplicate
     * @param dummy  ignored
     */
    public DuplicateKeyException( String dupKey, Boolean dummy )
    {
        super( "Rejecting duplicate key \"" + dupKey + "\"" );
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
    public DuplicateKeyException(String message, Throwable cause)
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
    public DuplicateKeyException(Throwable cause)
    {
        super(cause);
    }

    private static final long serialVersionUID = -40230673092348397L;
}
