package ClippyV2.ui;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import javax.swing.ImageIcon;
import javax.swing.JButton;

/**
 * A Class that creates a round button 
 * @author GavinC 
 */
public class RoundButton extends JButton {
    
    Shape btnShape;
 /**
  * 
  * @param img the image icon of the button
  */
  public RoundButton(ImageIcon img) {
    super(img);
    setBackground(Color.lightGray);
// Creates a circle 
    Dimension size = getPreferredSize();
    size.width = size.height = Math.max(size.width, size.height);
    setPreferredSize(size);
// Round button can be paintable
    setContentAreaFilled(false);
  }

  /**
   * Paints the round button 
   * @param g graphics color  
   */
  protected void paintComponent(Graphics g) {
    if (getModel().isArmed()) {
      g.setColor(Color.gray);
    } else {
      g.setColor(getBackground());
    }
    g.fillOval(0, 0, getSize().width-1, 
      getSize().height-1);

// Paints the label
    super.paintComponent(g);
  }

  /**
   * Paints the border of the button 
   * @param g graphics color
   */
  protected void paintBorder(Graphics g) {
    g.setColor(getForeground());
    g.drawOval(0, 0, getSize().width-1, 
      getSize().height-1);
  }
  
  public boolean contains(int x, int y) {
// If the button has changed size, 
   // make a new shape object.
    if (btnShape == null || 
      !btnShape.getBounds().equals(getBounds())) {
      btnShape = new Ellipse2D.Float(0, 0, 
        getWidth(), getHeight());
    }
    return btnShape.contains(x, y);
  }
}
