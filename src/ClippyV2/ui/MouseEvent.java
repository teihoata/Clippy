/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ClippyV2.ui;

import ClippyV2.ui.View.HelpMenu;
import java.applet.Applet;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;

/**
 *
 * @author GavinC
 */
public class MouseEvent extends MouseAdapter {
    HelpMenu help;
    public MouseEvent(){
      
    }
    
    public void mousePressed( MouseEvent e ) {  
        help = new HelpMenu();
        help.setVisible(true);
    }
    
}

   
