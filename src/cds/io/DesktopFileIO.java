package cds.io;

import com.google.gson.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;


public class DesktopFileIO implements IFileIO {
	
	public JsonObject readJsonFileToJsonObject(String filename) throws FileNotFoundException {
		try {
			JsonParser parser = new JsonParser();
			Object obj = parser.parse(new FileReader(filename));
			System.out.println(obj);
			JsonObject jsonObject = (JsonObject) obj;
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