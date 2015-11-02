package gui.components;

import controllers.graphAnalyzer.GlobalGraphInfo;
import controllers.schema.Field;
import controllers.schema.SchemaObj;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;


public class ResultCard extends JPanel{

    private JTree tree;
    private SchemaObj schema;
    private JScrollPane scrollPane;

    public ResultCard(JSONObject data) {
        super();
        this.schema = SchemaObj.fromJSONArray(data.getJSONArray("schema"));
        this.setSize(800, 540);
        this.setPreferredSize(new Dimension(800, 540));
        this.setBackground(new Color(255, 255, 255));
        display(data.getJSONArray("results"));
    }

    public void display(JSONArray results) {

        DefaultMutableTreeNode top = new DefaultMutableTreeNode("All results");


        for (int i = 0; i < results.size(); i++) {
            JSONArray group = results.getJSONArray(i);
            if (group.size() == 0) continue;
            DefaultMutableTreeNode currentGroupNode = new DefaultMutableTreeNode("Group " + (i+1));
            top.add(currentGroupNode);
            for (int j = 0; j < group.size(); j++) {
                JSONObject current = group.getJSONObject(j);
                String txt = "<html>";
                for (Field field : schema.getAllFields()) {
                    if (current.containsKey(field.fieldName) && field.toString(current).trim().length() > 0 ) {
                        txt += field.fieldName + ": " + field.toString(current) + "<p>";
                    }
                }
                txt += "Record provided by source(s): " + current.getString("Included by") + "</html>";
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(txt);
                currentGroupNode.add(node);

                // Graph Analyzing Info Extraction
                // -------------------------------------------------------------
                String key = current.getString("Included by");
                if(GlobalGraphInfo.sourceToCount.containsKey(key) == true){
                    int currentCount = GlobalGraphInfo.sourceToCount.get(key);
                    GlobalGraphInfo.sourceToCount.put(key, currentCount + 1);
                }
                else{
                    if(!key.contains(",")){
                        GlobalGraphInfo.sourceToCount.put(key, 0);
                        GlobalGraphInfo.keys.add(key);
                    }
                }
                // --------------------------------------------------------------
            }
        }

        this.tree = new JTree(top);
        tree.setRowHeight(0);
        tree.setCellRenderer(new DefaultTreeCellRenderer() {
            private Border border = BorderFactory.createEmptyBorder ( 6, 6, 6, 6 );
            public Component getTreeCellRendererComponent ( JTree tree, Object value, boolean sel,
                                                            boolean expanded, boolean leaf, int row,
                                                            boolean hasFocus )
            {
                JLabel label = ( JLabel ) super
                        .getTreeCellRendererComponent ( tree, value, sel, expanded, leaf, row,
                                hasFocus );
                label.setBorder ( border );
                return label;
            }
        });
        this.tree.putClientProperty("JTree.lineStyle", "Horizontal");
        this.scrollPane = new JScrollPane(this.tree);
        this.add(this.scrollPane);
        this.scrollPane.setSize(800, 535);
        this.scrollPane.setPreferredSize(new Dimension(800, 535));
        this.tree.setVisible(true);
        this.scrollPane.setVisible(true);

    }

}
