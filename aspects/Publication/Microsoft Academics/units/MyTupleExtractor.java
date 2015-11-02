
import controllers.graphAnalyzer.GlobalGraphInfo;
import controllers.wrapper.sourceWrapper.interfaces.TupleExtractor;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.lang.Exception;
import java.lang.Override;
import java.lang.System;

import controllers.graphAnalyzer.GraphAnalyzer.*;

public class MyTupleExtractor implements TupleExtractor{

    @Override
    public JSONArray getTuples(JSON all) {
        JSONObject allAsObject = JSONObject.fromObject(all);

        try {
            return allAsObject.getJSONArray("results");
        } catch (Exception e) {
            JSONObject rec = allAsObject.getJSONObject("results");
            JSONArray ret = new JSONArray();
            ret.add(rec);
            return ret;
        }
    }

}