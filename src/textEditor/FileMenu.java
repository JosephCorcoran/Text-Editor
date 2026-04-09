package textEditor;

import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

//Class for the File Menu and its items
public class FileMenu extends JMenu {
	JMenuItem newWindow;
	JMenuItem openFile;
	JMenuItem saveFile;
	JMenuItem saveAsFile;
	JMenuItem closeFile;
	JMenuItem exit;
	private TextEditor window;
	private MainPanel mainPanel;

	// Constructor
	public FileMenu(TextEditor window, MainPanel mainPanel) {
		this.setText("File");
		this.window = window;
		this.mainPanel = mainPanel;

		// Create file menu items
		newWindow = new JMenuItem("New Window");
		openFile = new JMenuItem("Open File");
		saveFile = new JMenuItem("Save");
		saveAsFile = new JMenuItem("Save As");
		closeFile = new JMenuItem("Close File");
		exit = new JMenuItem("Exit");

		// Add keyboard shortcuts
		newWindow.setAccelerator(KeyStroke.getKeyStroke("control N"));
		openFile.setAccelerator(KeyStroke.getKeyStroke("control O"));
		saveFile.setAccelerator(KeyStroke.getKeyStroke("control S"));
		saveAsFile.setAccelerator(KeyStroke.getKeyStroke("control shift S"));
		exit.setAccelerator(KeyStroke.getKeyStroke("alt F4"));

		// Add action listeners to each menu item
		newWindow.addActionListener(e -> new TextEditor());
		openFile.addActionListener(e -> {
			if (!window.confirmSave(mainPanel, "opening a file", "Save Unfinished Changes?"))
				return;

			File file = ActionDispatcher.openFile(window.getCurrentFile(), window, mainPanel);
			if (file != null) {
				window.setCurrentFile(file);
			}
		});
		saveFile.addActionListener(e -> {
			File file = ActionDispatcher.saveFile(window.getCurrentFile(), window, mainPanel);
			if (file != null) {
				window.setCurrentFile(file);
			}
		});
		saveAsFile.addActionListener(e -> {
			File file = ActionDispatcher.saveAsFile(window, mainPanel);
			if (file != null) {
				window.setCurrentFile(file);
			}
		});
		closeFile.addActionListener(e -> {
			if (!window.confirmSave(mainPanel, "closing the file", "Save Unfinished Changes?"))
				return;

			window.setCurrentFile(null);
			mainPanel.getTextPane().setText("");
			mainPanel.isModified = false;
			mainPanel.fileTitle = "Untitled";
			mainPanel.updateTitle();
		});
		exit.addActionListener(e -> {
			window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
		});

		// Add file menu items to the menu
		this.add(newWindow);
		this.add(openFile);
		this.add(saveFile);
		this.add(saveAsFile);
		this.add(closeFile);
		this.add(exit);
	}
}
