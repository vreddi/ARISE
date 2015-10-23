package controllers.schema.specificFields;

import controllers.schema.Field;
import info.debatty.java.stringsimilarity.JaroWinkler;
import net.sf.json.JSONObject;

public class NameField extends Field{
	public NameField(String fieldName, boolean isPrimaryKey, String dataType) {
        super(fieldName, isPrimaryKey, dataType);
    }

    @Override
    public int equalHelper(Object o1, Object o2) {
    	JaroWinkler jw = new JaroWinkler();
    	double result = jw.similarity(o1.toString().toLowerCase().trim(), o2.toString().toLowerCase().trim());
        if (result >= 0.9) return 2;
        if (result >= 0.7) return 1;
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
                ret += "    From " + sources + " : " + conflictingField.getString(sources) + "\n";
            }
            return ret;
        } else {
            return record.getString(fieldName);
        }
    }

    /**
     * Test NameField comparison
     public static void main(String[] args) {
    	Field f = new NameField("test", true, "Name");
    	Field g = new NameField("test2", false, "Name");
    	String s1 = "Jiawei";
    	String s2 = "Jeawei";
    	String s3 = "Jiwen";
    	boolean sim = f.equals(s1, s2);
    	boolean sim2 = f.equals(s1, s3);
    	boolean sim3 = g.equals(s1, s3);
    	System.out.println("primary: "+sim);
    	System.out.println("primary2: "+sim2);
    	System.out.println("non primary: "+sim3);
    }
     */
    
}
