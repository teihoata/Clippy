/*
 * Copyright 1999-2004 Carnegie Mellon University.
 * Portions Copyright 2004 Sun Microsystems, Inc.
 * Portions Copyright 2004 Mitsubishi Electric Research Laboratories.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 *
 */

package ClippyAlpha1;

import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.IntellitypeListener;
import com.melloware.jintellitype.JIntellitype;
import com.sun.speech.engine.recognition.BaseRecognizer;
import com.sun.speech.engine.recognition.BaseRuleGrammar;
import edu.cmu.sphinx.jsgf.JSGFGrammarException;
import edu.cmu.sphinx.jsgf.JSGFGrammarParseException;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.TimerPool;
import edu.cmu.sphinx.util.props.ConfigurationManager;
import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.LayoutManager;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.speech.recognition.GrammarException;
import javax.speech.recognition.Rule;
import javax.speech.recognition.RuleGrammar;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import org.apache.commons.io.FileUtils;

/**
 * A JFrame class to demonstrate word recognition and organisation
 * @author Marcus Ball
 */
public class WordCollection extends JFrame implements HotkeyListener, IntellitypeListener {
    private final static Color backgroundColor = new Color(0x42, 0x42, 0x42);

    private JTextArea messageTextField;
    private JScrollPane messageScroll;
    private JButton speakButton;
    private String menu;

    private WordRecognizer wordsRecognizer;    
    private static final int WINDOWS_A = 88;

    /**
     * WordCollection constructor
     *
     */
    public WordCollection() {
        super("Clippy");
        this.setUndecorated(true);
        this.setAlwaysOnTop(true);
        this.setOpacity(0.5f);

        setSize(200, 250);
        System.out.println((int)java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth());
        this.setLocation((int)java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth()-200, (int)java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight()-280);
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
     *  Replaces the splash s4 logo with the map
     */
    public void displayMap() {
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
            setMessage(menu + "\n " + "Clippy is listening...");
        } else {
            setMessage(
            "Sorry, can't find the microphone on your computer.");
        }
    }

    /**
     * Perform any needed initializations and then enable the 'speak'
     * button, allowing recognition to proceed
     */
    public void go() throws IOException {
        URL  url = WordCollection.class.getResource("dialog.config.xml");
            
            ConfigurationManager cm = new ConfigurationManager(url);

            wordsRecognizer = (WordRecognizer)
                    cm.lookup("dialogManager");            
            
            wordsRecognizer.addNode("menu", new MyBehavior());
            wordsRecognizer.addNode("email", new MyBehavior());
            wordsRecognizer.addNode("games", new MyBehavior());
            wordsRecognizer.addNode("news", new MyBehavior());
            wordsRecognizer.addNode("music", new MyMusicBehavior());
            wordsRecognizer.addNode("movies", new MyBehavior());
            wordsRecognizer.addNode("books", new MyBehavior());
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
                public void notify(String word) {
                    updateMenu(word);
                }
            });

            displayMap();   
                setMessage("Ready when you are :)");
        } 

    /**
     * Update the display with the new menu information.
     *
     * @param The word recognised
     */
    private void updateMenu(final String word) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (word == null) {
                    setMessage("I didn't understand what you said");
                } else {
                    if (word == null) {
                        setMessage("Can't find " + word  + " in the database");
                    } else {
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
    public void setMessage(final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                messageTextField.setText(message);
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
     * Returns a JPanel with the given layout and custom background color.
     *
     * @param layout the LayoutManager to use for the returned JPanel
     *
     * @return a JPanel
     */
    private JPanel getJPanel(LayoutManager layout) {
        JPanel panel = getJPanel();
        panel.setLayout(layout);
        return panel;
    }

    /**
     * Returns a JPanel with the custom background color.
     *
     * @return a JPanel
     */
    private JPanel getJPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(backgroundColor);
        return panel;
    }

    /**
     * Constructs the main Panel of this LiveFrame.
     *
     * @return the main Panel of this LiveFrame
     */
    private JPanel createMainPanel() {
        JPanel mainPanel = getJPanel(new BorderLayout());
        speakButton = new JButton("Speak");
        speakButton.setEnabled(false);
        speakButton.setMnemonic('s');
        // if this is continuous mode, don't add the speak button
        // since it is not necessary
        
        mainPanel.add(createMessagePanel(), BorderLayout.CENTER);
        messageScroll = new JScrollPane(messageTextField);
        mainPanel.add(messageScroll,BorderLayout.CENTER);
        mainPanel.add(speakButton, BorderLayout.SOUTH);
        
        return mainPanel;
    }

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
            JIntellitype.getInstance().registerHotKey(WINDOWS_A, JIntellitype.MOD_WIN, 'A');
            System.out.println("JIntellitype initialized");
        } catch (RuntimeException ex) {
            System.out.println("Either you are not on Windows, or there is a problem with the JIntellitype library!");
        }
    }

    /**
     * Creates a Panel that contains a label for messages.
     * This Panel should be located at the bottom of this Frame.
     *
     * @return a Panel that contains a label for messages
     */
    private JPanel createMessagePanel() {
        JPanel messagePanel = getJPanel(new BorderLayout());
        messageTextField = new JTextArea
            ("Please wait while I'm loading...");
        
        messageTextField.setBackground(backgroundColor);
        messageTextField.setForeground(Color.WHITE);
        messageTextField.setEditable(false);
        messageTextField.setBorder(new EmptyBorder(1,1,1,1));
        messagePanel.add(messageTextField, BorderLayout.CENTER);
        
        return messagePanel;
    }

    /** 
     * Returns an ImageIcon, or null if the path was invalid. 
     *
     * @param path the path to the image resource.
     * @param description a description of the resource
     *
     * @return the image icon or null if the resource could not be
     * found.
     */
    protected ImageIcon createImageIcon(String path, String description) {
        URL imgURL = WordCollection.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, description);
        } else {
            return null;
        }
    }


    /**
     * Main Method
     */
    public static void main(String[] args) throws IOException {
        WordCollection wordCollect = new WordCollection();
        wordCollect.setVisible(true);
        wordCollect.go();
    }

    /**
 * Defines the standard behavior for a node. The standard behavior is: <ul> <li> On entry the set of sentences that can
 * be spoken is displayed. <li> On recognition if a tag returned contains the prefix 'dialog_' it indicates that control
 * should transfer to another dialog node. </ul>
 */
