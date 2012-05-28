/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Clippy;

import ClippyV2.ui.View.ClippyGui;
import ClippyV2.ui.View.NavMenu;
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
import javax.speech.recognition.GrammarException;
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
        private String result;
    
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
        
        public MyBehavior()
        {
            //Constructor used for testing class methods
        }
        
        public MyBehavior(ClippyGui gui)
        {
            this.gui = gui;
            defaultMenuOptions = new ArrayList<>();
            defaultMenuOptions.add("main menu");
            defaultMenuOptions.add("computer control");
            defaultMenuOptions.add("surf the web");
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
            list.add("close clippy");
            list.add("surf the web");
            list.add("computer control");
            list.add("play music");
            list.add("watch movies");
            list.add("get directions");
            list.add("google search");
            list.add("tell me the time");
            try{
                gui.setCurrentBehavior(this);
                gui.setCurrentMenu(list);
            }
            catch(Exception e){
                
            }
        }
        
        /**
         * Used for testing
         * @return the default menu 
         */
        private ArrayList<String> getDefaultList()
        {
            return defaultMenuOptions;
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
            menu = "";
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
        
        public boolean processResult(String result)
        {
            return true;
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
            if(this.getName().equalsIgnoreCase("menu"))
            {
                setDefaultList();
            }
        }
        
        

        /**
         * Displays the help message for this node. Currently we display the
         * name of the node and the list of sentences that can be spoken.
         */
        protected void help()
        {
            menu = "";
            gui.setHeader(getGrammarName());
        }
        
        public String getCurrentMenuOptions()
        {
            return this.menu;
        }
            
        public String onRecognizeByString(String result) throws GrammarException
        {
            String tag = getTagStringFromString(result);
            String listen = result;
            String end = "";
            if (tag != null)
            {
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
           end = "processed";
        }
        else if(listen.equalsIgnoreCase("scroll down"))
        {
            sendCommand("scroll down");
            end = "processed";
        }
                else if (listen.equalsIgnoreCase("close active program")) {
                    sendCommand("close");
                    help();
                    end = "processed";
                }
                else if (tag.equals("time")) {
                    DateFormat dateFormat = new SimpleDateFormat("HH:mm");
                    Calendar cal = Calendar.getInstance();
                    String time = dateFormat.format(cal.getTime());
                    Thread speak = new Speak(time);
                    gui.setClippyTxt(time);
                    speak.start();
                    end = "processed";
                }
                else if(tag.equals("search"))
                {
                    String search = JOptionPane.showInputDialog(null, "Enter text to search");
                try { 
                Map<String, String> map = GoogleSearch.getSearchResult(search);
                Iterator it = map.keySet().iterator(); 
                while (it.hasNext()) { 
                    String key = it.next().toString(); 
                    System.out.println(key + " <==> " + map.get(key)); } 
                } 
                catch (IOException ex) { } 
                catch (JSONException ex) { } 
                return "processed";
                }
                else if(tag.equals("directions"))
                {
                    NavMenu navMenu = new NavMenu();
                    navMenu.pack();
                    navMenu.setVisible(true);
                    return "processed";
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
            return end;
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
            String onRecognizeByString = onRecognizeByString(listen);
            return onRecognizeByString;
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
