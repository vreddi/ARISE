package controllers.wrapper.sourceWrapper.typeSpecificWrappers;

import controllers.schema.Field;
import controllers.schema.SchemaObj;
import controllers.wrapper.sourceWrapper.DirectSourceWrapper;
import controllers.wrapper.sourceWrapper.GeneralSourceWrapper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import util.SQLDBConnector;
import java.sql.ResultSet;

public class SQLDBHandler extends DirectSourceWrapper {

    public SQLDBHandler(SchemaObj schema, String name, JSONObject settings) {
        super(schema, name, settings);
    }

    @Override
    public JSONArray getResultAsJSONArray(JSONObject searchConditions) {
        String driver = null;
        String protocol = null;
        String url = settings.getString("location");
        String dbkind = settings.getString("dbkind").trim().toLowerCase();
        if (dbkind.equals("mysql")) {
            protocol = "jdbc:mysql";
            driver = "com.mysql.jdbc.Driver";
        }
        if (driver == null || protocol == null) {
            //  Unknown kind of database
            return null;
        }
        url = url.replaceAll("^https?", protocol);
        if (!url.startsWith(protocol)) {
            url = protocol + "://" + url;
        }
        SQLDBConnector conn = new SQLDBConnector(
                driver, url, settings.getString("user"), settings.getString("password")
        );
        if (conn.connect()) {
            String SQLCommand = settings.getString("sqltemplate");
            SQLCommand = SQLCommand.replaceAll("\\[\\[first\\]\\]", searchConditions.getString("first"));
            SQLCommand = SQLCommand.replaceAll("\\[\\[last\\]\\]", searchConditions.getString("last"));
            ResultSet rs = conn.executeSQL(SQLCommand);
            JSONArray results = new JSONArray();
            try {
                if (!rs.isAfterLast()) {
                    JSONObject mappings = settings.getJSONObject("mappings");
                    rs.first();
                    while (!rs.isAfterLast()) {
                        JSONObject record = new JSONObject();
                        for (Field field : schema.getAllFields()) {
                            if (mappings.containsKey(field.fieldName)) {
                                if (field.dataType.equalsIgnoreCase("Text")) {
                                    record.put(field.fieldName, rs.getString(mappings.getString(field.fieldName)));
                                } else if (field.dataType.equalsIgnoreCase("Integer")) {
                                    record.put(field.fieldName, rs.getInt(mappings.getString(field.fieldName)));
                                } else if (field.dataType.equalsIgnoreCase("LongInteger")) {
                                    record.put(field.fieldName, rs.getLong(mappings.getString(field.fieldName)));
                                }
                            }
                        }
                        results.add(record);
                        rs.next();
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            return results;
        } else {
            System.out.println("Failed to establish connection with database server at " + settings.getString("location"));
            return null;
        }

    }
}
