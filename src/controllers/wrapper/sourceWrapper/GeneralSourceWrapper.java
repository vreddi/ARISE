package controllers.wrapper.sourceWrapper;

import controllers.schema.SchemaObj;
import controllers.wrapper.GeneralWrapper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


/*
* General schema for source wrapper.
* Settings are read by aspect wrapper and passed in as parameter.
* */
public abstract class GeneralSourceWrapper implements GeneralWrapper{

    public String name;
    protected boolean isValid;
    protected SchemaObj schema;

    public GeneralSourceWrapper() {
        this.name = null;
        this.schema = null;
        this.isValid = false;
    }

    public GeneralSourceWrapper(SchemaObj schema, String name) {
        this.name = name;
        this.schema = schema;
        this.isValid = true;    //  May be replaced by integrity check later.
    }

    public boolean isValid() {
        return this.isValid;
    }

    @Override
    public abstract JSONArray getResultAsJSONArray(JSONObject searchConditions);

    @Override
    public void print() {
        System.out.println("\t" + name);
    }

    public JSONArray timedGetResultAsJSONArray(JSONObject searchConditions) {
        System.out.println("Souce " + name + " started execution.");
        long startTime = System.currentTimeMillis();
        JSONArray ret = getResultAsJSONArray(searchConditions);
        long endTime = System.currentTimeMillis();
        System.out.println("Source " + name + " ended execution. Time elapsed: " + ((double)(endTime - startTime)/1000.0) + " seconds.");
        return ret;
    }
}
