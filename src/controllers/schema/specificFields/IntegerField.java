package controllers.schema.specificFields;

import controllers.schema.Field;
import net.sf.json.JSONObject;

public class IntegerField extends Field{

    public IntegerField(String fieldName, boolean isPrimaryKey, String dataType) {
        super(fieldName, isPrimaryKey, dataType);
    }

    @Override
    public int equalHelper(Object o1, Object o2) {
    	int i1 = Integer.parseInt(o1.toString());
    	int i2 = Integer.parseInt(o2.toString());
    	if (i1 < 2500 && i2 < 2500) {
    		if ( Math.abs(i1 - i2) <= 2) return 2;
    		if ( Math.abs(i1 - i2) <= 3) return 1;
    	}
    	else {
    		if ( (double)(i1 - i2)/(double)Math.max(i1, i2) <= 0.002) return 2;
    		if ( (double)(i1 - i2)/(double)Math.max(i1, i2) <= 0.005) return 1;
    	}
        return 0;
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

    /**
     * Test IntegerField comparison
    public static void main(String[] args) {
    	Field f = new IntegerField("test", true, "Integer");
    	Field g = new IntegerField("test2", false, "Integer");
    	String s1 = "2012";
    	String s2 = "2010";
    	String s3 = "2005";
    	int sim = f.equals(s1, s2);
    	int sim2 = f.equals(s1, s3);
    	int sim3 = g.equals(s1, s3);
    	System.out.println("primary1: "+sim);
    	System.out.println("primary2: "+sim2);
    	System.out.println("non primary: "+sim3);
    }
    */
}
