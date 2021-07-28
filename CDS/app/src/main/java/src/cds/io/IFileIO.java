package cds.io;

import java.io.FileNotFoundException;
import org.json.simple.JSONObject;

public interface IFileIO {

	JSONObject readJsonFileToJsonObject(String filepath) throws FileNotFoundException;

	String readFileToString(String filepath) throws FileNotFoundException;
	
	void writeStringToFile(String data, String filepath);

}