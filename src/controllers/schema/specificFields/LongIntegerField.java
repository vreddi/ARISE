package controllers.schema.specificFields;

import controllers.schema.Field;
import net.sf.json.JSONObject;

public class LongIntegerField extends Field{

    public LongIntegerField(String fieldName, boolean isPrimaryKey, String dataType) {
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
            String ret = "Data conflicts across different sources:\n";
            JSONObject conflictingField = (JSONObject)data;
            for (Object k : conflictingField.keySet()) {
                String sources = (String)k;
                ret += "    From " + sources + " : " + conflictingField.getLong(sources) + "\n";
            }
            return ret.substring(0, ret.length()-1);
        } else {
            return Long.toString(record.getLong(fieldName));
        }
    }

}
