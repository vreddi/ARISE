package controllers.schema.specificFields;

import controllers.schema.Field;
import net.sf.json.JSONObject;

public class IntegerField extends Field{

    public IntegerField(String fieldName, boolean isPrimaryKey, String dataType) {
        super(fieldName, isPrimaryKey, dataType);
    }

    @Override
    public boolean equals(Object o1, Object o2) {
        return o1.toString().trim().equalsIgnoreCase(o2.toString().trim());
    }

    @Override
    public String toString(JSONObject record) {
        Object data = record.get(fieldName);
        if (data instanceof JSONObject) {
            String ret = "\n";
            JSONObject conflictingField = (JSONObject)data;
            for (Object k : conflictingField.keySet()) {
                String sources = (String)k;
                ret += "  From " + sources + " : " + conflictingField.getInt(sources) + "\n";
            }
            return ret;
        } else {
            return Integer.toString(record.getInt(fieldName));
        }
    }

    @Override
    public boolean validate(Object value) {
        return value instanceof Integer;
    }

}
