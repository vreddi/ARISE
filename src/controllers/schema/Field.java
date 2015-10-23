package controllers.schema;

import net.sf.json.JSONObject;

import java.util.Set;

public abstract class Field {

    public String fieldName;
    public boolean isPrimaryKey;
    public String dataType; //dataTypes: Integer, LongInteger, Text, Name, Location

    public Field(String fieldName, boolean isPrimaryKey, String dataType) {
        this.fieldName = fieldName;
        this.isPrimaryKey = isPrimaryKey;
        this.dataType = dataType;
    }
    
    /**
     * Check if this field matches
     * @param o1 
     * @param o2
     * @return 2 for match, 1 for possible match, 0 for not a match
     */
    public int equals(Object o1, Object o2) {
    	// if the resolved field object is a JSONObject, check with all the values respectively
		if (o1 instanceof JSONObject) {
    		JSONObject compareList = (JSONObject)o1;
    		Set list = compareList.keySet();
    		boolean notAMatch = true;
    		for (Object key : list) {
    			Object curr = compareList.get(key);
    			int matchResult = equalHelper(curr, o2);
    			if (matchResult == 2) return 2;
    			if (matchResult == 1) notAMatch = false;
    		}
    		return notAMatch ? 0 : 1;
    	}
		return equalHelper(o1, o2);
	}
    
    public abstract int equalHelper(Object o1, Object o2);

    public abstract String toString(JSONObject record);

}
