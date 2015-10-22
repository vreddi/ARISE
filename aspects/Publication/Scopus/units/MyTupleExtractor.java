import controllers.wrapper.sourceWrapper.interfaces.TupleExtractor;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.lang.Exception;
import java.lang.Override;

public class MyTupleExtractor implements TupleExtractor{

    @Override
    public JSONArray getTuples(JSON all) {
        JSONObject allAsObject = JSONObject.fromObject(all);
        try {
            return allAsObject.getJSONArray("entry");
        } catch (Exception e) {
            JSONObject rec = allAsObject.getJSONObject("entry");
            JSONArray ret = new JSONArray();
            ret.add(rec);
            return ret;
        }
    }

}