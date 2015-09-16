package controllers.wrapper.sourceWrapper;

import controllers.SearchHandler;
import controllers.schema.SchemaObj;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import util.MyHTTP;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.net.URLEncoder;
import static util.Constants.localServerPortNumber;

public class IndirectSourceWrapper extends GeneralSourceWrapper {

    private String aspect;

    public IndirectSourceWrapper() {
        super();
        aspect = null;
    }

    public IndirectSourceWrapper(SchemaObj schema, String name, String aspect, SearchHandler handler) {
        super(schema, name);
        this.aspect = aspect;
    }

    @Override
    public JSONArray getResultAsJSONArray(JSONObject searchConditions) {
        Map<String, String> params = new HashMap<String, String>();
        for (Object k : searchConditions.keySet()) {
            String key = (String)k;
            params.put(key, searchConditions.getString(key));
        }
        String url = null;
        try {
            url = "http://localhost:" + localServerPortNumber + "/" + URLEncoder.encode(this.aspect, "UTF-8") + "/" + URLEncoder.encode(this.name, "UTF-8");
        } catch (IOException e) {
            System.out.println("Failed to encode aspect or source name for source: " + this.name);
            return null;
        }
        String response = MyHTTP.get(url, params);
        if (response == null) {
            return null;
        } else {
            return JSONArray.fromObject(response);
        }
    }
}
