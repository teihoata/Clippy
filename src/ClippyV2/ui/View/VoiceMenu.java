/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ClippyV2.ui.View;

import Clippy.WordRecognizer;
import Clippy.MyBehavior;
import ClippyV2.ui.Frame;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author GavinC
 */
public class VoiceMenu extends Frame {
    private JList voiceTxt = new JList();;
    private JScrollPane voiceScroll = new JScrollPane(voiceTxt);
    private JPanel voicePnl = new JPanel();
    private MyBehavior behavior;
    private WordRecognizer wordsRecognizer;
    
    public VoiceMenu(MyBehavior behavior){
        super(350,300,350,600);
        this.behavior = behavior;
        setPnl(voicePnl);
        setMenu();
    }
    
    public void setBehavior(MyBehavior behavior)
    {
        this.behavior = behavior;
    }
    
    
    
    public final void setMenu(){
        MouseListener mouseListener = new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    System.out.println("Double Click!!1");
                    String selectedItem = voiceTxt.getSelectedValue().toString();
                    selectedItem = selectedItem.replaceAll("\\s+", " ");
                    selectedItem = selectedItem.toLowerCase();
                    selectedItem = selectedItem.trim();
                    //behavior.processResult(selectedItem);
                    
                    wordsRecognizer.enterNextNode(selectedItem);
                }
            }
        };
        voiceTxt.addMouseListener(mouseListener);
        voiceScroll.setBounds(0, 0, 350, 300);
        voicePnl.add(voiceScroll);
    }
    
    public void setVoiceMenu(ArrayList txt){
        voiceTxt.clearSelection();
        voiceTxt.setListData(txt.toArray());
    }
    
    public void setSingleMenuItem(String item)
    {
        voiceTxt.clearSelection();
        List data = new ArrayList();
        data.add(item);
        voiceTxt.setListData(data.toArray());
    }

    void setWordsRecognizer(WordRecognizer wordsRecognizer) {
        this.wordsRecognizer = wordsRecognizer;
    }
}
