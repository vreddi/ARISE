package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

public class WebGetter {

    public static InputStream getInputStream(String url, Map<String, String> params) {
        String fullURL = url;
        if (!params.isEmpty()) {
            fullURL += "?";
            for (String key : params.keySet()) {
                String data = params.get(key);
                if (data == null || data.trim().length() == 0) {
                    System.out.println("Failed to instantiate value for parameter " + key);
                    return null;
                }
                fullURL += key + "=" + data + "&";
            }
            fullURL = fullURL.substring(0, fullURL.length()-1);
        }

        //  Send request and receive response
        InputStream in = null;
        try {
            URL urlObject = new URL(fullURL);

            URLConnection connection = urlObject.openConnection();
            connection.connect();
            in = connection.getInputStream();

            return in;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static BufferedReader getBufferedReader(String url, Map<String, String> params) {
        InputStream in = getInputStream(url, params);
        if (in == null) {
            return null;
        } else {
            return new BufferedReader(new InputStreamReader(in));
        }
    }

    public static String getResponseString(String url, Map<String, String> params) {
        String res = "";
        BufferedReader reader = getBufferedReader(url, params);
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                res += line;
            }
            return res;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

}
