/*
 * This class controls the websites behavior
 */
package Clippy;

import ClippyV2.ui.View.ClippyGui;
import com.sun.speech.engine.recognition.BaseRecognizer;
import com.sun.speech.engine.recognition.BaseRuleGrammar;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import db.clippy.dbConnect.DBOperator;
import edu.cmu.sphinx.jsgf.JSGFGrammarException;
import edu.cmu.sphinx.jsgf.JSGFGrammarParseException;
import edu.cmu.sphinx.result.Result;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.speech.recognition.GrammarException;
import javax.speech.recognition.Rule;
import javax.speech.recognition.RuleGrammar;
import javax.swing.JOptionPane;

/**
 *
 * @author Marcus Ball
 */
public class MyWebsiteBehavior extends MyBehavior
{

    ArrayList<String> websiteList; //holds the names of current websites
    private int count; //number of rules within the dialog
    private ArrayList<String> menuList;
    // added by Ray
    private DBOperator dbo = new DBOperator();

    /**
     * Simple Constructor
     */
    public MyWebsiteBehavior(ClippyGui gui)
    {
        super(gui);
        count = 1;
    }

    public void onEntry() throws IOException, JSGFGrammarParseException, JSGFGrammarException
    {
        super.onEntry();
        menuList = new ArrayList<>();
        addDefaultListWithoutCurrent(menuList, "surf the web");
        BaseRecognizer recognizer = new BaseRecognizer(getGrammar().getGrammarManager());
        try
        {
            recognizer.allocate();
        } catch (Exception e)
        {
        }

        RuleGrammar ruleGrammar = new BaseRuleGrammar(recognizer, getGrammar().getRuleGrammar());
        websiteList = new ArrayList<>();
        List<String> websites = dbo.getWebSites();  // added by Ray

        String detail = "";
        Iterator<String> it = websites.iterator();
        while (it.hasNext())
        {
            detail = it.next();
            try
            {
                detail = detail.substring(detail.indexOf(".") + 1, detail.indexOf(".", detail.indexOf(".") + 1));
            } catch (StringIndexOutOfBoundsException e)
            {
            }
            websiteList.add(detail);
        }

        String ruleName = "web";
        for (String app : websiteList)
        {
            if (!app.isEmpty() && app != null)
            {
                menuList.add("open " + app);
                addGrammar(ruleGrammar, ruleName, "open " + app);
            }
        }
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
            newRule = ruleGrammar.ruleForJSGF(grammarName);
            ruleGrammar.setRule(newRuleName, newRule, true);
            ruleGrammar.setEnabled(newRuleName, true);
            count++;
        } catch (GrammarException ex)
        {
            System.out.println("Trouble with the grammar ");
        }
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

    public boolean processResult(String result)
    {
        boolean processed = false;
        List<String> websites = dbo.getWebSites();
        if (result.equalsIgnoreCase("remove website"))
        {
            processed = true;
        }
        else if (result.equalsIgnoreCase("add new website"))
        {
            try
            {
                String websiteName = (String) JOptionPane.showInputDialog(null, "Enter the address of your website starting with 'www.'", "Add Website", JOptionPane.INFORMATION_MESSAGE, null, null, "www.");
                if (websiteName != null || !websiteName.isEmpty() || !websiteName.equalsIgnoreCase("www."))
                {
                    websites.add(websiteName);
                    dbo.removeAllWebsite();
                    dbo.storeWebSite(websites);
                    onEntry();
                    processed = true;
                    help();
                }
            } catch (JSGFGrammarParseException ex)
            {
                Logger.getLogger(MyWebsiteBehavior.class.getName()).log(Level.SEVERE, null, ex);
            } catch (JSGFGrammarException ex)
            {
                Logger.getLogger(MyWebsiteBehavior.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex)
            {
                Logger.getLogger(MyWebsiteBehavior.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if (result.startsWith("open"))
        {
            String url = "";
            String nameOfCurrentWebsite = "";
            String detail = "";
            Iterator<String> it = websites.iterator();
            while (it.hasNext())
            {
                detail = it.next();
                String currentWebsite = detail;

                try
                {
                    detail = detail.substring(detail.indexOf(".") + 1, detail.indexOf(".", detail.indexOf(".") + 1));

                    if (result.substring(result.indexOf("open") + 5).equalsIgnoreCase(detail))
                    {
                        url = currentWebsite;
                        nameOfCurrentWebsite = detail;
                    }
                } catch (StringIndexOutOfBoundsException e)
                {
                }
                websiteList.add(detail);
            }
            try
            {
                java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
                String voiceName = "kevin16";
                VoiceManager voiceManager = VoiceManager.getInstance();
                Voice voice = voiceManager.getVoice(voiceName);
                voice.allocate();
                voice.speak("opened " + nameOfCurrentWebsite);
                voice.deallocate();
            } catch (IOException ex)
            {
                System.out.println("Couldn't find ClippyAlpha2.exe in Windows Control in root");
            }
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
