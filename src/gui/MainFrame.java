package gui;

import controllers.SearchHandler;
import controllers.wrapper.GeneralWrapper;
import gui.components.ResultPanel;
import gui.components.SearchPanel;
import gui.components.adders.AspectAdder;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import static util.Utilities.getNameTokens;
import static util.Utilities.readFileContent;

public class MainFrame extends JFrame{

    private SearchPanel spanel;
    private ResultPanel rpanel;
    private JMenuBar menubar;
    private SearchHandler searchHandler;

    public MainFrame() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        this.setTitle("ARISE Researcher Profiling System");
        this.setLayout(new BorderLayout(5, 5));
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        /* Initialize search handler */
        this.searchHandler = new SearchHandler();
        initializeMenuBar();
        initializeSearchPanel();
        initializeResultPanel();
        this.pack();
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(
                (int)(size.getWidth() - this.getWidth())/2,
                (int)(size.getHeight() - this.getHeight())/2
        );
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                spanel.promptAndFocus();
            }
        });
        this.setMinimumSize(this.getSize());
        this.setResizable(false);
        this.setVisible(true);
    }

    public JSONObject search(JSONObject searchConditions) {
        return searchHandler.search(searchConditions);
    }

    public void startSearch(String name, String affiliation) {
        if (name.equals("Name, i.e. Jiawei Han")) {
            return;
        }
        JSONObject searchConditions = new JSONObject();
        String[] nameTokens = getNameTokens(name);
        searchConditions.put("first", nameTokens[0]);
        searchConditions.put("last", nameTokens[1]);
        if (nameTokens[2].length() != 0) {
            searchConditions.put("middle", nameTokens[2]);
        }
        searchConditions.put("fullName", name);
        if(!affiliation.equals("Affiliation, i.e. UIUC")) {
            searchConditions.put("affiliation", affiliation);
        }
        JSONObject results = searchHandler.search(searchConditions);
        this.remove(this.rpanel);
        initializeResultPanel();
        this.rpanel.display(results);
        this.validate();
        this.repaint();
    }

    public void addAspect(JSONObject description) {
        for (Object field : description.getJSONArray("fields")) {
            JSONObject f = (JSONObject)field;
        }
        searchHandler.addAspect(description);
        initializeMenuBar();
    }

    private void initializeMenuBar() {
        JMenuBar menubar  = new JMenuBar();
        JMenu registration = new JMenu("Registration");
        menubar.add(registration);
        JSONObject registeredAspects = this.searchHandler.getRegisteredAspects();
        for (Object k : registeredAspects.keySet()) {
            final String aspect = (String)k;
            JMenu aspectItem = new JMenu(aspect);
            registration.add(aspectItem);
            JSONArray sources = registeredAspects.getJSONArray(aspect);
            for (Object s : sources) {
                final String source = (String)s;
                JMenuItem sourceItem = new JMenuItem(source);
                sourceItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String settings = readFileContent(GeneralWrapper.basePath + "/" + aspect + "/" + source + "/settings.json");
                        int dialogResult = JOptionPane.showConfirmDialog(
                            null,
                            settings + "\nDo you want to activate or continue the activation of this source?",
                            "Settings of " + source,
                            JOptionPane.YES_NO_OPTION
                        );
                        searchHandler.setActivation(aspect, source, dialogResult == JOptionPane.YES_OPTION);
                    }
                });
                aspectItem.add(sourceItem);
            }
        }
        final MainFrame currentFrame = this;
        JMenuItem addAspectItem = new JMenuItem("Add more...");
        addAspectItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AspectAdder adder = new AspectAdder(currentFrame);
            }
        });
        registration.add(addAspectItem);
        this.setJMenuBar(menubar);
        this.menubar = menubar;
        this.validate();
        this.repaint();
    }

    private void initializeSearchPanel() {
        this.spanel = new SearchPanel(this);
        this.getContentPane().add(this.spanel, BorderLayout.NORTH);
    }

    private void initializeResultPanel() {
        this.rpanel = new ResultPanel();
        this.getContentPane().add(this.rpanel, BorderLayout.CENTER);
    }
}
