/*
 * Menu Behavior, is extended by all other menus
 * Extends the new grammar 
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import javax.speech.recognition.GrammarException;
import javax.swing.JOptionPane;

/**
 * @author Marcus Ball
 */
public class MyBehavior extends NewGrammarBehavior
{

    private ArrayList<String> list; //holds the current grammar menu
    private ArrayList<String> defaultMenuOptions; //default menu options
    private String menu; //holds the title of the menu
    private ClippyGui gui;
    private Map<String, String> searchResults; //Used by the google search results

    /**
     * Constructor
     *
     * @param ClippyGui gui
     */
    public MyBehavior(ClippyGui gui)
    {
        this.gui = gui;
        setDefaultMenuOptions();
    }

    /**
     * Sets the menu list options
     */
    private void setDefaultMenuOptions()
    {
        defaultMenuOptions = new ArrayList<>();
        defaultMenuOptions.add("read menu");
        defaultMenuOptions.add("main menu");
        defaultMenuOptions.add("computer control");
        defaultMenuOptions.add("surf the web");
        defaultMenuOptions.add("play music");
        defaultMenuOptions.add("watch movies");
        defaultMenuOptions.add("tell me the time");
    }

    /**
     * Sets the menu list
     *
     * @param list
     */
    public void setList(ArrayList<String> list)
    {
        this.list = list;
        addTitle();
        updateList();
    }

    /**
     * Sets the default main menu option
     */
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
            System.err.println("Cannot set the next menu option " + e.getMessage());
        }
    }

    /**
     * Adds the main menu options to the list without the current menu option
     *
     * @param list to add menu options too
     * @param current name of current option
     */
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

    /**
     * Adds the title to the list
     */
    public void addTitle()
    {
        menu = "";
        list.add(0, menu);
    }

    /**
     * Updates the current menu on the gui
     */
    public void updateList()
    {
        gui.setCurrentMenu(list);
    }

    /**
     * Returns the current menu list options
     *
     * @return
     */
    public ArrayList getCurrentList()
    {
        return list;
    }

    /**
     * process result which is overridden by child methods
     *
     * @param result
     * @return
     */
    @Override
    public boolean processResult(String result)
    {
        return false;
    }

    /**
     * maps out the search results
     *
     * @return
     */
    public Map<String, String> getSearchResults()
    {
        //Uncomment to read search results in terminal
        //System.out.println("returning searchResults " + searchResults.size());
        return this.searchResults;
    }

    /**
     * Executed when we are ready to recognize
     */
    @Override
    public void onReady()
    {
        super.onReady();
        menu = "";
        reset();
    }

    /**
     * Called upon entering the node
     *
     * @throws IOException
     * @throws JSGFGrammarParseException
     * @throws JSGFGrammarException
     */
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
     * Resets the current menu and the title
     */
    protected void reset()
    {
        menu = "";
        gui.setHeader(getGrammarName());
    }

    /**
     * gets the current menu
     *
     * @return
     */
    public String getCurrentMenuOptions()
    {
        return this.menu;
    }

    /**
     * Called by the list and after processing the voice. Processes the result
     *
     * @param result string version of the result
     * @return the next menu name or whether the data was processed
     * @throws GrammarException
     */
    @Override
    public String onRecognizeByString(String result) throws GrammarException
    {
        //Get the tag associated with the string
        String tag = getTagStringFromString(result);
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
            else if (result.equalsIgnoreCase("read menu"))
            {
                Thread speak = new Speak(list);
                speak.start();
                end = "processed";
            }
            else if (result.equalsIgnoreCase("close active program"))
            {
                sendCommand("close");
                reset();
                end = "processed";
            }
            else if (tag.equals("time"))
            {
                DateFormat dateFormat = new SimpleDateFormat("HH:mm");
                Calendar cal = Calendar.getInstance();
                String time = dateFormat.format(cal.getTime());
                Thread speak = new Speak(time);
                speak.start();
                gui.setClippyTxt(time);
                end = "processed";
            }
            else if (tag.equals("search"))
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
                    } catch (Exception ex)
                    {
                        System.err.println("Couldn't " + ex.getMessage());
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
     * Executed when the recognizer generates a result
     *
     * @param result the recongition result
     * @return the name of the next menu node or null if control should remain
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
     * used by multiple children
     * @param command
     */
    public void sendCommand(String command)
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
