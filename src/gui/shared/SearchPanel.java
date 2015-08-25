package gui.shared;

import gui.local.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SearchPanel extends JPanel {

    private JTextField nameInput;
    private JTextField affiliationInput;
    private JButton searchButton;
    private MainFrame parentWindow;

    public SearchPanel(MainFrame parentWindow) {
        this.parentWindow = parentWindow;
        setSize(800, 80);
        setBackground(new Color(231, 231, 231));
        setPreferredSize(new Dimension(800, 80));
        setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        //  Set up and add name input
        nameInput = new JTextField();
        nameInput.setPreferredSize(new Dimension((this.getWidth() / 2) - 5, this.getHeight() - 40));
        nameInput.addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                if (nameInput.getText().equals("Name, i.e. Jiawei Han")) {
                    nameInput.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                if (nameInput.getText().length() == 0) {
                    nameInput.setText("Name, i.e. Jiawei Han");
                }
            }
        });
        add(this.nameInput);
        //  Set up and add affiliation input
        affiliationInput = new JTextField();
        affiliationInput.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                if (affiliationInput.getText().equals("Affiliation, i.e. UIUC")) {
                    affiliationInput.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                if (affiliationInput.getText().length() == 0) {
                    affiliationInput.setText("Affiliation, i.e. UIUC");
                }
            }
        });
        affiliationInput.setPreferredSize(new Dimension((this.getWidth()/2)-5, this.getHeight()-40));
        add(this.affiliationInput);
        //  Set up and add search button
        searchButton = new JButton("Get Profile");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startSearch();
            }
        });
        searchButton.setSize(120, 30);
        add(searchButton);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                requestFocus();
            }
        });
    }

    public String getInputName() {
        return this.nameInput.getText();
    }

    public String getInputAffiliation() {
        return this.affiliationInput.getText();
    }

    public void startSearch() {
        this.parentWindow.startSearch(this.getInputName(), this.getInputAffiliation());
    }

    public void promptAndFocus() {
        this.searchButton.requestFocus();
        this.nameInput.setText("Name, i.e. Jiawei Han");
        this.affiliationInput.setText("Affiliation, i.e. UIUC");
    }

}

