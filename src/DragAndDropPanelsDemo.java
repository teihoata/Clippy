import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceMotionListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.TransferHandler;

/**
 * <p>Example of dragging and dropping panels in Java 5.</p>
 * <p>Everything kept in simple class for pursposes of simplicity.</p>
 * @author Bryan E. Smith - bryanesmith@gmail.com
 */
public class DragAndDropPanelsDemo extends JFrame {

    /**
     * Button user uses to add a RandomDragAndDropPanel to GUI.
     */
    private final JButton addPanelButton;
    
    /**
     * Constants for laying out the GUI.
     */
    protected final static int DEMO_HEIGHT = 500,  DEMO_WIDTH = 250,  PANEL_INSETS = 15;
    
    /**
     * Keep a list of the user-added panels so can re-add
     */
    private final List<RandomDragAndDropPanel> panels;
    
    /**
     * This is the panel that will hold everything.
     */
    private final DemoRootPanel rootPanel;
    
    /**
     * <p>This represents the data that is transmitted in drag and drop.</p>
     * <p>In our limited case with only 1 type of dropped item, it will be a panel object!</p>
     * <p>Note DataFlavor can represent more than classes -- easily text, images, etc.</p>
     */
    private static DataFlavor dragAndDropPanelDataFlavor = null;

    public DragAndDropPanelsDemo() throws Exception {
        final String title = "Drag and drop panels demo";
        this.setTitle(title);

        this.setBackground(Color.LIGHT_GRAY);

        // Create the root panel and add to this frame.
        rootPanel = new DemoRootPanel(DragAndDropPanelsDemo.this);
        rootPanel.setLayout(new GridBagLayout());
        this.add(rootPanel);

        // Create a list to hold all the panels
        panels = new ArrayList<RandomDragAndDropPanel>();

        // Create a button and add a listener, which will add the
        // draggable button for us.
        this.addPanelButton = new JButton("+ Add a panel");
        this.addPanelButton.addActionListener(new ActionListener() {
            
            /**
             * <p>When user clicks on button, create a new panel, add to list and relayout.</p>
             */
            public void actionPerformed(ActionEvent e) {
                RandomDragAndDropPanel p = new RandomDragAndDropPanel();

                // Add to list so will appear after next relayout
                // of panels
                getRandomDragAndDropPanels().add(p);

                // Relayout the panels.
                relayout();
            }
        });
        Dimension d = new Dimension(DEMO_WIDTH, 50);
        this.addPanelButton.setMinimumSize(d);
        this.addPanelButton.setPreferredSize(d);

        // Adds button. For subsequent calls, also adds any panels
        relayout();
    }

    /**
     * <p>Removes all components from our root panel and re-adds them.</p>
     * <p>This is important for two things:</p>
     * <ul>
     *   <li>Adding a new panel (user clicks on button)</li>
     *   <li>Re-ordering panels (user drags and drops a panel to acceptable drop target region)</li>
     * </ul>
     */
    protected void relayout() {

        // Create the constraints, and go ahead and set those
        // that don't change for components
        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.NONE;

        int row = 0;

        // Clear out all previously added items
        rootPanel.removeAll();

        // Add the button
        gbc.gridy = row++;
        rootPanel.add(addPanelButton, gbc);

        // Put a lot of room around panels so can drop easily!
        gbc.insets = new Insets(PANEL_INSETS, PANEL_INSETS, PANEL_INSETS, PANEL_INSETS);
        
        // Add the panels, if any
        for (RandomDragAndDropPanel p : getRandomDragAndDropPanels()) {
            gbc.gridy = row++;
            rootPanel.add(p, gbc);
        }

        // Add a vertical strut to push content to top.
        gbc.weighty = 1.0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = row++;
        Component strut = Box.createVerticalStrut(1);
        rootPanel.add(strut, gbc);

        this.validate();
        this.repaint();
    }

    /**
     * <p>Returns (creating, if necessary) the DataFlavor representing RandomDragAndDropPanel</p>
     * @return
     */
    public static DataFlavor getDragAndDropPanelDataFlavor() throws Exception {
        // Lazy load/create the flavor
        if (dragAndDropPanelDataFlavor == null) {
            dragAndDropPanelDataFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=RandomDragAndDropPanel");
        }

        return dragAndDropPanelDataFlavor;
    }

