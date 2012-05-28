/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Clippy;

import ClippyV2.ui.View.ClippyGui;
import edu.cmu.sphinx.result.Result;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Marzipan
 */
public class MyBehaviorTest {
    
    public MyBehaviorTest() {
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
     * Test of addDefaultListWithoutCurrent method, of class MyBehavior.
     */
    @Test
    public void testAddDefaultListWithoutCurrent() {
        System.out.println("addDefaultListWithoutCurrent");
        ArrayList<String> list = new ArrayList<>();
        ArrayList<String> list2 = list;
        String current = "music";
        MyBehavior instance = new MyBehavior();
        instance.addDefaultListWithoutCurrent(list, current);
        assertEquals(list, list2.remove("music"));
    }

    /**
     * Test of addTitle method, of class MyBehavior.
     */
    @Test
    public void testAddTitle() {
        System.out.println("addTitle");
        MyBehavior instance = null;
        instance.addTitle();
    }

    /**
     * Test of updateList method, of class MyBehavior.
     */
    @Test
    public void testUpdateList() {
        System.out.println("updateList");
        MyBehavior instance = null;
        instance.updateList();
    }

    /**
     * Test of getCurrentList method, of class MyBehavior.
     */
    @Test
    public void testGetCurrentList() {
        System.out.println("getCurrentList");
        MyBehavior instance = null;
        ArrayList expResult = null;
        ArrayList result = instance.getCurrentList();
        assertEquals(expResult, result);
    }

    /**
     * Test of processResult method, of class MyBehavior.
     */
    @Test
    public void testProcessResult() {
        System.out.println("processResult");
        String result_2 = "";
        MyBehavior instance = null;
        boolean expResult = false;
        boolean result = instance.processResult(result_2);
        assertEquals(expResult, result);
    }

    /**
     * Test of onReady method, of class MyBehavior.
     */
    @Test
    public void testOnReady() {
        System.out.println("onReady");
        MyBehavior instance = null;
        instance.onReady();
    }

    /**
     * Test of onEntry method, of class MyBehavior.
     */
    @Test
    public void testOnEntry() throws Exception {
        System.out.println("onEntry");
        MyBehavior instance = null;
        instance.onEntry();
    }

    /**
     * Test of help method, of class MyBehavior.
     */
    @Test
    public void testHelp() {
        System.out.println("help");
        MyBehavior instance = null;
        instance.help();
    }

    /**
     * Test of getCurrentMenuOptions method, of class MyBehavior.
     */
    @Test
    public void testGetCurrentMenuOptions() {
        System.out.println("getCurrentMenuOptions");
        MyBehavior instance = null;
        String expResult = "";
        String result = instance.getCurrentMenuOptions();
        assertEquals(expResult, result);
    }

    /**
     * Test of onRecognizeByString method, of class MyBehavior.
     */
    @Test
    public void testOnRecognizeByString() throws Exception {
        System.out.println("onRecognizeByString");
        String result_2 = "";
        MyBehavior instance = null;
        String expResult = "";
        String result = instance.onRecognizeByString(result_2);
        assertEquals(expResult, result);
    }

    /**
     * Test of onRecognize method, of class MyBehavior.
     */
    @Test
    public void testOnRecognize() throws Exception {
        System.out.println("onRecognize");
        Result result_2 = null;
        MyBehavior instance = null;
        String expResult = "";
        String result = instance.onRecognize(result_2);
        assertEquals(expResult, result);
    }
}
