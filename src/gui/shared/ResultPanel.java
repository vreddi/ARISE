package gui.shared;

import net.sf.json.JSONObject;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ResultPanel extends JPanel{

    private CardLayout layout;
    private JPanel infoArea;
    private JPanel switchArea;
    private ButtonGroup switchButtons;

    public ResultPanel() {
        super();
        this.setSize(800, 610);
        this.setPreferredSize(new Dimension(800, 610));
        this.infoArea = new JPanel();
        this.infoArea.setSize(800, 540);
        this.infoArea.setSize(new Dimension(800, 540));
        this.layout = new CardLayout();
        this.infoArea.setLayout(this.layout);
        this.infoArea.setVisible(true);
        this.switchArea = new JPanel(new FlowLayout());
        this.switchArea.setSize(800, 60);
        this.switchArea.setSize(new Dimension(800, 60));
        this.switchArea.setVisible(true);
        this.setLayout(new FlowLayout());
        this.add(this.infoArea);
        this.add(this.switchArea);
        this.setVisible(true);
    }

    public void display(JSONObject results) {
        this.layout = new CardLayout();
        this.infoArea.setLayout(this.layout);
        this.switchArea.setLayout(new FlowLayout());
        this.switchButtons = new ButtonGroup();
        for (Object k : results.keySet()) {
            String cardName = (String)k;
            this.infoArea.add(new ResultCard(results.getJSONObject(cardName)), cardName);
            final JRadioButton button = new JRadioButton(cardName);
            button.setVisible(true);
            button.setSize(100, 30);
            button.setPreferredSize(new Dimension(100, 30));
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    switchCard(button.getText());
                }
            });
            this.switchButtons.add(button);
            this.switchArea.add(button);
        }
        layout.first(this.infoArea);
    }

    private void switchCard(String name) {
        this.layout.show(this.infoArea, name);
    }

}
