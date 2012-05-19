/*
 *
 */
package ClippyV2.ui.View;
import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.IntellitypeListener;
import com.melloware.jintellitype.JIntellitype;
import edu.cmu.sphinx.util.props.ConfigurationManager;
import ClippyV2.ui.*;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.border.EtchedBorder;

/**
 * The GUI of the Clippy Project 
 * @author GavinC 
 */
public class ClippyGui extends Frame implements Runnable, IntellitypeListener, HotkeyListener
{
    private ImageIcon newImage = new ImageIcon(getClass()
    .getResource("Images/clippyIntro.gif"));
    private ImageIcon micIcon = new ImageIcon(getClass()
    .getResource("Images/mic.png")) ;
    private ImageIcon editIcon = new ImageIcon(getClass()
    .getResource("Images/edit.png"));
    private ImageIcon speechIcon = new ImageIcon(getClass()
    .getResource("Images/speech.png"));
    private ImageIcon exitIcon = new ImageIcon(getClass()
    .getResource("Images/exit.png"));
    private JPanel clipPnl = new JPanel();
    private JLabel imgLbl = new JLabel();
    private JLabel speechLbl = new JLabel();
    private JTextArea exeTxt = new JTextArea();
    private JTextArea clippyTxt;
    private JScrollPane clippyScroll;
    private Button searchBtn = new Button("Search");
    private Button exeBtn = new Button("Execute");
    private Button helpBtn = new Button("Help");
    private RoundButton exitBtn = new RoundButton(exitIcon);
    private RoundButton voiceBtn = new RoundButton(micIcon);
    private RoundButton editBtn = new RoundButton(editIcon);
    private String user = "";
    private boolean isIdle;
    private boolean exeState;
    private boolean searchState;
    private boolean errorState;
    private boolean exitState;
    private boolean voiceState;
    private Font txtFont = new Font("Castellar", Font.BOLD, 12);
    private HelpMenu helpMenu = new HelpMenu();
    private EditMenu editMenu = new EditMenu();
    private NavMenu navMenu = new NavMenu();
    private VoiceMenu voiceMenu = new VoiceMenu();
    private WordRecognizer wordsRecognizer;
    private MyBehavior currentBehavior;
    
