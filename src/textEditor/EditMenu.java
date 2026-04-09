package textEditor;

import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.undo.UndoManager;

//Class for the Edit Menu and its items
public class EditMenu extends JMenu {
	JMenuItem undo;
	JMenuItem redo;
	JMenuItem cut;
	JMenuItem copy;
	JMenuItem paste;
	JMenuItem selectAll;
	JMenuItem search;
	private UndoManager undoManager;
	private MainPanel mainPanel;
	private JDialog searchDialog;
	private String lastSearchText;
	private TextEditor window;
	private JTextField searchField;

	// Constructor
	public EditMenu(UndoManager undoManager, TextEditor window, MainPanel mainPanel) {
		this.setText("Edit");
		this.undoManager = undoManager;
		this.mainPanel = mainPanel;
		this.window = window;
		searchDialog = new JDialog(window, "Search", false);

		// Create Menu items
		undo = new JMenuItem("Undo");
		redo = new JMenuItem("Redo");
		cut = new JMenuItem("Cut");
		copy = new JMenuItem("Copy");
		paste = new JMenuItem("Paste");
		selectAll = new JMenuItem("SelectAll");
		search = new JMenuItem("Search");

		// Create keyboard shortcuts
		undo.setAccelerator(KeyStroke.getKeyStroke("control Z"));
		redo.setAccelerator(KeyStroke.getKeyStroke("control Y"));
		cut.setAccelerator(KeyStroke.getKeyStroke("control X"));
		copy.setAccelerator(KeyStroke.getKeyStroke("control C"));
		paste.setAccelerator(KeyStroke.getKeyStroke("control V"));
		selectAll.setAccelerator(KeyStroke.getKeyStroke("control A"));
		search.setAccelerator(KeyStroke.getKeyStroke("control F"));

		// Add action listeners to menu items
		undo.addActionListener(e -> {
			// Close currentEdit before undoing
			if (mainPanel.currentEdit != null) {
				mainPanel.currentEdit.end();
				undoManager.addEdit(mainPanel.currentEdit);
				mainPanel.currentEdit = null;
			}
			if (undoManager.canUndo()) {
				undoManager.undo();
			}
		});
		redo.addActionListener(e -> {
			// Close currentEdit before redoing
			if (mainPanel.currentEdit != null) {
				mainPanel.currentEdit.end();
				undoManager.addEdit(mainPanel.currentEdit);
				mainPanel.currentEdit = null;
			}
			if (undoManager.canRedo()) {
				undoManager.redo();
			}
		});
		cut.addActionListener(e -> mainPanel.getTextPane().cut());
		copy.addActionListener(e -> mainPanel.getTextPane().copy());
		paste.addActionListener(e -> mainPanel.getTextPane().paste());
		selectAll.addActionListener(e -> mainPanel.getTextPane().selectAll());
		search.addActionListener(e -> {
			ActionDispatcher.searchWindow(searchDialog, window, mainPanel.getTextPane());
		});

		// Add Edit Menu items to the menu
		this.add(undo);
		this.add(redo);
		this.add(cut);
		this.add(copy);
		this.add(paste);
		this.add(selectAll);
		this.add(search);

		lastSearchText = null;
	}
}
