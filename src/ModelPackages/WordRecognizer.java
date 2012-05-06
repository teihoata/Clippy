package ModelPackages;

/**
 * Manages the speech recognition for Word Collection
 */


import com.sun.speech.engine.recognition.BaseRecognizer;
import com.sun.speech.engine.recognition.BaseRuleGrammar;
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
import java.util.logging.Logger;
import javax.speech.recognition.GrammarException;
import javax.speech.recognition.RuleGrammar;
import javax.speech.recognition.RuleParse;

/**
 * @author Marcus Ball
 */
public class WordRecognizer implements Runnable, Configurable {

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
    @S4Component(type = JSGFGrammar.class)
    public final static String PROP_JSGF_GRAMMAR = "jsgfGrammar";
    @S4Component(type = Microphone.class)
    public final static String PROP_MICROPHONE = "microphone";  
    @S4Component(type = Recognizer.class)
    public final static String PROP_RECOGNIZER = "recognizer";
    
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
            URL url = WordCollection.class.getResource("clippy.config.xml");
            if (url == null)
            {
                throw new IOException("Can't find config xml file");
            }
            ConfigurationManager cm = new ConfigurationManager(url);
            recognizer = (Recognizer) cm.lookup("recognizer");
            microphone = (Microphone) cm.lookup("microphone");
        }
        catch (PropertyException e)
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
     * Performs a single recognition, called when the button is pressed or JIntelli
     * JIntellitype is initialised
     */
    @Override
    public void run()
    {
        microphone.clear();
        microphone.startRecording();
        //play the entry sound to signify to the user that recognition has started
        try {
            AePlayWave aw = new AePlayWave("./models/siri_entry.wav");
            aw.start();
        } catch (Exception e) {
            System.out.println("Couldn't find siri entry sound");
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
            }
            catch (NullPointerException e)
            {
            }
            //if it doesn't understand what you say
            if (nextStateName == null || nextStateName.isEmpty())
            {
                try
                    {
                        AePlayWave aw = new AePlayWave("./models/siri_notHeard.wav");
                        aw.start();
                    }
                    catch (Exception e)
                    {
                    }
                fireListeners(null);
            }
            else
            {
                fireListeners(nextStateName);
                DialogNode node = nodeMap.get(nextStateName);
                //Invalid dialog heard
                if (node == null)
                {
                    System.out.println("Can't transition to unknown state "
                            + nextStateName);
                }
                else
                {
                    try
                    {
                        AePlayWave aw = new AePlayWave("./models/siri_tone.wav");
                        aw.start();
                    }
                    catch (Exception e){
                        System.out.println("Couldn't play tone");
                    }
                    curNode = node;  
                    curNode.enter();
                }
            }
        }
        catch (IOException ioe)
        {
            error("problem loading grammar in state " + curNode.getName()
                    + ' ' + ioe);
        }
        catch (JSGFGrammarParseException ex)
        {
        }
        catch (JSGFGrammarException ex)
        {
        }
        catch (GrammarException ex)
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
    private synchronized void fireListeners(String recogWord)
    {
        for (WordsListener word : wordListeners)
        {
            word.notify(recogWord);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * edu.cmu.sphinx.util.props.Configurable#newProperties(edu.cmu.sphinx.util.props.PropertySheet)
     */
    @Override
    public void newProperties(PropertySheet ps) throws PropertyException
    {
        logger = ps.getLogger();
        grammar =(JSGFGrammar) ps.getComponent(PROP_JSGF_GRAMMAR);
        microphone = (Microphone) ps.getComponent(PROP_MICROPHONE);
        recognizer = (Recognizer) ps.getComponent(PROP_RECOGNIZER);
    }

    /**
     * Adds a new node to the dialog manager. The dialog manager maintains a set
     * of dialog nodes. When a new node is added the application specific beh
     *
     * @param name the name of the node
     * @param behavior the application specified behavior for the node
     */
    public void addNode(String name, DialogNodeBehavior behavior)
    {
        DialogNode node = new DialogNode(name, behavior);
        putNode(node);
    }

    /**
     * Sets the name of the initial node for the dialog manager
     *
     * @param name the name of the initial node. Must be the name of a
     * previously added dialog node.
     */
    public void setInitialNode(String name)
    {
        if (getNode(name) == null)
        {
            throw new IllegalArgumentException("Unknown node " + name);
        }
        initialNode = getNode(name);
        curNode = initialNode;
        ((MyBehavior)curNode.behavior).help();
    }

    /**
     * Gets the recognizer and the dialog nodes ready to run
     *
     * @throws IOException if an error occurs while allocating the recognizer.
     */
    public void allocate() throws IOException
    {
        recognizer.allocate();

        for (DialogNode node : nodeMap.values())
        {
            node.init();
        }
    }

    /**
     * Releases all resources allocated by the dialog manager
     */
    public void deallocate()
    {
        recognizer.deallocate();
    }

    /**
     * Invokes the dialog manager. The dialog manager begin to process the
     * dialog states starting at the initial node. This method will not return
     * until the dialog manager is finished processing states
     *
     * @throws JSGFGrammarException
     * @throws JSGFGrammarParseException
     */
    public void go() throws JSGFGrammarParseException, JSGFGrammarException
    {
    }

    /**
     * Returns the name of this component
     *
     * @return the name of the component.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Gets the dialog node with the given name
     *
     * @param name the name of the node
     */
    private DialogNode getNode(String name)
    {
        return nodeMap.get(name);
    }

    /**
     * Puts a node into the node map
     *
     * @param node the node to place into the node map
     */
    private void putNode(DialogNode node)
    {
        nodeMap.put(node.getName(), node);
    }

    /**
     * Issues a warning message
     *
     * @param s the message
     */
    private void warn(String s)
    {
        System.out.println("Warning: " + s);
    }

    /**
     * Issues an error message
     *
     * @param s the message
     */
    private void error(String s)
    {
        System.out.println("Error: " + s);
    }

    /**
     * Issues a tracing message
     *
     * @parma s the message
     */
    private void trace(String s)
    {
        System.out.println(s);
        logger.info(s);
    }

    public Recognizer getRecognizer()
    {
        return recognizer;
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
     * Represents a node in the dialog
     */
    class DialogNode {

        private DialogNodeBehavior behavior;
        private String name;

        /**
         * Creates a dialog node with the given name an application behavior
         *
         * @param name the name of the node
         *
         * @param behavior the application behavor for the node
         *
         */
        DialogNode(String name, DialogNodeBehavior behavior)
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
         * Enters the node, prepares it for recognition
         *
         * @throws JSGFGrammarException
         * @throws JSGFGrammarParseException
         */
        void enter() throws IOException, JSGFGrammarParseException, JSGFGrammarException
        {
            trace("Entering " + name);
            behavior.onEntry();
            behavior.onReady();
        }

        /**
         * Performs recognition at the node.
         *
         * @return the result tag
         */
        String recognize() throws GrammarException
        {
            trace("Recognize " + name);
            Result result = recognizer.recognize();
            return behavior.onRecognize(result);
        }

        String recognize(Result result) throws GrammarException
        {
            trace("Recognize " + name);
            return behavior.onRecognize(result);
        }

        /**
         * Exits the node
         */
        void exit()
        {
            trace("Exiting " + name);
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
         * Returns the JSGF Grammar for the dialog manager that contains this
         * node
         *
         * @return the grammar
         */
        public JSGFGrammar getGrammar()
        {
            return grammar;
        }

        /**
         * Traces a message
         *
         * @param msg the message to trace
         */
        public void trace(String msg)
        {
            WordRecognizer.this.trace(msg);
        }

        public WordRecognizer getDialogManager()
        {
            return WordRecognizer.this;
        }
    }
}

/**
 * Provides the default behavior for dialog node. Applications will typically
 * extend this class and override methods as appropriate
 */
class DialogNodeBehavior {

    private WordRecognizer.DialogNode node;

    /**
     * Called during the initialization phase
     *
     * @param node the dialog node that the behavior is attached to
     */
    public void onInit(WordRecognizer.DialogNode node)
    {
        this.node = node;
    }

    /**
     * Called when this node becomes the active node
     *
     * @throws JSGFGrammarException
     * @throws JSGFGrammarParseException
     */
    public void onEntry() throws IOException, JSGFGrammarParseException, JSGFGrammarException
    {
        trace("Entering " + getName());
    }

    /**
     * Called when this node is ready to perform recognition
     */
    public void onReady()
    {
        trace("Ready " + getName());
    }

    /*
     * Called with the recognition results. Should return a string representing
     * the name of the next node.
     */
    public String onRecognize(Result result) throws GrammarException
    {
        String tagString = getTagString(result);
        trace("Recognize result: " + result.getBestFinalResultNoFiller());
        trace("Recognize tag   : " + tagString);
        return tagString;
    }

    /**
     * Called when this node is no lnoger the active node
     */
    public void onExit()
    {
        trace("Exiting " + getName());
    }

    /**
     * Returns the name for this node
     *
     * @return the name of the node
     */
    public String getName()
    {
        return node.getName();
    }

    /**
     * Returns the string representation of this object
     *
     * @return the string representation of this object
     */
    public String toString()
    {
        return "Node " + getName();
    }

    /**
     * Retrieves the grammar associated with this ndoe
     *
     * @return the grammar
     */
    public JSGFGrammar getGrammar()
    {
        return node.getGrammar();
    }

    /**
     * Retrieves the rule parse for the given result
     *
     * @param the recognition result
     * @return the rule parse for the result
     * @throws GrammarException if there is an error while parsing the result
     */
    RuleParse getRuleParse(Result result) throws GrammarException
    {
        String resultText = result.getBestFinalResultNoFiller();
        BaseRecognizer jsapiRecognizer = new BaseRecognizer(getGrammar().getGrammarManager());
        try
        {
            jsapiRecognizer.allocate();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        RuleGrammar ruleGrammar = new BaseRuleGrammar(jsapiRecognizer, getGrammar().getRuleGrammar());
        RuleParse ruleParse = ruleGrammar.parse(resultText, null);
        return ruleParse;
    }

    /**
     * Gets a space delimited string of tags representing the result
     *
     * @param result the recognition result
     * @return the tag string
     * @throws GrammarException if there is an error while parsing the result
     */
    String getTagString(Result result) throws GrammarException
    {
        RuleParse ruleParse = getRuleParse(result);
        if (ruleParse == null)
        {
            return null;
        }
        String[] tags = ruleParse.getTags();
        if (tags == null)
        {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (String tag : tags)
        {
            sb.append(tag).append(' ');
        }
        return sb.toString().trim();
    }

    /**
     * Outputs a trace message
     *
     * @param the trace message
     */
    void trace(String msg)
    {
        node.trace(msg);
    }
}

/**
 * A Dialog node behavior that loads a completely new grammar upon entry into
 * the node
 */
class NewGrammarDialogNodeBehavior extends DialogNodeBehavior {

    /**
     * creates a NewGrammarDialogNodeBehavior
     *
     * @param grammarName the grammar name
     */
    public NewGrammarDialogNodeBehavior()
    {
    }

    /**
     * Called with the dialog manager enters this entry
     *
     * @throws JSGFGrammarException
     * @throws JSGFGrammarParseException
     */
    public void onEntry() throws IOException, JSGFGrammarParseException, JSGFGrammarException
    {
        super.onEntry();
        getGrammar().loadJSGF(getGrammarName());
        
    }

    /**
     * Returns the name of the grammar. The name of the grammar is the same as
     * the name of the node
     *
     * @return the grammar name
     */
    public String getGrammarName()
    {
        return getName();
    }
}

/**
 * An interface for word listeners
 */
interface WordsListener {

    /**
     * Invoked when a new word is recognized
     */
    void notify(String word);
}
