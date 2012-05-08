/*
 * This class controls the desktop behavior and how it interacts with the system
 */
package ModelPackages;

import com.sun.speech.engine.recognition.BaseRecognizer;
import com.sun.speech.engine.recognition.BaseRuleGrammar;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import edu.cmu.sphinx.jsgf.JSGFGrammarException;
import edu.cmu.sphinx.jsgf.JSGFGrammarParseException;
import edu.cmu.sphinx.result.Result;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.speech.recognition.GrammarException;
import javax.speech.recognition.Rule;
import javax.speech.recognition.RuleGrammar;

/**
 *
 * @author Marcus Ball
 */
public class MyDesktopBehavior extends MyBehavior {

    ArrayList<String> processTitles; //holds the names of current running windows
    private int count; //number of rules within the dialog

    /**
     * Simple Constructor
     */
    public MyDesktopBehavior()
    {
        count = 1;
    }
    
    public void onEntry() throws IOException, JSGFGrammarParseException, JSGFGrammarException
    {
        super.onEntry();
        BaseRecognizer recognizer = new BaseRecognizer(getGrammar().getGrammarManager());
        try {
            recognizer.allocate();
        } catch (Exception e) {
        }

        RuleGrammar ruleGrammar = new BaseRuleGrammar(recognizer, getGrammar().getRuleGrammar());
        //use processes.txt to hold the current open windows
        File file = new File("./processes.txt");
        if (file.exists())
        {
            file.delete();
        }
        processTitles = new ArrayList<>();
        try
        {
            Process process = new ProcessBuilder("./Windows Control/ClippyAlpha2.exe", "get open windows").start();
            try
            {
                //wait for all processes to be found and written to file
                while (process.waitFor() != 0) {}
            }
            catch (InterruptedException ex)
            {
            }
            BufferedReader empdtil = new BufferedReader(new FileReader("./processes.txt"));
            String detail = new String();
            while ((detail = empdtil.readLine()) != null)
            {
                try{
                    detail = detail.substring(0, detail.indexOf("exe"));
                }
                catch(StringIndexOutOfBoundsException e){}
                String[] words = detail.split(" ");
                detail = "";
                for (int i = 0; i < 2; i++)
                {
                    detail += words[i] + " ";
                    if (words.length < 2)
                    {
                        break;
                    }
                }
                processTitles.add(detail);
            }
            empdtil.close();
        }
        catch (IOException ex)
        {
            System.out.println(ex.getMessage());
        }

        String ruleName = "application";
            for (String app : processTitles)
            {
                addGrammar(ruleGrammar, ruleName, "switch to " + app);
            }
            
            addGrammar(ruleGrammar, ruleName, "close active program");
            addGrammar(ruleGrammar, ruleName, "scroll up");
            addGrammar(ruleGrammar, ruleName, "scroll down");
            addGrammar(ruleGrammar, ruleName, "minimize window");
            addGrammar(ruleGrammar, ruleName, "maximize window");
        
        // now lets commit the changes
        getGrammar().commitChanges();
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
                    newRule = ruleGrammar.ruleForJSGF(grammarName
                            + " { " + newRuleName + " }");
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
        if (listen.startsWith("switch to"))
        {
            BufferedReader empdtil = null;
            try
            {
                empdtil = new BufferedReader(new FileReader("./processes.txt"));
                String detail = new String();
                String switchApp = "";
                while ((detail = empdtil.readLine()) != null)
                {
                    try{
                    detail = detail.substring(0 , detail.indexOf(".exe"));
                    }catch(StringIndexOutOfBoundsException e){}
                    String[] words = detail.split(" ");
                    String combined = "";
                for (int i = 0; i < 2; i++)
                {
                    combined += words[i] + " ";
                    if (words.length < 2)
                    {
                        break;
                    }
                }   
                combined = combined.trim();
                String listen2 = listen.substring(10).trim();
                    if (combined.equalsIgnoreCase(listen2))
                    {
                        switchApp = detail;
                    }
                }
                empdtil.close();
                try
                {
                    Process process = new ProcessBuilder("./Windows Control/ClippyAlpha2.exe", "switch", "\"" + switchApp + "\"").start();
                    String voiceName = "kevin16";
                    VoiceManager voiceManager = VoiceManager.getInstance();
                    Voice voice = voiceManager.getVoice(voiceName);
                    voice.allocate();
                    voice.speak("opened " + switchApp);
                    voice.deallocate();
                }
                catch (IOException ex)
                {
                    System.out.println("Couldn't find ClippyAlpha2.exe in Windows Control in root");
                }

            }
            catch (IOException ex)
            {
                Logger.getLogger(MyDesktopBehavior.class.getName()).log(Level.SEVERE, null, ex);
            } 
        }
        else if(listen.equalsIgnoreCase("minimize window"))
        {
            sendCommand("minimize");
        }
        else if(listen.equalsIgnoreCase("maximize window"))
        {
            sendCommand("maximize");
        }
        return tag;
    }
}
