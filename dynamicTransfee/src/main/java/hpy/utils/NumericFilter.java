package hpy.utils;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class NumericFilter extends DocumentFilter {

    private boolean isValid(String text) {
        if (text == null || text.length() == 0) {
            return true;
        }
        return text.matches("[0-9]*");
    }
    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
        if (isValid(string)) {
            super.insertString(fb, offset, string, attr);
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        if (isValid(text)) {
            super.replace(fb, offset, length, text, attrs);
        }

    }
}
