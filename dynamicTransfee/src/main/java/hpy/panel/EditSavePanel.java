package hpy.panel;

import hpy.enums.ExcelFormat;
import hpy.utils.ComponentUtils;
import hpy.utils.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class EditSavePanel extends JPanel {
    private String format;
    private String filePath;
    private String exportPath;
    private final GridBagConstraints gbc = new GridBagConstraints();
    private final JCheckBox checkBox = new JCheckBox();
    private int count = 0;

    public EditSavePanel(Map data, Consumer<Object> onBack) {
        this.format = (String) data.get("format");
        this.filePath = (String) data.get("filePath");
        this.exportPath = (String) data.get("exportPath");
        checkboxHeaderInit();
        initStep2();
    }

    private void initStep2() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        ComponentUtils.addLabel(this, gbc, upCount(), "토·일요일은 제외 할까요?");
        ComponentUtils.addField(this, gbc, upCount(), checkBox, null);



    }

    private void checkboxHeaderInit() {
        try {
            int headerRow = ExcelFormat.from(format).getHeaderRow();
            List<String> headers = FileUtils.getHeaders(String.valueOf(headerRow), filePath);

            for (String header : headers) {

            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "파일 오류", JOptionPane.ERROR_MESSAGE);

        }
    }

    private int getCount() {
        return this.count;
    }

    private int upCount() {
        return this.count++;
    }
}
