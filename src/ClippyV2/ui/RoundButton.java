package ClippyV2.ui;


import java.awt.*;
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
    setBackground(Color.white);
// Creates a circle 
    Dimension size = getPreferredSize();
    size.width = size.height = Math.max(size.width, size.height);
    setPreferredSize(size);
    setFocusPainted(false);
// Round button can be paintable
    setContentAreaFilled(false);
  }

  /**
   * Paints the round button 
   * @param g graphics color  
   */
  protected void paintComponent(Graphics g) {
      Graphics2D g2d = (Graphics2D) g;
    if (getModel().isArmed()) {
      g2d.setColor(Color.gray);
    } else {
      g2d.setColor(getBackground());
    }
    g2d.fillOval(0, 0, getSize().width-1, 
      getSize().height-1);
    
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
    RenderingHints.VALUE_ANTIALIAS_ON);

// Paints the label
    super.paintComponent(g2d);
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
