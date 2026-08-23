package hpy.utils;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import hpy.enums.Headers;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExportPDFListener implements ActionListener {

    private final int format;
    private final String filePath;
    private final String exportPath;
    private final JCheckBox excludeCheckBox;
    private final List<JCheckBox> listCheckBox;
    private JTextField excludeInput;
    private int dateColIdx = -1;
    private final Map<Integer, String> writeMap = new HashMap<>();

    public ExportPDFListener(int format, String filePath, String exportPath, JCheckBox excludeCheckBox, List<JCheckBox> listCheckBox, JTextField excludeInput) {
        this.format = format;
        this.filePath = filePath;
        this.exportPath = exportPath;
        this.excludeCheckBox = excludeCheckBox;
        this.listCheckBox = listCheckBox;
        this.excludeInput = excludeInput;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        // ① 진행 다이얼로그 준비 (부모 창을 owner로 주면 그 위에 모달로 뜬다)
        Window owner = SwingUtilities.getWindowAncestor((Component) e.getSource());
        JDialog progress = new JDialog(owner, "처리 중", Dialog.ModalityType.APPLICATION_MODAL);
        progress.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);  // X버튼으로 못 닫게
        JLabel label = new JLabel("PDF를 생성하는 중입니다. 잠시만 기다려 주세요...");
        label.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        progress.add(label);
        progress.pack();
        progress.setLocationRelativeTo(owner);

        SwingWorker<Boolean, Void> worker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                PdfPTable table = null;
                File file = new File(filePath);
                /**
                 * Export 파일명 중복 체크
                 */
                String FILE_NAME = "test";
                String FILE_EXT = "pdf";
                String exportFileName = exportPath + File.separator + FILE_NAME + "." + FILE_EXT;
                File exportFileCheck = new File(exportFileName);
                int idx = 1;
                while (exportFileCheck.exists()) {
                    exportFileCheck = new File(exportPath + File.separator + FILE_NAME + "_" + idx++ + "." + FILE_EXT);
                }

                try (
                        FileInputStream fis = new FileInputStream(file);
                        XSSFWorkbook workbook = new XSSFWorkbook(fis);
                        FileOutputStream fos = new FileOutputStream(exportFileCheck);
                ) {
                    XSSFSheet sheet = workbook.getSheetAt(0);
                    Document document = new Document(PageSize.A4.rotate());

                    try {
                        // PDF 생성 객체
                        PdfWriter.getInstance(document, fos);
                        document.open();

                        table = new PdfPTable(listCheckBox.size());
                        table.setWidthPercentage(100);


                        int lastRowNum = sheet.getLastRowNum();
                        for (int i = format; i <= lastRowNum; i++) {

                            XSSFRow row = sheet.getRow(i);
                            int cells = row.getLastCellNum();

                            XSSFCell checkCell = row.getCell(1, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                            if (cells == -1 || checkCell == null) {
                                continue;
                            }

                            if (i == 10) {
                                for (int cellIdx = 0; cellIdx < cells; cellIdx++) {
                                    XSSFCell cell = row.getCell(cellIdx, XSSFRow.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                                    if (cell == null) {
                                        continue;
                                    }
                                    String cellValue = cell.getStringCellValue();

                                    if (cellValue.equals("거래일시")) {
                                        dateColIdx = cellIdx;
                                    }
                                    for (JCheckBox cb : listCheckBox) {
                                        if (cb.isSelected() && cb.getText().equals(cellValue)) {
                                            writeMap.put(cellIdx, cb.getText());
                                            break;   // 매칭되면 더 볼 필요 없으니 break
                                        }
                                    }
                                }

                                // 선택된 헤더가 하나도 없으면 만들 표가 없다.
                                if (writeMap.isEmpty()) {
                                    JOptionPane.showMessageDialog(null, "출력할 헤더를 하나 이상 선택해 주세요", "경고", JOptionPane.ERROR_MESSAGE);
                                    return false;
                                }

                                // 2차 패스: 데이터행과 동일하게 cellIdx 오름차순으로 헤더 셀을 그린다.
                                //          (writeMap이 HashMap이라 순회 순서가 뒤섞일 수 있으므로 cellIdx 기준으로 그려 순서를 맞춘다)
                                Font headerFont = new Font(FileUtils.getKoreanBaseFont(), 9, Font.BOLD, BaseColor.WHITE);
                                for (int cellIdx = 0; cellIdx < cells; cellIdx++) {
                                    if (!isPass(cellIdx)) {
                                        continue;
                                    }
                                    PdfPCell headerCell = new PdfPCell(new Phrase(writeMap.get(cellIdx), headerFont));
                                    headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                                    headerCell.setBackgroundColor(BaseColor.DARK_GRAY);
                                    headerCell.setPadding(4);
                                    table.addCell(headerCell);
                                }
                                continue;   // 헤더행 처리 완료 → 다음 행(데이터)으로
                            }

                            // ===== 아래는 데이터행 처리 =====
                            // 중간에 반복되어 나오는 헤더행("거래종류")은 건너뛴다.
                            if (checkCell.getStringCellValue().equals("거래종류")) {
                                continue;
                            }

                            // [2026-08-16] 주말 제외: 셀을 그리기 전에 행 단위로 판정하여 토/일이면 행 전체를 건너뛴다.
                            // TODO 입력된 날짜 제외 추가
                            if (excludeCheckBox.isSelected() && isWeekendRow(row) || isExcludedInput(row, excludeInput)) {
                                continue;
                            }

                            for (int cellIdx = 0; cellIdx < cells; cellIdx++) {
                                XSSFCell cell = row.getCell(cellIdx, XSSFRow.MissingCellPolicy.RETURN_BLANK_AS_NULL);

                                String cellValue = "";
                                if (cell != null) {
                                    cellValue = cell.getStringCellValue();
                                }

                                // writeMap(선택된 헤더)에 있는 컬럼만 그린다.
                                if (!isPass(cellIdx)) {
                                    continue;
                                }

                                Font font = new Font(FileUtils.getKoreanBaseFont(), 8, Font.NORMAL, BaseColor.BLACK);
                                // TODO PDF 화면 꾸미기
                                extracted(cellValue, font, table);
                            }
                        }

                        document.add(table);

                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    } finally {
                        document.close();
                    }

                } catch (IOException foe) {
                    // 5번 항목: 사용자에게도 알림
                    JOptionPane.showMessageDialog(null, "변환 중 오류가 발생했습니다: " + foe.getMessage(),
                            "오류", JOptionPane.ERROR_MESSAGE);
                    foe.printStackTrace();
                }
                return true;
            }

            @Override
            protected void done() {
                // ③ 작업이 끝나면 가장 먼저 다이얼로그를 닫는다 (done은 EDT)
                progress.dispose();
                try {
                    Boolean ok = (Boolean) get();
                    if (Boolean.FALSE.equals(ok)) {
                        JOptionPane.showMessageDialog(owner, "출력할 헤더를 하나 이상 선택해 주세요",
                                "경고", JOptionPane.WARNING_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(owner, "PDF 생성이 완료되었습니다.",
                                "완료", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception ex) {
                    Throwable cause = (ex.getCause() != null) ? ex.getCause() : ex;
                    JOptionPane.showMessageDialog(owner,
                            "변환 중 오류가 발생했습니다: " + cause.getMessage(),
                            "오류", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
        progress.setVisible(true);
    }

    public boolean isPass(int index) {
        return writeMap.containsKey(index);
    }

    /**
     * [2026-08-16 추가] 해당 행의 거래일시가 토요일/일요일인지 판정한다.
     * 거래일시 컬럼을 못 찾았거나(dateColIdx < 0) 날짜 파싱에 실패하면(비어있음 등)
     * 주말이 아닌 것으로 간주하여 데이터가 임의로 사라지지 않도록 안전하게 처리한다.
     */
    private boolean isWeekendRow(XSSFRow row) {
        if (dateColIdx < 0) {
            return false;
        }
        XSSFCell cell = row.getCell(dateColIdx, XSSFRow.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) {
            return false;
        }
        try {
            String value = cell.getStringCellValue();
            LocalDate localDate = LocalDate.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            DayOfWeek dayOfWeek = localDate.getDayOfWeek();
            return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
        } catch (Exception ex) {
            return false;
        }
    }

    private boolean isExcludedInput(XSSFRow row, JTextField excludeInput) {
        if (dateColIdx < 0) {
            return false;
        }
        XSSFCell cell = row.getCell(dateColIdx, XSSFRow.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) {
            return false;
        }
        try {
            String value = cell.getStringCellValue();
            LocalDate localDate = LocalDate.parse(value);
            int dayOfMonth = localDate.getDayOfMonth();
            String text = excludeInput.getText();
            String[] split = text.split(",");
            return Arrays.asList(split).contains(String.valueOf(dayOfMonth));
        } catch (Exception ex) {
            return false;
        }
    }



    private void extracted(String cellValue, Font font, PdfPTable table) {
        PdfPCell cell = new PdfPCell();
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPhrase(new Phrase(cellValue, font));
        table.addCell(cell);
    }

    private void tmpExtracted(Headers findHeader, String cellValue, Font font, PdfPTable table) {
        PdfPCell cell = new PdfPCell();
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        switch (findHeader) {
            case TRANSACTION_TYPE:
            case BUS_ROUTE:
            case BOARDING_STATION:
            case ALIGHTING_STATION:
            case MERCHANT:
            case TRANSACTION_DATETIME:
            case AMOUNT:
                cell.setPhrase(new Phrase(cellValue, font));
                table.addCell(cell);
                break;
        }
    }
}
