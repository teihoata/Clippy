/*
 * A class used primarily for voice feedback using the FreeTTS engine
 */
package Clippy;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author Marcus Ball
 */
public class Speak extends Thread
{
    //list of strings to read out one after the other
    private ArrayList<String> list;
    //or a single string or sentence of strings to read
    private String sentence;

    /**
     * Constructor for single string reading
     * @param sentence 
     */
    public Speak(String sentence)
    {
        this.sentence = sentence;
    }

    /**
     * Constructor for lists of string readings
     * @param list 
     */
    public Speak(ArrayList<String> list)
    {
        this.list = list;
    }

    @Override
    public void run()
    {
        speak();
    }

    /**
     * Speaks the list or single string given
     */
    private synchronized void speak()
    {
        //if it is a list
        if (list != null)
        {
            Thread t = new Thread(new Runnable()
            {

                public void run()
                {
                    JOptionPane.showMessageDialog(null, "Stop Reading?");
                }
            });
            t.start();
            for (String string : list)
            {
                String voiceName = "kevin16";
                VoiceManager voiceManager = VoiceManager.getInstance();
                Voice voice = voiceManager.getVoice(voiceName);
                voice.setPitch(115.0f);
                voice.setPitchRange(12.0f);
                voice.setPitchShift(1.0f);
                voice.setRate(140f);
                voice.allocate();
                voice.speak(string);
                voice.deallocate();
                if (!t.isAlive())
                {
                    break;
                }
            }
            if(t.isAlive())
            {
                t.interrupt();
            }
        }
        else
        { 
            String voiceName = "kevin16";
            VoiceManager voiceManager = VoiceManager.getInstance();
            Voice voice = voiceManager.getVoice(voiceName);
            voice.allocate();
            voice.speak(sentence);
            voice.deallocate();
        }
    }
}
