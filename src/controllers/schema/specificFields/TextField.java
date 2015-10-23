package controllers.schema.specificFields;

import controllers.schema.Field;
import info.debatty.java.stringsimilarity.NormalizedLevenshtein;
import net.sf.json.JSONObject;

public class TextField extends Field{

    public TextField(String fieldName, boolean isPrimaryKey, String dataType) {
        super(fieldName, isPrimaryKey, dataType);
    }

    @Override
    public int equalHelper(Object o1, Object o2) {
    	NormalizedLevenshtein leven = new NormalizedLevenshtein();
    	double result = leven.similarity(o1.toString().toLowerCase().trim(), o2.toString().toLowerCase().trim());
        if (result >= 0.75) return 2;
        if (result >= 0.5) return 1;
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
                ret += "  From " + sources + " : " + conflictingField.getString(sources) + "\n";
            }
            return ret;
        } else {
            return record.getString(fieldName);
        }
    }

    /**
     * Test TextField comparison
     public static void main(String[] args) {
    	Field f = new TextField("test", true, "Text");
    	Field g = new TextField("test2", false, "Text");
    	String s1 = "Study of Levenshtein comparison";
    	String s2 = "A Study of Levenschtein comparing";
    	String s3 = "LevenShtein comparison";
    	boolean sim = f.equals(s1, s2);
    	boolean sim2 = f.equals(s1, s3);
    	boolean sim3 = g.equals(s1, s3);
    	System.out.println("primary1: "+sim);
    	System.out.println("primary2: "+sim2);
    	System.out.println("non primary: "+sim3);
    }
    */
    
}
