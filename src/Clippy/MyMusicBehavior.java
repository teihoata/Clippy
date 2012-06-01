/*
 * Class that handles the music behavior of Clippy
 */
package Clippy;

import ClippyV2.ui.View.ClippyGui;
import com.sun.speech.engine.recognition.BaseRecognizer;
import com.sun.speech.engine.recognition.BaseRuleGrammar;
import edu.cmu.sphinx.jsgf.JSGFGrammarException;
import edu.cmu.sphinx.jsgf.JSGFGrammarParseException;
import edu.cmu.sphinx.result.Result;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import javax.speech.EngineException;
import javax.speech.EngineStateError;
import javax.speech.recognition.GrammarException;
import javax.speech.recognition.Rule;
import javax.speech.recognition.RuleGrammar;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Marcus Ball
 */
public class MyMusicBehavior extends MyBehavior
{

    private ArrayList<String> songList; //holds the list of menu options
    private Collection files; //stores all music files
    private File selectedFile; //current selected file
    private ClippyGui gui;

    /**
     * Creates a music behavior
     */
    public MyMusicBehavior(ClippyGui gui) throws IOException
    {
        super(gui);
        this.gui = gui;
        songList = new ArrayList<String>();
        songList.add("pause");
        songList.add("play next song");
        songList.add("volume up");
        songList.add("volume down");
        File file = new File("C:\\Users\\" + System.getProperty("user.name") + "\\Music\\");
        String[] extensions =
        {
            "mp3", "wma", "aac"
        };
        files = FileUtils.listFiles(file, extensions, true);
        for (Iterator iterator = files.iterator(); iterator.hasNext();)
        {
            File file1 = (File) iterator.next();
            String fileName = file1.getName().substring(0, file1.getName().indexOf('.'));
            fileName = fileName.replaceAll("[^A-Za-z&&[^']]", " ");
            songList.add(fileName);
        }
    }

    /**
     * Executed when we enter this node. Displays the active grammar
     *
     * @throws JSGFGrammarException
     * @throws JSGFGrammarParseException
     */
    @Override
    public void onEntry() throws IOException, JSGFGrammarParseException, JSGFGrammarException
    {
        super.onEntry();
        BaseRecognizer recognizer = new BaseRecognizer(getGrammar().getGrammarManager());
        try
        {
            recognizer.allocate();
        } catch (EngineException | EngineStateError e)
        {
            System.err.println("Couldn't allocate recognizer " + e.getMessage());
        }
        RuleGrammar ruleGrammar = new BaseRuleGrammar(recognizer, getGrammar().getRuleGrammar());

        // now lets add a rule for each song in the play list
        String ruleName = "song";
        int count = 1;
        ArrayList<String> listOfOptions = new ArrayList<>();
        //Add default main options to the top of the list
        addDefaultListWithoutCurrent(listOfOptions, "play music");
        ArrayList<String> musicList = new ArrayList<>();
        try
        {
            for (String song : songList)
            {
                String newRuleName = ruleName + count;
                Rule newRule = null;
                if (song.equalsIgnoreCase("pause") || song.equalsIgnoreCase("volume up") || song.equalsIgnoreCase("volume down"))
                {
                    listOfOptions.add(song);
                    newRule = ruleGrammar.ruleForJSGF(song
                            + " { " + newRuleName + " }");
                }
                else
                {
                    if (song.equalsIgnoreCase("play next song"))
                    {
                        listOfOptions.add(song);
                        newRule = ruleGrammar.ruleForJSGF(song
                                + " { " + newRuleName + " }");
                    }
                    else
                    {
                        musicList.add("play " + song);
                        newRule = ruleGrammar.ruleForJSGF("play " + song
                                + " { " + newRuleName + " }");
                    }
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
        Collections.sort(musicList);
        listOfOptions.addAll(musicList);
        this.setList(listOfOptions);
        gui.setCurrentBehavior(this);
    }

    /**
     * Processes the result given
     * @param result
     * @return 
     */
    @Override
    public boolean processResult(String result)
    {
        boolean processed = false;
        if (result.startsWith("play") && !result.equalsIgnoreCase("play next song"))
        {
            String substring = result.substring(5);
            processed = playMusic(substring);
        }
        else if (result.equalsIgnoreCase("pause"))
        {
            try
            {
                gui.setClippyTxt("pause");
                new ProcessBuilder("./Windows Control/ClippyAlpha2.exe", "Play").start();
            } catch (IOException ex)
            {
                System.err.println("Couldn't find Media Control Program in root/windows control/");
            }
            processed = true;
        }
        else if (result.equalsIgnoreCase("volume up"))
        {
            try
            {
                gui.setClippyTxt("increasing volume");
                new ProcessBuilder("./Windows Control/ClippyAlpha2.exe", "VUp").start();
            } catch (IOException ex)
            {
                System.err.println("Couldn't find Remote.exe file");
            }
            processed = true;
        }
        else if (result.equalsIgnoreCase("volume down"))
        {
            gui.setClippyTxt("decreasing volume");
            try
            {
                new ProcessBuilder("./Windows Control/ClippyAlpha2.exe", "VDown").start();
            } catch (IOException ex)
            {
            }
            processed = true;
        }
        else if (result.equalsIgnoreCase("play next song"))
        {
            gui.setClippyTxt("playing next song");
            ArrayList<File> list = new ArrayList(files);
            selectedFile = list.get((int) (Math.random() * list.size()));
            String fileName = selectedFile.getName().substring(0, selectedFile.getName().indexOf('.')).replaceAll("[^A-Za-z&&[^']]", " ").replaceAll("\\s+", " ").trim();
            processed = playMusic(fileName);
        }
        return processed;
    }

    /**
     * plays the given file name
     * @param songToPlay
     * @return 
     */
    private boolean playMusic(String songToPlay)
    {
        boolean processed = false;
        selectedFile = null;
        for (Iterator iterator = files.iterator(); iterator.hasNext();)
        {
            File file1 = (File) iterator.next();
            String fileName = file1.getName().substring(0, file1.getName().indexOf('.')).replaceAll("[^A-Za-z&&[^']]", " ").replaceAll("\\s+", " ").trim();
            if (fileName.equalsIgnoreCase(songToPlay))
            {
                gui.setClippyTxt("opening " + file1.getName());
                selectedFile = file1;
                processed = true;
            }
        }
        try
        {
            Desktop.getDesktop().open(new File(selectedFile.getAbsoluteFile() + "\\"));
            //Problems with windows media player not allowing access to playing songs
            //had to resort to using it directly from the executables location
        } catch (Exception ex)
        {
            System.err.println(ex.getMessage() + "\n Will now try to open using windows media player executeable in program files x86");
            try
            {
                Process process = new ProcessBuilder("C:\\Program Files (x86)\\Windows Media Player\\wmplayer.exe", selectedFile.getAbsolutePath()).start();
            } catch (IOException ex1)
            {
                System.err.println("Didn't work, Trying one last option");
                try
                {
                    Process process = new ProcessBuilder("C:\\Program Files\\Windows Media Player\\wmplayer.exe", selectedFile.getAbsolutePath()).start();
                } catch (IOException ex2)
                {
                    System.err.println("Sorry cannot open music files");
                }
            }
        }
        return processed;
    }

    @Override
    public String onRecognize(Result result) throws GrammarException
    {
        String next = super.onRecognize(result);
        String listen = result.getBestFinalResultNoFiller();
        if (processResult(listen))
        {
            next = "processed";
        }
        return next;
    }
}
