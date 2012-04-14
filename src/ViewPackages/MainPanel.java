/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ViewPackages;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.LayoutManager;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Marzipan
 */
public class MainPanel 
{
    private JButton speakButton;
    private JTextArea messageTextField;
    private final static Color backgroundColor = new Color(0x42, 0x42, 0x42);
    private JScrollPane messageScroll;
    
    public MainPanel()
    {
        
    }
    
    /**
     * Returns a JPanel with the given layout and custom background color.
     *
     * @param layout the LayoutManager to use for the returned JPanel
     *
     * @return a JPanel
     */
    private JPanel getJPanel(LayoutManager layout)
    {
        JPanel panel = new JPanel();
        panel.setLayout(layout);
        panel.setBackground(backgroundColor);
        return panel;
    }
    
    /**
     * Constructs the main Panel of this LiveFrame.
     *
     * @return the main Panel of this LiveFrame
     */
    private JPanel createMainPanel()
    {
        
        JPanel mainPanel = getJPanel(new BorderLayout());
        speakButton = new JButton("Speak");
        speakButton.setEnabled(false);
        speakButton.setMnemonic('s');
        // if this is continuous mode, don't add the speak button
        // since it is not necessary

        mainPanel.add(createMessagePanel(), BorderLayout.CENTER);
        messageScroll = new JScrollPane(messageTextField);
        mainPanel.add(messageScroll, BorderLayout.CENTER);
        mainPanel.add(speakButton, BorderLayout.SOUTH);

        return mainPanel;
    }
    
    /**
     * Creates a Panel that contains a label for messages. This Panel should be
     * located at the bottom of this Frame.
     *
     * @return a Panel that contains a label for messages
     */
    private JPanel createMessagePanel()
    {
        JPanel messagePanel = getJPanel(new BorderLayout());
        messageTextField = new JTextArea("Please wait while I'm loading...");
        messageTextField.setBackground(backgroundColor);
        messageTextField.setForeground(Color.WHITE);
        messageTextField.setEditable(false);
        messageTextField.setBorder(new EmptyBorder(1, 1, 1, 1));
        messagePanel.add(messageTextField, BorderLayout.CENTER);
        return messagePanel;
    }
}
