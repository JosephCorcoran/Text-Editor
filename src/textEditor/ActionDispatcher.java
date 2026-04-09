package textEditor;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

//Action Dispatcher class handles many of the methods for different menu items' functions
public class ActionDispatcher {

	// Method to open a file
	public static File openFile(File selectedFile, TextEditor window, MainPanel mainPanel) {
		// Select File
		JFileChooser fileSelector = new JFileChooser();
		fileSelector.setDialogTitle("Open File");
		int result = fileSelector.showOpenDialog(fileSelector);
		if (result != JFileChooser.APPROVE_OPTION) {
			return null;
		}
		if (result == JFileChooser.APPROVE_OPTION) {
			selectedFile = fileSelector.getSelectedFile();
		}

		// Open File's contents into the textpane
		Scanner fileReader = null;
		try {
			fileReader = new Scanner(selectedFile);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// Clear Text Area
		mainPanel.getTextPane().setText("");
		mainPanel.undoManager.discardAllEdits();
		mainPanel.currentEdit = null;

		StyledDocument doc = mainPanel.getTextPane().getStyledDocument();
		// Write file to the textpane
		while (fileReader.hasNextLine()) {
			String line = fileReader.nextLine();

			try {
				doc.insertString(doc.getLength(), line + "\n", null);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
			mainPanel.getTextPane().setCaretPosition(doc.getLength());

		}
		fileReader.close();

		// Change the program title to reflect selected file and reset the modified
		// status to false
		mainPanel.fileTitle = selectedFile.getName();
		mainPanel.isModified = false;
		mainPanel.updateTitle();

		return selectedFile;
	}

	// Method to save file
	public static File saveFile(File selectedFile, TextEditor window, MainPanel mainPanel) {
		// If file is empty, switch to saveAsFile
		if (selectedFile == null) {
			return saveAsFile(window, mainPanel);
		}

		// Overwrite selectedFile's contents with textArea's contents
		try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(selectedFile))) {
			fileWriter.write(mainPanel.getTextPane().getText());
			mainPanel.isModified = false;
			mainPanel.updateTitle();
			return selectedFile;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	// Method to select the file to save as
	public static File saveAsFile(TextEditor window, MainPanel mainPanel) {
		JFileChooser fileSelector = new JFileChooser();
		fileSelector.setDialogTitle("Save As");
		fileSelector.setAcceptAllFileFilterUsed(false);

		// Setup .txt file filter
		FileNameExtensionFilter txtFilter = new FileNameExtensionFilter("Text Files (*.txt)", "txt");
		fileSelector.addChoosableFileFilter(txtFilter);

		// Check to make sure file is valid
		int result = fileSelector.showOpenDialog(null);
		if (result != JFileChooser.APPROVE_OPTION) {
			return null;
		}
		File file = fileSelector.getSelectedFile();

		// Force it to save as .txt file
		if (!file.getName().toLowerCase().endsWith(".txt")) {
			file = new File(file.getAbsolutePath() + ".txt");
		}

		// Confirm overwriting text if file already exists
		if (file.exists()) {
			int choice = JOptionPane.showConfirmDialog(null, "File already exists.Overwrite?", "Confirm Save As",
					JOptionPane.YES_NO_OPTION);

			if (choice != JOptionPane.YES_OPTION) {
				return null;
			}
		}

		// Write to file
		try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(file))) {
			fileWriter.write(mainPanel.getTextPane().getText());
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Failed to save file:\n" + e.getMessage(), "Save Error",
					JOptionPane.ERROR_MESSAGE);
			return null;
		}

		// Change the program title to reflect selected file and reset the modified
		// status to false
		mainPanel.fileTitle = file.getName();
		mainPanel.isModified = false;
		mainPanel.updateTitle();

