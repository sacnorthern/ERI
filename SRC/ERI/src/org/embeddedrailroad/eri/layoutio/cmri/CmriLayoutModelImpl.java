/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.embeddedrailroad.eri.layoutio.cmri;

// import java.lang.Integer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;
import org.embeddedrailroad.eri.layoutio.LayoutIoModel;
import org.embeddedrailroad.eri.layoutio.UnknownLayoutUnitException;


/***
 *  Layout Model for one bank of CMRI units, maintained by one CmriLayoutTransport object;
 *  they are paired.
 *  CMRI units are addressed with a single number, from 0 to 31.  One the wire, 65 is added
 *  to the address, e.g. unit #0 address is encoded as 'A', unit #1 address is 'B', etc.
 *
 * <p> See http://www.onjava.com/pub/a/onjava/2004/07/07/genericmvc.html
 *
 * @author brian
 */
public class CmriLayoutModelImpl extends AbstractLayoutIoControllerInteger
{
    public CmriLayoutModelImpl()
    {
    }

    //--------------------------  INSTANCE VARS  -------------------------


    /***  Logging output spigot. */
    transient private static final Logger LOG = Logger.getLogger( CmriLayoutModelImpl.class.getName() );

}
