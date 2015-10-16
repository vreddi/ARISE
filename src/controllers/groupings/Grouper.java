package controllers.groupings;

import controllers.schema.Field;
import controllers.schema.SchemaObj;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.util.*;

public class Grouper {

    public static JSONArray group(JSONArray records, SchemaObj schema) {

        int numRecords = records.size();
        ArrayList<Field> fields = schema.getAllFields();

        //  Computes keywords
        TreeMap keywords = new TreeMap<String, ArrayList<String>>();
        for (Field field : fields) {
            if (field.dataType.equalsIgnoreCase("Text")) {
                Map counts = new HashMap<String, Integer>();
                for (Object record : records) {
                    JSONObject cur = (JSONObject)record;
                    if (cur.containsKey(field.fieldName)) {
                        String curVal = cur.getString(field.fieldName);
                        String[] words = curVal.split("\\W");
                        for (String word : words) {
                            if (counts.containsKey(word)) {
                                int c = (Integer)counts.get(word);
                                counts.replace(word, c+1);
                            } else {
                                counts.put(word, 1);
                            }
                        }
                    }
                }

                ArrayList<String> kws = new ArrayList<String>();
                Set ent = counts.entrySet();
                for (Object e : ent) {
                    Map.Entry curEnt = (Map.Entry) e;
                    if ((Integer)curEnt.getValue() * 5 >= numRecords) {
                        kws.add((String)curEnt.getKey());
                    }
                }
                keywords.put(field.fieldName, kws);
            }
        }

        //  Total number of keywords
        int numKWs = 0;
        for (Object e : keywords.entrySet()) {
            Map.Entry<String, ArrayList<String>> ent = (Map.Entry<String, ArrayList<String>>)e;
            numKWs += ent.getValue().size();
        }

        //  Transform the records into a new feature space
        int[][] newFeatures = new int[numRecords][numKWs];
        for (int i = 0; i < numRecords; i++) {
            int j = 0;
            for (Object e : keywords.entrySet()) {
                Map.Entry<String, ArrayList<String>> ent = (Map.Entry<String, ArrayList<String>>)e;
                JSONObject rec = (JSONObject)records.get(i);
                String curVal;
                if (rec.containsKey(ent.getKey())) curVal = rec.getString(ent.getKey());
                else curVal = "";
                for (String kw : ent.getValue()) {
                    newFeatures[i][j] = StringUtils.countMatches(curVal, kw);
                    j += 1;
                }
            }
        }

        //  From wikipedia: rule of thumb: # of clusters = sqrt(n/2)
        int numClusters = (int)Math.sqrt(((double)numRecords)/2);

        //  K-mean clustering with L-1 norm as measure of distance
        Random rng = new Random();
        double[][] centroids = new double[numClusters][numKWs];
        for (int i = 0; i < numClusters; i++) {
            int[] from = newFeatures[Math.abs(rng.nextInt()) % numRecords];
            for (int j = 0; j < numKWs; j++) {
                centroids[i][j] = (double)(from[j]);
            }
        }
        int[] belongings = new int[numRecords];
        boolean shouldContinue = true;
        while (shouldContinue) {
            shouldContinue = false;

            int[] numBelongings = new int[numClusters];
            Arrays.fill(numBelongings, 0);
            //  Re-assign clustering
            for (int i = 0; i < numRecords; i++) {
                //  Compute closest centroid
                int closest = -1;
                double closestDist = Double.MAX_VALUE;
                for (int j = 0; j < numClusters; j++) {
                    double dist = 0;
                    for (int k = 0; k < numKWs; k++) {
                        dist += Math.abs(centroids[j][k] - newFeatures[i][k]);
                    }
                    if (dist < closestDist) {
                        closestDist = dist;
                        closest = j;
                    }
                }
                numBelongings[closest] += 1;
                if (belongings[i] != closest) {
                    belongings[i] = closest;
                    shouldContinue = true;
                }

            }

            if (!shouldContinue) {
                break;
            }

            //  Re-calculating centroids
            for (int i = 0; i < numClusters; i++) {
                Arrays.fill(centroids[i], 0.0);
            }
            for (int i = 0; i < numRecords; i++) {
                int c = belongings[i];
                for (int j = 0; j < numKWs; j++) {
                    centroids[c][j] += newFeatures[i][j];
                }
            }
            for (int i = 0; i < numClusters; i++) {
                for (int j = 0; j < numKWs; j++) {
                    centroids[i][j] /= numBelongings[i];
                }
            }

        }

        //  Construct final anonymously grouped result
        JSONArray[] clusters = new JSONArray[numClusters];
        for (int i = 0; i < numClusters; i++) {
            clusters[i] = new JSONArray();
        }
        for (int i = 0; i < numRecords; i++) {
            int c = belongings[i];
            clusters[c].add(records.get(i));
        }
        JSONArray ret = new JSONArray();
        for (int i = 0; i < numClusters; i++) {
            ret.add(clusters[i]);
        }

        return ret;
    }

}





