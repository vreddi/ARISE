package controllers.schema;

import controllers.schema.specificFields.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;

public class SchemaObj {

    public ArrayList<Field> fields;

    public SchemaObj() {
        this.fields = new ArrayList<Field>(0);
    }
    
    public void addField(String fieldName, String isPK, String dataType) {
        boolean isPrimaryKey = isPK.equals("Yes");
        addField(fieldName, isPrimaryKey, dataType);
    }    

    private void addField(String fieldName, boolean isPK, String dataType) {
        Field f = null;
        if (dataType.equalsIgnoreCase("Text")) {
            f = new TextField(fieldName, isPK, dataType);
        } else if (dataType.equalsIgnoreCase("Integer")) {
            f = new IntegerField(fieldName, isPK, dataType);
        } else if (dataType.equalsIgnoreCase("LongInteger")) {
            f = new LongIntegerField(fieldName, isPK, dataType);
        } else if (dataType.equalsIgnoreCase("Name")) {
        	f = new NameField(fieldName, isPK, dataType);
        } else if (dataType.equalsIgnoreCase("Location")) {
        	f = new LocationField(fieldName, isPK, dataType);
        } // Location is for attributes that have acronyms and have a real world location association
        this.fields.add(f);
    }

    public void addField(Field f) {
        this.fields.add(f);
    }

    public ArrayList<Field> getPrimaryKeys() {
        ArrayList<Field> pk = new ArrayList<Field>();
        for (Field f : this.fields) {
            if (f.isPrimaryKey) {
                pk.add(f);
            }
        }
        return pk;
    }

    public ArrayList<Field> getAllFields() {
        return fields;
    }

    public ArrayList<String> getAllFieldsAsStrings() {
        ArrayList<String> ret = new ArrayList<String>();
        for (Field field : fields) {
            ret.add(field.fieldName);
        }
        return ret;
    }

    public void merge(JSONObject curResolvedRec, JSONObject rec) {
        for (Field field : fields) {
            String fieldName = field.fieldName;
            if (!curResolvedRec.containsKey(fieldName) && rec.containsKey(fieldName)) {
                curResolvedRec.put(fieldName, rec.get(fieldName));
            } else if (curResolvedRec.containsKey(fieldName) && rec.containsKey(fieldName)) {
                Object resolvedField = curResolvedRec.get(fieldName);
                Object newField = rec.get(fieldName);
                if (field.equals(resolvedField, newField) != 2) {
                    if (resolvedField instanceof JSONObject) {
                        ((JSONObject)resolvedField).put(rec.getString("Included by"), newField);
                    } else {
                        JSONObject conflictField = new JSONObject();
                        conflictField.put(curResolvedRec.getString("Included by"), resolvedField);
                        conflictField.put(rec.getString("Included by"), newField);
                        curResolvedRec.put(fieldName, conflictField);
                    }
                }
            }
        }
        String newInclusion = curResolvedRec.getString("Included by") + ", " + rec.getString("Included by");
        curResolvedRec.put("Included by", newInclusion);
    }

  /**
   * @param record1
   * @param record2
   * @return
   */
    public boolean mergeable(JSONObject record1, JSONObject record2) {
    	/**
    	 * Set of rules to determine whether the records match
    	 */
    	int primaryMatch = 0;
    	int primaryMaybe = 0;
    	int nonPrimaryMatch = 0;
    	int nonPrimaryMaybe = 0;
        for (Field field : this.fields) {
            String fieldName = field.fieldName;
            if (record1.containsKey(fieldName) && record2.containsKey(fieldName)) {
            	if (field.isPrimaryKey) {
            		int temp = field.equals(record1.get(fieldName), record2.get(fieldName));
            		if (temp == 0) return false;
            		if (temp == 2) primaryMatch++;
            		else primaryMaybe++;
            	}
            	else {
            		int temp = field.equals(record1.get(fieldName), record2.get(fieldName));
            		if (temp >= 1) nonPrimaryMatch++;
            		if (temp == 0) nonPrimaryMaybe++;
            	}
            }
        }
        if (primaryMatch >= primaryMaybe*3 && nonPrimaryMatch >= nonPrimaryMaybe*2) return true; 
        return false;
    }

    public JSONArray toJSONArray() {
        JSONArray ret = new JSONArray();
        for (Field field : fields) {
            JSONObject f = new JSONObject();
            f.put("name", field.fieldName);
            f.put("isPK", field.isPrimaryKey);
            f.put("syntax", field.dataType);
            ret.add(f);
        }
        return ret;
    }

    public static SchemaObj fromJSONArray(JSONArray datum) {
        SchemaObj ret = new SchemaObj();
        for (Object o : datum) {
            JSONObject field = (JSONObject)o;
            ret.addField(field.getString("name"), field.getBoolean("isPK"), field.getString("syntax"));
        }
        return ret;
    }

}
