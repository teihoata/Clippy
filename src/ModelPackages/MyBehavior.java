/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ModelPackages;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.TimerPool;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.speech.recognition.GrammarException;

/**
 *
 * @author Marzipan
 */
public class MyBehavior extends NewGrammarDialogNodeBehavior {

        private Collection<String> sampleUtterances;
        private String menu;
        /**
         * Executed when we are ready to recognize
         */
        public void onReady()
        {
            super.onReady();
            menu = "";
            help();
        }

        /**
         * Displays the help message for this node. Currently we display the
         * name of the node and the list of sentences that can be spoken.
         */
        protected void help()
        {
            menu = " ======== " + getGrammarName() + " =======\n";
            dumpSampleUtterances();
            WordCollection.setMessage(menu);
            WordCollection.setCurrentMenu(menu);
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
            String tag = super.onRecognize(result);
            String listen = result.getBestFinalResultNoFiller();
            if (tag != null)
            {

                System.out.println("\n "
                        + result.getBestFinalResultNoFiller() + '\n');

                if (tag.equals("exit"))
                {

                    System.out.println("Goodbye! Thanks for visiting!\n");
                    System.exit(0);
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
                    String voiceName = "kevin16";
                    VoiceManager voiceManager = VoiceManager.getInstance();
                    Voice voice = voiceManager.getVoice(voiceName);
                    DateFormat dateFormat = new SimpleDateFormat("HH:mm");
                    Calendar cal = Calendar.getInstance();
                    voice.allocate();
                    voice.speak(dateFormat.format(cal.getTime()));
                    voice.deallocate();
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
         * Collects the set of possible utterances.
         * <p/>
         * TODO: Note the current implementation just generates a large set of
         * random utterances and tosses away any duplicates. There's no
         * guarantee that this will generate all of the possible utterances.
         * (yep, this is a hack)
         *
         * @return the set of sample utterances
         */
        private Collection<String> collectSampleUtterances()
        {
            Set<String> set = new HashSet<String>();
            for (int i = 0; i < 100; i++)
            {
                String s = getGrammar().getRandomSentence();
                if (!set.contains(s))
                {
                    set.add(s);
                }
            }

            List<String> sampleList = new ArrayList<String>(set);
            Collections.sort(sampleList);
            return sampleList;
        }

        /**
         * Dumps out the set of sample utterances for this node
         */
        private void dumpSampleUtterances()
        {
            if (sampleUtterances == null)
            {
                sampleUtterances = collectSampleUtterances();
            }

            for (String sampleUtterance : sampleUtterances)
            {
                menu += ("  " + sampleUtterance + "\n");
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
