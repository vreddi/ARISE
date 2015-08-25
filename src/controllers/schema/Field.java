package controllers.schema;

import net.sf.json.JSONObject;

public abstract class Field {

    public String fieldName;
    public boolean isPrimaryKey;
    public String dataType;

    public Field(String fieldName, boolean isPrimaryKey, String dataType) {
        this.fieldName = fieldName;
        this.isPrimaryKey = isPrimaryKey;
        this.dataType = dataType;
    }

    public abstract boolean equals(Object o1, Object o2);

    public abstract String toString(JSONObject record);

}
