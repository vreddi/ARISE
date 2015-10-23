package controllers.wrapper.aspectWrapper.indirectWrappers;

import controllers.schema.SchemaReader;
import controllers.wrapper.aspectWrapper.GeneralAspectWrapper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import util.MyHTTP;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public class RemoteAspectWrapper extends GeneralAspectWrapper {

    private String url;
    public boolean isActive;

    public RemoteAspectWrapper(String name, String url) {
        super(name);
        this.url = url;
        this.isActive = true;
        if (url != null) {
            try {
                URL urlObject = new URL(url + "/schema");
                URLConnection connection = urlObject.openConnection();
                connection.connect();
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                SchemaReader sReader = new SchemaReader(in);
                this.schema = sReader.getSchemaObject();
                this.isValid = true;
            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
                this.schema = null;
                this.isValid = false;
            }
        }
    }

    @Override
    public JSONObject getResultAsJSON(JSONObject searchConditions) {
        Map<String, String> params = new HashMap<String, String>();
        for (Object k : searchConditions.keySet()) {
            String key = (String)k;
            params.put(key, searchConditions.getString(key));
        }
        return JSONObject.fromObject(MyHTTP.get(url, params));
    }

    @Override
    public JSONArray getRegisteredSources() {
        return new JSONArray();
    }

    @Override
    public JSONObject timedGetResultAsJSON(JSONObject searchConditions)  {
        System.out.println("Retrieving data pertaining to aspect " + name + ".");
        long startTime = System.currentTimeMillis();
        JSONObject ret = getResultAsJSON(searchConditions);
        long endTime = System.currentTimeMillis();
        System.out.println("Aspect " + name + " finished execution. Time elapsed: " + ((double)(endTime - startTime)/1000.0) + " seconds.");
        return ret;
    }

    @Override
    public boolean isActivated() {
        return this.isActive;
    }

    @Override
    public void setActivation(String source, boolean newIsActive) {
        this.isActive = newIsActive;
    }

    @Override
    public void print() {
        System.out.println(name + " at " + url + "\n");
    }
}
