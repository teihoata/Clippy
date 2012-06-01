package Clippy;

import edu.cmu.sphinx.jsgf.JSGFGrammarException;
import edu.cmu.sphinx.jsgf.JSGFGrammarParseException;
import java.io.IOException;

/**
 * Parent class of MyBehavior 
 * @author Marcus Ball
 */
public class NewGrammarBehavior extends NodeBehavior
{
    /**
     * simple constructor
     */
    public NewGrammarBehavior()
    {
    }

    /**
     * Called when entered
     */
    public void onEntry() throws IOException, JSGFGrammarParseException, JSGFGrammarException
    {
        super.onEntry();
        //Load the grammar for the name of the behavior
        getGrammar().loadJSGF(getGrammarName());
    }

    /**
     * @return the grammar name
     */
    public String getGrammarName()
    {
        return getName();
    }
}

