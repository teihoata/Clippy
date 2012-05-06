package ModelPackages;

import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.IntellitypeListener;
import com.melloware.jintellitype.JIntellitype;
import edu.cmu.sphinx.util.props.ConfigurationManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URL;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * A JFrame class to demonstrate word recognition and organisation
 *
 * @author Marcus Ball
 */
public class WordCollection extends JFrame implements HotkeyListener, IntellitypeListener {

    private final static Color backgroundColor = new Color(0x42, 0x42, 0x42);
    private static JTextArea messageTextField;
    private JScrollPane messageScroll;
    private JButton speakButton;
    private static String menu;
    private WordRecognizer wordsRecognizer;

    /**
     * WordCollection constructor
     *
     */
    public WordCollection() 
    {
        super("Clippy");
        this.setUndecorated(true);
        this.setAlwaysOnTop(true);
        this.setOpacity(0.5f);

        setSize(200, 250);
        this.setLocation((int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth() - 200, (int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 280);
        getContentPane().add(createMainPanel(), BorderLayout.CENTER);
        
        // exit if the window is closed

        addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                if (wordsRecognizer != null) {
                    wordsRecognizer.shutdown();
                }
                System.exit(0);
            }
        });

        // when the 'speak' button is pressed, enable recognition
        speakButton.setBorderPainted(true);
        speakButton.setBackground(Color.gray);
        speakButton.setOpaque(true);
        speakButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                if (speakButton.isEnabled()) {
                    speakButton.setEnabled(false);
                    startListening();
                }
            }
        });
    }

    /**
     * updates the button on the gui
     */
    public void updateGui() {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                speakButton.setEnabled(true);
                validate();
                repaint();
            }
        });

    }

    /**
     * Starts listening for words
     */
    private void startListening() {
        if (wordsRecognizer.microphoneOn()) {
            //setMessage(menu + "\n " + "Clippy is listening...");
        }
        else {
            setMessage(
                    "Sorry, can't find the microphone on your computer.");
        }
    }

    public static void setCurrentMenu(String menu) {
        WordCollection.menu = menu;
    }

    public static void addToCurrentMenu(String menu) {    
        WordCollection.menu += menu;
    }

    /**
     * Perform any needed initializations and then enable the 'speak' button,
     * allowing recognition to proceed
     */
    public void go() throws IOException {
        //Get the configuration from the xml resource
        URL url = WordCollection.class.getResource("clippy.config.xml");
        ConfigurationManager cm = new ConfigurationManager(url);
        wordsRecognizer = (WordRecognizer) cm.lookup("dialogManager");

        //Add each menu node to the words to be recognised
        wordsRecognizer.addNode("menu", new MyBehavior());
        wordsRecognizer.addNode("tell me the time", new MyBehavior());
        wordsRecognizer.addNode("music", new MyMusicBehavior());
        wordsRecognizer.addNode("movies", new MyMovieBehavior());
        wordsRecognizer.addNode("desktop", new MyDesktopBehavior());
        wordsRecognizer.addNode("web", new MyWebsiteBehavior());
        wordsRecognizer.addNode("remove website", new removeWebsiteBehavior());
   
        setMessage("Loading IntelliJ");
        initJIntellitype();

        setMessage("Loading dialogs ...");
        wordsRecognizer.allocate();

        setMessage("Running  ...");

        wordsRecognizer.addWordListener(new WordsListener() {

            public void notify(String word) {
                updateMenu(word);
            }
        });

        updateGui();
        //Set the first menu as the main menu option
        wordsRecognizer.setInitialNode("menu");
    }
  
    /**
     * Update the display with the new menu information.
     *
     * @param update menu according to the word recognised
     */
    private void updateMenu(final String word) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                if (word == null) {
                    //setMessage("I didn't understand what you said");
                }
                else {
                    //setMessage(word);
                }

                speakButton.setEnabled(true);
            }
        });
    }

    /**
     * Sets the message to be displayed at the bottom of the Frame.
     *
     * @param message message to be displayed
     */
    public static void setMessage(final String message) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                messageTextField.setText(message);
            }
        });
    }

    /**
     * Adds a string to the menu
     * @param message string to be added
     */
    public static void addToMessage(final String message) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                messageTextField.setText(menu + "\n" + message);

            }
        });
    }

    /**
     * Enables or disables the "Speak" button.
     *
     * @param enable boolean to enable or disable
     */
    public void setSpeakButtonEnabled(final boolean enabled) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                speakButton.setEnabled(enabled);
            }
        });
    }

    /**
     * Temp JPanel
     *
     * @param layout the LayoutManager to use for the returned JPanel
     *
     * @return a JPanel
     */
    private JPanel getJPanel(LayoutManager layout) {
        JPanel panel = new JPanel();
        panel.setLayout(layout);
        panel.setBackground(backgroundColor);
        return panel;
    }

    /**
     * Constructs the main Panel will be deleted when new UI is created
     *
     * @return the main Panel
     */
    private JPanel createMainPanel() {
        JPanel mainPanel = getJPanel(new BorderLayout());
        speakButton = new JButton("Speak");
        speakButton.setEnabled(false);
        mainPanel.add(createMessagePanel(), BorderLayout.CENTER);
        messageScroll = new JScrollPane(messageTextField);
        mainPanel.add(messageScroll, BorderLayout.CENTER);
        mainPanel.add(speakButton, BorderLayout.SOUTH);

        return mainPanel;
    }

    /**
     * Called when the keys Win+CTRL are pressed
     * @param i 
     */
    @Override
    public void onHotKey(int i) {
        if (speakButton.isEnabled()) {
            speakButton.setEnabled(false);
            startListening();
        }
    }

    @Override
    public void onIntellitype(int i) {
        if (i == JIntellitype.MOD_WIN) {
            System.out.println("Pushed");
        }
    }

    public void initJIntellitype() {
        try {
            // initialize JIntellitype with the frame so all windows commands can
            // be attached to this window
            JIntellitype.getInstance().addHotKeyListener(this);
            JIntellitype.getInstance().addIntellitypeListener(this);
            JIntellitype.getInstance().registerHotKey(1, JIntellitype.MOD_WIN + JIntellitype.MOD_CONTROL, 0);
            System.out.println("JIntellitype initialized");
        } catch (RuntimeException ex) {
            System.out.println("Either you are not on Windows, or there is a problem with the JIntellitype library!");
            System.out.println("Replace JIntellitype.dll in the root of the project with the corresponding dll"
                    + " in the Jintellitype 32bit or 64bit folders");
        }
    }

    /**
     * Creates a Panel that contains a label for messages. This Panel should be
     * located at the bottom of this Frame. Will be deleted when a new UI is implemented
     *
     * @return a Panel that contains a label for messages
     */
    private JPanel createMessagePanel() {
        JPanel messagePanel = getJPanel(new BorderLayout());
        messageTextField = new JTextArea("Loading...");
        messageTextField.setBackground(backgroundColor);
        messageTextField.setForeground(Color.WHITE);
        messageTextField.setEditable(false);
        messageTextField.setWrapStyleWord(true);
        messageTextField.setLineWrap(true);
        messageTextField.setBorder(new EmptyBorder(1, 1, 1, 1));
        messagePanel.add(messageTextField, BorderLayout.CENTER);
        return messagePanel;
    }
}