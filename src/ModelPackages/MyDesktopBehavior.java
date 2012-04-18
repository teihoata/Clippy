/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ModelPackages;

import com.sun.speech.engine.recognition.BaseRecognizer;
import com.sun.speech.engine.recognition.BaseRuleGrammar;
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

/**
 *
 * @author Marzipan
 */
public class MyDesktopBehavior extends MyBehavior {

    ArrayList<String> processTitles;

    @Override
    public void onEntry() throws IOException, JSGFGrammarParseException, JSGFGrammarException
    {
        super.onEntry();
        File file = new File("./processes.txt");
        if (file.exists())
        {
            file.delete();
        }
        processTitles = new ArrayList<>();
        try
        {
            System.out.println("Finding open windows");
            Process process = new ProcessBuilder("./Windows Control/OpenProg.exe", "get open windows").start();
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
                detail = detail.substring(detail.indexOf("exe")).substring(4);
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
//                WordCollection.addToMessage(processTitles);

        BaseRecognizer recognizer = new BaseRecognizer(getGrammar().getGrammarManager());
        try
        {
            recognizer.allocate();
        }
        catch (Exception e)
        {
        }

        RuleGrammar ruleGrammar = new BaseRuleGrammar(recognizer, getGrammar().getRuleGrammar());

        // now lets add a rule for each song in the play list

        String ruleName = "application";
        int count = 1;
        try
        {
            for (String app : processTitles)
            {
                String newRuleName = ruleName + count;
                Rule newRule = null;
                newRule = ruleGrammar.ruleForJSGF("switch to " + app
                        + " { " + newRuleName + " }");
                ruleGrammar.setRule(newRuleName, newRule, true);
                ruleGrammar.setEnabled(newRuleName, true);
                count++;
            }
        }
        catch (GrammarException ge)
        {
            System.out.println("Trouble with the grammar " + ge);
            throw new IOException("Can't add rules for playlist " + ge);
        }
        // now lets commit the changes
        getGrammar().commitChanges();
        grammarChanged();
    }

    @Override
    public String onRecognize(Result result) throws GrammarException
    {
        //String tag = super.onRecognize(result);
        String listen = result.getBestFinalResultNoFiller();
        String tag = "";
        if (listen.equalsIgnoreCase("main menu") || listen.equalsIgnoreCase("menu"))
        {
            tag = "menu";
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
                    detail = detail.substring(detail.indexOf("exe")).substring(4);
                    //detail = detail.replaceAll("[^A-Za-z0-9]", " ");
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
                    System.out.println(combined + " comparing to " + listen2);
                    
                    if (combined.equalsIgnoreCase(listen2))
                    {
                        System.out.println(detail);
                        switchApp = detail;
                    }
                }
                System.out.println("Switch to application : " + switchApp);
                try
                {
                    Process process = new ProcessBuilder("./Windows Control/OpenProg.exe", "switch to", "\"" + switchApp + "\"").start();
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
