/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Clippy;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author Marzipan
 */
public class Speak extends Thread
{

    private ArrayList<String> list;
    private String sentence;
    private String result;

    public Speak(String sentence)
    {
        this.sentence = sentence;
    }

    public Speak(ArrayList<String> list)
    {
        this.list = list;
    }

    @Override
    public void run()
    {
        speak();
    }

    private synchronized void speak()
    {
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
