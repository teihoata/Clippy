/*
 * This class controls the websites behavior
 */
package Clippy;

import ClippyV2.ui.View.ClippyGui;
import ClippyV2.ui.View.WebMenu;
import com.sun.speech.engine.recognition.BaseRecognizer;
import com.sun.speech.engine.recognition.BaseRuleGrammar;
import db.clippy.dbConnect.DBOperator;
import edu.cmu.sphinx.jsgf.JSGFGrammarException;
import edu.cmu.sphinx.jsgf.JSGFGrammarParseException;
import edu.cmu.sphinx.result.Result;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.speech.EngineException;
import javax.speech.EngineStateError;
import javax.speech.recognition.GrammarException;
import javax.speech.recognition.Rule;
import javax.speech.recognition.RuleGrammar;
import javax.swing.JOptionPane;
import org.apache.commons.validator.UrlValidator;

/**
 *
 * @author Marcus Ball
 * @edited by Gavin Chan & Rayleigh Zhang
 */
public class MyWebsiteBehavior extends MyBehavior
{

    private final int OPENGAP = 5;
    ArrayList<String> websiteList; //holds the names of current websites
    private int count; //number of rules within the dialog
    private ArrayList<String> menuList; //list of strings for the web menu
    private boolean processed; //tells the program that an action was processed
    private ClippyGui gui;
    // added by Ray
    private DBOperator dbo = new DBOperator();

    /**
     * Simple Constructor
     */
    public MyWebsiteBehavior(ClippyGui gui)
    {
        super(gui);
        this.gui = gui;
        count = 1;
    }

    /**
     * OnEntry called when this menu node is entered i.e. selected
     * @throws IOException
     * @throws JSGFGrammarParseException
     * @throws JSGFGrammarException 
     */
    @Override
    public void onEntry() throws IOException, JSGFGrammarParseException, JSGFGrammarException
    {
        super.onEntry();
        menuList = new ArrayList<>();
        //Add other menu options to the current list
        addDefaultListWithoutCurrent(menuList, "surf the web");
        //Set up the recognizer
        BaseRecognizer recognizer = new BaseRecognizer(getGrammar().getGrammarManager());
        try
        {
            recognizer.allocate();
        } catch (EngineException | EngineStateError ex)
        {
            System.err.println("Couldn't initiate recognizer engine");
        }
        //Initiate grammar rules for web behavior
        RuleGrammar ruleGrammar = new BaseRuleGrammar(recognizer, getGrammar().getRuleGrammar());
        
        websiteList = new ArrayList<>();
        
        List<String> websites = dbo.getWebSites();  // added by Ray

        Iterator<String> it = websites.iterator();
        while (it.hasNext())
        {
            String detail = it.next();
            try
            {
                //Get the website name between the www. and the .com/org/etc
                detail = detail.substring(detail.indexOf(".") + 1, detail.indexOf(".", detail.indexOf(".") + 1));
            } catch (StringIndexOutOfBoundsException e)
            {
            }
            websiteList.add(detail);
        }

        //Dynamically create the new rules for web behavior
        String ruleName = "web";
        for (String app : websiteList)
        {
            if (!app.isEmpty() && app != null)
            {
                menuList.add("open " + app);
                addGrammar(ruleGrammar, ruleName, "open " + app);
            }
        }
        //Add unique menu options to the menu list and understandable grammar
        menuList.add("add new website");
        menuList.add("remove website");
        menuList.add("scroll up");
        menuList.add("scroll down");
        addGrammar(ruleGrammar, ruleName, "add new website");
        addGrammar(ruleGrammar, ruleName, "remove website");
        addGrammar(ruleGrammar, ruleName, "close active program");
        addGrammar(ruleGrammar, ruleName, "scroll up");
        addGrammar(ruleGrammar, ruleName, "scroll down");

        // now lets commit the changes
        getGrammar().commitChanges();
        this.setList(menuList);
    }

   /**
     * Adds the grammar rule to the list of dynamic grammars for website behavior
     * @param ruleGrammar
     * @param ruleName
     * @param grammarName 
     */
    private void addGrammar(RuleGrammar ruleGrammar, String ruleName, String grammarName)
    {
        try {
            String newRuleName = ruleName + count;
            Rule newRule = null;
            newRule = ruleGrammar.ruleForJSGF(grammarName);
            ruleGrammar.setRule(newRuleName, newRule, true);
            ruleGrammar.setEnabled(newRuleName, true);
            count++;
        } catch (GrammarException ex) {
            System.err.println("Trouble with the grammar ");
        }
    }
    
