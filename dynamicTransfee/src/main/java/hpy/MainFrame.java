package hpy;

import hpy.panel.EditSavePanel;
import hpy.panel.ReadPanel;

import javax.swing.*;
import java.awt.*;

/**
 * 모든 패널을 붙일 프레임
 */
public class MainFrame extends JFrame {

    private final JPanel container = new JPanel(new CardLayout());
    private EditSavePanel editSavePanel; // 다음 클릭 시점에 생성

    public MainFrame() {
        setTitle("동적 Excel To PDF");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        CardLayout cardLayout = (CardLayout) container.getLayout();

        ReadPanel readPanel = new ReadPanel(data -> {
            // 파일 재선택 후 다시 들어오는 경우 이전 패널 제거
            if (editSavePanel != null) {
                container.remove(editSavePanel);
            }
            editSavePanel = new EditSavePanel(data, back -> cardLayout.show(container, "step1"));
            container.add(editSavePanel, "step2");
            cardLayout.show(container, "step2");
            pack(); // step2 크기에 맞춰 프레임 다시 조정
        });

        container.add(readPanel, "step1");
        add(container);
        pack();
        setResizable(false);
        setLocationRelativeTo(null); // 화면 중앙에 배치
    }
}
