package gui.components.adders;

import gui.MainFrame;

import javax.swing.*;
import java.awt.*;

public class SourceAdder extends JFrame {

    private final MainFrame mainFrame;
    private final int lineHeight = 25;
    private final int componentWidth = 680;

    private Choice aspect;
    private JTextField name;

    public SourceAdder(final MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

}
