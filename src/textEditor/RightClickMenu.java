package textEditor;

import java.awt.event.KeyEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.undo.UndoManager;

//RightClickMenu class creates the popup menu for when the user right clicks on the program
public class RightClickMenu extends JPopupMenu {
	MainPanel mainPanel;
	JTextPane textPane;
	UndoManager undoManager;
	JMenuItem undoItem;
	JMenuItem redoItem;
	JMenuItem cutItem;
	JMenuItem copyItem;
	JMenuItem pasteItem;
	JMenuItem selectAllItem;
	JMenuItem boldItem;
	JMenuItem italicItem;
	JMenuItem zoomInItem;
	JMenuItem zoomOutItem;

	// Constructor
	public RightClickMenu(MainPanel mainPanel) {
		this.mainPanel = mainPanel;
		this.textPane = mainPanel.getTextPane();
		this.undoManager = mainPanel.undoManager;

		// Setup menu items
		undoItem = new JMenuItem("Undo");
		redoItem = new JMenuItem("Redo");
		cutItem = new JMenuItem("Cut");
		copyItem = new JMenuItem("Copy");
		pasteItem = new JMenuItem("Paste");
		selectAllItem = new JMenuItem("Select All");
		boldItem = new JMenuItem("Bold");
		italicItem = new JMenuItem("Italic");
		zoomInItem = new JMenuItem("Zoom In");
		zoomOutItem = new JMenuItem("Zoom Out");

		// Add shortcuts for displaying in the menu
		undoItem.setAccelerator(KeyStroke.getKeyStroke("control Z"));
		redoItem.setAccelerator(KeyStroke.getKeyStroke("control Y"));
		cutItem.setAccelerator(KeyStroke.getKeyStroke("control X"));
		copyItem.setAccelerator(KeyStroke.getKeyStroke("control C"));
		pasteItem.setAccelerator(KeyStroke.getKeyStroke("control V"));
		selectAllItem.setAccelerator(KeyStroke.getKeyStroke("control A"));
		boldItem.setAccelerator(KeyStroke.getKeyStroke("control B"));
		italicItem.setAccelerator(KeyStroke.getKeyStroke("control I"));
		zoomInItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, KeyEvent.CTRL_DOWN_MASK));
		zoomOutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, KeyEvent.CTRL_DOWN_MASK));

		// Add action listeners for each menu item
		undoItem.addActionListener(e -> {
			if (undoManager.canUndo())
				undoManager.undo();
		});
		redoItem.addActionListener(e -> {
			if (undoManager.canRedo())
				undoManager.redo();
		});
		cutItem.addActionListener(e -> textPane.cut());
		copyItem.addActionListener(e -> textPane.copy());
		pasteItem.addActionListener(e -> textPane.paste());
		selectAllItem.addActionListener(e -> textPane.selectAll());
		boldItem.addActionListener(e -> mainPanel.toggleBold());
		italicItem.addActionListener(e -> mainPanel.toggleItalic());
		zoomInItem.addActionListener(e -> mainPanel.zoomIn());
		zoomOutItem.addActionListener(e -> mainPanel.zoomOut());

		// Add each menu item to the menu. Separators included for grouping.
		add(undoItem);
		add(redoItem);
		addSeparator();
		add(cutItem);
		add(copyItem);
		add(pasteItem);
		add(selectAllItem);
		addSeparator();
		add(boldItem);
		add(italicItem);
		addSeparator();
		add(zoomInItem);
		add(zoomOutItem);
	}
}
