package controllers.wrapper.sourceWrapper;

import com.sun.tools.javac.Main;
import controllers.schema.Field;
import controllers.schema.SchemaObj;
import controllers.wrapper.sourceWrapper.interfaces.FieldExtractor;
import controllers.wrapper.sourceWrapper.interfaces.Formatter;
import controllers.wrapper.sourceWrapper.interfaces.Getter;
import controllers.wrapper.sourceWrapper.interfaces.TupleExtractor;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DirectSourceWrapper extends GeneralSourceWrapper {

    /*
    * Internal functional parts
    * */
    private String aspect;
    private Getter getter;
    private Formatter formatter;
    private TupleExtractor tupleExtractor;
    private Map<String, FieldExtractor> fieldExtractorMap;

    /*
    * Compile getter, formatter, and extractors.
    * Returns true iff all compilation succeeds.
    * */
    private boolean compileFiles() {
        String destinationPath = "bin/" + aspect + "/" + name;
        String unitsFolder = basePath + "/" + aspect + "/" + name + "/units/";

        //  Compilation starts here
        File dest = new File(destinationPath);
        if (!dest.exists()) dest.mkdir();
        int errorCode = Main.compile(new String[] {
                "-d", destinationPath,
                unitsFolder + "MyGetter.java"
        });
        if (errorCode != 0) {
            return false;
        }
        errorCode = Main.compile(new String[] {
                "-d", destinationPath,
                unitsFolder + "MyFormatter.java"
        });
        if (errorCode != 0) {
            return false;
        }
        errorCode = Main.compile(new String[] {
                "-d", destinationPath,
                unitsFolder + "MyTupleExtractor.java"
        });
        if (errorCode != 0) {
            return false;
        }
        for (String field : schema.getAllFieldsAsStrings()) {
            //  Each field extractors is put inside a folder to avoid invalid field name as class name.
            dest = new File(destinationPath + "/FieldExtractors/" + field);
            if (!dest.exists()) dest.mkdir();
            errorCode = Main.compile(new String[]{
                    "-d", destinationPath + "/FieldExtractors/" + field,
                    unitsFolder + "FieldExtractors/" + field + "/MyFieldExtractor.java"
            });
            if (errorCode != 0) {
                return false;
            }
        }
        return true;
    }

    /*
    * Initialize instances of functional units
    * This function should be called only after compileFiles.
    * */
    private void initializeFunctionalUnits()
            throws MalformedURLException, ClassNotFoundException, IllegalAccessException, InstantiationException
    {
        String destinationPath = "bin/" + aspect + "/" + name;

        //  Load getter
        URL classesDir = new File(destinationPath).toURI().toURL();
        ClassLoader parentLoader = Getter.class.getClassLoader();
        URLClassLoader loader = new URLClassLoader(
                new URL[] { classesDir }, parentLoader
        );
        Class getterClass = loader.loadClass("MyGetter");
        this.getter = (Getter) getterClass.newInstance();

        //  Load formatter
        parentLoader = Formatter.class.getClassLoader();
        loader = new URLClassLoader(
                new URL[] { classesDir }, parentLoader
        );
        Class formatterClass = loader.loadClass("MyFormatter");
        this.formatter = (Formatter) formatterClass.newInstance();

        //  Load tuple extractor
        parentLoader = TupleExtractor.class.getClassLoader();
        loader = new URLClassLoader(
                new URL[] { classesDir }, parentLoader
        );
        Class tupleClass = loader.loadClass("MyTupleExtractor");
        this.tupleExtractor = (TupleExtractor) tupleClass.newInstance();

        //  Load field extractor(s)
        this.fieldExtractorMap = new HashMap<String, FieldExtractor>();
        for (String field : schema.getAllFieldsAsStrings()) {
            classesDir = new File(destinationPath + "/FieldExtractors/" + field).toURI().toURL();
            parentLoader = FieldExtractor.class.getClassLoader();
            loader = new URLClassLoader(
                    new URL[] { classesDir }, parentLoader
            );
            Class fieldClass = loader.loadClass("MyFieldExtractor");
            this.fieldExtractorMap.put(field, (FieldExtractor) fieldClass.newInstance());
        }
    }

    /*
    * Fully construct a source wrapper.
    * This compilation and initialization of functional parts are done here, in order to speed up queries.
    * */
    public DirectSourceWrapper(SchemaObj schema, String name, String aspect) {
        super(schema, name);
        this.aspect = aspect;

        compileFiles();

        try {
            initializeFunctionalUnits();
        } catch (MalformedURLException e) {
            System.err.println("Interal Error: malformed URL when loading class " + aspect + ":" + name);
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.err.println("Interal Error: class not found when loading class " + aspect + ":" + name);
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            System.err.println("Interal Error: illegal access when loading class " + aspect + ":" + name);
            e.printStackTrace();
        } catch (InstantiationException e) {
            System.err.println("Interal Error: can not instantiate class " + aspect + ":" + name);
            e.printStackTrace();
        }
    }

    /*
    * A execution of a query reduces to utilization of all the functioanl parts.
    * */
    @Override
    public JSONArray getResultAsJSONArray(JSONObject searchConditions) {
        try {
            Object rawResponse = getter.getResult(searchConditions);
            JSON JSONResponse = formatter.convertToJSON(rawResponse);
            JSONArray tuples = tupleExtractor.getTuples(JSONResponse);
            JSONArray result = new JSONArray();
            ArrayList<Field> fields = schema.getAllFields();
            for (Object t : tuples) {
                JSON tuple = (JSON)t;
                JSONObject construct = new JSONObject();
                for (Field field : fields) {
                    if (fieldExtractorMap.containsKey(field.fieldName)) {
                        try{
                            FieldExtractor extractor = fieldExtractorMap.get(field.fieldName);
                            Object extractedValue = extractor.extractField(tuple);
                            //  Sanity check for variable types
                            if (field.validate(extractedValue))
                                construct.put(field.fieldName, extractedValue);
                        } catch (Exception e) {
                            continue;   //  Does nothing, skip this value
                        }
                    }
                }
                if (!construct.isEmpty()) {
                    result.add(construct);
                }
            }
            return result;
        } catch (Exception e) {
            System.err.println("Error occurred while aquiring partial result from " + name);
            e.printStackTrace();
            return null;
        }
    }
}