class MyBehavior extends NewGrammarDialogNodeBehavior {

    private Collection<String> sampleUtterances;


    /** Executed when we are ready to recognize */
    public void onReady() {
        super.onReady();
        help();
    }


    /**
     * Displays the help message for this node. Currently we display the name of the node and the list of sentences that
     * can be spoken.
     */
    protected void help() {
        menu  = (" ======== " + getGrammarName() + " =======\n");
        dumpSampleUtterances();
        System.out.println(" =================================");
    }


    /**
     * Executed when the recognizer generates a result. Returns the name of the next dialog node to become active, or
     * null if we should stay in this node
     *
     * @param result the recongition result
     * @return the name of the next dialog node or null if control should remain in the current node.
     */
    @Override
    public String onRecognize(Result result) throws GrammarException {
        String tag = super.onRecognize(result);
        
        if (tag != null) {

                System.out.println("\n "
                    + result.getBestFinalResultNoFiller() + '\n');
            
            if (tag.equals("exit")) {

                System.out.println("Goodbye! Thanks for visiting!\n");
                System.exit(0);
            
                
            }
            if (tag.equals("help")) {

                help();
            
            } else if (tag.equals("stats")) {
                TimerPool.dumpAll();
            } else if (tag.startsWith("goto_")) {
                return tag.replaceFirst("goto_", "");
            } else if (tag.startsWith("browse")) {
                execute(tag);
            }
        } else {

            System.out.println("\n Oops! didn't hear you.\n");
            
        }
        return null;
    }


