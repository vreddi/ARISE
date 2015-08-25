package gui.shared.adders;

import gui.local.MainFrame;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class AspectAdder extends JFrame {

    private final int nameWidth = 150;
    private final int pkWidth = 75;
    private final int syntaxWidth = 150;
    private final int lineHeight = 25;
    private final int buttonHeight = 25;

    private class AspectLine extends JPanel{

        public JTextField name;
        public Choice isPrimaryKey;
        public Choice syntax;

        public AspectLine() {
            this.setAlignmentX(Component.LEFT_ALIGNMENT);
            BoxLayout layout = new BoxLayout(this, BoxLayout.X_AXIS);
            this.setLayout(layout);
            this.setSize(nameWidth + pkWidth + syntaxWidth, lineHeight);
            this.setPreferredSize(new Dimension(nameWidth + pkWidth + syntaxWidth, lineHeight));
            name = new JTextField();
            this.add(name);
            name.setSize(nameWidth, lineHeight);
            name.setPreferredSize(new Dimension(nameWidth, lineHeight));
            this.add(Box.createHorizontalStrut(3));
            isPrimaryKey = new Choice();
            this.add(isPrimaryKey);
            isPrimaryKey.addItem("Yes");
            isPrimaryKey.addItem("No");
            isPrimaryKey.setSize(pkWidth, lineHeight);
            isPrimaryKey.setPreferredSize(new Dimension(pkWidth, lineHeight));
            this.add(Box.createHorizontalStrut(3));
            syntax = new Choice();
            this.add(syntax);
            syntax.addItem("Text");
            syntax.addItem("Integer");
            syntax.addItem("LongInteger");
            syntax.setSize(syntaxWidth, lineHeight);
            syntax.setPreferredSize(new Dimension(syntaxWidth, lineHeight));
            this.setVisible(true);
        }

    }

    public JTextField name;
    public ArrayList<AspectLine> fields;
    public JButton addLineButton;
    public JButton finishButton;
    private JPanel lines;
    private MainFrame mainFrame;

    public AspectAdder(final MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.fields = new ArrayList<AspectLine>();
        this.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        name = new JTextField("Input aspect name here.");
        name.setSize(nameWidth + pkWidth + syntaxWidth + 10, lineHeight + 5);
        name.setPreferredSize(new Dimension(nameWidth + pkWidth + syntaxWidth + 10, lineHeight + 5));
        this.getContentPane().add(name);
        JLabel nameLabel = new JLabel("Name", SwingConstants.CENTER);
        nameLabel.setSize(nameWidth, lineHeight);
        nameLabel.setPreferredSize(new Dimension(nameWidth, lineHeight));
        JLabel pkLabel = new JLabel("Primary Key", SwingConstants.CENTER);
        pkLabel.setSize(pkWidth, lineHeight);
        pkLabel.setPreferredSize(new Dimension(pkWidth, lineHeight));
        JLabel syntaxLabel = new JLabel("Syntax", SwingConstants.CENTER);
        syntaxLabel.setSize(syntaxWidth, lineHeight);
        syntaxLabel.setPreferredSize(new Dimension(syntaxWidth, lineHeight));
        this.getContentPane().add(nameLabel);
        this.getContentPane().add(pkLabel);
        this.getContentPane().add(syntaxLabel);
        addLineButton = new JButton("Add another field");
        addLineButton.setSize((nameWidth + pkWidth + syntaxWidth) / 2, buttonHeight);
        addLineButton.setPreferredSize(new Dimension((nameWidth + pkWidth + syntaxWidth + 10) / 2, buttonHeight));
        addLineButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addLine();
            }
        });
        finishButton = new JButton("Finish");
        finishButton.setSize((nameWidth + pkWidth + syntaxWidth) / 2, buttonHeight);
        finishButton.setPreferredSize(new Dimension((nameWidth + pkWidth + syntaxWidth + 10) / 2, buttonHeight));
        finishButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                finish();
            }
        });
        lines = new JPanel();
        lines.setLayout(new FlowLayout());
        lines.setSize(
                nameWidth + pkWidth + syntaxWidth,
                (lineHeight + 5) * 10
        );
        lines.setPreferredSize(new Dimension(
                nameWidth + pkWidth + syntaxWidth,
                (lineHeight + 5) * 10
        ));
        this.getContentPane().add(lines);
        this.getContentPane().add(addLineButton);
        this.getContentPane().add(finishButton);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                super.windowOpened(e);
                name.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        super.focusGained(e);
                        if (name.getText().equals("Input aspect name here.")) {
                            name.setText("");
                        }
                    }

                    @Override
                    public void focusLost(FocusEvent e) {
                        super.focusLost(e);
                        if (name.getText().trim().length() == 0) {
                            name.setText("Input aspect name here.");
                        }
                    }
                });
            }
        });
        this.setSize(
                nameWidth + pkWidth + syntaxWidth,
                name.getHeight() + nameLabel.getHeight() + lines.getHeight() + addLineButton.getHeight() + 45
        );
        this.setResizable(false);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(
                (int) (size.getWidth() - this.getWidth()) / 2,
                (int) (size.getHeight() - this.getHeight()) / 2
        );
        this.setVisible(true);
        this.addLine();
        this.requestFocus();
    }

    public void addLine() {
        AspectLine line = new AspectLine();
        fields.add(line);
        lines.add(line);
        this.validate();
        this.repaint();
    }

    public void finish() {
        if (name.getText().equals("Input aspect name here.")) {
            this.dispose();
            return;
        }
        JSONObject description = new JSONObject();
        description.put("name", name.getText());
        JSONArray fieldList = new JSONArray();
        for (AspectLine field : fields) {
            JSONObject f = new JSONObject();
            f.put("name", field.name.getText());
            f.put("isPK", field.isPrimaryKey.getSelectedItem());
            f.put("syntax", field.syntax.getSelectedItem());
            fieldList.add(f);
        }
        description.put("fields", fieldList);
        mainFrame.addAspect(description);
        this.dispose();
    }

}

