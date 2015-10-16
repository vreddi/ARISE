package controllers.schema.specificFields;

import controllers.schema.Field;
import net.sf.json.JSONObject;

public class DecimalField extends Field {

    public DecimalField(String fieldName, boolean isPrimaryKey, String dataType) {
        super(fieldName, isPrimaryKey, dataType);
    }

    @Override
    public boolean equals(Object o1, Object o2) {
        return Double.parseDouble(o1.toString()) == Double.parseDouble(o2.toString());
    }

    @Override
    public String toString(JSONObject record) {
        Object data = record.get(fieldName);
        if (data instanceof JSONObject) {
            String ret = "\n";
            JSONObject conflictingField = (JSONObject)data;
            for (Object k : conflictingField.keySet()) {
                String sources = (String)k;
                ret += "  From " + sources + " : " + conflictingField.getDouble(sources) + "\n";
            }
            return ret;
        } else {
            return Double.toString(record.getDouble(fieldName));
        }
    }

    @Override
    public boolean validate(Object value) {
        return value instanceof Double || value instanceof Float;
    }
}