    /**
     * <p>Instantiate the demo.</p>
     * @param args
     */
    public static void main(String args[]) throws Exception {
        DragAndDropPanelsDemo dndp = new DragAndDropPanelsDemo();
        dndp.setSize(new Dimension(DEMO_WIDTH, DEMO_HEIGHT));
        dndp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dndp.setVisible(true);
    }

    /**
     * <p>Returns the List of user-added panels.</p>
     * <p>Note that for drag and drop, these will be cleared, and the panels will be added back in the correct order!</p>
     * @return
     */
    protected List<RandomDragAndDropPanel> getRandomDragAndDropPanels() {
        return panels;
    }
}
/**
 * <p>This is the panel within the GUI that holds the button and RandomDragAndDropPanel's.</p>
 * <p>You can see that this has a DropTarget associated with it!</p>
 * @author besmit
 */
class DemoRootPanel extends JPanel {
    private final DragAndDropPanelsDemo demo;
    DemoRootPanel(DragAndDropPanelsDemo demo) {
        super();
        
        // Need to keep reference so can later communicate with drop listener
        this.demo = demo;
        
        // Again, needs to negotiate with the draggable object
        this.setTransferHandler(new DragAndDropTransferHandler());
        
        // Create the listener to do the work when dropping on this object!
        this.setDropTarget(new DropTarget(DemoRootPanel.this, new DemoPanelDropTargetListener(DemoRootPanel.this)));
    }

    public DragAndDropPanelsDemo getDragAndDropPanelsDemo() {
        return demo;
    }
}
/**
 * <p>Panel with random number. Can be dragged and dropped.</p>
 * @author besmit
 */
class RandomDragAndDropPanel extends JPanel implements Transferable {

    final static Random random = new Random();
    
    private static int counter = 0;

    public RandomDragAndDropPanel() {

        // Add the listener which will export this panel for dragging
        this.addMouseListener(new DraggableMouseListener());
        
        // Add the handler, which negotiates between drop target and this 
        // draggable panel
        this.setTransferHandler(new DragAndDropTransferHandler());

        // Create a label with this panel's number
        counter++;
        JLabel valueLabel = new JLabel(String.valueOf(counter));
        valueLabel.setForeground(Color.WHITE);
        this.add(valueLabel);

        // Style it a bit to set apart from container
        this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        
        // Set a random background color so can easily distinguish
        switch (random.nextInt(6)) {
            case 0:
                this.setBackground(Color.BLUE);
                break;
            case 1:
                this.setBackground(Color.DARK_GRAY);
                break;
            case 2:
                this.setBackground(Color.GREEN);
                break;
            case 3:
                this.setBackground(Color.ORANGE);
                break;
            case 4:
                this.setBackground(Color.RED);
                break;
            case 5:
                this.setBackground(Color.BLACK);
                break;
        }

        // This won't take the entire width for easy drag and drop
        final Dimension d = new Dimension(DragAndDropPanelsDemo.DEMO_WIDTH - 2 * DragAndDropPanelsDemo.PANEL_INSETS, 30);
        this.setPreferredSize(d);
        this.setMinimumSize(d);
    }

    /**
     * <p>One of three methods defined by the Transferable interface.</p>
     * <p>If multiple DataFlavor's are supported, can choose what Object to return.</p>
     * <p>In this case, we only support one: the actual JPanel.</p>
     * <p>Note we could easily support more than one. For example, if supports text and drops to a JTextField, could return the label's text or any arbitrary text.</p>
     * @param flavor
     * @return
     */
    public Object getTransferData(DataFlavor flavor) {

        System.out.println("Step 7 of 7: Returning the data from the Transferable object. In this case, the actual panel is now transfered!");
        
        DataFlavor thisFlavor = null;

        try {
            thisFlavor = DragAndDropPanelsDemo.getDragAndDropPanelDataFlavor();
        } catch (Exception ex) {
            System.err.println("Problem lazy loading: " + ex.getMessage());
            ex.printStackTrace(System.err);
            return null;
        }

        // For now, assume wants this class... see loadDnD
        if (thisFlavor != null && flavor.equals(thisFlavor)) {
            return RandomDragAndDropPanel.this;
        }

        return null;
    }

