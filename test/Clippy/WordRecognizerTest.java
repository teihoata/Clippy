/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Clippy;

import ClippyV2.ui.View.ClippyGui;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.util.props.PropertySheet;
import java.io.IOException;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Marzipan
 */
public class WordRecognizerTest {
    
    public WordRecognizerTest() {
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
     * Test of microphoneOn method, of class WordRecognizer.
     */
    @Test
    public void testMicrophoneOn() throws IOException {
        System.out.println("microphoneOn");
        WordRecognizer instance = new WordRecognizer();
        boolean expResult = false;
        boolean result = instance.microphoneOn();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of microphoneOff method, of class WordRecognizer.
     */
    @Test
    public void testMicrophoneOff() throws IOException {
        System.out.println("microphoneOff");
        WordRecognizer instance = new WordRecognizer();
        instance.microphoneOff();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of shutdown method, of class WordRecognizer.
     */
    @Test
    public void testShutdown() throws IOException {
        System.out.println("shutdown");
        WordRecognizer instance = new WordRecognizer();
        instance.shutdown();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of enterNextNode method, of class WordRecognizer.
     */
    @Test
    public void testEnterNextNode() throws IOException {
        System.out.println("enterNextNode");
        String nextState = "";
        WordRecognizer instance = new WordRecognizer();
        instance.enterNextNode(nextState);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of run method, of class WordRecognizer.
     */
    @Test
    public void testRun() throws IOException {
        System.out.println("run");
        WordRecognizer instance = new WordRecognizer();
        instance.run();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addWordListener method, of class WordRecognizer.
     */
    @Test
    public void testAddWordListener() throws IOException {
        System.out.println("addWordListener");
        WordsListener wordListener = null;
        WordRecognizer instance = new WordRecognizer();
        instance.addWordListener(wordListener);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of removeWordListener method, of class WordRecognizer.
     */
    @Test
    public void testRemoveWordListener() throws IOException {
        System.out.println("removeWordListener");
        WordsListener wordListener = null;
        WordRecognizer instance = new WordRecognizer();
        instance.removeWordListener(wordListener);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of newProperties method, of class WordRecognizer.
     */
    @Test
    public void testNewProperties() throws IOException {
        System.out.println("newProperties");
        PropertySheet ps = null;
        WordRecognizer instance = new WordRecognizer();
        instance.newProperties(ps);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addNode method, of class WordRecognizer.
     */
    @Test
    public void testAddNode() throws IOException {
        System.out.println("addNode");
        String name = "";
        DialogNodeBehavior behavior = null;
        WordRecognizer instance = new WordRecognizer();
        instance.addNode(name, behavior);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setInitialNode method, of class WordRecognizer.
     */
    @Test
    public void testSetInitialNode() throws IOException {
        System.out.println("setInitialNode");
        String name = "";
        WordRecognizer instance = new WordRecognizer();
        instance.setInitialNode(name);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of allocate method, of class WordRecognizer.
     */
    @Test
    public void testAllocate() throws Exception {
        System.out.println("allocate");
        WordRecognizer instance = new WordRecognizer();
        instance.allocate();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of deallocate method, of class WordRecognizer.
     */
    @Test
    public void testDeallocate() throws IOException {
        System.out.println("deallocate");
        WordRecognizer instance = new WordRecognizer();
        instance.deallocate();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of go method, of class WordRecognizer.
     */
    @Test
    public void testGo() throws Exception {
        System.out.println("go");
        WordRecognizer instance = new WordRecognizer();
        instance.go();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getName method, of class WordRecognizer.
     */
    @Test
    public void testGetName() throws IOException {
        System.out.println("getName");
        WordRecognizer instance = new WordRecognizer();
        String expResult = "";
        String result = instance.getName();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getRecognizer method, of class WordRecognizer.
     */
    @Test
    public void testGetRecognizer() throws IOException {
        System.out.println("getRecognizer");
        WordRecognizer instance = new WordRecognizer();
        Recognizer expResult = null;
        Recognizer result = instance.getRecognizer();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setRecognizer method, of class WordRecognizer.
     */
    @Test
    public void testSetRecognizer() throws IOException {
        System.out.println("setRecognizer");
        Recognizer recognizer = null;
        WordRecognizer instance = new WordRecognizer();
        instance.setRecognizer(recognizer);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setGui method, of class WordRecognizer.
     */
    @Test
    public void testSetGui() throws IOException {
        System.out.println("setGui");
        ClippyGui gui = null;
        WordRecognizer instance = new WordRecognizer();
        instance.setGui(gui);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}
