package controllers.schema.specificFields;

import controllers.schema.Field;
import net.sf.json.JSONObject;

public class LongIntegerField extends Field{

    public LongIntegerField(String fieldName, boolean isPrimaryKey, String dataType) {
        super(fieldName, isPrimaryKey, dataType);
    }

    @Override
    public int equalHelper(Object o1, Object o2) {
    	int i1 = Integer.parseInt(o1.toString());
    	int i2 = Integer.parseInt(o2.toString());
    	if ((double)Math.abs(i1 - i2)/(double)Math.max(i1, i2) <= 0.01) return 2;
    	else if ( (double)Math.abs(i1 - i2)/(double)Math.max(i1, i2) <= 0.1) return 1;
    	return 0;
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

    /**
     * Test IntegerField comparison
   public static void main(String[] args) {
    	Field f = new LongIntegerField("test", true, "LongInteger");
    	Field g = new LongIntegerField("test2", false, "LongInteger");
    	String s1 = "200500";
    	String s2 = "202500";
    	String s3 = "220000";
    	int sim = f.equals(s1, s2);
    	int sim2 = f.equals(s1, s3);
    	int sim3 = g.equals(s1, s3);
    	System.out.println("primary1: "+sim);
    	System.out.println("primary2: "+sim2);
    	System.out.println("non primary: "+sim3);
    }
    */
}
