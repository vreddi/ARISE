package controllers;

import controllers.wrapper.GeneralWrapper;
import controllers.wrapper.aspectWrapper.GeneralAspectWrapper;
import controllers.wrapper.aspectWrapper.indirectWrappers.LocalAspectWrapper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import util.MyHTTP;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static util.Constants.localServerPortNumber;

public class SearchHandler{

    private Set<GeneralAspectWrapper> registeredAspects;
    private Map<String, Boolean> activation;
    private boolean isValid;
    private LocalSearchServer server;

    /*
    * Constructor of a search handler:
    *   1.  Read all registered aspects.
    *   2.  Start local background service.
    * */
    public SearchHandler() {
        this.reset();
        server = new LocalSearchServer();
        server.start();
    }

    /*
    * Reset aspect registration.
    * Default activation state of all sources is ACTIVATED.
    * */
    public void reset() {
        File aspDir = new File(GeneralWrapper.basePath);
        this.registeredAspects = new HashSet<GeneralAspectWrapper>();
        this.activation = new HashMap<String, Boolean>();
        if (aspDir.exists() && aspDir.isDirectory()) {
            File[] aspects = aspDir.listFiles();
            try {
                for (File aspect : aspects != null ? aspects : new File[0]) {
                    if (aspect.getName().startsWith(".") || !(aspect.isDirectory())) {
                        continue;
                    }
                    GeneralAspectWrapper aWrapper = new LocalAspectWrapper(aspect.getName());
                    if (aWrapper.isValid()) {
                        this.registeredAspects.add(aWrapper);
                        this.activation.put(aspect.getName(), true);
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

    /*
    * Stop search handler/background service.
    * */
    public void stop() {
        server.stop();
        //  Possibly cleaning up
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

    /*
    * Change the activation state of a give source.
    * */
    public void setActivation(String aspect, String source, boolean newIsActive) {
        for (GeneralAspectWrapper registeredAspect : registeredAspects) {
            if (registeredAspect.name.equals(aspect)) {
                registeredAspect.setActivation(source, newIsActive);
            }
        }
    }

    /*
    * Report readiness to handle requests.
    * This function only reports whether any error occurred
    * during the constructing/resetting phase of the handler/registration.
    * */
    public boolean isValid() {
        return this.isValid;
    }

    /*
    * For debugging purposes: print registered aspects.
    * */
    public void print() {
        for (GeneralAspectWrapper registeredAspect : registeredAspects) {
            registeredAspect.print();
        }
    }

    /*
    * Report registration.
    * */
    public JSONObject getRegisteredAspects() {
        JSONObject ret = new JSONObject();
        for (GeneralAspectWrapper registeredAspect : this.registeredAspects) {
            ret.put(registeredAspect.name, registeredAspect.getRegisteredSources());
        }
        return ret;
    }

    /*
    * Add a new aspect:
    *   1.  Create corresponding file(s)/directory(s) in the file system, which contains only indirect sources.
    *   2.  Inform the local background service.
    * */
    public void addAspect(JSONObject description) {
        String name = description.getString("name");
        File f = new File(GeneralWrapper.basePath + "/" + name);
        if (f.exists()) {
            System.out.println("Aspect " + name + " already exists!");
        } else {
            boolean success = f.mkdir();
            if (!success) {
                System.out.println("Error generating directory " + GeneralAspectWrapper.basePath + "/" + name);
                return;
            }
            File schemaFile = new File(GeneralWrapper.basePath + "/" + name + "/schema.tsv");
            BufferedWriter writer;
            try {
                success = schemaFile.createNewFile();
                if (!success) {
                    boolean ignore = f.delete();
                    System.out.println("Error generating schema file at " + GeneralAspectWrapper.basePath + "/" + name + "/schema.tsv");
                    return;
                }
                writer = new BufferedWriter(new FileWriter(schemaFile.getAbsoluteFile()));
                writer = new BufferedWriter(new FileWriter(schemaFile.getAbsoluteFile()));
                writer.write("Field Name\tIs Primary Key\tSyntax");
                JSONArray fields = description.getJSONArray("fields");
                for (Object o : fields) {
                    JSONObject field = (JSONObject)o;
                    writer.write('\n');
                    writer.write(field.getString("name") + "\t" + field.getString("isPK") + "\t" + field.getString("syntax"));
                }
                writer.close();
                LocalAspectWrapper awrapper = new LocalAspectWrapper(name);
                if (awrapper.isValid()) {
                    registeredAspects.add(awrapper);
                }
                //  Inform the background service
                MyHTTP.post("http://localhost:" + localServerPortNumber + "/reset", null);
            } catch (Exception e) {
                if (schemaFile.exists()) {
                    boolean ignore = schemaFile.delete();
                }
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
