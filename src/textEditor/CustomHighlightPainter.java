package textEditor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;

//CustomHighlightPainter class handles highlighting the current line. To be called by MainPanel class
public class CustomHighlightPainter implements Highlighter.HighlightPainter {
	private Color color;

	// Constructor
	public CustomHighlightPainter(Color color) {
		this.color = color;
	}

	// Override paint Method to highlight the background of the selected line
	@Override
	public void paint(Graphics g, int start, int end, Shape bounds, JTextComponent c) {
		try {
			Rectangle2D startRect = c.modelToView2D(start);

			if (startRect == null)
				return;

			g.setColor(color);

			// Calculate the rectangle for the highlight
			int y = (int) startRect.getY();
			int height = (int) startRect.getHeight();
			int width = c.getWidth();

			// Paint the rectangle
			g.fillRect(0, y, width, height);

		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
}
