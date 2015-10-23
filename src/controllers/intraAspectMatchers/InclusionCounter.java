package controllers.intraAspectMatchers;

import controllers.schema.SchemaObj;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.Iterator;

public class InclusionCounter {

    public static JSONArray countInclusion(SchemaObj schema, JSONObject records) {
    	/*
        *   Mark initial inclusion on every records
        */
        for (Iterator sources = records.keys(); sources.hasNext();) {
            String sourceName = (String)sources.next();
            for (Object rec : (JSONArray)records.get(sourceName)) {
                JSONObject record = (JSONObject)rec;
                if (!record.containsKey("Included by")) {
                    ((JSONObject) rec).put("Included by", sourceName);
                }
            }
        }
        
        /*
        *   merging
        */
        JSONArray ret = new JSONArray();
        for (Iterator sources = records.keys(); sources.hasNext();) {
            String sourceName = (String)sources.next();
            for (Object rec : (JSONArray)records.get(sourceName)) {
                if (ret.isEmpty()) {
                    ret.add(rec);
                } else {
                    boolean merged = false;
                    for (Object resolvedRec : ret) {
                        JSONObject curResolvedRec = (JSONObject)resolvedRec;
                        if (schema.mergeable(curResolvedRec, (JSONObject)rec)) {
                            schema.merge(curResolvedRec, (JSONObject)rec);
                            merged = true;
                            break;
                        }
                    }
                    if (!merged) {
                        ret.add(rec);
                    }
                }
            }
        }
        return ret;
    }
    
}
