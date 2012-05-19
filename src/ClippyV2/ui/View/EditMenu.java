/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ClippyV2.ui.View;

import ClippyV2.ui.Frame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author GavinC
 */
public class EditMenu extends Frame {
    private JPanel editPnl = new JPanel();
    private JLabel editLbl = new JLabel(); 
    private JLabel titleLbl = new JLabel(); 
    
    public EditMenu(){
        super(350,300,350,900);
        setPnl(editPnl);
    }
    
    public void addLbl(){
        editPnl.add(editLbl);
    }
    
    public void addButtons(){
        
    }
    
           
}
