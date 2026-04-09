package textEditor;

import java.awt.event.KeyEvent;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

//Class for the Help Menu and its items
public class HelpMenu extends JMenu {
	JMenuItem shortcuts;
	JMenuItem about;
	private JFrame window;

	// Constructor
	public HelpMenu(JFrame window) {
		this.setText("Help");
		this.window = window;

		// Create Help Menu Items
		shortcuts = new JMenuItem("Shortcuts");
		about = new JMenuItem("About");

		// Setup Shortcuts
		shortcuts.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
		about.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));

		// Add action listeners to each menu item
		shortcuts.addActionListener(e -> showShortcuts());
		about.addActionListener(e -> {
			new AboutDialog(window);
		});

		// Add help menu items to the menu
		this.add(shortcuts);
		this.add(about);
	}

	// Method to display text for each shortcut in the program. Grouped by each menu
	// in the program
	private void showShortcuts() {
		String[][] fileData = { { "<b>New Window</b>", "<i>Ctrl + N</i>" }, { "<b>Open File</b>", "<i>Ctrl + O</i>" },
				{ "<b>Save File</b>", "<i>Ctrl + S</i>" }, { "<b>Save File As</b>", "<i>Ctrl + Shift + S</i>" },
				{ "<b>Exit</b>", "<i>Alt + F4</i>" } };

		String[][] editData = { { "<b>Undo</b>", "<i>Ctrl + Z</i>" }, { "<b>Redo</b>", "<i>Ctrl + Y</i>" },
				{ "<b>Cut</b>", "<i>Ctrl + X</i>" }, { "<b>Copy</b>", "<i>Ctrl + C</i>" },
				{ "<b>Paste</b>", "<i>Ctrl + V</i>" }, { "<b>Select All</b>", "<i>Ctrl + A</i>" },
				{ "<b>Search</b>", "<i>Ctrl + F</i>" } };

		String[][] formatData = { { "<b>Clear Highlight</b>", "<i>Ctrl + Q</i>" }, { "<b>Bold</b>", "<i>Ctrl + B</i>" },
				{ "<b>Italics</b>", "<i>Ctrl + I</i>" },
				{ "<b>Increase Font Size</b>", "<i>Ctrl + Shift + . (Period)</i>" },
				{ "<b>Decrease Font Size</b>", "<i>Ctrl + Shift + , (Comma)</i>" } };

		String[][] viewData = { { "<b>Zoom In</b>", "<i>Ctrl + =, Ctrl + +, and Ctrl + Scroll Up</i>" },
				{ "<b>Zoom Out</b>", "<i>Ctrl + - and Ctrl + Scroll Down</i>" },
				{ "<b>Reset Zoom</b>", "<i>Ctrl + 0</i>" }, { "<b>Toggle Line Numbers</b>", "<i>Ctrl + L</i>" },
				{ "<b>Toggle Status Bar</b>", "<i>Ctrl + Shift + B</i>" },
				{ "<b>Toggle Current Line Highlight</b>", "<i>Ctrl + Shift + L</i>" },
				{ "<b>To Top of Document</b>", "<i>Ctrl + HOME</i>" },
				{ "<b>To Bottom of Document</b>", "<i>Ctrl + END</i>" }, { "<b>To Start of Line</b>", "<i>HOME</i>" },
				{ "<b>To End of Line</b>", "<i>END</i>" } };

		String[][] helpData = { { "<b>Shortcut Menu</b>", "<i>F1</i>" }, { "<b>About</b>", "<i>F2</i>" } };

		// Use tabs for the shortcuts related to each menu
		JTabbedPane tabs = new JTabbedPane();
		tabs.addTab("File", createShortcutTable(fileData));
		tabs.addTab("Edit", createShortcutTable(editData));
		tabs.addTab("Format", createShortcutTable(formatData));
		tabs.addTab("View", createShortcutTable(viewData));
		tabs.addTab("Help", createShortcutTable(helpData));

		// Create the dialog window to display the shortcuts
		JDialog dialog = new JDialog(window, "Keyboard Shortcuts", true);
		dialog.add(tabs);
		dialog.pack();
		dialog.setLocationRelativeTo(window);
		dialog.setVisible(true);
	}

	// Method to create tables for displaying info about each menu's shortcuts
	private JScrollPane createShortcutTable(String[][] data) {
		String[] columns = { "Action", "Shortcut" };

		JTable table = new JTable(data, columns) {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		// HTML renderer
		table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
			public void setValue(Object value) {
				setText(value == null ? "" : "<html>" + value.toString() + "</html>");
			}
		});

		table.setRowHeight(24);
		table.setFillsViewportHeight(true);

		return new JScrollPane(table);
	}

	// Private class AboutDialog handles the window for displaying information about
	// the program
	private class AboutDialog extends JDialog {
		public AboutDialog(JFrame window) {
			super(window, "About", true);

			setSize(300, 200);
			setLocationRelativeTo(window);

			JLabel info = new JLabel("<html><center>" + "<h2>Text Editor</h2>" + "<font color='gray'>Version 1.0</font>"
					+ "<br><hr><br>" + "Created by Joseph Corcoran" + "</center></html>", SwingConstants.CENTER);

			add(info);

			setVisible(true);
		}
	}
}