		// Return the file after saving
		return file;
	}

	// Method to search for the next instance of a string of text
	public static void searchNext(JDialog searchDialog, JTextPane textPane, String searchText) {
		// Setup
		StyledDocument doc = textPane.getStyledDocument();
		String text = "";
		try {
			text = doc.getText(0, doc.getLength());
		} catch (BadLocationException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(textPane, "Error reading document", "Error", JOptionPane.ERROR_MESSAGE);
		}
		int start = textPane.getSelectionEnd();
		int index = text.indexOf(searchText, start);

		// Wrap around if not found after current spot
		if (index == -1 && start > 0) {
			index = textPane.getText().indexOf(searchText);
		}

		// If found
		if (index >= 0) {
			textPane.setSelectionStart(index);
			textPane.setSelectionEnd(index + searchText.length());
			textPane.requestFocusInWindow();
		}
		// If not found
		else {
			JOptionPane.showMessageDialog(searchDialog, "Text Not Found", "Search", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	// Method to search for the previous instance of a string of text
	public static void searchPrevious(JDialog searchDialog, JTextPane textPane, String searchText) {
		// Setup
		StyledDocument doc = textPane.getStyledDocument();
		String text = "";
		try {
			text = doc.getText(0, doc.getLength());
		} catch (BadLocationException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(textPane, "Error reading document", "Error", JOptionPane.ERROR_MESSAGE);
		}

		// String text = textPane.getText();
		int start = textPane.getSelectionStart() - 1;

		// If at the beginning of the TextPane, wrap around to the end of the TextPane
		if (start < 0) {
			start = text.length() - 1;
		}

		int index = text.lastIndexOf(searchText, start);

		// Wrap around if not found before current spot
		if (index == -1 && start < text.length() - 1) {
			index = text.lastIndexOf(searchText);
		}

		// If found
		if (index >= 0) {
			textPane.setSelectionStart(index);
			textPane.setSelectionEnd(index + searchText.length());
			textPane.requestFocusInWindow();
		}
		// If not found
		else {
			JOptionPane.showMessageDialog(searchDialog, "Text Not Found", "Search", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	// Method to create a window for searching for instances of text
	public static void searchWindow(JDialog searchDialog, TextEditor window, JTextPane textPane) {
		// Check if a window is already open
		if (searchDialog.isVisible()) {
			searchDialog.setVisible(true);
			return;
		}

		// Setup
		JTextField searchField = new JTextField(20);
		JButton searchNextButton = new JButton("Next");
		JButton searchPreviousButton = new JButton("Previous");

		// Next button calls the searchNext button
		searchNextButton.addActionListener(e -> {
			String text = searchField.getText();
			if (text == null || text.isEmpty())
				return;
			searchNext(searchDialog, textPane, text);
		});

		// Previous button calls the searchPrevious method
		searchPreviousButton.addActionListener(e -> {
			String text = searchField.getText();
			if (text == null || text.isEmpty())
				return;
			searchPrevious(searchDialog, textPane, text);
		});

		// Setup the JPanel
		JPanel panel = new JPanel();
		panel.add(new JLabel("Search For:"));
		panel.add(searchField);
		panel.add(searchNextButton);
		panel.add(searchPreviousButton);

		searchDialog.add(panel);
		searchDialog.pack();
		searchDialog.setLocationRelativeTo(window);
		searchDialog.setVisible(true);
	}

	// Method to apply color to selected and/or future text
	public static void applyTextColor(JTextPane textPane, Color color) {
		// If no color was selected, exit the method
		if (color == null)
			return;

		// Setup variables
		StyledDocument doc = textPane.getStyledDocument();
		int start = textPane.getSelectionStart();
		int end = textPane.getSelectionEnd();

		// Create a new style and add it to textPane document
		Style style = textPane.addStyle("colorStyle", null);
		StyleConstants.setForeground(style, color);

		// If any text is highlighted
		if (start != end) {
			doc.setCharacterAttributes(start, end - start, style, false);
		}
		// If no text is highlighted
		else {
			textPane.setCharacterAttributes(style, false);
		}

	}

	// Method to apply a background highlight to selected and/or future text
	public static void applyHighlightColor(JTextPane textPane, Color color) {
		// If no color was selected, exit the method
		if (color == null)
			return;

		// Setup variables
		StyledDocument doc = textPane.getStyledDocument();
		int start = textPane.getSelectionStart();
		int end = textPane.getSelectionEnd();

		// Create a new style and add it to textPane document
		Style style = textPane.addStyle("highlightColorStyle", null);
		StyleConstants.setBackground(style, color);

		// If any text is highlighted
		if (start != end) {
			doc.setCharacterAttributes(start, end - start, style, false);
		}
		// If no text is highlighted
		else {
			textPane.setCharacterAttributes(style, false);
		}
	}

	// Method to clear the background highlight from selected and/or future text
	public static void clearHighlightColor(JTextPane textPane) {

		// Setup variables
		StyledDocument doc = textPane.getStyledDocument();
		int start = textPane.getSelectionStart();
		int end = textPane.getSelectionEnd();

		// Create a new style and add it to textPane document
		Style style = textPane.addStyle("clearBg", null);
		StyleConstants.setBackground(style, textPane.getBackground());

		// If any text is highlighted
		if (start != end) {
			doc.setCharacterAttributes(start, end - start, style, false);
		}
		// If no text is highlighted
		else {
			textPane.setCharacterAttributes(style, false);
		}
	}

	// Method to apply a zoom factor to the textpane
	public static void applyZoom(JTextPane textPane, double zoomFactor) {
		StyledDocument doc = textPane.getStyledDocument();

		// While loop to go through entire document to search for sections of text with
		// same font size
		int i = 0;
		while (i < doc.getLength()) {
			Element element = doc.getCharacterElement(i);
			int start = element.getStartOffset();
			int end = element.getEndOffset();

			AttributeSet as = element.getAttributes();

			// Get original size (or store it if missing)
			Integer baseSize = (Integer) as.getAttribute("baseFontSize");
			if (baseSize == null) {
				baseSize = StyleConstants.getFontSize(as);
			}

			// Calculate zoomed size
			int newSize = (int) Math.round(baseSize * zoomFactor);

			// Limit the size
			newSize = Math.max(6, Math.min(72, newSize));

			// Create style
			Style style = textPane.addStyle("zoomStyle", null);
			StyleConstants.setFontSize(style, newSize);

			// Store base size
			style.addAttribute("baseFontSize", baseSize);

			// Apply the change
			doc.setCharacterAttributes(start, end - start, style, false);

			i = end;
		}
	}
}
