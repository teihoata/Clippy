/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ClippyV2.ui.View;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import db.clippy.SearchEngine.GoogleSearch;
import edu.cmu.sphinx.jsgf.JSGFGrammarException;
import edu.cmu.sphinx.jsgf.JSGFGrammarParseException;
import edu.cmu.sphinx.result.Result;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.speech.recognition.GrammarException;
import javax.speech.recognition.RuleParse;
import javax.swing.JOptionPane;
import org.json.JSONException;

/**
 *
 * @author Marzipan
 */
public class MyBehavior extends NewGrammarDialogNodeBehavior {
    
    private ArrayList<String> list;
    private ArrayList<String> defaultMenuOptions;
    
    public static class Speak extends Thread {

        private String sentence;
    
    public Speak(String sentence)
    {
        this.sentence = sentence;
    }
    
    @Override
    public void run() {
        String voiceName = "kevin16";
        VoiceManager voiceManager = VoiceManager.getInstance();
        Voice voice = voiceManager.getVoice(voiceName);
        voice.allocate();
        voice.speak(sentence);
        voice.deallocate();
    }
    }

        private Collection<String> sampleUtterances;
        private String menu;
        private ClippyGui gui;
        
        public MyBehavior(ClippyGui gui)
        {
            this.gui = gui;
            defaultMenuOptions = new ArrayList<>();
            defaultMenuOptions.add("main menu");
            defaultMenuOptions.add("computer control");
            defaultMenuOptions.add("browse internet");
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
            list.add(" ======== " + "menu" + " =======\n");
            list.add("close clippy");
            list.add("browse internet");
            list.add("computer control");
            list.add("play music");
            list.add("watch movies");
            list.add("get directions");
            list.add("google search");
            list.add("tell me the time");
            updateList();
            gui.setCurrentBehavior(this);
        }
        
        
        public void addDefaultListWithoutCurrent(ArrayList<String> list, String current)
        {
            for (String string : defaultMenuOptions) {
                if(!string.equalsIgnoreCase(current))
                {
                    list.add(string);
                }   
            }
        }
        
        public void addTitle()
        {
            menu = " ======== " + getGrammarName() + " =======\n";
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
            setDefaultList();
            
        }
        
        

        /**
         * Displays the help message for this node. Currently we display the
         * name of the node and the list of sentences that can be spoken.
         */
        protected void help()
        {
            menu = " ======== " + getGrammarName() + " =======\n";
            //gui.setCurrentMenu(menu);
        }
        
        public String getCurrentMenuOptions()
        {
            return this.menu;
        }
        
        public void processResult(String result)
        {
        try {
            onRecognizeByString(result);
        } catch (GrammarException ex) {
            Logger.getLogger(MyBehavior.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
            
        public String onRecognizeByString(String result) throws GrammarException
        {
            System.out.println("Result = " + result);
            String tag = getTagStringFromString(result);
            System.out.println("Tag = " + tag);
            String listen = result;
            if (tag != null)
            {

                System.out.println("\n "
                        + result + '\n');

                if (tag.equals("exit"))
                {

                    System.out.println("Goodbye! Thanks for visiting!\n");
                    gui.exitClippy();
                }
                else if(tag.equals("menu"))
                {
                    setDefaultList();
                    return "menu";
                }
                else if (tag.equals("help"))
                {

                    help();

                }
                else if(listen.equalsIgnoreCase("scroll up"))
        {
           sendCommand("scroll up");
        }
        else if(listen.equalsIgnoreCase("scroll down"))
        {
            sendCommand("scroll down");
        }
                else if (listen.equalsIgnoreCase("close active program")) {
                    sendCommand("close");
                    help();
                }
                else if (tag.equals("time")) {
                    DateFormat dateFormat = new SimpleDateFormat("HH:mm");
                    Calendar cal = Calendar.getInstance();
                    Thread speak = new Speak(dateFormat.format(cal.getTime()));
                    speak.start();
                }
                else if(tag.equals("search"))
                {
                    String search = JOptionPane.showInputDialog(null, "Enter text to search");
                try { Map<String, String> map = GoogleSearch.getSearchResult(search);
         Iterator it = map.keySet().iterator(); while (it.hasNext()) { String
          key = it.next().toString(); System.out.println(key + " <==> " +
          map.get(key)); } } 
                catch (IOException ex) { } 
                catch (JSONException ex) { } 
                }
                else if(tag.equals("directions"))
                {
                    NavMenu navMenu = new NavMenu();
                    navMenu.pack();
                    navMenu.setVisible(true);
                }
                else if (tag.startsWith("goto_")) {
                    System.out.println("Got heree");
                    return tag.replaceFirst("goto_", "");
                }
            }
            else
            {
                try
                    {
                        AePlayWave aw = new AePlayWave("./models/siri_notHeard.wav");
                        aw.start();
                    }
                    catch (Exception e)
                    {
                    } 
            }
            return "";
        }
        


        /**
         * Executed when the recognizer generates a result. Returns the name of
         * the next dialog node to become active, or null if we should stay in
         * this node
         *
         * @param result the recongition result
         * @return the name of the next dialog node or null if control should
         * remain in the current node.
         */
    @Override
        public String onRecognize(Result result) throws GrammarException
        {
            System.out.println("Result = " + result);
            String tag = super.onRecognize(result);
            String listen = result.getBestFinalResultNoFiller();
            if (tag != null)
            {

                System.out.println("\n "
                        + result.getBestFinalResultNoFiller() + '\n');

                if (tag.equals("exit"))
                {

                    System.out.println("Goodbye! Thanks for visiting!\n");
                    gui.exitClippy();
                }
                else if(tag.equals("menu"))
                {
                    return "menu";
                }
                else if (tag.equals("help"))
                {

                    help();

                }
                else if(listen.equalsIgnoreCase("scroll up"))
        {
           sendCommand("scroll up");
        }
        else if(listen.equalsIgnoreCase("scroll down"))
        {
            sendCommand("scroll down");
        }
                else if (listen.equalsIgnoreCase("close active program")) {
                    sendCommand("close");
                    help();
                }
                else if (tag.equals("time")) {
                    DateFormat dateFormat = new SimpleDateFormat("HH:mm");
                    Calendar cal = Calendar.getInstance();
                    Thread speak = new Speak(dateFormat.format(cal.getTime()));
                    speak.start();
                }
                else if(tag.equals("search"))
                {
                    String search = JOptionPane.showInputDialog(null, "Enter text to search");
                try { Map<String, String> map = GoogleSearch.getSearchResult(search);
         Iterator it = map.keySet().iterator(); while (it.hasNext()) { String
          key = it.next().toString(); System.out.println(key + " <==> " +
          map.get(key)); } } 
                catch (IOException ex) { } 
                catch (JSONException ex) { } 
                }
                else if(tag.equals("directions"))
                {
                    NavMenu navMenu = new NavMenu();
                    navMenu.pack();
                    navMenu.setVisible(true);
                }
                else if (tag.startsWith("goto_")) {
                    return tag.replaceFirst("goto_", "");
                }
            }
            else
            {
                try
                    {
                        AePlayWave aw = new AePlayWave("./models/siri_notHeard.wav");
                        aw.start();
                    }
                    catch (Exception e)
                    {
                    } 
            }
            return "";
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
         * Indicated that the grammar has changed and the collection of sample
         * utterances should be regenerated.
         */
        protected void grammarChanged()
        {
            sampleUtterances = null;
        }
    }
