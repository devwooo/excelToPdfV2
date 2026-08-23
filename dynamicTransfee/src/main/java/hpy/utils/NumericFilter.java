package hpy.utils;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

public class NumericFilter extends DocumentFilter {

    private boolean isValid(String text) {
        String trimmedText = text.trim();
        if (trimmedText == null) return false;
        if (trimmedText.isEmpty()) return true;          // 전부 지운 상태 허용
        if (trimmedText.startsWith(",")) return false;   // 맨 앞 콤마 금지
        if (trimmedText.contains(",,")) return false;    // 연속 콤마 금지
        return text.matches("[0-9,]*");           // 숫자와 콤마만 허용 (후행 콤마는 허용)
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
        Document doc = fb.getDocument();
        String current = doc.getText(0, doc.getLength());
        String result = current.substring(0, offset) + string + current.substring(offset);
        if (isValid(result)) {          // ← string이 아니라 result
            super.insertString(fb, offset, string, attr);
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        Document doc = fb.getDocument();
        String current = doc.getText(0, doc.getLength());
        String result = current.substring(0, offset) + text + current.substring(offset + length);
        if (isValid(result)) {          // ← text가 아니라 result
            super.replace(fb, offset, length, text, attrs);
        }
    }
}
