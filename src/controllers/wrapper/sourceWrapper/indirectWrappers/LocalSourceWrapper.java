package controllers.wrapper.sourceWrapper.indirectWrappers;

import controllers.SearchHandler;
import controllers.schema.SchemaObj;
import controllers.wrapper.sourceWrapper.GeneralSourceWrapper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import static util.Constants.localCommunicationEndingMessage;
import static util.Utilities.constructFullSourceName;

public class LocalSourceWrapper extends GeneralSourceWrapper{

    private SearchHandler handler;
    private String aspect;

    public LocalSourceWrapper() {
        super();
        handler = null;
    }

    public LocalSourceWrapper(SchemaObj schema, String name, String aspect, SearchHandler handler) {
        super(schema, name);
        this.aspect = aspect;
        this.handler = handler;
    }

    @Override
    public JSONArray getResultAsJSONArray(JSONObject searchConditions) {
        try {
            Socket socket = new Socket(handler.getServerAddress(), handler.getServerPort());
            InputStreamReader input = new InputStreamReader(socket.getInputStream());
            BufferedReader in = new BufferedReader(input);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            JSONObject toSend = new JSONObject();
            toSend.put("fullSourceName", constructFullSourceName(aspect, name));
            toSend.put("searchConditions", searchConditions);
            out.println(toSend.toString());
            out.println(localCommunicationEndingMessage);
            out.flush();
            String results = "";
            String line;
            while (true) {
                line = in.readLine();
                if (line == null || line.trim().equals(localCommunicationEndingMessage)) {
                    break;
                }
                results += line;
            }
            in.close();
            out.close();
            socket.close();
            return JSONArray.fromObject(results);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
