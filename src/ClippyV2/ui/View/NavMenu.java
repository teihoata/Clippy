/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ClippyV2.ui.View;

import ClippyV2.ui.Button;
import ClippyV2.ui.Frame;
import Test.TestMain;
import db.clippy.SearchEngine.GoogleNavigation;
import db.clippy.Vo.GoogleNaviVo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import org.json.JSONException;

/**
 * A class that creates the navigation menu 
 * @author GavinC
 */
public class NavMenu extends Frame{
    JPanel navPnl = new JPanel();
    private Button navBtn = new Button("Navigate");
    private Button exitBtn = getExitBtn();
    private JLabel locatLbl = new JLabel("Enter Current Location: "); 
    private JLabel desLbl = new JLabel("Enter Destination: ");
    private JTextField locatTxt = new JTextField();
    private JTextField desTxt = new JTextField();
    private JTextArea navTxt = new JTextArea();
    private JScrollPane navScroll = new JScrollPane(navTxt);
    public NavMenu(){
        super(500,500,850,500); 
        this.setOpacity(0.7f);
        setPnl(navPnl);
        addPnl();
    }    
    
    private void addPnl(){
        setLbl();
        setBtn();
        addNavTxt();
        
    }
    
    public void setLbl(){
        locatLbl.setBounds(50, 320, 200, 20);
        desLbl.setBounds(50, 370, 200, 20);
        locatTxt.setBounds(250, 320, 200, 20);
        desTxt.setBounds(250, 370, 200, 20);
        navPnl.add(locatLbl);
        navPnl.add(desLbl);
        navPnl.add(locatTxt);
        navPnl.add(desTxt);
    }
    
    public void setBtn(){
        navBtn.setLocation(100, 420);
        addNavAction();
        exitBtn.setLocation(300,420);
        navPnl.add(navBtn);
        navPnl.add(exitBtn);   
    }
    
    public void addNavTxt(){
        navTxt.setLineWrap(true);
        navTxt.setWrapStyleWord(true);
        navTxt.setEditable(false);
        navScroll.setBounds(0,0,500,300);
        navPnl.add(navScroll);
    }
    
    public void setNavTxt(String text){
        navTxt.setText(text);
    }
    
    public void addNavAction(){
        navBtn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                checkNav();
            }
        });
        
    }
    
    public void checkNav(){
        try {
            List<GoogleNaviVo> list = new ArrayList<>();
            String start =
                    java.net.URLEncoder.encode(locatTxt.getText(), "UTF-8");
            String end =
                    java.net.URLEncoder.encode(desTxt.getText(), "UTF-8");
            System.out.println(start + " <===> " + end);
                list =
                        GoogleNavigation.GoogleNavigation(start, end);
            String navigationInstructions = "";
            if(list.size() > 1)
            {
            for (int i = 0; i < list.size(); i++) {
                navigationInstructions += (list.get(i).getHtml_instructions() + "\n");
            }
            }
            else{
                navigationInstructions = "Couldn't Calculate route from " + locatTxt.getText() + " to " + desTxt.getText();
            }
            navTxt.setText(navigationInstructions);
            
        } catch (Exception ex) {
            navTxt.setText("Couldn't Calculate route from " + locatTxt.getText() + " to " + desTxt.getText());
            this.repaint();
        }
        this.repaint();
    }
}
