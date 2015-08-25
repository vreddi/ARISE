package controllers.wrapper.sourceWrapper;

import controllers.schema.SchemaObj;
import net.sf.json.JSONObject;

public abstract class DirectSourceWrapper extends GeneralSourceWrapper {

    protected JSONObject settings;

    public DirectSourceWrapper() {
        super();
        settings = null;
    }

    public DirectSourceWrapper(SchemaObj schema, String name, JSONObject settings) {
        super(schema, name);
        this.settings = settings;
    }
}