    /**
     * <p>One of three methods defined by the Transferable interface.</p>
     * <p>Returns supported DataFlavor. Again, we're only supporting this actual Object within the JVM.</p>
     * <p>For more information, see the JavaDoc for DataFlavor.</p>
     * @return
     */
    public DataFlavor[] getTransferDataFlavors() {

        DataFlavor[] flavors = {null};
        
        System.out.println("Step 4 of 7: Querying for acceptable DataFlavors to determine what is available. Our example only supports our custom RandomDragAndDropPanel DataFlavor.");
        
        try {
            flavors[0] = DragAndDropPanelsDemo.getDragAndDropPanelDataFlavor();
        } catch (Exception ex) {
            System.err.println("Problem lazy loading: " + ex.getMessage());
            ex.printStackTrace(System.err);
            return null;
        }

        return flavors;
    }

    /**
     * <p>One of three methods defined by the Transferable interface.</p>
     * <p>Determines whether this object supports the DataFlavor. In this case, only one is supported: for this object itself.</p>
     * @param flavor
     * @return True if DataFlavor is supported, otherwise false.
     */
    public boolean isDataFlavorSupported(DataFlavor flavor) {

        System.out.println("Step 6 of 7: Verifying that DataFlavor is supported.  Our example only supports our custom RandomDragAndDropPanel DataFlavor.");
        
        DataFlavor[] flavors = {null};
        try {
            flavors[0] = DragAndDropPanelsDemo.getDragAndDropPanelDataFlavor();
        } catch (Exception ex) {
            System.err.println("Problem lazy loading: " + ex.getMessage());
            ex.printStackTrace(System.err);
            return false;
        }

        for (DataFlavor f : flavors) {
            if (f.equals(flavor)) {
                return true;
            }
        }

        return false;
    }
} // RandomDragAndDropPanelsDemo

/**
 * <p>Listener that make source draggable.</p>
 * <p>Thanks, source modified from: http://www.zetcode.com/tutorials/javaswingtutorial/draganddrop/</p>
 */
class DraggableMouseListener extends MouseAdapter {

    @Override()
    public void mousePressed(MouseEvent e) {
        System.out.println("Step 1 of 7: Mouse pressed. Going to export our RandomDragAndDropPanel so that it is draggable.");
        
        JComponent c = (JComponent) e.getSource();
        TransferHandler handler = c.getTransferHandler();
        handler.exportAsDrag(c, e, TransferHandler.COPY);
    }
} // DraggableMouseListener

/**
 * <p>Used by both the draggable class and the target for negotiating data.</p>
 * <p>Note that this should be set for both the draggable object and the drop target.</p>
 * @author besmit
 */
class DragAndDropTransferHandler extends TransferHandler implements DragSourceMotionListener {

    public DragAndDropTransferHandler() {
        super();
    }

    /**
     * <p>This creates the Transferable object. In our case, RandomDragAndDropPanel implements Transferable, so this requires only a type cast.</p>
     * @param c
     * @return
     */
    @Override()
    public Transferable createTransferable(JComponent c) {

        System.out.println("Step 3 of 7: Casting the RandomDragAndDropPanel as Transferable. The Transferable RandomDragAndDropPanel will be queried for acceptable DataFlavors as it enters drop targets, as well as eventually present the target with the Object it transfers.");
        
        // TaskInstancePanel implements Transferable
        if (c instanceof RandomDragAndDropPanel) {
            Transferable tip = (RandomDragAndDropPanel) c;
            return tip;
        }

        // Not found
        return null;
    }

    public void dragMouseMoved(DragSourceDragEvent dsde) {}

    /**
     * <p>This is queried to see whether the component can be copied, moved, both or neither. We are only concerned with copying.</p>
     * @param c
     * @return
     */
    @Override()
    public int getSourceActions(JComponent c) {
        
        System.out.println("Step 2 of 7: Returning the acceptable TransferHandler action. Our RandomDragAndDropPanel accepts Copy only.");
        
        if (c instanceof RandomDragAndDropPanel) {
            return TransferHandler.COPY;
        }
        
        return TransferHandler.NONE;
    }
} // DragAndDropTransferHandler

/**
 * <p>Listens for drops and performs the updates.</p>
 * <p>The real magic behind the drop!</p>
 */
class DemoPanelDropTargetListener implements DropTargetListener {

    private final DemoRootPanel rootPanel;
    
