package controllers.wrapper.sourceWrapper.typeSpecificWrappers;

import controllers.schema.SchemaObj;
import controllers.wrapper.sourceWrapper.DirectSourceWrapper;
import controllers.wrapper.sourceWrapper.GeneralSourceWrapper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class WebAPIHandler extends DirectSourceWrapper {

    public WebAPIHandler(SchemaObj schema, String name, JSONObject settings) {
        super(schema, name, settings);
    }

    @Override
    public JSONArray getResultAsJSONArray(JSONObject searchConditions) {

        //  Construct URL
        JSONObject params = this.settings.getJSONObject("params");
        String fullURL = this.settings.getString("location");
        Pattern pattern = Pattern.compile("%5B%5B\\w*%5D%5D");
        if (!params.isEmpty()) {
            fullURL += "?";
            for (Object k : params.keySet()) {
                String key = (String)k;
                String data = replaceTemplateValuesAndEscape(params.getString(key), searchConditions);
                if (data == null) {
                    System.out.println("Failed to instantiate value for parameter " + key);
                    return null;
                } else if (!pattern.matcher(data).find()) {
                    fullURL += key + "=" + data + "&";
                }
            }
            fullURL = fullURL.substring(0, fullURL.length()-1);
        }

        //  Send request and receive response
        InputStream in = null;
        try {
            URL url = new URL(fullURL);

            URLConnection connection = url.openConnection();
            connection.connect();
            in = connection.getInputStream();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        JSONArray ret = null;
        //  Interpreting the response
        if (this.settings.getString("format").equalsIgnoreCase("XML")) {
            ret = this.interpretXML(in);
        } else if (this.settings.getString("format").equalsIgnoreCase("JSON")) {
            ret = this.interpretJSON("");
        }

        try {
            if (in != null) {
                in.close();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        return ret;
    }

    private JSONArray interpretXML(InputStream in) {
        JSONArray ret = new JSONArray();
        JSONObject mappings = this.settings.getJSONObject("mappings");
        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document doc = builder.parse(in);
            NodeList recordContainers = doc.getElementsByTagName(this.settings.getString("recordContainerTag"));
            for (int i = 0; i < recordContainers.getLength(); i++) {
                Element record = (Element)recordContainers.item(i);
                JSONObject currentRecord = new JSONObject();
                for (Object t : mappings.keySet()) {
                    String tag = (String)t;
                    String data = "";
                    NodeList dataNode = record.getElementsByTagName(tag);
                    if (dataNode.getLength() != 1) {
                        continue;
                    }
                    currentRecord.put(mappings.getString(tag), dataNode.item(0).getTextContent().trim());
                }
                ret.add(currentRecord);
            }
            return ret;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private JSONArray interpretJSON(String JSONString) {
        return null;
    }

    private String replaceTemplateValuesAndEscape(String str, JSONObject searchConditions) {
        for (Object k : searchConditions.keySet()) {
            String key = (String)k;
            str = str.replaceAll("\\[\\["+key+"\\]\\]", searchConditions.getString(key));
        }
        try {
            return java.net.URLEncoder.encode(str, "UTF-8");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

}
