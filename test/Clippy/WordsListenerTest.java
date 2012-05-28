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
public class WordsListenerTest {
    
    public WordsListenerTest() {
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
     * Test of notify method, of class WordsListener.
     */
    @Test
    public void testNotify() {
        System.out.println("notify");
        String word = "";
        WordsListener instance = new WordsListenerImpl();
        instance.notify(word);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    public class WordsListenerImpl implements WordsListener {

        public void notify(String word) {
        }
    }
}
