package ModelPackages;

import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.IntellitypeListener;
import com.melloware.jintellitype.JIntellitype;
import edu.cmu.sphinx.util.props.ConfigurationManager;
import java.awt.*;
import java.awt.event.*;
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
        System.out.println((int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth());
        this.setLocation((int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth() - 200, (int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 280);
        getContentPane().add(createMainPanel(), BorderLayout.CENTER);

        // exit if the window is closed

        addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e)
            {
                if (wordsRecognizer != null)
                {
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

            public void actionPerformed(ActionEvent ae)
            {
                if (speakButton.isEnabled())
                {
                    speakButton.setEnabled(false);
                    startListening();
                }
            }
        });
    }

    /**
     * updates the button on the gui
     */
    public void updateGui()
    {
        SwingUtilities.invokeLater(new Runnable() {

            public void run()
            {
                speakButton.setEnabled(true);
                validate();
                repaint();
            }
        });

    }

    /**
     * Starts listening for words
     */
    private void startListening()
    {
        if (wordsRecognizer.microphoneOn())
        {
            setMessage(menu + "\n " + "Clippy is listening...");
        }
        else
        {
            setMessage(
                    "Sorry, can't find the microphone on your computer.");
        }
    }
    
    public static void setCurrentMenu(String menu)
    {
        WordCollection.menu = menu;
    }
    
    public static void addToCurrentMenu(String menu)
    {
        WordCollection.menu += menu;
    }
            

    /**
     * Perform any needed initializations and then enable the 'speak' button,
     * allowing recognition to proceed
     */
    public void go() throws IOException
    {
        URL url = WordCollection.class.getResource("dialog.config.xml");

        ConfigurationManager cm = new ConfigurationManager(url);

        wordsRecognizer = (WordRecognizer) cm.lookup("dialogManager");

        wordsRecognizer.addNode("menu", new MyBehavior());
        wordsRecognizer.addNode("email", new MyBehavior());
        wordsRecognizer.addNode("games", new MyBehavior());
        wordsRecognizer.addNode("news", new MyBehavior());
        wordsRecognizer.addNode("music", new MyMusicBehavior());
        wordsRecognizer.addNode("movies", new MyMovieBehavior());
        wordsRecognizer.addNode("desktop", new MyDesktopBehavior());

        wordsRecognizer.setInitialNode("menu");

        setMessage("Starting recognizer...");
        wordsRecognizer.startup();

        System.out.println("Loading IntelliJ");

        initJIntellitype();

        System.out.println("Loading dialogs ...");
        wordsRecognizer.allocate();

        System.out.println("Running  ...");

        wordsRecognizer.addWordListener(new WordsListener() {

            public void notify(String word)
            {
                updateMenu(word);
            }
        });

        updateGui();
        setMessage("Ready when you are :)");
    }

    /**
     * Update the display with the new menu information.
     *
     * @param The word recognised
     */
    private void updateMenu(final String word)
    {
        SwingUtilities.invokeLater(new Runnable() {

            public void run()
            {
                if (word == null)
                {
                    //setMessage("I didn't understand what you said");
                    
                }
                else
                {
                    if (word == null)
                    {
                        setMessage("Can't find " + word + " in the database");
                    }
                    else
                    {
                        System.out.println(word);
                        setMessage(word);
                    }
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
    public static void setMessage(final String message)
    {
        SwingUtilities.invokeLater(new Runnable() {

            public void run()
            {
                messageTextField.setText(message);
            }
        });
    }
    
    public static void addToMessage(final String message)
    {
        SwingUtilities.invokeLater(new Runnable() {

            public void run()
            {
                messageTextField.setText(menu + "\n" + message);
                
            }
        });
    }

    /**
     * Enables or disables the "Speak" button.
     *
     * @param enable boolean to enable or disable
     */
    public void setSpeakButtonEnabled(final boolean enabled)
    {
        SwingUtilities.invokeLater(new Runnable() {

            public void run()
            {
                speakButton.setEnabled(enabled);
            }
        });
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

    @Override
    public void onHotKey(int i)
    {
        if (speakButton.isEnabled())
        {
            speakButton.setEnabled(false);
            startListening();
        }
    }

    @Override
    public void onIntellitype(int i)
    {
        if (i == JIntellitype.MOD_WIN)
        {
            System.out.println("Pushed");
        }
    }

    public void initJIntellitype()
    {
        try
        {
            // initialize JIntellitype with the frame so all windows commands can
            // be attached to this window
            JIntellitype.getInstance().addHotKeyListener(this);
            JIntellitype.getInstance().addIntellitypeListener(this);
            JIntellitype.getInstance().registerHotKey(1, JIntellitype.MOD_WIN + JIntellitype.MOD_CONTROL , 0);
            System.out.println("JIntellitype initialized");
        }
        catch (RuntimeException ex)
        {
            System.out.println("Either you are not on Windows, or there is a problem with the JIntellitype library!");
            System.out.println("Replace JIntellitype.dll in the root of the project with the corresponding dll"
                    + " in the Jintellitype 32bit or 64bit folders");
        }
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

    /**
     * Returns an ImageIcon, or null if the path was invalid.
     *
     * @param path the path to the image resource.
     * @param description a description of the resource
     *
     * @return the image icon or null if the resource could not be found.
     */
    protected ImageIcon createImageIcon(String path, String description)
    {
        URL imgURL = WordCollection.class.getResource(path);
        if (imgURL != null)
        {
            return new ImageIcon(imgURL, description);
        }
        else
        {
            return null;
        }
    }
}