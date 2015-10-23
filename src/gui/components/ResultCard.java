package gui.components;

import controllers.schema.Field;
import controllers.schema.SchemaObj;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import util.Constants;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.util.Map;


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
        display(data.getJSONObject("results"));
    }

    public void display(JSONObject results) {

        DefaultMutableTreeNode top = new DefaultMutableTreeNode("Keywords:");


        for (Object e : results.entrySet()) {
            Map.Entry<String, JSONArray> ent = (Map.Entry)e;
            String[] kws = ent.getKey().split(Constants.kwDelimiter);
            JSONArray group = ent.getValue();
            if (group.size() == 0) continue;
            String groupName = "";
            for (int i = 0; i < kws.length; i++) {
                groupName += kws[i] + ", ";
            }
            groupName = groupName.substring(0, groupName.length()-2);
            DefaultMutableTreeNode currentGroupNode = new DefaultMutableTreeNode(groupName);
            top.add(currentGroupNode);
            for (int j = 0; j < group.size(); j++) {
                JSONObject current = group.getJSONObject(j);
                String txt = "<html>";
                for (Field field : schema.getAllFields()) {
                    if (current.containsKey(field.fieldName) && field.toString(current).trim().length() > 0 ) {
                        txt += "<b>" + field.fieldName + "</b>: " + field.toString(current) + "<p>";
                    }
                }
                txt += "Record provided by source(s): " + current.getString("Included by") + "</html>";
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(txt);
                currentGroupNode.add(node);
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
