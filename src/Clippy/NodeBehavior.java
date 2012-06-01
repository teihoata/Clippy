/*
 * Provides the default voice behavior for each node.
 */
package Clippy;

import com.sun.speech.engine.recognition.BaseRecognizer;
import com.sun.speech.engine.recognition.BaseRuleGrammar;
import edu.cmu.sphinx.jsgf.JSGFGrammar;
import edu.cmu.sphinx.jsgf.JSGFGrammarException;
import edu.cmu.sphinx.jsgf.JSGFGrammarParseException;
import edu.cmu.sphinx.result.Result;
import java.io.IOException;
import javax.speech.EngineException;
import javax.speech.EngineStateError;
import javax.speech.recognition.GrammarException;
import javax.speech.recognition.RuleGrammar;
import javax.speech.recognition.RuleParse;

/**
 *
 * @author Marcus Ball
 */
public class NodeBehavior
{

    private WordRecognizer.DialogNode node;

    /**
     * Initializes the word recognizing node
     */
    public void onInit(WordRecognizer.DialogNode node)
    {
        this.node = node;
    }

    /**
     * Calls this method when the node is entered
     *
     * @throws JSGFGrammarException
     * @throws JSGFGrammarParseException
     */
    public void onEntry() throws IOException, JSGFGrammarParseException, JSGFGrammarException
    {
        //overriden
    }

    /**
     * Called when this node is ready to perform recognition
     */
    public void onReady()
    {
        //overriden
    }

    /*
     * Called with the recognition results. Should return a string representing
     * the name of the next node.
     */
    public String onRecognize(Result result) throws GrammarException
    {
        String tagString = getTagString(result);
        return tagString;
    }

    public boolean processResult(String result)
    {
        return false;
    }

    /*
     * Called with the recognition results. Should return a string representing
     * the name of the next node.
     */
    public String onRecognizeFromString(String result) throws GrammarException
    {
        String tagString = getTagStringFromString(result);
        return onRecognizeByString(result);
    }

    /**
     * Called when this node is no lnoger the active node
     */
    public void onExit()
    {
    }

    /**
     * @return the name of the node
     */
    public String getName()
    {
        return node.getName();
    }

    /**
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
     * @throws GrammarException
     */
    RuleParse getRuleParse(Result result) throws GrammarException
    {
        String resultText = result.getBestFinalResultNoFiller();
        BaseRecognizer jsapiRecognizer = new BaseRecognizer(getGrammar().getGrammarManager());
        try
        {
            jsapiRecognizer.allocate();
        } catch (EngineException | EngineStateError e)
        {
            System.err.println("failed to allocate jsapi recognizer: " + e.getMessage());
        }
        RuleGrammar ruleGrammar = new BaseRuleGrammar(jsapiRecognizer, getGrammar().getRuleGrammar());
        RuleParse ruleParse = ruleGrammar.parse(resultText, null);
        return ruleParse;
    }

    /**
     * featches the rules associated with the string
     *
     * @param result
     * @return
     * @throws GrammarException
     */
    RuleParse getRuleParseFromString(String result) throws GrammarException
    {
        BaseRecognizer jsapiRecognizer = new BaseRecognizer(getGrammar().getGrammarManager());
        try
        {
            jsapiRecognizer.allocate();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        RuleGrammar ruleGrammar = new BaseRuleGrammar(jsapiRecognizer, getGrammar().getRuleGrammar());
        RuleParse ruleParse = ruleGrammar.parse(result, null);
        return ruleParse;
    }

    /**
     * featches the tag from the string
     *
     * @param result
     * @return
     * @throws GrammarException
     */
    String getTagStringFromString(String result) throws GrammarException
    {
        RuleParse ruleParse = getRuleParseFromString(result);
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
     * Overriden method that recognizes the result given a string
     *
     * @param result
     * @return
     * @throws GrammarException
     */
    public String onRecognizeByString(String result) throws GrammarException
    {
        return "";
    }

    /**
     * Gets a tag associated with the result
     *
     * @param result the recognition result
     * @return the tag string
     * @throws GrammarException
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
}
