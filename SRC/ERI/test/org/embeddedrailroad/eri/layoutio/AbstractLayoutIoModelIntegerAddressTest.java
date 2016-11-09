/***  This file is dedicated to the public domain, 2016 Brian Witt in USA.  ***/

package org.embeddedrailroad.eri.layoutio;

import com.crunchynoodles.util.TableOfBoolean;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author brian
 */
public class AbstractLayoutIoModelIntegerAddressTest {

    public AbstractLayoutIoModelIntegerAddressTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @Before
    public void setUp()
    {
        //  Called before every test method.
    }

    /**
     * Test of getUnitAddressType method, of class AbstractLayoutIoModelIntegerAddress.
     */
    @Test
    public void testGetUnitAddressType()
    {
        System.out.println( "getUnitAddressType" );
        AbstractLayoutIoModelIntegerAddress instance = new AbstractLayoutIoModelIntegerAddress();
        Class expResult = Integer.class;
        Class result = instance.getUnitAddressType();
        assertEquals( expResult, result );
    }

    /**
     * Test of setUnitInitializationStrings method, of class AbstractLayoutIoModelIntegerAddress.
     */
    @Test
    public void testSetUnitInitializationStrings() {
        System.out.println( "setUnitInitializationStrings" );
        Integer unit = 3;
        ArrayList mesgs = null;
        AbstractLayoutIoModelIntegerAddress instance = new AbstractLayoutIoModelIntegerAddress();
        instance.setUnitInitializationStrings( unit, mesgs );
        // TODO review the generated test code and remove the default call to fail.
        fail( "The test case is a prototype." );
    }

    /**
     * Test of getUnitInitializationStrings method, of class AbstractLayoutIoModelIntegerAddress.
     */
    @Test
    public void testGetUnitInitializationStrings() {
        System.out.println( "getUnitInitializationStrings" );
        Integer unit = null;
        AbstractLayoutIoModelIntegerAddress instance = new AbstractLayoutIoModelIntegerAddress();
        ArrayList expResult = null;
        ArrayList result = instance.getUnitInitializationStrings( unit );
        assertEquals( expResult, result );
        // TODO review the generated test code and remove the default call to fail.
        fail( "The test case is a prototype." );
    }

    /**
     * Test of setUnitQueryMessage method, of class AbstractLayoutIoModelIntegerAddress.
     */
    @Test
    public void testSetUnitQueryMessage() {
        System.out.println( "setUnitQueryMessage" );
        Integer unit = 3;
        byte[] query = null;
        AbstractLayoutIoModelIntegerAddress instance = new AbstractLayoutIoModelIntegerAddress();
        instance.setUnitQueryMessage( unit, query );
        // TODO review the generated test code and remove the default call to fail.
        fail( "The test case is a prototype." );
    }

    /**
     * Test of getUnitQueryMessage method, of class AbstractLayoutIoModelIntegerAddress.
     */
    @Test
    public void testGetUnitQueryMessage() {
        System.out.println( "getUnitQueryMessage" );
        Integer unit = 3;
        AbstractLayoutIoModelIntegerAddress instance = new AbstractLayoutIoModelIntegerAddress();
        byte[] expResult = null;
        byte[] result = instance.getUnitQueryMessage( unit );
        assertArrayEquals( expResult, result );
        // TODO review the generated test code and remove the default call to fail.
        fail( "The test case is a prototype." );
    }

    /**
     * Test of setSensedBinaryData method, of class AbstractLayoutIoModelIntegerAddress.
     */
    @Test
    public void testSetSensedBinaryData_Integer_booleanArr() {
        System.out.println( "setSensedBinaryData" );
        Integer device = 3;
        boolean[] newBits = null;
        AbstractLayoutIoModelIntegerAddress instance = new AbstractLayoutIoModelIntegerAddress();
        instance.setSensedBinaryData( device, newBits );
        // TODO review the generated test code and remove the default call to fail.
        fail( "The test case is a prototype." );
    }

    /**
     * Test of setSensedBinaryData method, of class AbstractLayoutIoModelIntegerAddress.
     */
    @Test
    public void testSetSensedBinaryData_Integer_TableOfBoolean() {
        System.out.println( "setSensedBinaryData" );
        Integer device = 3;
        TableOfBoolean individual_bits = null;
        AbstractLayoutIoModelIntegerAddress instance = new AbstractLayoutIoModelIntegerAddress();
        instance.setSensedBinaryData( device, individual_bits );
        // TODO review the generated test code and remove the default call to fail.
        fail( "The test case is a prototype." );
    }

    /**
     * Test of setSensedBinaryBlob method, of class AbstractLayoutIoModelIntegerAddress.
     */
    @Test
    public void testSetSensedBinaryBlob() {
        System.out.println( "setSensedBinaryBlob" );
        Integer device = 3;
        int subfunction = 0;
        byte[] blob = null;
        AbstractLayoutIoModelIntegerAddress instance = new AbstractLayoutIoModelIntegerAddress();
        instance.setSensedBinaryBlob( device, subfunction, blob );
        // TODO review the generated test code and remove the default call to fail.
        fail( "The test case is a prototype." );
    }

    /**
     * Test of getSensedDataAll method, of class AbstractLayoutIoModelIntegerAddress.
     */
    @Test
    public void testGetSensedDataAll() {
        System.out.println( "getSensedDataAll" );
        Integer device = 3;
        AbstractLayoutIoModelIntegerAddress instance = new AbstractLayoutIoModelIntegerAddress();
        TableOfBoolean expResult = null;
        TableOfBoolean result = instance.getSensedDataAll( device );
        assertEquals( expResult, result );
        // TODO review the generated test code and remove the default call to fail.
        fail( "The test case is a prototype." );
    }

    /**
     * Test of getSensedDataOne method, of class AbstractLayoutIoModelIntegerAddress.
     */
    @Test
    public void testGetSensedDataOne() {
        System.out.println( "getSensedDataOne" );
        Integer device = 3;
        int bit_number = 0;
        AbstractLayoutIoModelIntegerAddress instance = new AbstractLayoutIoModelIntegerAddress();
        boolean expResult = false;
        boolean result = instance.getSensedDataOne( device, bit_number );
        assertEquals( expResult, result );
        // TODO review the generated test code and remove the default call to fail.
        fail( "The test case is a prototype." );
    }

    /**
     * Test of getSensedBlob method, of class AbstractLayoutIoModelIntegerAddress.
     */
    @Test
    public void testGetSensedBlob() {
        System.out.println( "getSensedBlob" );
        Integer device = 3;
        int subfunction = 0;
        AbstractLayoutIoModelIntegerAddress instance = new AbstractLayoutIoModelIntegerAddress();
        byte[] expResult = null;
        byte[] result = instance.getSensedBlob( device, subfunction );
        assertArrayEquals( expResult, result );
        // TODO review the generated test code and remove the default call to fail.
        fail( "The test case is a prototype." );
    }

}
