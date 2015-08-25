package controllers;

import controllers.schema.SchemaObj;
import controllers.schema.SchemaReader;
import controllers.wrapper.GeneralWrapper;
import controllers.wrapper.sourceWrapper.DirectSourceWrapper;
import controllers.wrapper.sourceWrapper.typeSpecificWrappers.CommandlineExecutableHandler;
import controllers.wrapper.sourceWrapper.typeSpecificWrappers.SQLDBHandler;
import controllers.wrapper.sourceWrapper.typeSpecificWrappers.WebAPIHandler;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import util.Utilities;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import static util.Constants.localCommunicationEndingMessage;
import static util.Utilities.constructFullSourceName;


public class LocalSearchServer {

    private ServerSocket serverSocket;
    private boolean finished;
    private Map<String, DirectSourceWrapper> localSources;

    public LocalSearchServer() {
        try {
            serverSocket = new ServerSocket(0);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        finished = false;
        this.resetRegisteredSources();
    }

    public void resetRegisteredSources() {
        File aspDir = new File(GeneralWrapper.basePath);
        this.localSources = new HashMap<String, DirectSourceWrapper>();
        if (aspDir.exists() && aspDir.isDirectory()) {
            File[] aspects = aspDir.listFiles();
            try {
                //  Dive into each aspect folder
                for (File aspect : aspects) {
                    if (aspect.getName().startsWith(".") || !(aspect.isDirectory())) {
                        continue;
                    }
                    //  Read schema
                    String aspectPath = GeneralWrapper.basePath + "/" + aspect.getName();
                    SchemaReader sReader = new SchemaReader(aspectPath + "/schema.tsv");
                    if (!sReader.isValid()) {
                        System.out.println("Error occurred while reading schema for aspect " + aspect.getName());
                    } else {
                        SchemaObj schema = sReader.getSchemaObject();
                        //  Dive into each source folder
                        File[] sources = aspect.listFiles();
                        for(File source : sources) {
                            if (source.getName().startsWith(".") || !(source.isDirectory())) {
                                continue;
                            }
                            //  Initialize wrapper of specific kinds
                            JSONObject settings = Utilities.readJSONObject(aspectPath + "/" + source.getName() + "/settings.json");
                            if (settings != null) {
                                settings.put("ofAspect", aspect.getName());
                                String sType = settings.getString("type").trim().toLowerCase();
                                if (sType.equals("exec")) {
                                    localSources.put(
                                            constructFullSourceName(aspect.getName(), source.getName()),
                                            new CommandlineExecutableHandler(schema, source.getName(), settings)
                                    );
                                } else if (sType.equals("sqldb")) {
                                    localSources.put(
                                            constructFullSourceName(aspect.getName(), source.getName()),
                                            new SQLDBHandler(schema, source.getName(), settings)
                                    );
                                } else if (sType.equals("webapi")) {
                                    localSources.put(
                                            constructFullSourceName(aspect.getName(), source.getName()),
                                            new WebAPIHandler(schema, source.getName(), settings)
                                    );
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }

    public InetAddress getInetAddress() {
        return serverSocket.getInetAddress();
    }

    public void startAccepting() {
        while (!finished) {
            try {
                //  Receive a connection and initialize input and output streams
                Socket socket = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                //  Read input
                String received = "";
                while (true) {
                    String line = in.readLine();
                    if (line == null || line.trim().equals(localCommunicationEndingMessage)) {
                        break;
                    }
                    received += line;
                }
                JSONObject request = JSONObject.fromObject(received);
                //  Stop the server if commanded to do so
                if (request.containsKey("finish")) {
                    finished = true;
                } else if (request.containsKey("reset")) {
                    resetRegisteredSources();
                } else {
                    //  Handle request for data
                    String fullSourceName = request.getString("fullSourceName");
                    JSONObject searchConditions = request.getJSONObject("searchConditions");
                    DirectSourceWrapper sourceWrapper = localSources.get(fullSourceName);
                    JSONArray results = sourceWrapper.getResultAsJSONArray(searchConditions);
                    out.println(results.toString());
                    out.flush();
                }
                //  Cleaning up for this connection
                in.close();
                out.close();
            } catch (IOException e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
            }
        }
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
