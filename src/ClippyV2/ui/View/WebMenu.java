/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ClippyV2.ui.View;

import java.awt.BorderLayout;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author dns3948
 */
public class WebMenu extends JFrame {
    private ArrayList<String> webArray = new ArrayList();
    JPanel pnl = new JPanel();
    public WebMenu(ArrayList web){
        webArray = web;
        JComboBox webList = new JComboBox(webArray.toArray());
        webList.setSelectedIndex(0);
        pnl.add(webList, BorderLayout.CENTER);
        this.add(pnl, BorderLayout.NORTH);
        JPanel botPnl = new JPanel();
        botPnl.add(new JButton("Delete"), BorderLayout.EAST);
        botPnl.add(new JButton("Exit"), BorderLayout.WEST);
        this.add(botPnl, BorderLayout.SOUTH);
       // petList.addActionListener(this);   
        
    }
    
     public static void main(String[] args) {
         ArrayList<String> list = new ArrayList();
         list.add("rar");
         list.add("rar");
         list.add("rar");
         list.add("rar");
         WebMenu web = new WebMenu(list);
         web.setSize(240,100);
         web.setVisible(true);
         web.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     }
}
