/*
 * This class controls the websites behavior
 */
package ClippyV2.ui.View;

import com.sun.speech.engine.recognition.BaseRecognizer;
import com.sun.speech.engine.recognition.BaseRuleGrammar;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import edu.cmu.sphinx.jsgf.JSGFGrammarException;
import edu.cmu.sphinx.jsgf.JSGFGrammarParseException;
import edu.cmu.sphinx.result.Result;
import java.io.*;
import java.util.ArrayList;
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
public class MyWebsiteBehavior extends MyBehavior {

    ArrayList<String> websiteList; //holds the names of current websites
    private int count; //number of rules within the dialog
    private ArrayList<String> menuList;
    private ClippyGui gui;

    /**
     * Simple Constructor
     */
    public MyWebsiteBehavior(ClippyGui gui)
    {
        super(gui);
        this.gui = gui;
        count = 1;
    }
    
    @Override
    public void onEntry() throws IOException, JSGFGrammarParseException, JSGFGrammarException
    {
        super.onEntry();
        menuList = new ArrayList<>();
        addDefaultListWithoutCurrent(menuList, "browse internet");
        BaseRecognizer recognizer = new BaseRecognizer(getGrammar().getGrammarManager());
        try {
            recognizer.allocate();
        } catch (Exception e) {
        }

        RuleGrammar ruleGrammar = new BaseRuleGrammar(recognizer, getGrammar().getRuleGrammar());
        websiteList = new ArrayList<>();
        try
        {     
            BufferedReader empdtil = new BufferedReader(new FileReader("./websites.txt"));
            String detail = "";
            while ((detail = empdtil.readLine()) != null)
            {
                try{
                    detail = detail.substring(detail.indexOf(".")+1, detail.indexOf("." , detail.indexOf(".") + 1));
                }
                catch(StringIndexOutOfBoundsException e){}
                websiteList.add(detail);
            }
            empdtil.close();
        }
        catch (IOException ex)
        {
            System.out.println(ex.getMessage());
        }

        String ruleName = "web";
            for (String app : websiteList)
            {
                if(!app.isEmpty() && app != null)
                {
                    menuList.add("open " + app);
                    addGrammar(ruleGrammar, ruleName, "open " + app);
                }    
            }
            menuList.add("add new website");
            menuList.add("remove website");
            menuList.add("scroll up");
            menuList.add("scroll down");
            menuList.add("minimize");
            addGrammar(ruleGrammar, ruleName, "add new website");
            addGrammar(ruleGrammar, ruleName, "remove website");
            addGrammar(ruleGrammar, ruleName, "close active program");
            addGrammar(ruleGrammar, ruleName, "scroll up");
            addGrammar(ruleGrammar, ruleName, "scroll down");
            addGrammar(ruleGrammar, ruleName, "minimize");
        
        // now lets commit the changes
        getGrammar().commitChanges();
        this.setList(menuList);
        gui.setCurrentBehavior(this);
        grammarChanged();
    }
    
    /**
     * Adds the grammar rule to the list of dynamic grammars for desktop behavior
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
            System.out.println("Trouble with the grammar ");
        }
    }
    
    /**
     * sends the command to be executed by ClippyAlpha executable
     * @param command 
     */
    private void sendCommand(String command)
    {
        try {
            Process process = new ProcessBuilder("./Windows Control/ClippyAlpha2.exe", command).start();
        } catch (IOException ex) 
        {
            System.out.println("Couldn't find ClippyAlpha2.exe in Windows Control in root");
        }
    }
    
    @Override
    public boolean processResult(String result)
    {
        boolean processed = false;
         if(result.equalsIgnoreCase("remove website"))
        {
            
        }
        else if(result.equalsIgnoreCase("add new website"))
        {
            try {
                FileWriter print = null;
                    String websiteName = (String) JOptionPane.showInputDialog(null, "Enter the address of your website starting with 'www.'", "Add Website", JOptionPane.INFORMATION_MESSAGE, null, null, "www.");
                    if(websiteName != null || !websiteName.isEmpty() || !websiteName.equalsIgnoreCase("www."))
                    {
                        File file = new File("./websites.txt");
                        print = new FileWriter(file, true);
                        BufferedWriter bufferWritter = new BufferedWriter(print);
                        bufferWritter.write("\n" + websiteName);
                        bufferWritter.close();
                        onEntry();
                        help();
                    }
            } catch (JSGFGrammarParseException ex) {
                Logger.getLogger(MyWebsiteBehavior.class.getName()).log(Level.SEVERE, null, ex);
            } catch (JSGFGrammarException ex) {
                Logger.getLogger(MyWebsiteBehavior.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(MyWebsiteBehavior.class.getName()).log(Level.SEVERE, null, ex);
            }
            processed = true;
        }
        else if (result.startsWith("open"))
        {
            BufferedReader empdtil = null;
            String url = "";
            String nameOfCurrentWebsite = "";
            try
            {
                empdtil = new BufferedReader(new FileReader("./websites.txt"));
                String detail = "";
            while ((detail = empdtil.readLine()) != null)
            {
                String currentWebsite = detail;
                
                try{
                    detail = detail.substring(detail.indexOf(".")+1, detail.indexOf("." , detail.indexOf(".") + 1));
                    
                    if(result.substring(result.indexOf("open")+5).equalsIgnoreCase(detail))
                    {
                        url = currentWebsite;
                        nameOfCurrentWebsite = detail;
                    }
                }
                catch(StringIndexOutOfBoundsException e){}
                websiteList.add(detail);
            }
            empdtil.close();
                try
                {
                    java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
                    String voiceName = "kevin16";
                    VoiceManager voiceManager = VoiceManager.getInstance();
                    Voice voice = voiceManager.getVoice(voiceName);
                    voice.allocate();
                    voice.speak("opened " + nameOfCurrentWebsite);
                    voice.deallocate();
                }
                catch (IOException ex)
                {
                    System.out.println("Couldn't find ClippyAlpha2.exe in Windows Control in root");
                }

            }
            catch (IOException ex)
            {
                Logger.getLogger(MyWebsiteBehavior.class.getName()).log(Level.SEVERE, null, ex);
            } 
            processed = true;
        }
         return processed;
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
        trace("Recognize result: " + result.getBestFinalResultNoFiller());
        if(processResult(listen))
            {
                tag = "processed";
            }
        return tag;
    }
}
