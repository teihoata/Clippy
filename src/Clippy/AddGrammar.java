/*
 * Handles the adding of new grammar to clippys dictionary
 */
package Clippy;

import com.sun.speech.freetts.lexicon.LetterToSound;
import com.sun.speech.freetts.lexicon.LetterToSoundImpl;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/** 
 * @author Marcus Ball
 */
public class AddGrammar
{

    private LetterToSound lts = null;

    /**
     * Default Constructor
     */
    public AddGrammar()
    {
        try
        {
            File file = new File("./lib/cmulex_lts.bin");
            lts = new LetterToSoundImpl(file.toURI().toURL(), true);
        } catch (IOException e)
        {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Writes the word to the dictionary file
     * @param word
     * @throws IOException 
     */
    private void writeToDictionary(String word) throws IOException
    {
        File dictionary = new File("./models/acoustic/voxforge/etc/cmudict.0.7a");
        PrintWriter out = new PrintWriter(new FileWriter(dictionary, true));
        out.write(word);
        out.close();
    }

    /**
     * Changing the word to a valid grammar string
     * @param word
     * @throws IOException 
     */
    public void changeWordToGrammar(String word) throws IOException
    {
        String[] arrayStr;
        arrayStr = lts.getPhones(word, null);
        //All words in dictionary are upper case
        String result = "\n" + word.toUpperCase() + "\t";
        for (int i = 0; i < arrayStr.length; i++)
        {
            //Must format the string to follow the default sphinx 4 grammar
            //No Special characters
            arrayStr[i] = arrayStr[i].toString().replaceAll("[^A-Za-z]", " ");
            //Grammar that have AX are pronounced AH in sphinx
            if (arrayStr[i].toString().equalsIgnoreCase("AX"))
            {
                arrayStr[i] = "AH";
            }
            result += arrayStr[i].toString().toUpperCase() + " ";
        }
        writeToDictionary(result);
    }
}