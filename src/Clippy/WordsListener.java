/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Clippy;

/**
 * An interface for word listeners
 */
public interface WordsListener
{

    /**
     * Invoked when a new word is recognized
     */
    void notify(String word);
}
