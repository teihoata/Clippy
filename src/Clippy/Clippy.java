/*
 * Main Clippy Class which initializes everything
 */
package Clippy;

import ClippyV2.ui.View.ClippyGui;

/** 
 * @author GavinC, Marcus Ball
 */
public class Clippy {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {
        ClippyGui newClip = new ClippyGui();
        newClip.pack();
        newClip.setVisible(true);
        Thread clipImg = new Thread(newClip);
        clipImg.start();
    }
}
