/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ModelPackages;

import java.io.*;
import com.sun.speech.freetts.lexicon.LetterToSound;
import com.sun.speech.freetts.lexicon.LetterToSoundImpl;


public class AddGrammar {
    LetterToSound lts = null;
    
    public AddGrammar()
    {
        try {
            File file = new File("./lib/cmulex_lts.bin");
            lts = new LetterToSoundImpl(file.toURI().toURL(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void writeToDictionary(String word) throws IOException
    {
        File dictionary = new File("./models/acoustic/voxforge/etc/cmudict.0.7a");
        PrintWriter out = new PrintWriter(new FileWriter(dictionary,true));
        out.write(word);
        out.close();
    }
    
    public void changeWordToGrammar(String word) throws IOException 
    {
        String[] ltspM_phone_array;
        ltspM_phone_array = lts.getPhones(word, null);

        String result = "\n" + word.toUpperCase() + "\t";
        for (int i = 0; i < ltspM_phone_array.length; i++) 
        {        
            ltspM_phone_array[i] = ltspM_phone_array[i].toString().replaceAll("[^A-Za-z]", " ");
            if(ltspM_phone_array[i].toString().equalsIgnoreCase("AX"))
            {      
                ltspM_phone_array[i] = "AH";
            }
            result +=  ltspM_phone_array[i].toString().toUpperCase() + " ";
//            System.out.print(result);
        }
        writeToDictionary(result);
    }
}