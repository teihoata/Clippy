/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ModelPackages;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marzipan
 */
public class Clippy 
{
    public static void main(String[] args)
    {
        try
        {
            WordCollection wordCollect = new WordCollection();
            wordCollect.setVisible(true);
            wordCollect.go();
        }
        catch (IOException ex)
        {
            System.out.println(ex.getMessage());
        }
    }
   
}
