package textEditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoManager;

/*
 * Features to add in the future:
 * Themes Menu: Dark Mode, Caret color, Line number color
 * Auto Saving?
 */
public class MainPanel extends JPanel {
	private JTextPane textPane;
	private JScrollPane scrollPane;
	private JLabel statusBar;
	private LineNumberView lineNumbers;
	public JMenuBar menuBar;
	public FileMenu fileMenu;
	public EditMenu editMenu;
	public FormatMenu formatMenu;
	public ViewMenu viewMenu;
	public HelpMenu helpMenu;
	public CompoundEdit currentEdit;
	public Timer undoTimer;
	public UndoManager undoManager;
	private Highlighter.HighlightPainter linePainter;
	private boolean highlightLineEnabled = false;
	private Object currentLineHighlight;
	private double zoomFactor = 1.0;
	public boolean isModified = false;
	public String fileTitle = "Untitled";
	public TextEditor window;
	private static final double MIN_ZOOM = 0.5;
	private static final double MAX_ZOOM = 4.0;

	// Constructor
	public MainPanel(TextEditor window) {
		// Starting stuff
		BorderLayout mainLayout = new BorderLayout(10, 10);
		this.setLayout(mainLayout);
		this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		GridLayout menuLayout = new GridLayout(1, 8);
		JPanel menuPanel = new JPanel();
		menuPanel.setLayout(menuLayout);
		this.window = window;

		// Create the StatusBar
		statusBar = new JLabel("Line: 1, Column: 1 | Words: 0 | Characters: 0 | Selected: 0 | Zoom: 100%");
		statusBar.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));

		// Create the Text Area
		textPane = new JTextPane();
		scrollPane = new JScrollPane(textPane);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		textPane.setComponentPopupMenu(new RightClickMenu(this));

		// UndoManager setup
		undoManager = new UndoManager();
		undoTimer = new Timer(500, e -> {
			if (currentEdit != null) {
				currentEdit.end();
				undoManager.addEdit(currentEdit);
				currentEdit = null;
			}
		});
		undoTimer.setRepeats(false);
		textPane.getDocument().addUndoableEditListener(e -> {
			if (currentEdit == null) {
				currentEdit = new CompoundEdit();
			}
			currentEdit.addEdit(e.getEdit());
			undoTimer.restart();
		});

		// Create the LineNumbersView
		lineNumbers = new LineNumberView(textPane);
		scrollPane.setRowHeaderView(lineNumbers);

		// Create the the menus
		fileMenu = new FileMenu(this.window, this);
		editMenu = new EditMenu(undoManager, this.window, this);
		formatMenu = new FormatMenu(this.window, this);
		viewMenu = new ViewMenu(this.window, this);
		helpMenu = new HelpMenu(this.window);

		// Create the menubar and add menus to bar
		menuBar = new JMenuBar();
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(formatMenu);
		menuBar.add(viewMenu);
		menuBar.add(helpMenu);

		// Add menubar to panel
		menuPanel.add(menuBar);

		// Add to main Panel
		this.add(menuPanel, BorderLayout.NORTH);
		this.add(scrollPane, BorderLayout.CENTER);
		this.add(statusBar, BorderLayout.SOUTH);

		// Add a listener for any change in the TextPane to update the statusBar
		textPane.addCaretListener(e -> {
			updateStatusBar();
			highlightCurrentLine();
		});

		// Listen to update title
		textPane.getDocument().addDocumentListener(new DocumentListener() {
			private void markModified() {
				if (!isModified) {
					isModified = true;
					updateTitle();
				}
			}

			// Will mark the file as modified if there is any change to the document
			@Override
			public void insertUpdate(DocumentEvent e) {
				markModified();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				markModified();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				markModified();
			}
		});

		// Add Scroll wheel for zooming in and out
		scrollPane.addMouseWheelListener(e -> {
			// Only zoom if CTRL is pressed
			if (e.isControlDown()) {
				if (e.getWheelRotation() < 0) {
					zoomIn();
				} else {
					zoomOut();
				}
				e.consume();
			}
		});

		// Initialize the line highlighter
		linePainter = new CustomHighlightPainter(new Color(230, 230, 255));

		/*
		 * Create keyboard shortcuts
		 */

		// Zoom In and Out
		registerShortcut(textPane, KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, KeyEvent.CTRL_DOWN_MASK), "zoomIn",
				this::zoomIn);
		registerShortcut(textPane,
				KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK),
				"zoomIn", this::zoomIn);
		registerShortcut(textPane, KeyStroke.getKeyStroke(KeyEvent.VK_ADD, KeyEvent.CTRL_DOWN_MASK), "zoomIn",
				this::zoomIn);
		registerShortcut(textPane, KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, KeyEvent.CTRL_DOWN_MASK), "zoomOut",
				this::zoomOut);
		registerShortcut(textPane, KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, KeyEvent.CTRL_DOWN_MASK), "zoomOut",
				this::zoomOut);

		// Bold and Italic text
		registerShortcut(textPane, KeyStroke.getKeyStroke("control B"), "toggleBold", this::toggleBold);
		registerShortcut(textPane, KeyStroke.getKeyStroke("control I"), "toggleItalic", this::toggleItalic);

		// Change Font size
		registerShortcut(textPane,
				KeyStroke.getKeyStroke(KeyEvent.VK_PERIOD, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK),
				"increaseFontSize", () -> changeFontSize(2));
		registerShortcut(textPane,
				KeyStroke.getKeyStroke(KeyEvent.VK_COMMA, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK),
				"decreaseFontSize", () -> changeFontSize(-2));

		// Go to top or bottom of document
		registerShortcut(textPane, KeyStroke.getKeyStroke("control HOME"), "toTop", this::goToTop);
		registerShortcut(textPane, KeyStroke.getKeyStroke("control END"), "toBottom", this::goToBottom);

		// Go to start or end of line
		registerShortcut(textPane, KeyStroke.getKeyStroke("HOME"), "lineStart", this::goToLineStart);
		registerShortcut(textPane, KeyStroke.getKeyStroke("END"), "lineEnd", this::goToLineEnd);
	}

	// Method for handling shortcut registration for cleaner code
	private void registerShortcut(JTextPane textPane, KeyStroke keyStroke, String name, Runnable action) {
		InputMap im = textPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap am = textPane.getActionMap();

		im.put(keyStroke, name);
		am.put(name, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				action.run();
			}
		});
	}

	// Method for updating the status bar's values
	public void updateStatusBar() {
		// Caret position
		int caretPosition = textPane.getCaretPosition();
		Element root = textPane.getDocument().getDefaultRootElement();

		// Line number
		int line = root.getElementIndex(caretPosition) + 1;

		// Column number
		int lineStart = root.getElement(root.getElementIndex(caretPosition)).getStartOffset();
		int column = caretPosition - lineStart + 1;

		// Word Count
		String text = textPane.getText();
		int wordCount = 0;
		if (!text.isBlank()) {
			wordCount = text.trim().split("\\s+").length;
		}

		// Character Count
		int charCount = 0;
		if (!text.isBlank()) {
			charCount = (int) text.chars().filter(c -> !Character.isWhitespace(c)).count();
		}

		// Length of Selection
		int selectionLength = Math.abs(textPane.getSelectionEnd() - textPane.getSelectionStart());

		int percent = (int) Math.round(zoomFactor * 100);

		// Update the status bar
		statusBar.setText("Line: " + line + ", Column: " + column + " | Words: " + wordCount + " | Characters: "
				+ charCount + " | Selected: " + selectionLength + " | Zoom: " + percent + "%");
	}

	// Updates the window's title
	public void updateTitle() {
		String title = fileTitle;

		if (isModified) {
			title += " *";
		}

		window.setTitle(TextEditor.PROGRAM_NAME + title);
	}

	// Toggles the visibility of the status bar
	public void toggleStatusBar() {
		statusBar.setVisible(!statusBar.isVisible());
		revalidate();
		repaint();
	}

	// Toggles the visibility of the Line Numbers
	public void toggleLineNumbers() {
		if (scrollPane.getRowHeader().getView() == null) {
			scrollPane.setRowHeaderView(lineNumbers);
		} else {
			scrollPane.setRowHeaderView(null);
		}

		revalidate();
		repaint();
	}

	// Toggles the line highlight
	public void toggleCurrentLineHighlight() {
		highlightLineEnabled = !highlightLineEnabled;

		// Refresh highlight
		highlightCurrentLine();
	}

	// Method to highlight the line of the current caret position
	public void highlightCurrentLine() {
		Highlighter highlighter = textPane.getHighlighter();

		// Make sure to only erase line highlights and not user made highlights
		if (currentLineHighlight != null) {
			highlighter.removeHighlight(currentLineHighlight);
			currentLineHighlight = null;
		}

		// If highlight toggled off
		if (!highlightLineEnabled) {
			return;
		}

		// Highlight the line
		try {
			int caretPos = textPane.getCaretPosition();
			Element root = textPane.getDocument().getDefaultRootElement();

			int lineIndex = root.getElementIndex(caretPos);
			Element line = root.getElement(lineIndex);

			int start = line.getStartOffset();
			int end = line.getEndOffset();

			currentLineHighlight = highlighter.addHighlight(start, end, linePainter);

		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	// Method to zoom in by 10%
	public void zoomIn() {
		// +10%
		zoomFactor = Math.min(zoomFactor * 1.1, MAX_ZOOM);
		ActionDispatcher.applyZoom(textPane, zoomFactor);
		updateStatusBar();
	}

	// Method to zoom out by 10%
	public void zoomOut() {
		// -10%
		zoomFactor = Math.max(zoomFactor / 1.1, MIN_ZOOM);
		ActionDispatcher.applyZoom(textPane, zoomFactor);
		updateStatusBar();
	}

	// Method to reset the zoom
	public void resetZoom() {
		zoomFactor = 1.0;
		ActionDispatcher.applyZoom(textPane, zoomFactor);
		updateStatusBar();
	}

	// Method to toggle selected and/or future text as bold
	public void toggleBold() {
		StyledDocument doc = textPane.getStyledDocument();
		int start = textPane.getSelectionStart();
		int end = textPane.getSelectionEnd();

		// Get current style at caret
		AttributeSet attribute;
		if (start != end) {
			attribute = doc.getCharacterElement(start).getAttributes();
		} else {
			attribute = textPane.getInputAttributes();
		}
		boolean isBold = StyleConstants.isBold(attribute);

		// Copy existing attributes
		SimpleAttributeSet newAttribute = new SimpleAttributeSet(attribute);

		// Toggle bold
		StyleConstants.setBold(newAttribute, !isBold);

		// Apply to selected text
		if (start != end) {
			doc.setCharacterAttributes(start, end - start, newAttribute, true);
		}
		// Apply to future text
		textPane.setCharacterAttributes(newAttribute, false);
	}

	// Method to toggle selected and/or future text as italicized
	public void toggleItalic() {
		StyledDocument doc = textPane.getStyledDocument();
		int start = textPane.getSelectionStart();
		int end = textPane.getSelectionEnd();

		// Get current style at caret
		AttributeSet attribute;
		if (start != end) {
			attribute = doc.getCharacterElement(start).getAttributes();
		} else {
			attribute = textPane.getInputAttributes();
		}
		boolean isItalic = StyleConstants.isItalic(attribute);

		// Copy existing attributes
		SimpleAttributeSet newAttribute = new SimpleAttributeSet(attribute);

		// Toggle Italic
		StyleConstants.setItalic(newAttribute, !isItalic);

		// Apply to selected text
		if (start != end) {
			doc.setCharacterAttributes(start, end - start, newAttribute, true);
		}
		// Apply to future text
		textPane.setCharacterAttributes(newAttribute, false);
	}

	// Method to change the font size - for shortcut
	private void changeFontSize(int delta) {
		StyledDocument doc = textPane.getStyledDocument();
		int start = textPane.getSelectionStart();
		int end = textPane.getSelectionEnd();

		// Get current style at caret
		AttributeSet attribute;
		if (start != end) {
			attribute = doc.getCharacterElement(start).getAttributes();
		} else {
			attribute = textPane.getInputAttributes();
		}

		int currentSize = StyleConstants.getFontSize(attribute);
		// Use Math.max to prevent text from getting too small
		int newSize = Math.max(6, currentSize + delta);

		SimpleAttributeSet newAttributes = new SimpleAttributeSet(attribute);
		StyleConstants.setFontSize(newAttributes, newSize);

		if (start != end) {
			doc.setCharacterAttributes(start, end - start, newAttributes, true);
			textPane.setCharacterAttributes(newAttributes, false);
		} else {
			textPane.setCharacterAttributes(newAttributes, true);
		}
	}

	// Method to go to top of document - for shortcut
	private void goToTop() {
		textPane.setCaretPosition(0);
		textPane.requestFocusInWindow();
	}

	// Method to go to bottom of document - for shortcut
	private void goToBottom() {
		textPane.setCaretPosition(textPane.getDocument().getLength());
		textPane.requestFocusInWindow();
	}

	// Method to go to the start of the current line - for shortcut
	private void goToLineStart() {
		try {
			int caretPos = textPane.getCaretPosition();
			Element root = textPane.getDocument().getDefaultRootElement();
			int lineIndex = root.getElementIndex(caretPos);
			Element line = root.getElement(lineIndex);
			int start = line.getStartOffset();
			textPane.setCaretPosition(start);
			textPane.requestFocusInWindow();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Method to go to the end of the current line - for shortcut
	private void goToLineEnd() {
		try {
			int caretPos = textPane.getCaretPosition();
			Element root = textPane.getDocument().getDefaultRootElement();
			int lineIndex = root.getElementIndex(caretPos);
			Element line = root.getElement(lineIndex);
			int end = line.getEndOffset() - 1;
			textPane.setCaretPosition(end);
			textPane.requestFocusInWindow();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Getter Method
	public JTextPane getTextPane() {
		return textPane;
	}
}
