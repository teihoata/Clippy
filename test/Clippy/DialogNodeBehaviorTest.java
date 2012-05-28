/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Clippy;

import Clippy.WordRecognizer.DialogNode;
import edu.cmu.sphinx.jsgf.JSGFGrammar;
import edu.cmu.sphinx.result.Result;
import javax.speech.recognition.RuleParse;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Marzipan
 */
public class DialogNodeBehaviorTest {
    
    public DialogNodeBehaviorTest() {
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
     * Test of onInit method, of class DialogNodeBehavior.
     */
    @Test
    public void testOnInit() {
        System.out.println("onInit");
        DialogNode node = null;
        DialogNodeBehavior instance = new DialogNodeBehavior();
        instance.onInit(node);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of onEntry method, of class DialogNodeBehavior.
     */
    @Test
    public void testOnEntry() throws Exception {
        System.out.println("onEntry");
        DialogNodeBehavior instance = new DialogNodeBehavior();
        instance.onEntry();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of onReady method, of class DialogNodeBehavior.
     */
    @Test
    public void testOnReady() {
        System.out.println("onReady");
        DialogNodeBehavior instance = new DialogNodeBehavior();
        instance.onReady();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of onRecognize method, of class DialogNodeBehavior.
     */
    @Test
    public void testOnRecognize() throws Exception {
        System.out.println("onRecognize");
        Result result_2 = null;
        DialogNodeBehavior instance = new DialogNodeBehavior();
        String expResult = "";
        String result = instance.onRecognize(result_2);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of processResult method, of class DialogNodeBehavior.
     */
    @Test
    public void testProcessResult() {
        System.out.println("processResult");
        String result_2 = "";
        DialogNodeBehavior instance = new DialogNodeBehavior();
        boolean expResult = false;
        boolean result = instance.processResult(result_2);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of onRecognizeFromString method, of class DialogNodeBehavior.
     */
    @Test
    public void testOnRecognizeFromString() throws Exception {
        System.out.println("onRecognizeFromString");
        String result_2 = "";
        DialogNodeBehavior instance = new DialogNodeBehavior();
        String expResult = "";
        String result = instance.onRecognizeFromString(result_2);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of onExit method, of class DialogNodeBehavior.
     */
    @Test
    public void testOnExit() {
        System.out.println("onExit");
        DialogNodeBehavior instance = new DialogNodeBehavior();
        instance.onExit();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getName method, of class DialogNodeBehavior.
     */
    @Test
    public void testGetName() {
        System.out.println("getName");
        DialogNodeBehavior instance = new DialogNodeBehavior();
        String expResult = "";
        String result = instance.getName();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toString method, of class DialogNodeBehavior.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        DialogNodeBehavior instance = new DialogNodeBehavior();
        String expResult = "";
        String result = instance.toString();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getGrammar method, of class DialogNodeBehavior.
     */
    @Test
    public void testGetGrammar() {
        System.out.println("getGrammar");
        DialogNodeBehavior instance = new DialogNodeBehavior();
        JSGFGrammar expResult = null;
        JSGFGrammar result = instance.getGrammar();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getRuleParse method, of class DialogNodeBehavior.
     */
    @Test
    public void testGetRuleParse() throws Exception {
        System.out.println("getRuleParse");
        Result result_2 = null;
        DialogNodeBehavior instance = new DialogNodeBehavior();
        RuleParse expResult = null;
        RuleParse result = instance.getRuleParse(result_2);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getRuleParseFromString method, of class DialogNodeBehavior.
     */
    @Test
    public void testGetRuleParseFromString() throws Exception {
        System.out.println("getRuleParseFromString");
        String result_2 = "";
        DialogNodeBehavior instance = new DialogNodeBehavior();
        RuleParse expResult = null;
        RuleParse result = instance.getRuleParseFromString(result_2);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTagStringFromString method, of class DialogNodeBehavior.
     */
    @Test
    public void testGetTagStringFromString() throws Exception {
        System.out.println("getTagStringFromString");
        String result_2 = "";
        DialogNodeBehavior instance = new DialogNodeBehavior();
        String expResult = "";
        String result = instance.getTagStringFromString(result_2);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of onRecognizeByString method, of class DialogNodeBehavior.
     */
    @Test
    public void testOnRecognizeByString() throws Exception {
        System.out.println("onRecognizeByString");
        String result_2 = "";
        DialogNodeBehavior instance = new DialogNodeBehavior();
        String expResult = "";
        String result = instance.onRecognizeByString(result_2);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTagString method, of class DialogNodeBehavior.
     */
    @Test
    public void testGetTagString() throws Exception {
        System.out.println("getTagString");
        Result result_2 = null;
        DialogNodeBehavior instance = new DialogNodeBehavior();
        String expResult = "";
        String result = instance.getTagString(result_2);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of trace method, of class DialogNodeBehavior.
     */
    @Test
    public void testTrace() {
        System.out.println("trace");
        String msg = "";
        DialogNodeBehavior instance = new DialogNodeBehavior();
        instance.trace(msg);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}
