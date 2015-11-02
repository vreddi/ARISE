import controllers.schema.specificFields.IntegerField;
import controllers.wrapper.sourceWrapper.interfaces.FieldExtractor;
import net.sf.json.JSON;
import java.lang.Object;
import java.lang.String;

import net.sf.json.JSONObject;

public class MyFieldExtractor implements FieldExtractor {

    public Object extractField(JSON potentialTuple) {
        JSONObject tuple = (JSONObject) potentialTuple;
        return Integer.parseInt(tuple.getString("prism:coverDate"));
    }

}