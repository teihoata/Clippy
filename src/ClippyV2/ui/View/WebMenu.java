/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ClippyV2.ui.View;

import Clippy.MyWebsiteBehavior;
import ClippyV2.ui.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Marcus 
 */
public class WebMenu extends Frame {
    private ArrayList<String> webArray = new ArrayList();
    JPanel pnl = new JPanel();
    
    public WebMenu(List<String> webArray , final MyWebsiteBehavior webBehav){
        super(240,100,700,500); 
        webArray.add(0, "--Select a website to remove--");
        final JComboBox webList = new JComboBox(webArray.toArray());
        webList.setSelectedIndex(0);
        webList.setOpaque(true);
        webList.setBounds(10, 30, 220, 20);
        
        JLabel heading = new JLabel("Remove Website");
        heading.setBounds(10, 10, 220, 20);
        
        JButton deleteBtn = new JButton("Delete");
        deleteBtn.setBounds(5, 60, 70, 30);
        deleteBtn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                if(webList.getSelectedIndex() != 0)
                {
                    System.out.println(webList.getSelectedItem());
                    webBehav.removeWebsiteFromList(webList.getSelectedItem().toString());
                    webList.removeItem(webList.getSelectedItem());
                    webList.setSelectedIndex(0);
                }
            }
        });
        
        JButton exitBtn = new JButton("Exit");
        exitBtn.setBounds(165, 60, 70, 30);
        exitBtn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                dispose();
            }
        });
        
        pnl.add(webList);
        pnl.add(heading);
        pnl.add(deleteBtn);
        pnl.add(exitBtn);
        this.setPnl(pnl);  
    }
}
