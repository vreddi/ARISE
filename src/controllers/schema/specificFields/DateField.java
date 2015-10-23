package controllers.schema.specificFields;

import controllers.schema.Field;
import net.sf.json.JSONObject;

import java.util.StringTokenizer;

public class DateField extends Field {
	public DateField(String fieldName, boolean isPrimaryKey, String dataType) {
        super(fieldName, isPrimaryKey, dataType);
    }
	
	public int equalHelper(Object o1, Object o2) {
		// format yyyy-MM-dd
		String q1 = o1.toString().trim().toLowerCase();
        String q2 = o2.toString().trim().toLowerCase();
        StringTokenizer st1 = new StringTokenizer(q1, "-"); 
        StringTokenizer st2 = new StringTokenizer(q2, "-"); 
        int perfectMatch = 0;
        for (int i = 0; i < 3; i++) {
        	int i1 = Integer.parseInt(st1.nextToken());
        	int i2 = Integer.parseInt(st2.nextToken());
        	if (Math.abs(i1 - i2) > 2) return 0;
        	if (Math.abs(i1 - i2) == 0) perfectMatch++;
        }
		if (perfectMatch == 3) return 2;
		if (perfectMatch == 0) return 0;
		else return 1;
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
