package hpy.enums;

/**
 * 파일 양식별 헤더 ROW(0-based) 매핑
 */
public enum ExcelFormat {
    TMONEY("Tmoney", 10),
    TOSS("Toss", 11),
    KAKAO("Kakao", 12);

    private final String label;
    private final int headerRow;

    ExcelFormat(String label, int headerRow) {
        this.label = label;
        this.headerRow = headerRow;
    }

    public String getLabel() {
        return label;
    }

    public int getHeaderRow() {
        return headerRow;
    }

    public static ExcelFormat from(String label) {
        for (ExcelFormat format : values()) {
            if (format.label.equals(label)) {
                return format;
            }
        }
        throw new IllegalArgumentException("알 수 없는 양식: " + label);
    }
}
