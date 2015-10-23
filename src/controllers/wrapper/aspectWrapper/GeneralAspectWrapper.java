package controllers.wrapper.aspectWrapper;

import controllers.schema.SchemaObj;
import controllers.wrapper.GeneralWrapper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


/*
* General schema for aspect wrappers.
* */
public abstract class GeneralAspectWrapper implements GeneralWrapper{

    public String name;
    protected SchemaObj schema;
    protected boolean isValid;

    public GeneralAspectWrapper(String name) {
        this.name = name;
        this.schema = null;     //  Null since this class should not be instantiated
        this.isValid = false;   //  Not valid for the same reason
    }

    public boolean isValid() {
        return this.isValid;
    }

    public SchemaObj getSchema() {
        return this.schema;
    }

    public abstract JSONObject getResultAsJSON(JSONObject searchConditions);

    public abstract JSONArray getRegisteredSources();

    public abstract JSONObject timedGetResultAsJSON(JSONObject searchConditions);

    public abstract boolean isActivated();

    public abstract void setActivation(String source, boolean newIsActive);

}
