/*
 * Parent Frame class which sets the common UI for Clippy
 */
package ClippyV2.ui;

import ClippyV2.ui.View.ComponentMover;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 * A Class that creates a customizable frame for the interface 
 * @author GavinC
 */
public abstract class Frame extends JFrame {
    private static final Color backgroundColor = Color.white;
    protected Button exitButton;
    private int xSize = 0;
    private int ySize = 0;
    protected Dialog clipDialog = null;
    
    /**
     * Constructor for class Frame 
     * @param sizeX the length of the frame 
     * @param sizeY the height of the frame 
     * @param locatX the x location of where you want to set the frame 
     * @param locatY the y location of where you want to set the frame 
     */
    public Frame(int sizeX, int sizeY, int locatX, int locatY){
        super();
        xSize = sizeX;
        ySize = sizeY;
        Shape shape = null;
        this.setUndecorated(true);
        shape = new RoundRectangle2D.Float(0, 0, this.getWidth(), this.getHeight(), 30, 30);
        this.setShape(shape);
        this.setPreferredSize(new Dimension(xSize,ySize));
        this.setLocation((int)java.awt.Toolkit.getDefaultToolkit()
        .getScreenSize().getWidth()-locatX,(int)java.awt.Toolkit
        .getDefaultToolkit().getScreenSize().getHeight()-locatY);
        this.setBackground(new Color(1.0f, 1.0f, 1.0f, 0.01f));
        this.setLayout(null);
        this.setResizable(false);
        this.setAlwaysOnTop(true);
        exitButton = new Button("Close");
        createExitBtn();
    }
    
    /**
     * Gets the background color of the frame 
     * @return the background color 
     */
    public Color getBgColor(){
        return backgroundColor;
    }
    
    /**
     * Sets the new size of the frame 
     * @param x the new width of the frame
     * @param y the new height of the frame
     */
    public void setNewSize(int x, int y){
        this.setPreferredSize(new Dimension(x,y));
    }
    
    /**
     * Sets the layout, mover and bounds of a panel and adds it to the frame
     * @param pnl the selected panel 
     */
    public void setPnl(JComponent pnl){
        new ComponentMover(this, pnl);
        pnl.setLayout(null);
        pnl.setBounds(0,0,xSize,ySize);
        this.add(pnl);
    }
    
    /**
     * Gets a created exit button 
     * @return exit button 
     */
    public Button getExitBtn(){
        return exitButton;
    }
    
    /**
     * Creates a default exit button that closes the frame
     */
    public final void createExitBtn(){
        exitButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){ 
               dispose();
            }     
        });           
    }  
}
