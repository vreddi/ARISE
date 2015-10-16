import controllers.wrapper.sourceWrapper.interfaces.Getter;
import net.sf.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.Exception;
import java.lang.ProcessBuilder;
import java.lang.System;


public class MyGetter implements Getter {

	public Object getResult(JSONObject searchConditions) {
		try {
			String[] command = {"python", System.getProperty("user.dir") + "scholar.py-master/scholar.py", "-a", "\""+searchConditions.getString("fullName")+"\""};
			ProcessBuilder pb = new ProcessBuilder(command);
			Process p = pb.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			String ret = "";
			while ((line = reader.readLine()) != null) {
				ret += line + "\n";
			}
			p.waitFor();
			reader.close();
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

}