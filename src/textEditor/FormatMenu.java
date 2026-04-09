package textEditor;

import java.awt.Color;

import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

//Class for the Format Menu and its items
public class FormatMenu extends JMenu {
	JMenuItem font;
	JMenuItem textColor;
	JMenuItem highlightColor;
	JMenuItem clearHighlight;
	JDialog fontDialog;
	private TextEditor window;
	private MainPanel mainPanel;

	// Constructor
	public FormatMenu(TextEditor window, MainPanel mainPanel) {
		this.setText("Format");
		this.window = window;
		this.mainPanel = mainPanel;

		// Create menuItems
		font = new JMenuItem("Font");
		textColor = new JMenuItem("Text Color");
		highlightColor = new JMenuItem("Highlight");
		clearHighlight = new JMenuItem("Clear Highlight");

		// Create Shortcut
		clearHighlight.setAccelerator(KeyStroke.getKeyStroke("control Q"));

		// Handle each menuItems actionEvent
		font.addActionListener(e -> {
			new FontMenu(window, mainPanel.getTextPane()).setVisible(true);
		});
		textColor.addActionListener(e -> {
			Color chosenColor = JColorChooser.showDialog(window, "Select Text Color", Color.BLACK);
			ActionDispatcher.applyTextColor(mainPanel.getTextPane(), chosenColor);
		});
		highlightColor.addActionListener(e -> {
			Color chosenColor = JColorChooser.showDialog(window, "Select a Highlight Color", Color.YELLOW);
			ActionDispatcher.applyHighlightColor(mainPanel.getTextPane(), chosenColor);
		});
		clearHighlight.addActionListener(e -> {
			ActionDispatcher.clearHighlightColor(mainPanel.getTextPane());
		});

		this.add(font);
		this.add(textColor);
		this.add(highlightColor);
		this.add(clearHighlight);
	}
}
