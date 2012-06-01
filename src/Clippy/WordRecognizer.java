package Clippy;

/**
 * Manages the speech recognition for Clippy
 */
import ClippyV2.ui.View.ClippyGui;
import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.jsgf.JSGFGrammar;
import edu.cmu.sphinx.jsgf.JSGFGrammarException;
import edu.cmu.sphinx.jsgf.JSGFGrammarParseException;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.recognizer.Recognizer.State;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.speech.recognition.GrammarException;

/**
 * @author Marcus Ball
 */
public class WordRecognizer implements Runnable, Configurable
{

    public static boolean recording = false;
    public static boolean waitingForStop = false;
    private DialogNode initialNode;
    private Map<String, DialogNode> nodeMap = new HashMap<>();
    private String name;
    private List<WordsListener> wordListeners = new ArrayList<>();
    private DialogNode lastNode = null;
    private DialogNode curNode;
    public static boolean initialRun;
    private JSGFGrammar grammar;
    private Logger logger;
    private Recognizer recognizer;
    private Microphone microphone;
    //Get component configurations from clippy.config.xml file
    @S4Component(type = JSGFGrammar.class) public final static String PROP_JSGF_GRAMMAR = "jsgfGrammar";
    @S4Component(type = Microphone.class) public final static String PROP_MICROPHONE = "microphone";
    @S4Component(type = Recognizer.class) public final static String PROP_RECOGNIZER = "recognizer";
    private ClippyGui gui;

    /**
     * Creates the WordRecognizer.
     *
     * @throws IOException if an error occurs while loading resources
     */
    public WordRecognizer() throws IOException
    {
        initialRun = true;
        try
        {
            URL url = this.getClass().getResource("clippy.config.xml");
            if (url == null)
            {
                throw new IOException("Can't find config xml file");
            }
            ConfigurationManager cm = new ConfigurationManager(url);
            recognizer = (Recognizer) cm.lookup("recognizer");
            microphone = (Microphone) cm.lookup("microphone");
        } catch (PropertyException e)
        {
            throw new IOException("Problem configuring WordRecognizer " + e);
        }
    }

    /**
     * Turns on the microphone and starts recognition
     */
    public boolean microphoneOn()
    {
        if (microphone.getAudioFormat() == null)
        {
            return false;
        }
        else
        {
            new Thread(this).start();
            return true;
        }
    }

    /**
     * Turns off the microphone, ending the current recognition in progress
     */
    public void microphoneOff()
    {
        microphone.stopRecording();
    }

    /**
     * Releases recognition resources
     */
    public void shutdown()
    {
        microphoneOff();
        if (recognizer.getState() == State.ALLOCATED)
        {
            recognizer.deallocate();
        }
    }

