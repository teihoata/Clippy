/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Clippy;

import ClippyV2.ui.View.ClippyGui;
import ClippyV2.ui.View.NavMenu;
import db.clippy.SearchEngine.GoogleSearch;
import edu.cmu.sphinx.jsgf.JSGFGrammarException;
import edu.cmu.sphinx.jsgf.JSGFGrammarParseException;
import edu.cmu.sphinx.result.Result;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.speech.recognition.GrammarException;
import javax.swing.JOptionPane;
import org.json.JSONException;

/**
 *
 * @author Marzipan
 */
public class MyBehavior extends NewGrammarDialogNodeBehavior
{
    
    private ArrayList<String> list;
    private ArrayList<String> defaultMenuOptions;
    private String menu;
    private ClippyGui gui;
    private Map<String, String> searchResults;
    
    public MyBehavior()
    {
        //Constructor used for testing class methods
    }
    
    public MyBehavior(ClippyGui gui)
    {
        this.gui = gui;
        defaultMenuOptions = new ArrayList<>();
        defaultMenuOptions.add("read menu");
        defaultMenuOptions.add("main menu");
        defaultMenuOptions.add("computer control");
        defaultMenuOptions.add("surf the web");
        defaultMenuOptions.add("play music");
        defaultMenuOptions.add("watch movies");
        defaultMenuOptions.add("tell me the time");
    }
    
    public void setList(ArrayList<String> list)
    {
        this.list = list;
        addTitle();
        updateList();
    }
    
    public void setDefaultList()
    {
        list = new ArrayList<>();
        list.add("close clippy");
        list.add("read menu");
        list.add("surf the web");
        list.add("computer control");
        list.add("play music");
        list.add("watch movies");
        list.add("get directions");
        list.add("google search");
        list.add("tell me the time");
        try
        {
            gui.setCurrentBehavior(this);
            gui.setCurrentMenu(list);
        } catch (Exception e)
        {
            
        }
    }

    /**
     * Used for testing
     *
     * @return the default menu
     */
    private ArrayList<String> getDefaultList()
    {
        return defaultMenuOptions;
    }
    
    public void addDefaultListWithoutCurrent(ArrayList<String> list, String current)
    {
        for (String string : defaultMenuOptions)
        {
            if (!string.equalsIgnoreCase(current))
            {
                list.add(string);
            }            
        }
    }
    
    public void addTitle()
    {
        menu = "";
        list.add(0, menu);
    }
    
    public void updateList()
    {
        gui.setCurrentMenu(list);
    }
    
    public ArrayList getCurrentList()
    {
        return list;
    }
    
    public boolean processResult(String result)
    {
        return true;
    }
    
    public Map<String, String> getSearchResults()
    {
        System.out.println("returning searchResults " + searchResults.size());
        return this.searchResults;
    }

    /**
     * Executed when we are ready to recognize
     */
    public void onReady()
    {
        super.onReady();
        menu = "";
        help();
    }
    
    @Override
    public void onEntry() throws IOException, JSGFGrammarParseException, JSGFGrammarException
    {
        super.onEntry();
        gui.setCurrentBehavior(this);
        if (this.getName().equalsIgnoreCase("menu"))
        {
            setDefaultList();
        }
    }

    /**
     * Displays the help message for this node. Currently we display the name of
     * the node and the list of sentences that can be spoken.
     */
    protected void help()
    {
        menu = "";
        gui.setHeader(getGrammarName());
    }
    
    public String getCurrentMenuOptions()
    {
        return this.menu;
    }
    
    @Override
    public String onRecognizeByString(String result) throws GrammarException
    {
        String tag = getTagStringFromString(result);
        String listen = result;
        String end = "";
        if (tag != null)
        {
            if (tag.equals("exit"))
            {
                System.out.println("Goodbye! Thanks for visiting!\n");
                gui.exitClippy();
                end = "processed";
            }
            else if (tag.equals("menu"))
            {                
                setDefaultList();
                end = "menu";
            }
            else if (listen.equalsIgnoreCase("read menu"))
            {
                Thread speak = new Speak(list);
                speak.start();
                end = "processed";
            }
            else if (listen.equalsIgnoreCase("close active program"))
            {
                sendCommand("close");
                help();
                end = "processed";
            }
            else if (tag.equals("time"))
            {
                DateFormat dateFormat = new SimpleDateFormat("HH:mm");
                Calendar cal = Calendar.getInstance();
                String time = dateFormat.format(cal.getTime());
                Thread speak = new Speak(time);
                gui.setClippyTxt(time);
                speak.start();
                end = "processed";
            }
            else if(tag.equals("search"))
            {
                Thread searchSpeak = new Speak("Google Search. Enter what you want searched.");
        searchSpeak.start();
        String search = JOptionPane.showInputDialog(null, "Enter text to search");
        if (search != null && !search.isEmpty())
        {
            try
            {
                Thread speak = new Speak("Searching " + search);
                speak.start();
                gui.setClippyTxt("Searching " + search);
                gui.getSearchBehavior().setSearchList(GoogleSearch.getSearchResult(search));
                end = "search";
            } catch (IOException ex)
            {
            } catch (JSONException ex)
            {
            }
        }
            }
            else if (tag.equals("directions"))
            {
                NavMenu navMenu = new NavMenu();
                navMenu.pack();
                navMenu.setVisible(true);
                end = "processed";
            }
            else if (tag.startsWith("goto_"))
            {
                end = tag.replaceFirst("goto_", "");
            }
        }
        else
        {
            try
            {
                AePlayWave aw = new AePlayWave("./models/siri_notHeard.wav");
                aw.start();
            } catch (Exception e)
            {
                System.err.println("Couldn't find siri_notHeard.wac in models folder");
            }            
        }
        return end;
    }

    /**
     * Executed when the recognizer generates a result. Returns the name of the
     * next dialog node to become active, or null if we should stay in this node
     *
     * @param result the recongition result
     * @return the name of the next dialog node or null if control should remain
     * in the current node.
     */
    @Override
    public String onRecognize(Result result) throws GrammarException
    {
        System.out.println("Result = " + result);
        String tag = super.onRecognize(result);
        String listen = result.getBestFinalResultNoFiller();
        String onRecognizeByString = onRecognizeByString(listen);
        return onRecognizeByString;
    }

    /**
     * sends the command to be executed by ClippyAlpha executable
     *
     * @param command
     */
    private void sendCommand(String command)
    {
        try
        {
            Process process = new ProcessBuilder("./Windows Control/ClippyAlpha2.exe", command).start();
        } catch (IOException ex)        
        {
            System.out.println("Couldn't find ClippyAlpha2.exe in Windows Control in root");
        }
    }
}
