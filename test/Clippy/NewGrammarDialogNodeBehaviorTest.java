/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Clippy;

import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Marzipan
 */
public class NewGrammarDialogNodeBehaviorTest {
    
    public NewGrammarDialogNodeBehaviorTest() {
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
     * Test of onEntry method, of class NewGrammarDialogNodeBehavior.
     */
    @Test
    public void testOnEntry() throws Exception {
        System.out.println("onEntry");
        NewGrammarDialogNodeBehavior instance = new NewGrammarDialogNodeBehavior();
        instance.onEntry();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getGrammarName method, of class NewGrammarDialogNodeBehavior.
     */
    @Test
    public void testGetGrammarName() {
        System.out.println("getGrammarName");
        NewGrammarDialogNodeBehavior instance = new NewGrammarDialogNodeBehavior();
        String expResult = "";
        String result = instance.getGrammarName();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}
