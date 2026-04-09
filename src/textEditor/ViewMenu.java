package textEditor;

import java.awt.event.KeyEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

//Class for the View Menu and its items
public class ViewMenu extends JMenu {
	JMenuItem zoomIn;
	JMenuItem zoomOut;
	JMenuItem resetZoom;
	JCheckBoxMenuItem statusBar;
	JCheckBoxMenuItem lineNumbers;
	JCheckBoxMenuItem highlightLine;
	private TextEditor window;
	private MainPanel mainPanel;

	// Constructor
	public ViewMenu(TextEditor window, MainPanel mainPanel) {
		this.setText("View");
		this.window = window;
		this.mainPanel = mainPanel;

		// Create the menuItems
		zoomIn = new JMenuItem("Zoom In");
		zoomOut = new JMenuItem("Zoom Out");
		resetZoom = new JMenuItem("Reset Zoom");
		statusBar = new JCheckBoxMenuItem("Status Bar", true);
		lineNumbers = new JCheckBoxMenuItem("Line Numbers", true);
		highlightLine = new JCheckBoxMenuItem("Highlight Line", false);

		// Create Keyboard Shortcuts
		resetZoom.setAccelerator(KeyStroke.getKeyStroke("control 0"));
		zoomIn.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, KeyEvent.CTRL_DOWN_MASK));
		zoomOut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, KeyEvent.CTRL_DOWN_MASK));
		statusBar.setAccelerator(KeyStroke.getKeyStroke("control shift B"));
		lineNumbers.setAccelerator(KeyStroke.getKeyStroke("control L"));
		highlightLine.setAccelerator(KeyStroke.getKeyStroke("control shift L"));

		// Handle each menuItems actionEvent
		zoomIn.addActionListener(e -> mainPanel.zoomIn());
		zoomOut.addActionListener(e -> mainPanel.zoomOut());
		resetZoom.addActionListener(e -> mainPanel.resetZoom());
		statusBar.addActionListener(e -> mainPanel.toggleStatusBar());
		lineNumbers.addActionListener(e -> mainPanel.toggleLineNumbers());
		highlightLine.addActionListener(e -> mainPanel.toggleCurrentLineHighlight());

		// Add view menu items to the menu
		this.add(zoomIn);
		this.add(zoomOut);
		this.add(resetZoom);
		this.add(statusBar);
		this.add(lineNumbers);
		this.add(highlightLine);
	}
}
