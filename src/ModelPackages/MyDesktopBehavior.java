/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ModelPackages;

import edu.cmu.sphinx.result.Result;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import javax.speech.recognition.GrammarException;

/**
 *
 * @author Marzipan
 */
public class MyDesktopBehavior extends MyBehavior {

        private boolean stopped;

        @Override
        public String onRecognize(Result result) throws GrammarException
        {
            String tag = super.onRecognize(result);
            String listen = result.getBestFinalResultNoFiller();
            if (listen.equalsIgnoreCase("main menu") || listen.equalsIgnoreCase("menu"))
            {
                tag = "menu";
            }
            if (listen.equals("change window"))
            {

                try
                {
                    Robot robot = new Robot();
                    stopped = false;
                    robot.keyPress(KeyEvent.VK_ALT);
                    robot.keyPress(KeyEvent.VK_TAB);
                    long start = System.currentTimeMillis();
                    long end = start + 1000;
                    while (System.currentTimeMillis() < end)
                    {
                    }
                    robot.keyRelease(KeyEvent.VK_TAB);

                    start = System.currentTimeMillis();
                    end = start + 500;
                    while (System.currentTimeMillis() < end)
                    {
                    }
                    robot.keyPress(KeyEvent.VK_TAB);
                    start = System.currentTimeMillis();
                    end = start + 500;
                    while (System.currentTimeMillis() < end)
                    {
                    }
                    robot.keyRelease(KeyEvent.VK_TAB);
                    robot.keyRelease(KeyEvent.VK_TAB);
                    robot.keyRelease(KeyEvent.VK_ALT);
                    System.out.println("Finished");
                }
                catch (AWTException ex)
                {
                    System.out.println(ex.getMessage());
                }
            }
            
            else if(listen.equalsIgnoreCase("close window"))
            {
                try
                {
                    Robot robot = new Robot();
                        stopped = false;
                        robot.keyPress(KeyEvent.VK_ALT);
                        robot.keyPress(KeyEvent.VK_F4);
                        long start = System.currentTimeMillis();
                        long end = start + 1000;
                        while (System.currentTimeMillis() < end)
                        {
                        }
                        robot.keyRelease(KeyEvent.VK_F4);
                        robot.keyRelease(KeyEvent.VK_ALT);
                }
                catch (AWTException ex){}
            }
            
            else
            {
                if (listen.equalsIgnoreCase("stop"))
                {
                    System.out.println("stopped");
                    stopped = true;
                }
            }
            return tag;
        }
    }
