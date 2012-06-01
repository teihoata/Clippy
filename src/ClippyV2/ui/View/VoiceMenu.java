/*
 * A class which controls the list of options for clippy
 */
package ClippyV2.ui.View;

import Clippy.MyBehavior;
import Clippy.WordRecognizer;
import ClippyV2.ui.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.BevelBorder;

/**
 * A class that displays the Voice Command Selection menu 
 * @author GavinC
 */
public class VoiceMenu extends Frame {
    private JList voiceTxt = new JList();;
    private JScrollPane voiceScroll = new JScrollPane(voiceTxt);
    private JPanel voicePnl = new JPanel();
    private MyBehavior behavior;
    private WordRecognizer wordsRecognizer;
    private JLabel label = new JLabel();
    
    /**
     * Constructor for class VoiceMenu
     * @param behavior the logic behavior 
     */
    public VoiceMenu(MyBehavior behavior){
        super(350,300,350,600);
        this.behavior = behavior;
        setPnl(voicePnl);
        setMenu();
    }
    
    /**
     * Set the logic behavior 
     * @param behavior the selected behavior 
     */
    public void setBehavior(MyBehavior behavior)
    {
        this.behavior = behavior;
    }
    
    /**
     * Sets the voice menu 
     */
    public final void setMenu(){
        MouseListener mouseListener = new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String selectedItem = voiceTxt.getSelectedValue().toString();
                    selectedItem = selectedItem.replaceAll("\\s+", " ");
                    selectedItem = selectedItem.toLowerCase();
                    selectedItem = selectedItem.trim();
                    wordsRecognizer.enterNextNode(selectedItem);
                }
            }
        };
        voiceTxt.addMouseListener(mouseListener);
        voiceScroll.setBounds(0, 0, 350, 300);
        
      label = new JLabel("menu", JLabel.CENTER); 
      label.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
      voiceScroll.setColumnHeaderView(label);
      voicePnl.add(voiceScroll);
      new ComponentMover(this, label);
    }
    
    /**
     * Updates the voice header 
     * @param update the header 
     */
    public void setHeaderUpdate(String update)
    {
        label.setText(update);
    }
    
    /**
     * Sets the commands for the voice menu
     * @param txt the command list 
     */
    public void setVoiceMenu(ArrayList txt){
        voiceTxt.clearSelection();
        voiceTxt.setListData(txt.toArray());
    }
    
    /**
     * Sets and add a command into the menu list 
     * @param item the command being added 
     */
    public void setSingleMenuItem(String item)
    {
        voiceTxt.clearSelection();
        List data = new ArrayList();
        data.add(item);
        voiceTxt.setListData(data.toArray());
    }
    
    /**
     * Sets the wordRecognizer
     * @param wordsRecognizer selected wordRecognizer
     */
    void setWordsRecognizer(WordRecognizer wordsRecognizer) {
        this.wordsRecognizer = wordsRecognizer;
    }
}
