/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Clippy;

import edu.cmu.sphinx.result.Result;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Marzipan
 */
public class MyMusicBehaviorTest {
    
    public MyMusicBehaviorTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of onEntry method, of class MyMusicBehavior.
     */
    @Test
    public void testOnEntry() throws Exception {
        System.out.println("onEntry");
        MyMusicBehavior instance = null;
        instance.onEntry();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of processResult method, of class MyMusicBehavior.
     */
    @Test
    public void testProcessResult() {
        System.out.println("processResult");
        String result_2 = "";
        MyMusicBehavior instance = null;
        boolean expResult = false;
        boolean result = instance.processResult(result_2);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of onRecognize method, of class MyMusicBehavior.
     */
    @Test
    public void testOnRecognize() throws Exception {
        System.out.println("onRecognize");
        Result result_2 = null;
        MyMusicBehavior instance = null;
        String expResult = "";
        String result = instance.onRecognize(result_2);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}