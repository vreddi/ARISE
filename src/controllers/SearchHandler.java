package controllers;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import controllers.wrapper.GeneralWrapper;
import controllers.wrapper.aspectWrapper.GeneralAspectWrapper;
import controllers.wrapper.aspectWrapper.indirectWrappers.LocalAspectWrapper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import util.MyHTTP;

import static util.Constants.localServerPortNumber;

public class SearchHandler{

    private Set<GeneralAspectWrapper> registeredAspects;
    private Map<String, Boolean> activation;
    private boolean isValid;
    private LocalSearchServer server;

    public SearchHandler() {
        this.reset();
        server = new LocalSearchServer();
        server.start();
    }

    public void reset() {
        File aspDir = new File(GeneralWrapper.basePath);
        this.registeredAspects = new HashSet<GeneralAspectWrapper>();
        this.activation = new HashMap<String, Boolean>();
        if (aspDir.exists() && aspDir.isDirectory()) {
            File[] aspects = aspDir.listFiles();
            try {
                for (File aspect : aspects) {
                    if (aspect.getName().startsWith(".") || !(aspect.isDirectory())) {
                        continue;
                    }
                    GeneralAspectWrapper aWrapper = new LocalAspectWrapper(aspect.getName(), this);
                    if (aWrapper.isValid()) {
                        this.registeredAspects.add(aWrapper);
                        this.activation.put(aspect.getName(), new Boolean(true));
                    }
                }
                this.isValid = true;
            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
                this.isValid = false;
            }
        } else {
            this.isValid = false;
        }
    }

    public JSONObject search(JSONObject searchConditions) {
        JSONObject results = new JSONObject();
        for (GeneralAspectWrapper registeredAspect : this.registeredAspects) {
            if (registeredAspect.isActivated()) {
                JSONObject temp = new JSONObject();
                temp.put("schema", registeredAspect.getSchema().toJSONArray());
                temp.put("results", registeredAspect.timedGetResultAsJSONArray(searchConditions));
                results.put(registeredAspect.name, temp);
            }
        }
        return results;
    }

    public void setActivation(String aspect, String source, boolean newIsActive) {
        for (GeneralAspectWrapper registeredAspect : registeredAspects) {
            if (registeredAspect.name.equals(aspect)) {
                registeredAspect.setActivation(source, newIsActive);
            }
        }
    }

    public boolean isValid() {
        return this.isValid;
    }

    public void print() {
        for (GeneralAspectWrapper registeredAspect : registeredAspects) {
            registeredAspect.print();
        }
    }

    public JSONObject getRegisteredAspects() {
        JSONObject ret = new JSONObject();
        for (GeneralAspectWrapper registeredAspect : this.registeredAspects) {
            ret.put(registeredAspect.name, registeredAspect.getRegisteredSources());
        }
        return ret;
    }

    public InetSocketAddress getServerSocket() {
        return server.getSocketAddress();
    }

    public void addAspect(JSONObject description) {
        String name = description.getString("name");
        File f = new File(GeneralWrapper.basePath + "/" + name);
        if (f.exists()) {
            System.out.println("Aspect " + name + " already exists!");
        } else {
            f.mkdir();
            File schemaFile = new File(GeneralWrapper.basePath + "/" + name + "/schema.tsv");
            BufferedWriter writer;
            try {
                schemaFile.createNewFile();
                writer = new BufferedWriter(new FileWriter(schemaFile.getAbsoluteFile()));
                writer.write("Field Name\tIs Primary Key\tSyntax");
                JSONArray fields = description.getJSONArray("fields");
                for (Object o : fields) {
                    JSONObject field = (JSONObject)o;
                    writer.write('\n');
                    writer.write(field.getString("name") + "\t" + field.getString("isPK") + "\t" + field.getString("syntax"));
                }
                writer.close();
                LocalAspectWrapper awrapper = new LocalAspectWrapper(name, this);
                if (awrapper.isValid()) {
                    registeredAspects.add(awrapper);
                }
                //  Inform the background service
                MyHTTP.post("http://localhost:" + localServerPortNumber + "/reset", null);
            } catch (Exception e) {
                if (schemaFile.exists()) {
                    schemaFile.delete();
                }
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
