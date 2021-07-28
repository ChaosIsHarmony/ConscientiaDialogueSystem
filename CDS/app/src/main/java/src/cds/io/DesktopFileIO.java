package cds.io;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.net.URL;

import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;

public class DesktopFileIO implements IFileIO {
	
	public JSONObject readJsonFileToJsonObject(String filename) throws FileNotFoundException {
		try {
			JSONParser parser = new JSONParser();
			URL resource = getClass().getClassLoader().getResource(filename);
			Object obj = parser.parse(new FileReader(resource.getFile()));
			JSONObject jsonObject = (JSONObject) obj;
			return jsonObject;
		} catch (Exception e) {
			System.err.println("DesktopFileIO:readJsonFileToJsonObject: Could not open file: " + filename + " | " + e.getMessage());
			return null;
		}
	}

	public String readFileToString(String filepath) throws FileNotFoundException {
		try {
			String fileData = new String(Files.readAllBytes(Paths.get(filepath)));
			return fileData;
		} catch (Exception e) {
			System.err.println("DesktopFileIO:readFileToString: Could not open file: " + filepath + " | " + e.getMessage());
			return null;
		}
	}
	
	public void writeStringToFile(String data, String filepath) { 
		// TODO implement writing
	}
	
	
	
}