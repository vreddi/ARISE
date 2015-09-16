package controllers.wrapper.aspectWrapper;

import controllers.schema.SchemaObj;
import controllers.schema.SchemaReader;
import controllers.wrapper.GeneralWrapper;
import controllers.wrapper.sourceWrapper.GeneralSourceWrapper;
import controllers.wrapper.sourceWrapper.typeSpecificWrappers.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import util.Utilities;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import static controllers.intraAspectMatchers.InclusionCounter.countInclusion;


/*
* General schema for aspect wrappers.
* */
public abstract class GeneralAspectWrapper implements GeneralWrapper{

    public String name;
    protected SchemaObj schema;
    protected boolean isValid;

    public GeneralAspectWrapper() {
        this.name = null;
        this.schema = null;
        this.isValid = false;
    }

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

    public abstract JSONArray getResultAsJSONArray(JSONObject searchConditions);

    public abstract JSONArray getRegisteredSources();

    public abstract JSONArray timedGetResultAsJSONArray(JSONObject searchConditions);

    public abstract boolean isActivated();

    public abstract void setActivation(String source, boolean newIsActive);

}
