package controllers.schema.specificFields;

import controllers.schema.Field;
import net.sf.json.JSONObject;

public class TextField extends Field{

    public TextField(String fieldName, boolean isPrimaryKey, String dataType) {
        super(fieldName, isPrimaryKey, dataType);
    }

    @Override
    public boolean equals(Object o1, Object o2) {
        return ((String) o1).trim().equalsIgnoreCase(((String)o2).trim());
    }

    @Override
    public String toString(JSONObject record) {
        Object data = record.get(fieldName);
        if (data instanceof JSONObject) {
            String ret = "\n";
            JSONObject conflictingField = (JSONObject)data;
            for (Object k : conflictingField.keySet()) {
                String sources = (String)k;
                ret += "  From " + sources + " : " + conflictingField.getString(sources) + "\n";
            }
            return ret;
        } else {
            return record.getString(fieldName);
        }
    }

    @Override
    public boolean validate(Object value) {
        return value instanceof String;
    }

}
