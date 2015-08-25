package controllers.wrapper.sourceWrapper.indirectWrappers;

import controllers.schema.SchemaObj;
import controllers.wrapper.sourceWrapper.GeneralSourceWrapper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import util.WebGetter;
import java.util.HashMap;
import java.util.Map;

public class RemoteSourceWrapper extends GeneralSourceWrapper {

    private String url;

    public RemoteSourceWrapper() {
        super();
        url = null;
    }

    public RemoteSourceWrapper(SchemaObj schema, String name, String url) {
        super(schema, name);
        this.url = url;
    }

    @Override
    public JSONArray getResultAsJSONArray(JSONObject searchConditions) {
        Map<String, String> params = new HashMap<String, String>();
        for (Object k : searchConditions.keySet()) {
            String key = (String)k;
            params.put(key, searchConditions.getString(key));
        }
        String response = WebGetter.getResponseString(url, params);
        if (response == null) {
            return null;
        } else {
            return JSONArray.fromObject(response);
        }
    }
}