    /**
     * enters the next menu node
     * @param nextState 
     */
    public void enterNextNode(String nextState)
    {
        try
        {
            if (curNode != lastNode)
            {
                if (lastNode != null)
                {
                    lastNode.exit();
                }
                curNode.enter();
                lastNode = curNode;
            }
            String nextStateName = curNode.recognizeByString(nextState);
            //if it doesn't understand what you say
            if (nextStateName == null || nextStateName.isEmpty())
            {
                gui.getCurrentBehavior().processResult(nextState);
                try
                {
                    AePlayWave aw = new AePlayWave("./models/siri_notHeard.wav");
                    aw.start();
                } catch (Exception e)
                {
                    System.err.println("Couldn't find siri_notHeard.wav");
                }
                executeListeners(null);
            }
            else
            {
                executeListeners(nextStateName);
                DialogNode node = nodeMap.get(nextStateName);
                //Invalid dialog heard
                if (node == null)
                {
                    System.err.println("Can't transition to unknown state "
                            + nextStateName);
                }
                else
                {
                    Thread speakMenuName = new Speak("Entering " + node.getName());
                    speakMenuName.start();
                    curNode = node;
                    curNode.enter();
                }
            }
        } catch (GrammarException | IOException | JSGFGrammarParseException | JSGFGrammarException ex)
        {
            Logger.getLogger(WordRecognizer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Performs a single recognition, called when the button is pressed or
     * JIntellitype is initialised
     */
    @Override
    public void run()
    {
        microphone.clear();
        microphone.startRecording();
        //play the entry sound to signify to the user that recognition has started
        try
        {
            AePlayWave aw = new AePlayWave("./models/siri_entry.wav");
            aw.start();
        } catch (Exception e)
        {
            System.err.println("Couldn't find siri entry sound");
        }
        //Try to recognise
        try
        {
            if (curNode != lastNode)
            {
                if (lastNode != null)
                {
                    lastNode.exit();
                }
                curNode.enter();
                lastNode = curNode;
            }
            String nextStateName = " ";
            try
            {
                nextStateName = curNode.recognize();
            } catch (NullPointerException e)
            {
                System.err.println("current node is null" + e.getMessage());
            }
            //if it doesn't understand what you say
            if (nextStateName == null || nextStateName.isEmpty())
            {
                try
                {
                    AePlayWave aw = new AePlayWave("./models/siri_notHeard.wav");
                    aw.start();
                    gui.runErrorSte();
                } catch (Exception e)
                {
                    System.err.println("Cannot find siri_notHeard.wav file");
                }
                executeListeners(null);
            }
            else if (nextStateName.equalsIgnoreCase("processed"))
            {
                executeListeners(null);
            }
            else
            {
                executeListeners(nextStateName);
                DialogNode node = nodeMap.get(nextStateName);
                //Invalid dialog heard
                if (node == null)
                {
                    System.err.println("Can't transition to unknown state "
                            + nextStateName);
                }
                else
                {
                    Thread speakMenuName = new Speak("Entering " + node.getName());
                    speakMenuName.start();
                    curNode = node;
                    curNode.enter();
                }
            }
        } catch (IOException ioe)
        {
            System.err.println("problem loading grammar in state " + curNode.getName()
                    + ' ' + ioe);
        } catch (JSGFGrammarParseException ex)
        {
        } catch (JSGFGrammarException ex)
        {
        } catch (GrammarException ex)
        {
        }
        microphone.stopRecording();
    }

    /**
     * Adds a listener that is called when ever a new word is recognized
     *
     * @param new wordlistener
     */
    public synchronized void addWordListener(WordsListener wordListener)
    {
        wordListeners.add(wordListener);
    }

    /**
     * Removes a previously added wordlistener
     *
     * @param wordlistener to remove
     */
    public synchronized void removeWordListener(WordsListener wordListener)
    {
        wordListeners.remove(wordListener);
    }

    /**
     * Invoke all added word listeners
     *
     * @param the recognised word listener
     */
    private synchronized void executeListeners(String recogWord)
    {
        for (WordsListener word : wordListeners)
        {
            word.notify(recogWord);
        }
    }

    /*
     * Default method from sphinx configurables, configures the properties from
     * a new properly sheet
     */
    @Override
    public void newProperties(PropertySheet ps) throws PropertyException
    {
        logger = ps.getLogger();
        grammar = (JSGFGrammar) ps.getComponent(PROP_JSGF_GRAMMAR);
        microphone = (Microphone) ps.getComponent(PROP_MICROPHONE);
        recognizer = (Recognizer) ps.getComponent(PROP_RECOGNIZER);
    }

    /**
     * Adds a new node to the map of menus
     *
     * @param name
     * @param behavior
     */
    public void addNode(String name, NodeBehavior behavior)
    {
        DialogNode node = new DialogNode(name, behavior);
        putNode(node);
    }

    /**
     * Sets initial node
     *
     * @param name 
     */
    public void setInitialNode(String name)
    {
        if (getNode(name) == null)
        {
            throw new IllegalArgumentException("Not a valid initial node" + name);
        }
        initialNode = getNode(name);
        curNode = initialNode;
        ((MyBehavior) curNode.behavior).reset();
    }

    /**
     * Starts the recognizer and the behavior nodes
     *
     * @throws IOException
     */
    public void allocate() throws IOException
    {
        recognizer.allocate();

        for (DialogNode node : nodeMap.values())
        {
            //initialize all nodes
            node.init();
        }
    }

    /**
     * De-allocates all resources from the recognizer
     */
    public void deallocate()
    {
        recognizer.deallocate();
    }

    /**
     * @return the name of the component.
     */
    public String getName()
    {
        return name;
    }

    /**
     * gets the node from the name
     * @param name the name of the node
     */
    private DialogNode getNode(String name)
    {
        return nodeMap.get(name);
    }

    /**
     * adds node to the list of nodes
     * @param node
     */
    private void putNode(DialogNode node)
    {
        nodeMap.put(node.getName(), node);
    }

    /**
     * Sets the recognizer
     *
     * @param recognizer the recognizer
     */
    public void setRecognizer(Recognizer recognizer)
    {
        this.recognizer = recognizer;
    }

    /**
     * sets the gui to be used
     * @param gui 
     */
    public void setGui(ClippyGui gui)
    {
        this.gui = gui;
    }

    /**
     * Represents a single 
     */
    class DialogNode
    {

        private NodeBehavior behavior;
        private String name;

        /**
         * Creates a dialog node
         * @param name 
         * @param behavior
         */
        DialogNode(String name, NodeBehavior behavior)
        {
            this.behavior = behavior;
            this.name = name;
        }

        /**
         * Initializes the node
         */
        void init()
        {
            behavior.onInit(this);
        }

        /**
         * Enters the node and get it ready for recognition
         *
         * @throws JSGFGrammarException
         * @throws JSGFGrammarParseException
         */
        void enter() throws IOException, JSGFGrammarParseException, JSGFGrammarException
        {
            behavior.onEntry();
            behavior.onReady();
        }

        /**
         * Does recognition
         *
         * @return the result tag string
         */
        String recognize() throws GrammarException
        {
            Result result = recognizer.recognize();
            return behavior.onRecognize(result);
        }

        /**
         * Recognized by string called by the list optionss
         * @param result
         * @return
         * @throws GrammarException 
         */
        String recognizeByString(String result) throws GrammarException
        {
            return behavior.onRecognizeFromString(result);
        }

        /**
         * Exits the node
         */
        void exit()
        {
            behavior.onExit();
        }

        /**
         * Gets the name of the node
         *
         * @return the name of the node
         */
        public String getName()
        {
            return name;
        }

        /**
         * @return the grammar
         */
        public JSGFGrammar getGrammar()
        {
            return grammar;
        }
    }
}