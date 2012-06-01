/*
 * This class controls the google searching behavior
 */
package Clippy;

import ClippyV2.ui.View.ClippyGui;
import com.sun.speech.engine.recognition.BaseRecognizer;
import com.sun.speech.engine.recognition.BaseRuleGrammar;
import edu.cmu.sphinx.jsgf.JSGFGrammarException;
import edu.cmu.sphinx.jsgf.JSGFGrammarParseException;
import edu.cmu.sphinx.result.Result;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.speech.recognition.GrammarException;
import javax.speech.recognition.Rule;
import javax.speech.recognition.RuleGrammar;

/**
 *
 * @author Marcus Ball
 */
public class MyGoogleSearchBehavior extends MyBehavior
{

    private final int SKIP_RESULT = 7;
    ArrayList<String> processTitles; //holds the names of current running windows
    private int count; //number of rules within the dialog
    private ClippyGui gui;
    private Map<String, String> searchResult;
    private HashMap<String, Object> urlMap; //holds a url for each result
    private ArrayList<String> optionsList;
    private ArrayList<String> readingList; //holds the result string to read

    /**
     * Simple Constructor
     */
    public MyGoogleSearchBehavior(ClippyGui gui)
    {
        super(gui);
        this.gui = gui;
        count = 0;
    }
    
    /**
     * Sets the search list 
     * @param searchResult 
     */
    public void setSearchList(Map<String, String> searchResult)
    {
        this.searchResult = searchResult;
    }

    /**
     * Called on entry of google search
     * @throws IOException
     * @throws JSGFGrammarParseException
     * @throws JSGFGrammarException 
     */
    @Override
    public void onEntry() throws IOException, JSGFGrammarParseException, JSGFGrammarException
    {
        super.onEntry();
        urlMap = new HashMap<>();
        this.optionsList = new ArrayList<>();
        this.readingList = new ArrayList<>();
        BaseRecognizer recognizer = new BaseRecognizer(getGrammar().getGrammarManager());
        try
        {
            recognizer.allocate();
        } catch (Exception e)
        {
            System.err.println("Couldn't allocate recognizer resources");
        }
        RuleGrammar ruleGrammar = new BaseRuleGrammar(recognizer, getGrammar().getRuleGrammar());

        String ruleName = "search";
        Iterator it = searchResult.keySet().iterator();
        //Go through all results and add the result to the menu and add grammar
        //for each result
        int resultNum = 1;
        while (it.hasNext())
        {
            String key = it.next().toString();
            String option = "<html>" + resultNum + ".<br>Say \" open result " + resultNum + "\" to open this result<br><br>Description:<br> " + key + 
                    "<br><br>URL:<br>" + searchResult.get(key) + "<br>";
            optionsList.add(option);
            readingList.add("Result " + resultNum + ". Description. " + key + ". U R L " + searchResult.get(key) + ".");
            urlMap.put("" + resultNum, searchResult.get(key));
            addGrammar(ruleGrammar, ruleName, "open result " + resultNum);
            resultNum++;
        }
        //Add only two other options in the menu
        optionsList.add(0, "main menu");
        optionsList.add(1, "read results");
        addGrammar(ruleGrammar, ruleName, "read results");
        // now lets commit the changes
        getGrammar().commitChanges();
        this.setList(optionsList);
        gui.setCurrentBehavior(this);
    }

    /**
     * Adds the grammar rule to the list of dynamic grammar google search
     * behavior
     *
     * @param ruleGrammar
     * @param ruleName
     * @param grammarName
     */
    private void addGrammar(RuleGrammar ruleGrammar, String ruleName, String grammarName)
    {
        try
        {
            String newRuleName = ruleName + count;
            Rule newRule = null;
            newRule = ruleGrammar.ruleForJSGF(grammarName
                    + " { " + newRuleName + " }");
            ruleGrammar.setRule(newRuleName, newRule, true);
            ruleGrammar.setEnabled(newRuleName, true);
            count++;
        } catch (GrammarException ex)
        {
            System.err.println("Trouble with the grammar " + ex.getMessage());
        }
    }

    /**
     * Processes the result
     * @param result
     * @return 
     */
    @Override
    public boolean processResult(String result)
    {
        boolean processed = false;
        //if the result is spoken
        if (result.startsWith("open"))
        {
            Object get = urlMap.get(result.substring(result.indexOf("result") + SKIP_RESULT));
            String chosenWebsite = get.toString();
            try
            {
                java.awt.Desktop.getDesktop().browse(java.net.URI.create(chosenWebsite));
            } catch (IOException ex)
            {
                System.err.println("Couldn't execute website address");
            }
            processed = true;
        }
        //If the result it double clicked
        else if(result.startsWith("<html>"))
        {
            //Get the number of the result from the result string
            String selection = result.substring(result.indexOf(">") + 1, result.indexOf(">") + 2);
            //Get that url from the number result
            Object get = urlMap.get(String.valueOf(selection));
            String chosenWebsite = get.toString();
            try
            {
                java.awt.Desktop.getDesktop().browse(java.net.URI.create(chosenWebsite));
            } catch (IOException ex)
            {
                System.err.println("Couldn't execute website address");
            }
            processed = true;
        }
        else if(result.equalsIgnoreCase("read results"))
        {
            Thread speak = new Speak(readingList);
            speak.start();
            processed = true;
        }
        return processed;
    }

    /**
     * Called when Clippy recognises an input
     *
     * @param result
     * @return
     * @throws GrammarException
     */
    @Override
    public String onRecognize(Result result) throws GrammarException
    {
        String tag = super.onRecognize(result);
        String listen = result.getBestFinalResultNoFiller();
        if (processResult(listen))
        {
            tag = "processed";
        }
        return tag;
    }
}
