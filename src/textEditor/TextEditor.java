package textEditor;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/*
 * Text Editor Program by Joseph Corcoran
 * 
 * Features: File Handling, Undo/Redo, Cut/Copy/Paste, Font Formatting, 
 * Line Numbers, Line Highlights, Status Bar, Zoom, Keyboard Shortcuts, Scrollwheel Zoom Shortcut,
 * Shortcut Menu, Search Menu
 */

public class TextEditor extends JFrame {
	public static final String PROGRAM_NAME = "A Basic Text Editor: ";
	private File currentFile = null;

	// Constructor
	public TextEditor() {
		setTitle(PROGRAM_NAME + "Untitled");
		setSize(1000, 800);
		MainPanel panel = new MainPanel(this);
		getContentPane().add(panel);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				handleExit(panel);
			}
		});
		setLocationRelativeTo(null);
		setVisible(true);
	}

	// Main method
	public static void main(String[] args) {
		TextEditor mainWindow = new TextEditor();
	}

	// Method to handle confirmation of saving unfinished changes
	public boolean confirmSave(MainPanel panel, String actionName, String titleName) {
		if (!panel.isModified) {
			return true;
		}
		int choice = JOptionPane.showConfirmDialog(this, "You have unsaved changes. Save before " + actionName + "?",
				titleName, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

		if (choice == JOptionPane.CANCEL_OPTION || choice == JOptionPane.CLOSED_OPTION) {
			return false;
		}

		if (choice == JOptionPane.YES_OPTION) {
			File file = ActionDispatcher.saveFile(getCurrentFile(), this, panel);
			if (file == null)
				return false;
			setCurrentFile(file);
		}

		return true;
	}

	// Method to handle closing the program
	public void handleExit(MainPanel panel) {
		if (confirmSave(panel, "exiting", "Exit")) {
			dispose();
		}
	}

	// Getter method
	public File getCurrentFile() {
		return currentFile;
	}

	// Setter method
	public void setCurrentFile(File currentFile) {
		this.currentFile = currentFile;
	}
}