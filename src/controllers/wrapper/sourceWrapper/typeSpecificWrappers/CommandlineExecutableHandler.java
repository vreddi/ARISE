package controllers.wrapper.sourceWrapper.typeSpecificWrappers;

import controllers.schema.SchemaObj;
import controllers.wrapper.sourceWrapper.DirectSourceWrapper;
import controllers.wrapper.sourceWrapper.GeneralSourceWrapper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import util.Utilities;

import java.io.File;

public class CommandlineExecutableHandler extends DirectSourceWrapper {

    public CommandlineExecutableHandler(SchemaObj schema, String name, JSONObject settings) {
        super(schema, name, settings);
    }

    @Override
    public JSONArray getResultAsJSONArray(JSONObject searchConditions) {
        String commandTemplate = settings.getString("commandTemplate");
        commandTemplate = commandTemplate.replaceAll("\\[\\[first\\]\\]", searchConditions.getString("first"));
        commandTemplate = commandTemplate.replaceAll("\\[\\[last\\]\\]", searchConditions.getString("last"));
        String workingDir = basePath + "/" + settings.getString("ofAspect") + "/" + this.name;
        try {
            Process process = Runtime.getRuntime().exec(
                    commandTemplate,
                    null,
                    new File(workingDir)
            );
            process.waitFor();
            return Utilities.readJSONArrayAndDelete(workingDir + "/results.json");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

}
