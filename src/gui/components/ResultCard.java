package gui.components;

import controllers.schema.Field;
import controllers.schema.SchemaObj;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import javax.swing.*;
import java.awt.*;


public class ResultCard extends JPanel{

    private JTextArea textArea;
    private SchemaObj schema;
    private JScrollPane scrollPane;

    public ResultCard(JSONObject data) {
        super();
        this.schema = SchemaObj.fromJSONArray(data.getJSONArray("schema"));
        this.textArea = new JTextArea("");
        this.textArea.setLineWrap(true);
        this.textArea.setEditable(false);
        this.scrollPane = new JScrollPane(this.textArea);
        this.scrollPane.setSize(800, 535);
        this.scrollPane.setPreferredSize(new Dimension(800, 535));
        this.setSize(800, 540);
        this.setPreferredSize(new Dimension(800, 540));
        this.setBackground(new Color(255, 255, 255));
        this.add(this.scrollPane);
        display(data.getJSONArray("results"));
    }

    public void display(JSONArray results) {
        String toDisplay = "";
        for (Object result : results) {
            JSONObject current = (JSONObject)result;
            for (Field field : schema.getAllFields()) {
                if (current.containsKey(field.fieldName) && field.toString(current).trim().length() > 0 ) {
                    toDisplay += field.fieldName + ": " + field.toString(current) + "\n";
                }
            }
            toDisplay += "Record provided by source(s): " + current.getString("Included by");
            toDisplay += "\n\n";
        }
        if (toDisplay.length() == 0) {
            toDisplay = "No record found for this researcher.";
        }
        this.textArea.setText(toDisplay);
    }

}
