/***  This file is dedicated to the public domain, 2014, 2016 Brian Witt in USA.  ***/

package org.embeddedrailroad.eri.layoutio.cmri;

import com.crunchynoodles.util.TableOfBoolean;
import java.util.HashMap;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.internal.ArrayComparisonFailure;

/**
 *
 * @author brian
 */
public class CmriLayoutModelImplTest {

    public CmriLayoutModelImplTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

	public static void assertBooleanArrayEquals(String mesg, boolean[] expecteds, boolean[] actuals)
                 throws ArrayComparisonFailure
        {
            if( expecteds == null && actuals == null )
                return;
            if( (expecteds == null && actuals != null) ||
                (expecteds != null && actuals == null) )
                throw new ArrayComparisonFailure( mesg, null, 0 );

            int  el = expecteds.length;
            if( el != actuals.length )
                throw new ArrayComparisonFailure( mesg, null, Math.max( el, actuals.length ) );

            for( int j = 0 ; j < el ; ++j )
            {
                if( expecteds[j] != actuals[j] )
                    throw new ArrayComparisonFailure( mesg, null, j );
            }
	}   /* end assertBooleanArrayEquals() */

    /**
     * Test of setSensedBinaryData method, of class CmriLayoutModelImpl.
     */
    @Test
    public void testSetSensedBinaryData_Integer_booleanArr()
    {
        System.out.println( "setSensedBinaryData" );
        Integer device = new Integer(3);            // Boxing here is "unnecessary" but it explains the API.
        boolean[] new_bits = { false, true, false, true, false, true };
        CmriLayoutModelImpl instance = new CmriLayoutModelImpl();
        instance.setSensedBinaryData( device, new_bits );

        TableOfBoolean  expResult = new TableOfBoolean( new_bits );

        TableOfBoolean  result = instance.getSensedDataAll( device );

        assertTrue( "Device #"+device + " failed.", expResult.isSame( result ));

    }

    /**
     * Test of setSensedBinaryData method, of class CmriLayoutModelImpl.
     */
    @Test
    public void testSetSensedBinaryData_Integer_HashMap()
    {
        System.out.println( "setSensedBinaryData" );
        Integer device = new Integer(3);            // Boxing here is "unnecessary" but it explains the API.

        TableOfBoolean individual_bits = new TableOfBoolean();
        individual_bits.put( new Integer(0), Boolean.FALSE );
        individual_bits.put( new Integer(2), Boolean.FALSE );
        individual_bits.put( new Integer(5), Boolean.TRUE );
        individual_bits.put( new Integer(49), Boolean.TRUE );

        CmriLayoutModelImpl instance = new CmriLayoutModelImpl();
        instance.setSensedBinaryData( device, individual_bits );

        TableOfBoolean  expResult = individual_bits;
        TableOfBoolean  result = instance.getSensedDataAll( device );
        assertTrue( "Device #"+device + " failed.", expResult.isSame( result ));
    }

    /**
     * Test of setSensedBinaryBlob method, of class CmriLayoutModelImpl.
     */
    @Test
    public void testSetSensedBinaryBlob()
    {
        System.out.println( "setSensedBinaryBlob" );
        Integer device = new Integer(3);
        int subfunction = 10;
        byte[] blob = {0x7E, 0x03, 0x7F };
        CmriLayoutModelImpl instance = new CmriLayoutModelImpl();
        instance.setSensedBinaryBlob( device, subfunction, blob );

        byte[]  expResult = blob;
        byte[]  result = instance.getSensedBlob( device, subfunction );
        assertArrayEquals( "Device #"+device, expResult, result );
    }

    /**
     * Test of getSensedDataAll method, of class CmriLayoutModelImpl.
     */
    @Test
    public void testGetSensedDataAll() {
        System.out.println( "getSensedDataAll" );
        Integer device = new Integer(3);
        boolean[] new_bits = { false, true, false, true, false, true };
        CmriLayoutModelImpl instance = new CmriLayoutModelImpl();
        instance.setSensedBinaryData( device, new_bits );

        TableOfBoolean expResult = new TableOfBoolean( new_bits );
        TableOfBoolean result = instance.getSensedDataAll( device );
        assertTrue( "Device #"+device + " failed.", expResult.isSame( result ));
    }

    /**
     * Test of getSensedDataOne method, of class CmriLayoutModelImpl.
     */
    @Test
    public void testGetSensedDataOne() {
        System.out.println( "getSensedDataOne" );
        Integer device = new Integer(3);
        boolean[] new_bits = { false, true, false, true, false, true };

        CmriLayoutModelImpl instance = new CmriLayoutModelImpl();
        instance.setSensedBinaryData( device, new_bits );

        for( int j = 0 ; j < new_bits.length ; ++j )
        {
            boolean expResult = new_bits[j];
            boolean result = instance.getSensedDataOne( device, j );
            assertEquals( "new_bits[" + j + "]", expResult, result );
        }
    }

    /**
     * Test of getSensedBlob method, of class CmriLayoutModelImpl.
     */
    @Test
    public void testGetSensedBlob() {
        System.out.println( "getSensedBlob" );

        Integer device = new Integer(3);
        int subfunction = 0;
        byte[] blob = {0x7E, 0x03, 0x7F };
        CmriLayoutModelImpl instance = new CmriLayoutModelImpl();
        instance.setSensedBinaryBlob( device, subfunction, blob );

        byte[] expResult = blob;
        byte[] result = instance.getSensedBlob( device, subfunction );
        assertArrayEquals( expResult, result );
    }
}