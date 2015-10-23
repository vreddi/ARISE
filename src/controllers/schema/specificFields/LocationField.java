package controllers.schema.specificFields;

import controllers.schema.Field;
import info.debatty.java.stringsimilarity.JaroWinkler;
import net.sf.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class LocationField extends Field{

	public LocationField(String fieldName, boolean isPrimaryKey, String dataType) {
        super(fieldName, isPrimaryKey, dataType);
    }
	
    @Override
    public int equalHelper(Object o1, Object o2) {
        String q1 = o1.toString().trim().toLowerCase();
        String q2 = o2.toString().trim().toLowerCase();
        if (q1.equals(q2)) return 2;
        if (q1.length() >= 8 || q2.length() >= 8) {
        	JaroWinkler jw = new JaroWinkler();
        	double result = jw.similarity(q1, q2);
        	if (result >= 0.9) return 2;
        }
        ArrayList<String> q1Results = new ArrayList<String>();
        ArrayList<String> q2Results = new ArrayList<String>();
        String google = "http://www.google.com/search?q=";
        String charset = "UTF-8";
        String userAgent = "ResearcherProfiler";
        try {
			Elements q1links = Jsoup.connect(google+URLEncoder.encode(q1,charset)).userAgent(userAgent).get().select("li.g>h3>a");
			for (Element link : q1links) {
				String title = link.text();
				q1Results.add(title);
				//System.out.println(title);
			}
			//System.out.println("\n");
			Elements q2links = Jsoup.connect(google+URLEncoder.encode(q2,charset)).userAgent(userAgent).get().select("li.g>h3>a");
			for (Element link : q2links) {
				String title = link.text();
				q2Results.add(title);
				//System.out.println(title);
			}
			if (q1Results.get(1).equals(q2Results.get(1))) return 2;
			int matchCount = 0;
			for (String q2Curr : q2Results) {
				if (q1Results.contains(q2Curr)) matchCount++;
			}
			if (matchCount >= 3) return 1;
			if (matchCount >= 1) return 1;
        } catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return 0;
    }

    @Override
    public String toString(JSONObject record) {
        Object data = record.get(fieldName);
        if (data instanceof JSONObject) {
            String ret = "Data conflicts across different sources:\n";
            JSONObject conflictingField = (JSONObject)data;
            for (Object k : conflictingField.keySet()) {
                String sources = (String)k;
                ret += "    From " + sources + " : " + conflictingField.getString(sources) + "\n";
            }
            return ret;
        } else {
            return record.getString(fieldName);
        }
    }
    
    /**
     * Test location equals
    public static void main(String[] args) {
    	Field f = new LocationField("test", true, "Location");
    	String s1 = "uni of i";
    	String s2 = "uiuc";
    	double sim = f.equals(s1, s2);
    	System.out.println(sim);
    }
     * 
     */
}
