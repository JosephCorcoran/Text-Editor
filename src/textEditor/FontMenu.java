package textEditor;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

//Class for the Font Menu Window and its items. To be called by the Format Menu Class
public class FontMenu extends JDialog {
	private JComboBox<String> fontFamilyBox;
	private JComboBox<String> fontStyleBox;
	private JSpinner fontSizeSpinner;
	private JLabel previewLabel;
	private JTextPane targetTextPane;
	private Font selectedFont;

	// Constructor
	public FontMenu(TextEditor window, JTextPane textPane) {
		super(window, "Font Selector", true);
		this.targetTextPane = textPane;
		this.selectedFont = textPane.getFont();

		setLayout(new BorderLayout());
		setSize(400, 300);
		setLocationRelativeTo(window);

		// Font selection panel
		JPanel selectionPanel = new JPanel();
		selectionPanel.setLayout(new GridLayout(3, 2, 10, 10));

		// Font Family Selector
		selectionPanel.add(new JLabel("Font:"));
		String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		fontFamilyBox = new JComboBox<>(fonts);
		fontFamilyBox.setSelectedItem(selectedFont.getFamily());
		selectionPanel.add(fontFamilyBox);

		// Font Style
		selectionPanel.add(new JLabel("Style:"));
		String[] styles = { "Plain", "Bold", "Italic", "Bold Italic" };
		fontStyleBox = new JComboBox<>(styles);
		fontStyleBox.setSelectedIndex(styleToIndex(selectedFont.getStyle()));
		selectionPanel.add(fontStyleBox);

		// Font Size
		selectionPanel.add(new JLabel("Size:"));
		fontSizeSpinner = new JSpinner(new SpinnerNumberModel(12, 6, 100, 1));
		selectionPanel.add(fontSizeSpinner);

		add(selectionPanel, BorderLayout.NORTH);

		// Preview Panel
		previewLabel = new JLabel("Sample Text");
		previewLabel.setFont(selectedFont);
		previewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		add(previewLabel, BorderLayout.CENTER);

		// Button Panel
		JPanel buttonPanel = new JPanel();
		JButton okButton = new JButton("OK");
		JButton cancelButton = new JButton("Cancel");
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		add(buttonPanel, BorderLayout.SOUTH);

		// Update Preview when selection changes
		ActionListener updatePreview = e -> updatePreview();
		fontFamilyBox.addActionListener(updatePreview);
		fontStyleBox.addActionListener(updatePreview);
		fontSizeSpinner.addChangeListener(e -> updatePreview());

		okButton.addActionListener(e -> {
			// Replace with new method
			// targetTextPane.setFont(selectedFont);
			setTypingFont(textPane, selectedFont);
			dispose();
		});

		cancelButton.addActionListener(e -> dispose());
	}

	// Method to update the font preview with whatever the user selected
	private void updatePreview() {
		String font = (String) fontFamilyBox.getSelectedItem();
		int style = indexToStyle(fontStyleBox.getSelectedIndex());
		int size = (Integer) fontSizeSpinner.getValue();
		selectedFont = new Font(font, style, size);
		previewLabel.setFont(selectedFont);
	}

	// Method to get the font style based on the font style box selection
	private int indexToStyle(int index) {
		switch (index) {
		case 1:
			return Font.BOLD;
		case 2:
			return Font.ITALIC;
		case 3:
			return Font.BOLD | Font.ITALIC;
		default:
			return Font.PLAIN;
		}
	}

	// Method to get the int variable relating to the font style
	private int styleToIndex(int style) {
		switch (style) {
		case Font.BOLD:
			return 1;
		case Font.ITALIC:
			return 2;
		case Font.BOLD | Font.ITALIC:
			return 3;
		default:
			return 0;
		}
	}

	// Method to set the font of the selected text and/or future text
	public void setTypingFont(JTextPane textPane, Font font) {
		StyledDocument doc = textPane.getStyledDocument();

		int start = textPane.getSelectionStart();
		int end = textPane.getSelectionEnd();

		// Create style
		Style style = textPane.addStyle("typingStyle", null);

		// Add Font Family, Style, and Size to the style
		StyleConstants.setFontFamily(style, font.getFamily());
		StyleConstants.setFontSize(style, font.getSize());
		StyleConstants.setBold(style, font.isBold());
		StyleConstants.setItalic(style, font.isItalic());

		style.addAttribute("baseFontSize", font.getSize());

		// Apply the new style
		if (start != end) {
			doc.setCharacterAttributes(start, end - start, style, false);
		} else {
			textPane.setCharacterAttributes(style, false);
		}
	}
}
