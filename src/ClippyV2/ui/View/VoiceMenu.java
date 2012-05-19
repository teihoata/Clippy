/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ClippyV2.ui.View;

import ClippyV2.ui.Frame;
import java.awt.Component;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author GavinC
 */
public class VoiceMenu extends Frame {
    private JTextArea voiceTxt = new JTextArea();
    private JScrollPane voiceScroll = new JScrollPane(voiceTxt);
    private JPanel voicePnl = new JPanel();
    
    public VoiceMenu(){
        super(350,300,350,600);
        setPnl(voicePnl);
        setMenu();
    }
    
    public final void setMenu(){
        voiceTxt.setLineWrap(true);
        voiceTxt.setWrapStyleWord(true);
        voiceTxt.setEditable(false);
        voiceScroll.setBounds(0, 0, 350, 300);
        voicePnl.add(voiceScroll);
        
    }
    
    public void setVoiceMenu(String txt){
        voiceTxt.setText(txt);
    }
}
