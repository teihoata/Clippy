/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.speech.recognition.GrammarException;
import javax.speech.recognition.Rule;
import javax.speech.recognition.RuleGrammar;

/**
 *
 * @author Marzipan
 */
public class MyDesktopBehavior extends MyBehavior {

    ArrayList<String> processTitles;
    private int count;

    public MyDesktopBehavior(){
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

        File file = new File("./processes.txt");
        if (file.exists())
        {
            file.delete();
        }
        processTitles = new ArrayList<>();
        try
        {
            System.out.println("Finding open windows");
            Process process = new ProcessBuilder("./Windows Control/ClippyAlpha2.exe", "get open windows").start();
            try
            {
                while (process.waitFor() != 0)
                {
                }
            }
            catch (InterruptedException ex)
            {
            }
            BufferedReader empdtil = new BufferedReader(new FileReader("./processes.txt"));
            String detail = new String();
            detail = empdtil.readLine();
            while ((detail = empdtil.readLine()) != null)
            {
                System.out.println(detail);
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
            addGrammar(ruleGrammar, ruleName, "minimize");
        
        // now lets commit the changes
        getGrammar().commitChanges();
        grammarChanged();
    }
    
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

    @Override
    public String onRecognize(Result result) throws GrammarException
    {
        //String tag = super.onRecognize(result);
        String listen = result.getBestFinalResultNoFiller();
        trace("Recognize result: " + result.getBestFinalResultNoFiller());
        String tag = "";
        if (listen.equalsIgnoreCase("main menu") || listen.equalsIgnoreCase("menu"))
        {
            tag = "menu";
        }
        else if(listen.equalsIgnoreCase("scroll up"))
        {
           try
                {
                    Process process = new ProcessBuilder("./Windows Control/ClippyAlpha2.exe", "scroll up").start();
                }
                catch (IOException ex)
                {
                } 
        }
        else if(listen.equalsIgnoreCase("scroll down"))
        {
            try
                {
                    Process process = new ProcessBuilder("./Windows Control/ClippyAlpha2.exe", "scroll down").start();
                }
                catch (IOException ex)
                {
                }   
        }
        else if(listen.equalsIgnoreCase("minimize"))
        {
            try
                {
                    Process process = new ProcessBuilder("./Windows Control/ClippyAlpha2.exe", "minimize").start();
                }
                catch (IOException ex)
                {
                } 
        }
        else if(listen.equalsIgnoreCase("close active program"))
        {
            try
                {
                    Process process = new ProcessBuilder("./Windows Control/ClippyAlpha2.exe", "close").start();
                }
                catch (IOException ex)
                {
                }
        }
        else if (listen.startsWith("switch to"))
        {
            BufferedReader empdtil = null;
            try
            {
                empdtil = new BufferedReader(new FileReader("./processes.txt"));
                String detail = new String();
                detail = empdtil.readLine();
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
                String voiceName = "kevin16";
                VoiceManager voiceManager = VoiceManager.getInstance();
                Voice voice = voiceManager.getVoice(voiceName);
                voice.allocate();
                voice.speak("opening " + switchApp);
                voice.deallocate();
                try
                {
                    Process process = new ProcessBuilder("./Windows Control/ClippyAlpha2.exe", "switch", "\"" + switchApp + "\"").start();
                }
                catch (IOException ex)
                {
                }

            }
            catch (IOException ex)
            {
                Logger.getLogger(MyDesktopBehavior.class.getName()).log(Level.SEVERE, null, ex);
            } finally
            {
                try
                {
                    empdtil.close();
                }
                catch (IOException ex)
                {
                    Logger.getLogger(MyDesktopBehavior.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return tag;
    }
}