    /**
     * Processes the result of the WebSite command of the user 
     * @param result the users command 
     * @return true if the process is successful otherwise false
     */
    @Override
    public boolean processResult(String result)
    {
        processed = false;
        List<String> websites = dbo.getWebSites();
        if(result.equalsIgnoreCase("remove website"))
         {
             removeWebsite(websites);    
         }
        
        else if(result.equalsIgnoreCase("add new website"))
        {
            addWebsite(websites);
        }
        else if (result.startsWith("open"))
        {
            openWebsite(result,websites);
        }
        else if(result.equalsIgnoreCase("scroll up"))
        {
           sendCommand("scroll up");
           processed = true;
        }
        else if(result.equalsIgnoreCase("scroll down"))
        {
            sendCommand("scroll down");
            processed = true;
        }
         return processed;
    }
    
    /**
     * Removes the website name from the database
     */
    public void removeWebsiteFromList(String website)
    {
        dbo.removeWebsite(website);
        try
        {
            onEntry();
        } catch (IOException | JSGFGrammarParseException | JSGFGrammarException ex)
        {
            System.err.println("Error entering the current website list");
        }
    }

    /**
     * Creates an input dialog that displays the saved WebSites from the database 
     * and allows the user to remove a WebSite from the database by inputting the link 
     * @param websites the WebSite database  
     */
    private void removeWebsite(List websites){
        
        WebMenu web = new WebMenu(websites, this);
        web.pack();
        web.setVisible(true);
        processed = true;
    }
    
    /**
     * Creates an input dialog that allows the user to add a WebSite to the database by inputting the link 
     * @param websites the WebSite database  
     */
    private void addWebsite(List websites)
    {
        String websiteName = (String) JOptionPane.showInputDialog(null, "Enter the address of your website starting with 'www.'", "Add Website", JOptionPane.INFORMATION_MESSAGE, null, null, "www.");
        //Add www to start if not already there
        if (websiteName.indexOf("www.") == -1)
        {
            websiteName = "www." + websiteName;
        }
        //Add http to start
        if (websiteName.indexOf("http://") == -1)
        {
            websiteName = "http://" + websiteName;
        }
        //testing schemes
        String[] schemes =
        {
            "http", "https"
        };
        //Validates the url using commons validator library
        UrlValidator urlValidator = new UrlValidator(schemes);
        if (urlValidator.isValid(websiteName))
        {
            try
            {
                //Don't add duplicates
                if(!websites.contains(websiteName))
                {
                    websites.add(websiteName);
                    dbo.removeAllWebsite();
                    dbo.storeWebSite(websites);
                    onEntry();
                    processed = true;
                    reset();
                }
                else
                {
                    gui.setClippyTxt("Address already Exists");
                }
            } catch (IOException ex)
            {
                Logger.getLogger(MyWebsiteBehavior.class.getName()).log(Level.SEVERE, null, ex);
            } catch (JSGFGrammarParseException ex)
            {
                Logger.getLogger(MyWebsiteBehavior.class.getName()).log(Level.SEVERE, null, ex);
            } catch (JSGFGrammarException ex)
            {
                Logger.getLogger(MyWebsiteBehavior.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else
        {
            gui.setClippyTxt("Invalid Address");
        }

    }
    /**
     * Searches through the WebSite database for the chosen WebSite and opens it
     * @param result the users chosen WebSite 
     * @param websites the WebSite database  
     */
    public void openWebsite(String result, List websites){
        String url = "";
        String nameOfCurrentWebsite = "";
        String detail = "";
        Iterator<String> it = websites.iterator();
        while (it.hasNext())
        {
            try
            {
                detail = it.next();
                String currentWebsite = detail;
                URL urlAddress = new URL(detail);
                try {
                    detail = detail.substring(detail.indexOf(".")+1, detail.indexOf("." , detail.indexOf(".") + 1));
                    if(result.substring(result.indexOf("open") + OPENGAP).equalsIgnoreCase(detail))
                    {
                        url = currentWebsite;
                        nameOfCurrentWebsite = detail;
                    }
                }
                    
                catch(StringIndexOutOfBoundsException e){
                
                }
                websiteList.add(detail);
            } catch (MalformedURLException ex)
            {
                Logger.getLogger(MyWebsiteBehavior.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try
        {
            java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
            Thread speak = new Speak("opened " + nameOfCurrentWebsite);
            speak.start();
        }
        catch (IOException ex)
        {
            System.err.println("Couldn't find ClippyAlpha2.exe in Windows Control in root");
        }
        processed = true;  
    }

    /**
     * Called when Clippy recognises an input
     * @param result
     * @return
     * @throws GrammarException 
     */
    @Override
    public String onRecognize(Result result) throws GrammarException
    {
        String tag = super.onRecognize(result);
        String listen = result.getBestFinalResultNoFiller();
        if(processResult(listen))
        {
            tag = "processed";
        }
        return tag;
    }
}
