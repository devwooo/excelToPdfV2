package hpy.utils;

import javax.swing.*;
import java.awt.*;

public class ComponentUtils {

    /** 라벨을 한 행 전체 폭으로 배치 */
    public static void addLabel(JPanel panel, GridBagConstraints gbc, int row, String text) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel(text), gbc);
    }

    /** 라벨을 한 행 전체 폭으로 배치 */
    public static void addCheckbox(JPanel panel, GridBagConstraints gbc, int row, String text) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JCheckBox(text), gbc);
    }

    /** 입력 필드를 배치하고, 버튼이 있으면 오른쪽에 붙인다 */
    public static void addField(JPanel panel, GridBagConstraints gbc, int row, JComponent field, JButton button) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 1.0;
        gbc.gridwidth = (button == null) ? 2 : 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        field.setPreferredSize(new Dimension(300, 28));
        panel.add(field, gbc);

        if (button != null) {
            gbc.gridx = 1;
            gbc.weightx = 0;
            gbc.gridwidth = 1;
            gbc.fill = GridBagConstraints.NONE;
            panel.add(button, gbc);
        }
    }


}
