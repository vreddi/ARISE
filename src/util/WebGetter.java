package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.URLConnection;
import java.net.SocketTimeoutException;
import java.util.Map;

public class WebGetter {

    /*
    * The only method to be called outside this class - get content from specified url with parameters appended.
    * Timeout values are set by util.Constant
    * */
    public static String getResponseString(String url, Map<String, String> params) {

        //  Get full URI
        String uri = getURI(url, params);

        try {
            //  Get connection and set parameter values
            URL urlObject = new URL(uri);
            URLConnection connection = urlObject.openConnection();
            connection.setConnectTimeout(Constants.connectTimeout);
            connection.setReadTimeout(Constants.readTimeout);
            connection.connect();

            //  Get input stream and try reading
            InputStream in = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
            String response = "";
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                response += line;
            }
            return response;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (SocketTimeoutException e) {
            System.out.println("Request to " + uri + " timed out");
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
    * Given url and parameters, as mappings from string to string, this method constructs URI for a HTTP request.
    * URL and key names, i.e. parameter names, are not escaped during this process, while values associated to keys are escaped.
    * */
    private static String getURI(String url, Map<String, String> params) {
        String uri = url;
        if (!params.isEmpty()) {
            uri += "?";
            for (String key : params.keySet()) {
                String data = params.get(key);
                if (data == null || data.trim().length() == 0) {
                    System.out.println("Failed to instantiate value for parameter " + key);
                    return null;
                }
                try {
                    uri += key + "=" + URLEncoder.encode(data, "UTF-8") + "&";
                } catch (IOException e) {
                    System.out.println("Skipped key-value pair: " + key + ", " + data);
                }
            }
            uri = uri.substring(0, uri.length()-1);
        }
        return uri;
    }
}