    /**
     * <p>Two cursors with which we are primarily interested while dragging:</p>
     * <ul>
     *   <li>Cursor for droppable condition</li>
     *   <li>Cursor for non-droppable consition</li>
     * </ul>
     * <p>After drop, we manually change the cursor back to default, though does this anyhow -- just to be complete.</p>
     */
    private static final Cursor droppableCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR),
            notDroppableCursor = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);

    public DemoPanelDropTargetListener(DemoRootPanel sheet) {
        this.rootPanel = sheet;
    }

    // Could easily find uses for these, like cursor changes, etc.
    public void dragEnter(DropTargetDragEvent dtde) {}
    public void dragOver(DropTargetDragEvent dtde) {
        if (!this.rootPanel.getCursor().equals(droppableCursor)) {
            this.rootPanel.setCursor(droppableCursor);
        }
    }
    public void dropActionChanged(DropTargetDragEvent dtde) {}
    public void dragExit(DropTargetEvent dte) {
        this.rootPanel.setCursor(notDroppableCursor);
    }

    /**
     * <p>The user drops the item. Performs the drag and drop calculations and layout.</p>
     * @param dtde
     */
    public void drop(DropTargetDropEvent dtde) {
        
        System.out.println("Step 5 of 7: The user dropped the panel. The drop(...) method will compare the drops location with other panels and reorder the panels accordingly.");
        
        // Done with cursors, dropping
        this.rootPanel.setCursor(Cursor.getDefaultCursor());
        
        // Just going to grab the expected DataFlavor to make sure
        // we know what is being dropped
        DataFlavor dragAndDropPanelFlavor = null;
        
        Object transferableObj = null;
        Transferable transferable = null;
        
        try {
            // Grab expected flavor
            dragAndDropPanelFlavor = DragAndDropPanelsDemo.getDragAndDropPanelDataFlavor();
            
            transferable = dtde.getTransferable();
            DropTargetContext c = dtde.getDropTargetContext();
            
            // What does the Transferable support
            if (transferable.isDataFlavorSupported(dragAndDropPanelFlavor)) {
                transferableObj = dtde.getTransferable().getTransferData(dragAndDropPanelFlavor);
            } 
            
        } catch (Exception ex) { /* nope, not the place */ }
        
        // If didn't find an item, bail
        if (transferableObj == null) {
            return;
        }
        
        // Cast it to the panel. By this point, we have verified it is 
        // a RandomDragAndDropPanel.
        RandomDragAndDropPanel droppedPanel = (RandomDragAndDropPanel)transferableObj;
        
        // Get the y offset from the top of the WorkFlowSheetPanel
        // for the drop option (the cursor on the drop)
        final int dropYLoc = dtde.getLocation().y;

        // We need to map the Y axis values of drop as well as other
        // RandomDragAndDropPanel so can sort by location.
        Map<Integer, RandomDragAndDropPanel> yLocMapForPanels = new HashMap<Integer, RandomDragAndDropPanel>();
        yLocMapForPanels.put(dropYLoc, droppedPanel);

        // Iterate through the existing demo panels. Going to find their locations.
        for (RandomDragAndDropPanel nextPanel : rootPanel.getDragAndDropPanelsDemo().getRandomDragAndDropPanels()) {

            // Grab the y value
            int y = nextPanel.getY();

            // If the dropped panel, skip
            if (!nextPanel.equals(droppedPanel)) {
                yLocMapForPanels.put(y, nextPanel);
            }
        }

        // Grab the Y values and sort them
        List<Integer> sortableYValues = new ArrayList<Integer>();
        sortableYValues.addAll(yLocMapForPanels.keySet());
        Collections.sort(sortableYValues);

        // Put the panels in list in order of appearance
        List<RandomDragAndDropPanel> orderedPanels = new ArrayList<RandomDragAndDropPanel>();
        for (Integer i : sortableYValues) {
            orderedPanels.add(yLocMapForPanels.get(i));
        }
        
        // Grab the in-memory list and re-add panels in order.
        List<RandomDragAndDropPanel> inMemoryPanelList = this.rootPanel.getDragAndDropPanelsDemo().getRandomDragAndDropPanels();
        inMemoryPanelList.clear();
        inMemoryPanelList.addAll(orderedPanels);
    
        // Request relayout contents, or else won't update GUI following drop.
        // Will add back in the order to which we just sorted
        this.rootPanel.getDragAndDropPanelsDemo().relayout();
    }
} // DemoPanelDropTargetListener
