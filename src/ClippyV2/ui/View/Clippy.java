/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ClippyV2.ui.View;

/**
 *
 * @author Marzipan
 */
public class Clippy 
{
    public static void main(String[] args)
    {
        ClippyGui newClip = new ClippyGui("Gavin");
        newClip.pack();
        newClip.setVisible(true);
        Thread clipImg = new Thread(newClip);
        clipImg.start();
    }
   
}
