package ClippyV2.ui.View;
import Clippy.*;
import ClippyV2.ui.Dialog;
import ClippyV2.ui.Frame;
import ClippyV2.ui.RoundButton;
import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.IntellitypeListener;
import com.melloware.jintellitype.JIntellitype;
import edu.cmu.sphinx.util.props.ConfigurationManager;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
import javax.swing.border.EtchedBorder;

/**
 * The GUI of the Clippy Project 
 * @author GavinC, Marcus Ball
 */
public class ClippyGui extends Frame implements Runnable, IntellitypeListener, HotkeyListener
{
    private ImageIcon newImage = new ImageIcon(getClass()
    .getResource("Images/clippyIntro.gif"));
    private ImageIcon micIcon = new ImageIcon(getClass()
    .getResource("Images/mic.png")) ;
    private ImageIcon editIcon = new ImageIcon(getClass()
    .getResource("Images/menu.png"));
    private ImageIcon speechIcon = new ImageIcon(getClass()
    .getResource("Images/speech2.png"));
    private ImageIcon exitIcon = new ImageIcon(getClass()
    .getResource("Images/exitx.png"));
    private JPanel clipPnl;
    private JLabel imgLbl = new JLabel();
    private JLabel speechLbl = new JLabel();
    private JTextArea exeTxt = new JTextArea();
    private JTextArea clippyTxt;
    private RoundButton exitBtn = new RoundButton(exitIcon);
    private RoundButton voiceBtn = new RoundButton(micIcon);
    private RoundButton editBtn = new RoundButton(editIcon);
    private boolean isIdle;
    private boolean exeState;
    private boolean errorState;
    private boolean exitState;
    private Font txtFont = new Font("Arial", Font.BOLD, 14);
    private VoiceMenu voiceMenu;
    private WordRecognizer wordsRecognizer;
    private MyBehavior currentBehavior;
    private JLayeredPane lpane = new JLayeredPane();
    private Thread newThread;
    private MyGoogleSearchBehavior search;
    private boolean micEnabled;
    
    /**
     * Constructor for class ClippyGUI
     */
    public ClippyGui() throws InterruptedException{
        super(300,200,350,300);
        clippyTxt = new JTextArea("Hello " + "!");
        createPnl();
        setComVisible(false);
        isIdle = true;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        currentBehavior.setDefaultList();
        currentBehavior.updateList();
        micEnabled = true;
    }
    
