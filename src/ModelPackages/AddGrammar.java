/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ModelPackages;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import java.io.*;
import com.sun.speech.freetts.lexicon.LetterToSound;
import com.sun.speech.freetts.lexicon.LetterToSoundImpl;
import org.apache.commons.lang3.StringUtils;


public class AddGrammar {
    private static LetterToSound lts = null;
    
    public AddGrammar()
    {
        try {
            File file = new File("./lib/cmulex_lts.bin");
            lts = new LetterToSoundImpl(file.toURI().toURL(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void changeWordToGrammar(String word) throws IOException 
    {
        File dictionary = new File("./models/acoustic/voxforge/etc/cmudict.0.7a");
        PrintWriter out = new PrintWriter(new FileWriter(dictionary,true));
        
        
        String[] ltspM_phone_array;
        ltspM_phone_array = lts.getPhones(word, null);

        String result = "\n" + word.toUpperCase() + "\t";
        for (int i = 0; i < ltspM_phone_array.length; i++) {        

            if(ltspM_phone_array[i].toString().toUpperCase().indexOf("1") != 0)
            {
                ltspM_phone_array[i] = StringUtils.strip(ltspM_phone_array[i].toString(), "1");
            }
            if(ltspM_phone_array[i].toString().equalsIgnoreCase("AX"))
            {      
                ltspM_phone_array[i] = "AH";
            }
            result +=   " " + ltspM_phone_array[i].toString().toUpperCase();
        }
        out.write(result);
        out.close();
    }

//        public static void main(String[] args) {
//        
//    }
//
//    
// 
//    public static void main(String[] args) {
// 
//        AddGrammar ltspM = new AddGrammar();
//        
//        String voiceName = "kevin16"; 
//        VoiceManager voiceManager = VoiceManager.getInstance();        
//        Voice voice = voiceManager.getVoice(voiceName);
// 
//         voice.setPitch(130f);
//         voice.setDurationStretch(0.7f);
//         voice.setVolume(1.0f);
//         
//        voice.allocate();        
//        voice.speak("starships");
//        voice.deallocate();
//    }
}