    /**
     * execute the given command
     *
     * @param cmd the command to execute
     */
    private void execute(String cmd) {
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            // if we can't run the command, just fall back to
            // a non-working demo.
        }
    }

 /**
     * Collects the set of possible utterances.
     * <p/>
     * TODO: Note the current implementation just generates a large set of random utterances and tosses away any
     * duplicates. There's no guarantee that this will generate all of the possible utterances. (yep, this is a hack)
     *
     * @return the set of sample utterances
     */
    private Collection<String> collectSampleUtterances() {
        Set<String> set = new HashSet<String>();
        for (int i = 0; i < 100; i++) {
            String s = getGrammar().getRandomSentence();
            if (!set.contains(s)) {
                set.add(s);
            }
        }

        List<String> sampleList = new ArrayList<String>(set);
        Collections.sort(sampleList);
        return sampleList;
    }


    /** Dumps out the set of sample utterances for this node */
    private void dumpSampleUtterances() {
        if (sampleUtterances == null) {
            sampleUtterances = collectSampleUtterances();
        }

        for (String sampleUtterance : sampleUtterances) {
            menu += ("  " + sampleUtterance + "\n");
        }
        setMessage(menu);
    }


    /** Indicated that the grammar has changed and the collection of sample utterances should be regenerated. */
    protected void grammarChanged() {
        sampleUtterances = null;
    }
}
    
    /**
 * An extension of the standard node behavior for music. This node will add rules to the grammar based upon the contents
 * of the music.txt file. This provides an example of how to extend a grammar directly from code as opposed to writing
 * out a JSGF file.
 */

    

    class MyDesktopBehavior extends MyBehavior {
     private boolean stopped;  
         @Override
    public String onRecognize(Result result) throws GrammarException {
        String tag = super.onRecognize(result);
        String listen = result.getBestFinalResultNoFiller();
        if (listen.equals("change window")) {

                try {
                    Robot robot = new Robot();
                    System.out.println("Found");
                    stopped = false;
                    robot.keyPress(KeyEvent.VK_ALT);
                    robot.keyPress(KeyEvent.VK_TAB);   
                    long start = System.currentTimeMillis();
                    long end = start + 1000;  
                    while (System.currentTimeMillis() < end) {} 
                    robot.keyRelease(KeyEvent.VK_TAB);


                         
                         start = System.currentTimeMillis();
                         end = start + 500;  
                         while (System.currentTimeMillis() < end) {} 
                         robot.keyPress(KeyEvent.VK_TAB);
                         start = System.currentTimeMillis();
                         end = start + 500;  
                         while (System.currentTimeMillis() < end) {} 
                         robot.keyRelease(KeyEvent.VK_TAB);
//                         stopped = DialogManager.waitingForStop;
                                                         
                                  
                    robot.keyRelease(KeyEvent.VK_TAB);
                    robot.keyRelease(KeyEvent.VK_ALT);
                    System.out.println("Finished");
                } catch (AWTException ex) {
                    System.out.println(ex.getMessage());
                }
        }
   
        else if(listen.equalsIgnoreCase("stop"))
        {
            System.out.println("stopped");
            stopped = true;
        }
        return tag;
         }
    }

    class MyMusicBehavior extends MyBehavior {

    private List<String> songList = new ArrayList<String>();
    private Collection files;
    private File selectedFile;

    /** Creates a music behavior */
    MyMusicBehavior() throws IOException {
        songList.add("pause song");
        songList.add("play next song");
        songList.add("volume up");
        songList.add("volume down");
        File playList = new File("./src/ClippyAlpha1/playlist.txt");
        FileWriter write = new FileWriter(playList);
        System.out.println("C:\\Users\\" + System.getProperty("user.name") + "\\Music\\");
        File file = new File("C:\\Users\\" + System.getProperty("user.name") + "\\Music\\musicTest\\");
        String[] extensions = {"mp3", "wma", "aac"};
        files = FileUtils.listFiles(file, extensions, true);
        for (Iterator iterator = files.iterator(); iterator.hasNext();) {
                File file1 = (File) iterator.next();
                write.write(file1.getName());
                songList.add(file1.getName().substring(0, file1.getName().indexOf('.')));
                System.out.println("File = " + file1.getName().substring(0, file1.getName().indexOf('.')));
            }
        write.close();
    }
    
    /** Executed when we enter this node. Displays the active grammar 
     * @throws JSGFGrammarException 
     * @throws JSGFGrammarParseException */
    public void onEntry() throws IOException, JSGFGrammarParseException, JSGFGrammarException {
        super.onEntry();

        // now lets add our custom songs from the play list
        // First, get the JSAPI RuleGrammar
        BaseRecognizer recognizer = new BaseRecognizer(getGrammar().getGrammarManager());
        try {
            recognizer.allocate();
        } catch (Exception e) {
        }
        
        RuleGrammar ruleGrammar = new BaseRuleGrammar (recognizer, getGrammar().getRuleGrammar());

        // now lets add a rule for each song in the play list

        String ruleName = "song";
        int count = 1;

        try {
            for (String song : songList) {
                String newRuleName = ruleName + count;
                Rule newRule = null;
                if(song.equalsIgnoreCase("pause song") || song.equalsIgnoreCase("volume up") || song.equalsIgnoreCase("volume down"))
                {
                     newRule = ruleGrammar.ruleForJSGF(song
                    + " { " + newRuleName + " }");
                }
                else if( song.equalsIgnoreCase("play next song"))
                {
                    newRule = ruleGrammar.ruleForJSGF(song
                    + " { " + newRuleName + " }");
                }
                else
                {
                     newRule = ruleGrammar.ruleForJSGF("play " + song
                    + " { " + newRuleName + " }");
                }
                
                ruleGrammar.setRule(newRuleName, newRule, true);
                ruleGrammar.setEnabled(newRuleName, true);
                count++;
            }
            
        } catch (GrammarException ge) {
            System.out.println("Trouble with the grammar " + ge);
            throw new IOException("Can't add rules for playlist " + ge);
        }
        // now lets commit the changes
        getGrammar().commitChanges();
        grammarChanged();
    }
    
    @Override
    public String onRecognize(Result result) throws GrammarException {
        String next = "";
        //super.onRecognize(result);
        trace("Recognize result: " + result.getBestFinalResultNoFiller());
        String listen = result.getBestFinalResultNoFiller();
        System.out.println(listen);
        if(listen.equalsIgnoreCase("main menu") || listen.equalsIgnoreCase("menu"))
        {
            next = "menu";
        }
        else if(listen.startsWith("play") && !listen.equalsIgnoreCase("play next song"))
        {
        String substring = listen.substring(5);
        System.out.println(substring);
        selectedFile = null;
        for (Iterator iterator = files.iterator(); iterator.hasNext();) {
                File file1 = (File) iterator.next();
                if(file1.getName().substring(0, file1.getName().indexOf('.')).equalsIgnoreCase(substring))
                {
                    selectedFile = file1;
                    System.out.println(selectedFile);
                }
            }
            try {
                System.out.println("File Path : " + selectedFile.getAbsoluteFile() + "\\");
                Desktop.getDesktop().open(new File(selectedFile.getAbsoluteFile() + "\\"));
            } catch (Exception ex) {
                System.out.println(ex.getMessage() + "\n Will now try to open using windows media player executeable in program files x86");
                    try {
                        
                        Process process = new ProcessBuilder("C:\\Program Files (x86)\\Windows Media Player\\wmplayer.exe",selectedFile.getAbsolutePath()).start();
                    } catch (IOException ex1) {
                        System.out.println("Didn't work :( Trying one last option");
                        try {
                            Process process = new ProcessBuilder("C:\\Program Files\\Windows Media Player\\wmplayer.exe",selectedFile.getAbsolutePath()).start();
                        } catch (IOException ex2) {
                            System.out.println("Sorry cannot open music files");
                        }
                    }
                
            }
            next = "";
        }
            else if (listen.equalsIgnoreCase("pause song")) {
            try {
                Process process = new ProcessBuilder("./UniversalMediaRemote.exe","Play").start();
            } catch (IOException ex) {}
            }
            else if (listen.equalsIgnoreCase("volume up")) {
            try {
                for (int i = 0; i < 10; i++) {
                    Process process = new ProcessBuilder("./UniversalMediaRemote.exe","VUp").start();  
                }
                
            } catch (IOException ex) {}
            }
            else if (listen.equalsIgnoreCase("volume down")) {
            try {
                for (int i = 0; i < 10; i++) {
                    Process process = new ProcessBuilder("./UniversalMediaRemote.exe","VDown").start();
                }
                
            } catch (IOException ex) {}
            }
            else if(listen.equalsIgnoreCase("play next song"))
            {
                System.out.println("playing next song");
                ArrayList<File> list = new ArrayList(files);
                for (int i = 0; i < list.size(); i++) {
                  if(list.get(i).equals(selectedFile))
                {
                    if((i+1)<list.size())
                    {
                        
                    selectedFile = list.get(i+1);
                    System.out.println(selectedFile.getName());
                    i=list.size()+1;
                }
                    else
                    {
                        selectedFile = list.get(0);
                        i=list.size()+1;
                    }
                }
                  try {
                try {
                System.out.println("File Path : " + selectedFile.getAbsoluteFile() + "\\");
                Desktop.getDesktop().open(new File(selectedFile.getAbsoluteFile() + "\\"));
            } catch (Exception ex) {
                System.out.println(ex.getMessage() + "\n Will now try to open using windows media player executeable in program files x86");
                    try {
                        
                        Process process = new ProcessBuilder("C:\\Program Files (x86)\\Windows Media Player\\wmplayer.exe",selectedFile.getAbsolutePath()).start();
                    } catch (IOException ex1) {
                        System.out.println("Didn't work :( Trying one last option");
                        try {
                            Process process = new ProcessBuilder("C:\\Program Files\\Windows Media Player\\wmplayer.exe",selectedFile.getAbsolutePath()).start();
                        } catch (IOException ex2) {
                            System.out.println("Sorry cannot open music files");
                        }
                    }
                
            }
            } catch (Exception ex) {}
                
            }
            }
            
        
            return next;
        }
    
}
}



  

