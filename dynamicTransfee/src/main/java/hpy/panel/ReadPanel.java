package hpy.panel;

import hpy.utils.ComponentUtils;
import hpy.utils.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ReadPanel extends JPanel {

    private final JRadioButton tmoneyRadio = new JRadioButton("Tmoney", true);
//    private final JRadioButton tossRadio = new JRadioButton("Toss");
//    private final JRadioButton kakaoRadio = new JRadioButton("Kakao");
    private final ButtonGroup formatGroup = new ButtonGroup();
    private final JTextField filePathField = new JTextField();
    private final JTextField exportPathField = new JTextField();
    private final GridBagConstraints gbc = new GridBagConstraints();

    public ReadPanel(Consumer<Map> onNext) {
        initComponents();

        // 4. 다음 버튼
        JButton nextBtn = new JButton("다음");
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(24, 6, 6, 6); // 윗부분과 간격 확보
        add(nextBtn, gbc);
        nextBtn.addActionListener(e -> {
            HashMap<String, String> rtnMap = new HashMap<>();
            rtnMap.put("format", getSelectedFormat());
            rtnMap.put("filePath", filePathField.getText());
            rtnMap.put("exportPath", exportPathField.getText());
            onNext.accept(rtnMap);
        });
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 1. 파일 양식 선택
        ComponentUtils.addLabel(this, gbc, 0, "파일 양식을 선택해 주세요");
        JPanel formatPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        for (JRadioButton radio : new JRadioButton[]{tmoneyRadio}) {
            formatGroup.add(radio);
            formatPanel.add(radio);
        }
        ComponentUtils.addField(this, gbc, 1, formatPanel, null);


        // 2. 추출할 파일 선택
        filePathField.setEditable(false);
        ComponentUtils.addLabel(this, gbc, 2, "추출할 파일을 선택해 주세요");
        JButton fileBtn = new JButton("찾아보기");
        fileBtn.addActionListener(e -> chooseFile());
        ComponentUtils.addField(this, gbc, 3, filePathField, fileBtn);

        // 3. 저장 경로 선택
        exportPathField.setEditable(false);
        ComponentUtils.addLabel(this, gbc, 4, "추출된 파일을 저장할 경로를 선택해 주세요");
        JButton exportBtn = new JButton("찾아보기");
        exportBtn.addActionListener(e -> chooseExportDir());
        ComponentUtils.addField(this, gbc, 5, exportPathField, exportBtn);
    }


    private String getSelectedFormat() {
//        if (tossRadio.isSelected()) return "Toss";
//        if (kakaoRadio.isSelected()) return "Kakao";
        return "Tmoney";
    }

    private void chooseFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File selected = fileChooser.getSelectedFile();
                FileUtils.isValidFile(selected);
                filePathField.setText(selected.getAbsolutePath());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "파일 오류", JOptionPane.ERROR_MESSAGE);
                // 실패 했다면 기존에 있던것도 삭제
                filePathField.setText("");
            }
        }
    }

    private void chooseExportDir() {
        JFileChooser exportChooser = new JFileChooser();
        exportChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (exportChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selected = exportChooser.getSelectedFile();
            exportPathField.setText(selected.getAbsolutePath());
        }
    }

    public void reset() {
        filePathField.setText("");
        exportPathField.setText("");
    }

}
