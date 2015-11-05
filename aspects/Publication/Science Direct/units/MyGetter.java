import controllers.wrapper.sourceWrapper.interfaces.Getter;

import java.lang.String;
import java.lang.System;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;
import util.Constants;
import util.MyHTTP;

public class MyGetter implements Getter {

	public Object getResult(JSONObject searchConditions) {
        Map<String, String> params = new HashMap<String, String>();

        if (searchConditions.containsKey("affiliation")) {
            params.put("query", searchConditions.getString("fullName") + " AND " + searchConditions.getString("affiliation"));
        }
        else{
            params.put("query", searchConditions.getString("fullName"));
        }

        params.put("apiKey", getApiKey());
        return MyHTTP.get("http://api.elsevier.com/content/search/scidir", params);
	}


    /**
     *  This method gets the API Key for Scopus from a remote
     *  location.
     *
     * @return apiKey :: The API key to validate the source
     */
     // TODO -> Make the Key Secret
	public String getApiKey(){

	    return "19b4b3546222699157deac547bc8e232";
	}


}