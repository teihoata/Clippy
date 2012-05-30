/*
 * This class controls the desktop behavior and how it interacts with the system
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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.speech.recognition.GrammarException;
import javax.speech.recognition.Rule;
import javax.speech.recognition.RuleGrammar;

/**
 *
 * @author Marcus Ball
 */
public class MyGoogleSearchBehavior extends MyBehavior
{

    ArrayList<String> processTitles; //holds the names of current running windows
    private int count; //number of rules within the dialog
    private ClippyGui gui;
    private Map<String, String> searchResult;
    private HashMap<String, Object> urlMap;
    private ArrayList<String> optionsList;
    private ArrayList<String> readingList;

    /**
     * Simple Constructor
     */
    public MyGoogleSearchBehavior(ClippyGui gui)
    {
        super(gui);
        this.gui = gui;
        count = 0;
    }
    
    public void setSearchList(Map<String, String> searchResult)
    {
        this.searchResult = searchResult;
    }

    @Override
    public void onEntry() throws IOException, JSGFGrammarParseException, JSGFGrammarException
    {
        super.onEntry();
        urlMap = new HashMap<String, Object>();
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
        int i = 1;
        while (it.hasNext())
        {
            String key = it.next().toString();
            URL url = new URL(searchResult.get(key));
            String urlStr = url.getHost();
            urlStr = urlStr.substring(urlStr.indexOf("."), urlStr.indexOf(".", urlStr.lastIndexOf(".")));
                
            urlStr = urlStr.replaceAll("[^A-Za-z&&[^']]", " ");
            
            String option = "<html>" + i + ".<br>Say \" open result " + i + "\" to open this result<br><br>Description:<br> " + key + 
                    "<br><br>URL:<br>" + searchResult.get(key) + "<br>";
            optionsList.add(option);
            readingList.add("Result " + i + ". Description. " + key + ". U R L " + searchResult.get(key) + ".");
            urlMap.put("" + i, searchResult.get(key));
            addGrammar(ruleGrammar, ruleName, "open result " + i);
            i++;
        }
        
        optionsList.add(0, "main menu");
        optionsList.add(1, "read results");
        addGrammar(ruleGrammar, ruleName, "read results");
        // now lets commit the changes
        getGrammar().commitChanges();
//        grammarChanged();
        this.setList(optionsList);
        gui.setCurrentBehavior(this);
    }

    /**
     * Adds the grammar rule to the list of dynamic grammars for desktop
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
            System.out.println("Trouble with the grammar ");
        }
    }

    @Override
    public boolean processResult(String result)
    {
        boolean processed = false;
        if (result.startsWith("open"))
        {
            Object get = urlMap.get(result.substring(result.indexOf("result") + 7));
            System.out.println("website: " + get.toString());
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
        else if(result.startsWith("<html>"))
        {
            String selection = result.substring(result.indexOf(">") + 1, result.indexOf(">") + 2);
            Object get = urlMap.get(String.valueOf(selection));
            System.out.println("website: " + get.toString());
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
        trace("Recognize result: " + result.getBestFinalResultNoFiller());
        if (processResult(listen))
        {
            tag = "processed";
        }
        return tag;
    }
}