    /**
     * Constructor for class ClippyGUI
     */
    public ClippyGui(String userName){
        super(350,300,350,300);
        user = userName;
        clippyTxt = new JTextArea("Hello " + user + "!");
  
            createPnl();
            setComVisible(false);
            isIdle = true;
            
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    
        
    }
    
    
    
 
    /**
     * Creates a panel for the components of the interface 
     */
     private void createPnl(){
        setPnl(clipPnl);
        setImage();
        addClippyTxt();
        setSpeechImg();
        addBtn();
        addField();
        clipPnl.setBackground(getBgColor());
        voiceMenu = new VoiceMenu();
        voiceMenu.pack();
        voiceMenu.setVisible(true);
//        ComponentMover cm = new ComponentMover();
        
        try {
            setup();
        } catch (IOException ex) {
            Logger.getLogger(ClippyGui.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
     private void setup() throws IOException
     {
         //Get the configuration from the xml resource
        URL url = WordCollection.class.getResource("clippy.config.xml");
        ConfigurationManager cm = new ConfigurationManager(url);
        wordsRecognizer = (WordRecognizer) cm.lookup("dialogManager");
        MyBehavior menu = new MyBehavior(this);
        MyBehavior time = new MyBehavior(this);
        MyBehavior music = new MyMusicBehavior(this);
        MyBehavior movie = new MyMovieBehavior(this);
        MyBehavior desktop = new MyDesktopBehavior(this);
        MyBehavior removeWeb = new MyWebsiteBehavior(this);
        MyBehavior website = new MyWebsiteBehavior(this);
        
        
        //Add each menu node to the words to be recognised
        wordsRecognizer.addNode("menu", menu);
        wordsRecognizer.addNode("tell me the time", time);
        wordsRecognizer.addNode("music", music);
        wordsRecognizer.addNode("movies", movie);
        wordsRecognizer.addNode("desktop", desktop);
        wordsRecognizer.addNode("web", website);
        wordsRecognizer.addNode("remove website", removeWeb);
        
        currentBehavior = menu;
   
        voiceMenu.setVoiceMenu("Loading IntelliJ");
        initJIntellitype();

        voiceMenu.setVoiceMenu("Loading dialogs ...");
        wordsRecognizer.allocate();

        voiceMenu.setVoiceMenu("Running  ...");

        wordsRecognizer.addWordListener(new WordsListener() {

            public void notify(String word) {
                updateMenu(word);
            }
        });

        wordsRecognizer.setInitialNode("menu");
        voiceMenu.setVoiceMenu(currentBehavior.getCurrentMenuOptions());
     }
     
     public void setCurrentMenu(String menu)
     {
         voiceMenu.setVoiceMenu(menu);
     }
    
     
     private void updateMenu(final String word) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                voiceBtn.setEnabled(true);
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
     * Sets the image to the panel
     */
    public void setImage(){
        try{
            imgLbl.setIcon(newImage);
            imgLbl.setBounds(120, 40,134,150); 
            clipPnl.add(imgLbl);
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
            clipPnl.add(speechLbl);
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
        setExeBtn();
        setEditBtn();
        setSearchBtn();
        setHelpBtn();
        setExitBtn();
    }
    
    
    /**
     * Creates voice button and set the action event 
     */
    private void setVoiceBtn(){
        voiceBtn.setBounds(260, 55, 45, 45);
        voiceBtn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                isIdle = false;
                voiceState = true;
                 if (voiceBtn.isEnabled()) {
                    voiceBtn.setEnabled(false);
                    startListening();
                }   
            }
        });
        clipPnl.add(voiceBtn);
    }
    
     /**
     * Starts listening for words
     */
    private void startListening() {
        if (wordsRecognizer.microphoneOn()) {
            //setMessage(menu + "\n " + "Clippy is listening...");
            setBtnEnabled(false);
            genImage("clippySearch");
            setClippyTxt("Listening...");
        }
        else {
            voiceMenu.setVoiceMenu("Sorry, can't find the microphone on your computer.");
        }
    }
    
    /**
     * Creates edit button and set the action event 
     */
    private void setEditBtn(){
        editBtn.setBounds(260, 110, 45, 45);
        editBtn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                try{
                    editMenu.dispose();
                    editMenu = new EditMenu();
                    editMenu.pack();
                    editMenu.setVisible(true);
                }
                catch(NullPointerException ex){
                    
                }   
            }
        });
        clipPnl.add(editBtn);
    }
    
    /**
     * Creates execute button and set the action event 
     */
    private void setExeBtn(){
        exeBtn.setLocation(70, 240);
        exeBtn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                isIdle = false;
                String txt = exeTxt.getText();
                if(txt.equals("")){   
                    errorState = true;      
                }
                else{
                    exeState = true;
                }               
            }
        });
        clipPnl.add(exeBtn);
    }
    
    /**
     * Creates search button and set the action event 
     */
    private void setSearchBtn(){
        searchBtn.setLocation(200, 240);
        searchBtn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){ 
                isIdle = false;  
                searchState = true;            
            }
        });
        clipPnl.add(searchBtn); 
    }
    
    /**
     * Creates help menu button and set the action event 
     */
    private void setHelpBtn(){
        helpBtn.setLocation(230, 170);
        helpBtn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                try{
                    helpMenu.dispose();
                    helpMenu = new HelpMenu();
                    helpMenu.pack();
                    helpMenu.setVisible(true);     
                }
                catch(NullPointerException ex){
                    
                }             
            }
        });
        clipPnl.add(helpBtn);
    }
    
    /**
     * Creates exit button and set the action event 
     */
    private void setExitBtn(){
        exitBtn.setBounds(300,1 ,21,21);
        exitBtn.setVisible(false);
        exitBtn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){ 
                exitClippy();
            }     
        });
        clipPnl.add(exitBtn);      
    }
    
    public void exitClippy()
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
     
    /**
     * Creates the design and add the input field to the panel 
     */
    public void addField(){
        exeTxt.setBounds(25, 195, 300, 30);
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
       // Test Clippy Image
        //System.out.println(fileName);    
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
        clippyScroll = new JScrollPane(clippyTxt);
        clippyScroll.setBackground(getBgColor());
        clippyScroll.setBorder(null);
        clippyScroll.setSize(119,38);
        clippyScroll.setLocation(16, 40);
        clipPnl.add(clippyScroll);
    }
    
    /**
     * Set the output text of Clippy
     * @param text Clippys output text/feedback
     */
    public void setClippyTxt(String text){
        clippyTxt.setText(text);
    }
    
    /**
     * Enable the user to enable buttons and disable them 
     * @param enabled true if button is enabled otherwise false
     */
    public void setBtnEnabled(boolean enabled){
        exitBtn.setEnabled(enabled);
        exeBtn.setEnabled(enabled);
        editBtn.setEnabled(enabled);
        helpBtn.setEnabled(enabled);
        searchBtn.setEnabled(enabled);
        voiceBtn.setEnabled(enabled);
    }
    /**
     * Set the visibility of the GUI components 
     * @param visible true if components are visible otherwise false
     */
    private void setComVisible(boolean visible){
         exitBtn.setVisible(visible);
         exeBtn.setVisible(visible);
         searchBtn.setVisible(visible);
         editBtn.setVisible(visible);
         voiceBtn.setVisible(visible);
         helpBtn.setVisible(visible);
    }

    /**
     * Runs the event when the user exits Clippy  
     */
    public void runExitSte(){
        try {
            clippyTxt.setVisible(false);
            clippyScroll.setVisible(false);
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
     * Runs the event when the user searches information using Clippy
     */
    public void runSearchSte() throws InterruptedException{
        setBtnEnabled(false);
        imgLbl.setVisible(false);
        genImage("clippySearch");
        setClippyTxt("Searching...");
    }
    
    /**
     * Runs the event when the user executes commands using input text  
     */
    public void runExeSte() throws InterruptedException{
        setBtnEnabled(false);
        imgLbl.setVisible(true);
        genImage("clippyVoice");
        setClippyTxt("Executing Command..."); 
        navMenu = new NavMenu();
        navMenu.pack();
        navMenu.setVisible(true);
    }
    
    /**
     * Runs the user interface event when Clippy gets an error 
     */
    public void runErrorSte() throws InterruptedException{
        setBtnEnabled(false);
        genImage("clippyError");
        setClippyTxt("Cannot Execute Error!");
        
        if(voiceState){
            setClippyTxt("What? Speak Again?");
        }
        else{
            
        }
        Thread.sleep(3800);
    }
    
    /**
     * Run the overall Clippy interface events 
     */
    
    public void runClippy() throws InterruptedException{
        while(isIdle){
            try {
                threadIdle();
            } 
            catch (InterruptedException ex) {
      
            }
        }
        if(exeState){
            runExeSte();
            exeState = false;
        }
        
        else if(exitState){
            runExitSte();
        }
        else if(searchState){
            runSearchSte();
            searchState = false;         
        }
        else if(errorState){
            runErrorSte();
            errorState = false;             
        }
        else if(voiceState){
            voiceState = false;
        }
        else{
              
        }       
        Thread.sleep(3800); 
        setBtnEnabled(true);
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


    

