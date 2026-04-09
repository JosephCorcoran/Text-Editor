package textEditor;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;

//LineNumberView class handles displaying the line numbers when toggled. To be called by MainPanel class
public class LineNumberView extends JComponent {
	private final JTextPane textPane;
	private final Font font = new Font("Monospaced", Font.PLAIN, 12);

	// Constructor
	public LineNumberView(JTextPane textPane) {
		this.textPane = textPane;
		setFont(font);

		// Add listener to the document to update the line numbers when any change is
		// made to the textPane's document
		textPane.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				repaint();
			}

			public void removeUpdate(DocumentEvent e) {
				repaint();
			}

			public void changedUpdate(DocumentEvent e) {
				repaint();
			}
		});

		textPane.addCaretListener(e -> repaint());
	}

	// Figures out how wide the line number column should be
	public Dimension getPreferredSize() {
		// Get how many digits will be needed to display line numbers
		int lineCount = textPane.getDocument().getDefaultRootElement().getElementCount();
		int digits = Math.max(3, String.valueOf(lineCount).length());

		// Finds the character width
		FontMetrics fm = getFontMetrics(font);
		int width = fm.charWidth('0') * digits + 12;

		// Returns the calculated width
		return new Dimension(width, Integer.MAX_VALUE);
	}

	// Override paintComponent Method to paint the line numbers
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		// Get the font info and padding
		FontMetrics fm = g.getFontMetrics(getFont());
		Insets insets = getInsets();

		// Get the document structure and the number of lines
		Element root = textPane.getDocument().getDefaultRootElement();
		int lineCount = root.getElementCount();

		// For loop iterates through the number of existing lines
		for (int i = 0; i < lineCount; i++) {
			Element line = root.getElement(i);
			// Get where the line starts in the document
			int startOffset = line.getStartOffset();

			try {
				Rectangle2D r = textPane.modelToView2D(startOffset);
				// Text is drawn from where text should sit instead of from the baseline using
				// fm.getAscent
				int y = (int) r.getY() + fm.getAscent();

				// Current line number
				String lineNumber = String.valueOf(i + 1);

				// Draw line number
				g.drawString(lineNumber, insets.left + 5, y);

			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
	}
}