     /**
      * Creates a panel for the components of the interface 
      */
     private void createPnl() throws InterruptedException{
        clipPnl = new JPanel();
        setPnl(lpane);
        setImage();
        setSpeechImg();
        addBtn();
        addClippyTxt();
        lpane.setOpaque(false);
        voiceMenu = new VoiceMenu(currentBehavior);
        voiceMenu.pack();
        voiceMenu.setVisible(true);
        setExitBtn();
        try {
            setup();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
     }
     
     /**
      * Initializes the Logic classes in the interface 
      * @throws IOException
      * @throws InterruptedException 
      */
     private void setup() throws IOException, InterruptedException
     {
         //Get the configuration from the xml resource
        URL url = WordRecognizer.class.getResource("clippy.config.xml");
        ConfigurationManager cm = new ConfigurationManager(url);
        wordsRecognizer = (WordRecognizer) cm.lookup("dialogManager");
        wordsRecognizer.setGui(this);
        MyBehavior menu = new MyBehavior(this);
        MyBehavior time = new MyBehavior(this);
        MyBehavior music = new MyMusicBehavior(this);
        MyBehavior movie = new MyMovieBehavior(this);
        MyBehavior desktop = new MyDesktopBehavior(this);
        MyBehavior website = new MyWebsiteBehavior(this);
        search = new MyGoogleSearchBehavior(this);
        
        //Add each menu node to the words to be recognised
        wordsRecognizer.addNode("menu", menu);
        wordsRecognizer.addNode("tell me the time", time);
        wordsRecognizer.addNode("music", music);
        wordsRecognizer.addNode("movies", movie);
        wordsRecognizer.addNode("desktop", desktop);
        wordsRecognizer.addNode("web", website);
        wordsRecognizer.addNode("search", search);
        currentBehavior = menu;
        
        setHeader("Loading IntelliJ");
        initJIntellitype();

        setHeader("Loading Dialogs");
        wordsRecognizer.allocate();

        setHeader("Running  ...");
        wordsRecognizer.addWordListener(new WordsListener() {

            public void notify(String word) {
                updateMenu(word);
            }
        });
        
        wordsRecognizer.setInitialNode("menu");
        voiceMenu.setWordsRecognizer(wordsRecognizer);
     }
     
    /**
     * Updates the voice menu header 
     * @param update the header 
     */
    public void setHeader(String update)
    {
        voiceMenu.setHeaderUpdate(update);
    }
     
    /**
     * Gets the current logic behavior 
     * @return behavior
     */
    public MyBehavior getCurrentBehavior()
    {
        return currentBehavior;
    }
     
    /**
     * Gets the google search behavior 
     * @return the google search behavior 
     */
    public MyGoogleSearchBehavior getSearchBehavior()
    {
        return this.search;
    }
    
    /**
     * Sets the menu to the current menu 
     * @param menu the selected menu 
     */
    public void setCurrentMenu(ArrayList menu)
    {
       javax.swing.Timer timer = new Timer(500, null);
       timer.start();
       voiceMenu.setVoiceMenu(menu);
    }
    
    /**
     * Sets and switch to the current behavior 
     * @param behav the selected behavior 
     */
    public void setCurrentBehavior(MyBehavior behav)
    {
        currentBehavior = behav;
        exeState = true;
        setClippyTxt("Entering " + currentBehavior.getName());
        voiceMenu.setBehavior(currentBehavior);
    }
    
    /**
     * Updates the menu and enables the mic 
     * @param word the menu 
     */  
    private void updateMenu(final String word) {
       SwingUtilities.invokeLater(new Runnable() {
           @Override
           public void run() {
               voiceBtn.setEnabled(micEnabled);
           }
        });
    }
     
    /**
     * Called when the keys Win+CTRL are pressed
     * @param i 
     */
    @Override
    public void onHotKey(int i) {
        if (voiceBtn.isEnabled()) {
            voiceBtn.setEnabled(false);
            startListening();
        }
    }

    /**
     * Push the intellitype 
     * @param i 
     */
    @Override
    public void onIntellitype(int i) {
        if (i == JIntellitype.MOD_WIN) {
        }
    }
    
    /**
     * Initializes the Intellitype hot key 
     * @param i 
     */ 
    public void initJIntellitype() {
        try {
            // initialize JIntellitype with the frame so all windows commands can
            // be attached to this window
            JIntellitype.getInstance().addHotKeyListener(this);
            JIntellitype.getInstance().addIntellitypeListener(this);
            JIntellitype.getInstance().registerHotKey(1, JIntellitype.MOD_WIN + JIntellitype.MOD_CONTROL, 0);
            setClippyTxt("JIntellitype initialized");
        } catch (RuntimeException ex) {
            System.err.println("Either you are not on Windows, or there is a problem with the JIntellitype library!");
            System.err.println("Replace JIntellitype.dll in the root of the project with the corresponding dll"
                    + " in the Jintellitype 32bit or 64bit folders");
        }
    }
     
    /**
     * Sets the image to the panel
     */
    public void setImage(){
        try{
            imgLbl.setIcon(newImage);
            imgLbl.setBounds(120, 40,134,150); 
            imgLbl.setOpaque(false);
            lpane.add(imgLbl, new Integer(0), 0);
            imgLbl.setVisible(true);
        }  
        catch(NullPointerException ex){
            System.err.println("Cannot find image");
        }
    }
    
    /**
     * Set and add the speech bubble image
     */
    public void setSpeechImg(){
        try{
            speechLbl.setIcon(speechIcon);
            speechLbl.setBounds(3, 10,150,113); 
            speechLbl.setOpaque(false);
            lpane.add(speechLbl, new Integer(1), 0);
            speechLbl.setVisible(true);
        }  
        catch(NullPointerException ex){
            System.err.println("Cannot find image");
        }
    }
    
    /**
     * Adds the buttons to the panel
     */
    public void addBtn(){
        setVoiceBtn();
        setEditBtn();
        setExitBtn();
    }
    
    
    /**
     * Creates voice button and set the action event 
     */
    private void setVoiceBtn(){
        voiceBtn.setBounds(235, 145, 45, 45);
        voiceBtn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                isIdle = false;
                 if (voiceBtn.isEnabled()) {
                    voiceBtn.setEnabled(false);
                    startListening();
                }
            }
        });
        lpane.add(voiceBtn, new Integer(2), 0);
    }
    
    /**
     * Starts listening for words
     */
    private void startListening() {
        if (wordsRecognizer.microphoneOn()) {
            genImage("clippySearch");
            setClippyTxt("Listening...");
        }
        else {
            Thread speak = new Thread("No Microphone found.");
            speak.start();
            setClippyTxt("Couldn't find microphone");
            micEnabled = false;
            voiceBtn.setEnabled(false);
            
        }
    }
    
    /**
     * Creates edit button and set the action event 
     */
    private void setEditBtn(){
        editBtn.setEnabled(true);
        editBtn.setBounds(235, 40, 45, 45);
        editBtn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                if(voiceMenu.isVisible())
                {
                voiceMenu.setVisible(false);
                }
                else
                {
                    voiceMenu.setVisible(true);
                }
            }
        });
        lpane.add(editBtn, new Integer(2), 0);
    }
     
    
    /**
     * Creates exit button and set the action event 
     */
    private void setExitBtn(){
        exitBtn.setBounds(265,1 ,21,21);
        exitBtn.setVisible(false);
        exitBtn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){ 
                exitClippy();
            }     
        });
        lpane.add(exitBtn, new Integer(2), 0);
    }
     
    /**
     * Closing Clippy method and shows a dialog for confirmation 
     */
    public void exitClippy()
    {
        if(!exitState)
        {
        clipDialog = new Dialog("Are you sure you want to exit?");
                clipDialog.showOptionDialog();
                if(clipDialog.getConfirmation()){
                    isIdle = false;
                    exitState = true;  
                    clipDialog = new Dialog("Exiting please wait....");
                    clipDialog.showPlainDialog();
                }
                else{
                    isIdle = true;
                }
        }
    }
     
    /**
     * Creates the design and add the input field to the panel 
     */
    public void addField(){
        exeTxt.setBounds(55, 195, 300, 30);
        exeTxt.setFont(txtFont);
        exeTxt.setBorder(new EtchedBorder());
        clipPnl.add(exeTxt);
    }
    
    /**
     * Generate a random idle Clippy animation image
     */
    public void genIdle(){
        String fileName = "";
        try{
            //Generate random image of clippy when idle  
            Random imgGen = new Random();
            int imgNum = imgGen.nextInt(4);
            fileName = "clippy" + imgNum;
            newImage = new ImageIcon(getClass()
            .getResource("Images/" + fileName + ".gif"));
            //Generate random comments that clippy says when idle 
            if(imgNum == 0){
                setClippyTxt("Uploading Virus....");
            }
            else if(imgNum ==1){
                setClippyTxt("Are you there?");
            }
            else{
                setClippyTxt("I know you're there");
            }
        }
        catch(NullPointerException ex){   
          System.err.println("Cannot find image");
        }   
    }
   
    /**
     * Generate a specified image 
     * @param imgName 
     */
    public void genImage(String imgName){
        try{
            String img = imgName;
            newImage = new ImageIcon(getClass()
            .getResource("Images/" + img + ".gif"));   
        }
        catch(NullPointerException ex){   
          System.err.println("Cannot find image");
        }      
        setImage();
    }
    
    /**
     * A thread that controls adding and setting default content when clippy is idle 
     * @throws InterruptedException  
     */
    public void threadIdle() throws InterruptedException{
            genIdle();
            setImage();
            Thread.sleep(4500);      
    }
    
    /**
     * Create and add Clippy's output text 
     */
    public void addClippyTxt(){
        clippyTxt.setLineWrap(true);
        clippyTxt.setWrapStyleWord(true);
        clippyTxt.setEditable(false);
        clippyTxt.setFont(txtFont);
        clippyTxt.setAlignmentX(JTextArea.CENTER_ALIGNMENT);
        clippyTxt.setAlignmentY(JTextArea.CENTER_ALIGNMENT);
        clippyTxt.setBorder(null);
        clippyTxt.setSize(105,35);
        clippyTxt.setLocation(25, 45);
        clippyTxt.setOpaque(false);
        lpane.add(clippyTxt, new Integer(1), 0);
    }
    
    /**
     * Set the output text of Clippy
     * @param text Clippys output text/feedback
     */
    public void setClippyTxt(String text){  
        clippyTxt.setText(text);   
        javax.swing.Timer time = new Timer(1000, null);
        time.start();
    }
    
   
    /**
     * Set the visibility of the GUI components 
     * @param visible true if components are visible otherwise false
     */
    private void setComVisible(boolean visible){
         exitBtn.setVisible(visible);
         editBtn.setVisible(visible);
         voiceBtn.setVisible(visible);
    }

    /**
     * Runs the event when the user exits Clippy  
     */
    public void runExitSte(){
        try {
            clippyTxt.setVisible(false);
            speechLbl.setVisible(false);
            setComVisible(false);
            genImage("clippyExit");
            Thread.sleep(3800);
            System.exit(0);
        } 
        catch (InterruptedException ex) {
            
        }
    }
    
    /**
     * Runs the event when the user executes commands using input text  
     */
    public void runExeSte() throws InterruptedException{
        genImage("clippyVoice");
        newThread = new Thread(this);
        newThread.sleep(3800);
    }
    
    /**
     * Runs the user interface event when Clippy gets an error 
     */
    public void runErrorSte() throws InterruptedException{
        genImage("clippyError");
        setClippyTxt("What? Speak Again?");
        javax.swing.Timer time = new Timer(3000, null);
        time.start();
    }
    
    /**
     * Run the overall Clippy interface events 
     */  
    public void runClippy() throws InterruptedException{
        if(isIdle){
           threadIdle();  
        }  
        
        if(exitState){
            runExitSte();
        }
        
        else if(exeState){
            runExeSte();
            exeState = false;
        }
        
        else if(errorState){
            runErrorSte();
            errorState = false;             
        }
        else{
              
        }       
        Thread idleThread = new Thread();
        idleThread.sleep(3800); 
        isIdle = true;
        runClippy();
    }
    
    /**
     * Runs the Clippy thread 
     */
    @Override
    public void run() {
        try {
            Thread.sleep(3800);
            setComVisible(true);
            runClippy();
        } 
        catch (InterruptedException | ArrayIndexOutOfBoundsException ex) {
           System.err.println("Thread Error");     
        }
    }
    
}


    

