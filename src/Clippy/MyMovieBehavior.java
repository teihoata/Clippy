/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Clippy;

import ClippyV2.ui.View.ClippyGui;
import com.sun.speech.engine.recognition.BaseRecognizer;
import com.sun.speech.engine.recognition.BaseRuleGrammar;
import db.clippy.dbConnect.DBOperator;
import edu.cmu.sphinx.jsgf.JSGFGrammarException;
import edu.cmu.sphinx.jsgf.JSGFGrammarParseException;
import edu.cmu.sphinx.result.Result;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.speech.recognition.GrammarException;
import javax.speech.recognition.Rule;
import javax.speech.recognition.RuleGrammar;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Marzipan
 */
public class MyMovieBehavior extends MyBehavior
{

    private List<String> playList = new ArrayList<String>();
    private Collection files;
    private File selectedFile;
    private ClippyGui gui;
    private DBOperator dbo = new DBOperator();// added by Ray

    /**
     * Creates a music behavior
     */
    public MyMovieBehavior(ClippyGui gui) throws IOException
    {
        super(gui);
        this.gui = gui;
        playList.add("pause");
        playList.add("volume up");
        playList.add("volume down");
        dbo.removeAllMovie();
        File file = new File("C:\\Users\\" + System.getProperty("user.name") + "\\Videos\\");
        String[] extensions =
        {
            "avi", "wmv", "mp4", "divx", "mkv"
        };
        files = FileUtils.listFiles(file, extensions, true);
        for (Iterator iterator = files.iterator(); iterator.hasNext();)
        {
            File file1 = (File) iterator.next();
            String fileName = file1.getName().substring(0, file1.getName().indexOf('.'));
            fileName = fileName.replaceAll("[^A-Za-z]", " ");  
            playList.add(fileName);
        }
        dbo.storeMovie(playList);
    }

    /**
     * Executed when we enter this node. Displays the active grammar
     *
     * @throws JSGFGrammarException
     * @throws JSGFGrammarParseException
     */
    public void onEntry() throws IOException, JSGFGrammarParseException, JSGFGrammarException
    {
        super.onEntry();
        // now lets add our custom songs from the play list
        // First, get the JSAPI RuleGrammar
        BaseRecognizer recognizer = new BaseRecognizer(getGrammar().getGrammarManager());
        try
        {
            recognizer.allocate();
        } catch (Exception e)
        {
        }

        RuleGrammar ruleGrammar = new BaseRuleGrammar(recognizer, getGrammar().getRuleGrammar());

        // now lets add a rule for each song in the play list
        ArrayList<String> menuList = new ArrayList<>();
        String ruleName = "movies";
        int count = 1;
        try
        {

            addDefaultListWithoutCurrent(menuList, "watch movies");
            for (String movie : playList)
            {
                String newRuleName = ruleName + count;
                Rule newRule = null;
                if (movie.equalsIgnoreCase("pause") || movie.equalsIgnoreCase("volume up") || movie.equalsIgnoreCase("volume down") || movie.equalsIgnoreCase("maximize window") || movie.equalsIgnoreCase("minimize window"))
                {
                    menuList.add(movie);
                    newRule = ruleGrammar.ruleForJSGF(movie
                            + " { " + newRuleName + " }");
                }
                else
                {
                    menuList.add("watch " + movie);
                    newRule = ruleGrammar.ruleForJSGF("watch " + movie
                            + " { " + newRuleName + " }");
                }
                ruleGrammar.setRule(newRuleName, newRule, true);
                ruleGrammar.setEnabled(newRuleName, true);
                count++;
            }
        } catch (GrammarException ge)
        {
            System.out.println("Trouble with the grammar " + ge);
            throw new IOException("Can't add rules for playlist " + ge);
        }
        // now lets commit the changes
        getGrammar().commitChanges();
        this.setList(menuList);
        gui.setCurrentBehavior(this);
    }

    @Override
    public boolean processResult(String result)
    {
        boolean processed = false;
        if (result.startsWith("watch "))
        {
            String substring = result.substring(6);
            System.out.println(substring);
            selectedFile = null;
            for (Iterator iterator = files.iterator(); iterator.hasNext();)
            {
                File file1 = (File) iterator.next();
                String fileName = file1.getName().substring(0, file1.getName().indexOf('.')).replaceAll("[^A-Za-z]", " ").replaceAll("\\s+", " ").trim();
                if (fileName.equalsIgnoreCase(substring))
                {
                    gui.setClippyTxt("opening " + file1.getName());
                    selectedFile = file1;
                    processed = true;

                }
            }
            try
            {
                System.out.println("File Path : " + selectedFile.getAbsoluteFile() + "\\");
                Desktop.getDesktop().open(new File(selectedFile.getAbsoluteFile() + "\\"));
            } catch (Exception ex)
            {
                System.out.println(ex.getMessage() + "\n Will now try to open using windows media player executeable in program files x86");
                try
                {
                    Process process = new ProcessBuilder("C:\\Program Files (x86)\\Windows Media Player\\wmplayer.exe", selectedFile.getAbsolutePath()).start();
                } catch (IOException ex1)
                {
                    System.out.println("Didn't work :( Trying one last option");
                    try
                    {
                        Process process = new ProcessBuilder("C:\\Program Files\\Windows Media Player\\wmplayer.exe", selectedFile.getAbsolutePath()).start();
                    } catch (IOException ex2)
                    {
                        System.out.println("Sorry cannot open music files");
                    }
                }

            }
        }
        else
        {
            if (result.equalsIgnoreCase("pause"))
            {
                try
                {
                    Process process = new ProcessBuilder("./Windows Control/UniversalMediaRemote.exe", "Play").start();
                } catch (IOException ex)
                {
                }
                processed = true;
            }
            else
            {
                if (result.equalsIgnoreCase("volume up"))
                {
                    try
                    {
                        for (int i = 0; i < 10; i++)
                        {
                            Process process = new ProcessBuilder("./Windows Control/UniversalMediaRemote.exe", "VUp").start();
                        }

                    } catch (IOException ex)
                    {
                    }
                    processed = true;
                }
                else
                {
                    if (result.equalsIgnoreCase("volume down"))
                    {
                        try
                        {
                            for (int i = 0; i < 10; i++)
                            {
                                Process process = new ProcessBuilder("./Windows Control/UniversalMediaRemote.exe", "VDown").start();
                            }

                        } catch (IOException ex)
                        {
                        }
                        processed = true;
                    }
                }
            }
        }
        return processed;
    }

    @Override
    public String onRecognize(Result result) throws GrammarException
    {
        String next = super.onRecognize(result);;
        String listen = result.getBestFinalResultNoFiller();
        if (processResult(listen))
        {
            next = "processed";
        }
        return next;
    }
}
