
import controllers.graphAnalyzer.GlobalGraphInfo;
import controllers.wrapper.sourceWrapper.interfaces.TupleExtractor;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.lang.Exception;
import java.lang.Override;
import java.lang.System;
import java.lang.reflect.Array;
import java.util.Arrays;

import static controllers.graphAnalyzer.GraphAnalyzer.*;

public class MyTupleExtractor implements TupleExtractor{

    @Override
    public JSONArray getTuples(JSON all) {
        JSONObject allAsObject = JSONObject.fromObject(all);

        try {
            JSONObject innerJSON = JSONObject.fromObject(allAsObject.get("search-results"));
            return innerJSON.getJSONArray("entry");
        } catch (Exception e) {
            JSONObject innerJSON = JSONObject.fromObject(allAsObject.get("search-results"));
            JSONObject rec = innerJSON.getJSONObject("entry");
            JSONArray ret = new JSONArray();
            ret.add(rec);
            return ret;
        }
    }

}