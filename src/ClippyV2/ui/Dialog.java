/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ClippyV2.ui;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * A Dialog class for the Clippy Interface 
 * @author GavinC 
 */
public class Dialog extends JOptionPane {

    JFrame frame = new JFrame();
    boolean confirm = false;
    ImageIcon optionIcon;
    ImageIcon newIcon;
    String dialogName = "Clippy Dialog";

    /**
     * Constructor for class Dialog
     * @param msg the error/confirmation message in the dialog
     */
    public Dialog(String msg){
        try {
            optionIcon = new ImageIcon("OptionIcon.jpg");
        }
        catch (NullPointerException ex){
            //Show Exception has been caught
            //System.out.println("Caught exception: " + ex);
            System.err.println("Cannot find image");
        }
        message = msg;
    }

    /**
     * Creates and show an error dialog
     */
    public void showErrorMsg(){
        showMessageDialog(frame, message, dialogName, ERROR_MESSAGE);
    }

    /**
     * Creates and show a option dialog
     */
    public void showOptionDialog(){
        Object[] options = {"Yes", "No"};
        int answer = showOptionDialog(frame, message,dialogName,
        YES_NO_OPTION,PLAIN_MESSAGE, optionIcon, options, options[0]);
        if (answer == JOptionPane.YES_OPTION) {
            confirm = true;
        }
        else {
            
        }
    }

    /**
     * Creates and show a plain message dialog
     */
    public void showPlainDialog(){
        showMessageDialog(frame, message, dialogName, PLAIN_MESSAGE);
    }

    /**
     * Gets the user's confirmation when the user selects the option dialog
     * @return true if the user selects Yes otherwise false
     */
    public boolean getConfirmation(){
        return confirm;
    }
    
}
