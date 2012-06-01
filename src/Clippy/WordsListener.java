/*
 * Interface used for creating word listeners
 */
package Clippy;

/**
 * An interface for word listeners
 */
public interface WordsListener
{

    /**
     * Called when a new word is recognized
     */
    void notify(String word);
}
