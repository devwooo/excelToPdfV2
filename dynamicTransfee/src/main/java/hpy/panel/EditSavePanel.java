package hpy.panel;

import hpy.enums.ExcelFormat;
import hpy.utils.ComponentUtils;
import hpy.utils.ExportPDFListener;
import hpy.utils.FileUtils;
import hpy.utils.NumericFilter;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class EditSavePanel extends JPanel {
    private final String format;
    private final String filePath;
    private final String exportPath;
    private final GridBagConstraints gbc = new GridBagConstraints();
    private final JCheckBox checkBox = new JCheckBox();
    private final List<JCheckBox> listCheckBox = new ArrayList<>();
    JTextField excludeInput = new JTextField();
    private int count = 0;

    public EditSavePanel(Map<String, String> data, Consumer<Object> onBack) {
        this.format = data.get("format");
        this.filePath = data.get("filePath");
        this.exportPath = data.get("exportPath");

        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        checkboxHeaderInit();
        initStep2();
        addBtn(onBack);
    }


    private void initStep2() {
        ComponentUtils.addLabel(this, gbc, upCount(), "2. 토·일요일은 제외 할까요?");
        ComponentUtils.addField(this, gbc, upCount(), checkBox, null);

        ComponentUtils.addLabel(this, gbc, upCount(), "3. 제외할 날짜를 입력해 주세요 (1,15,20...)");
        ((AbstractDocument) excludeInput.getDocument()).setDocumentFilter(new NumericFilter());
        ComponentUtils.addField(this, gbc, upCount(), excludeInput, null);



    }

    private void checkboxHeaderInit() {
        try {
            // 라벨
            ComponentUtils.addLabel(this, gbc, upCount(), "1. 추가할 헤더를 선택해 주세요");

            // 체크박스
            int headerRow = ExcelFormat.from(format).getHeaderRow();
            List<String> headers = FileUtils.getHeaders(String.valueOf(headerRow), filePath);
            JPanel checkboxPanel = new JPanel(new GridLayout(0, 3, 10, 10));
            for (String header : headers) {
                JCheckBox cb = new JCheckBox(header);
                cb.setSelected(true);
                checkboxPanel.add(cb);
                listCheckBox.add(cb);
            }

            // checkboxHeaderInit() 안에서
            gbc.gridx = 0;
            gbc.gridy = upCount();
            gbc.weightx = 1.0;
            gbc.gridwidth = 2;                         // 버튼 칸까지 다 쓰기
            gbc.fill = GridBagConstraints.HORIZONTAL;
            this.add(checkboxPanel, gbc);              // setPreferredSize 안 거침

        } catch (Exception e) {
            // TODO 헤더가 비어있을경우 이전 패널로 가야하지 않을까?
            JOptionPane.showMessageDialog(this, e.getMessage(), "파일 오류", JOptionPane.ERROR_MESSAGE);

        }
    }

    private void addBtn(Consumer<Object> onBack) {
        JPanel btnPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        JButton prevBtn = new JButton("이전");
        JButton makePDF = new JButton("PDF 변환");

        btnPanel.add(prevBtn);
        btnPanel.add(makePDF);
        ComponentUtils.addField(this, gbc, upCount(), btnPanel, null);
        prevBtn.addActionListener(e -> onBack.accept(null));
        makePDF.addActionListener(new ExportPDFListener(ExcelFormat.from(format).getHeaderRow(), filePath, exportPath, checkBox, listCheckBox, excludeInput));
    }

    private int getCount() {
        return this.count;
    }

    private int upCount() {
        return this.count++;
    }


}
