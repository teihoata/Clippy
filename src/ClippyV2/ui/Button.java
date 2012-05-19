/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ClippyV2.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import javax.swing.JButton;

/**
 * Button class for customizing the buttons in the Clippy interface
 * @author GavinC
 */
public class Button extends JButton {
    Font btnFont = new Font(null);
    
    public Button(String name){
        super(name);
        this.setSize(100,20);
    }
    
    public void setBgColor(Color c){
        this.setBackground(c);
    }
    
    public void setFgColor(Color c){
        this.setFgColor(c);
    }
   
}
