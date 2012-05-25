/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
 *
 * @author GavinC
 */
public abstract class Frame extends JFrame {
    private static final Color backgroundColor = Color.white;
    protected Button exitButton;
    private int xSize = 0;
    private int ySize = 0;
    protected Dialog clipDialog = null;

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
        //this.setOpacity(0.2f);
        this.setLayout(null);
        this.setResizable(false);
        this.setAlwaysOnTop(true);
        exitButton = new Button("Close");
        createExitBtn();
    }
    
    public Color getBgColor(){
        return backgroundColor;
    }
    
    public void setNewSize(int x, int y){
        this.setPreferredSize(new Dimension(x,y));
    }
    
    public void setPnl(JComponent pnl){
        new ComponentMover(this, pnl);
        pnl.setLayout(null);
        pnl.setBounds(0,0,xSize,ySize);
        this.add(pnl);
    }
    
    public Button getExitBtn(){
        return exitButton;
    }
    
    public final void createExitBtn(){
        exitButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){ 
               dispose();
            }     
        });
                    
    }
    
}
