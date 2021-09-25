package cds.io;

import com.google.gson.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;


public class DesktopFileIO implements IFileIO {

	public JsonObject readJsonFileToJsonObject(String filename) throws FileNotFoundException {
		try {
			JsonParser parser = new JsonParser();
			Object obj = parser.parse(new FileReader(filename));
			JsonObject jsonObject = (JsonObject) obj;
			return jsonObject;
		} catch (IOException e) {
			System.err.println("DesktopFileIO:readJsonFileToJsonObject: Could not open file: " + filename + " | " + e.getMessage());
			return null;
		}
	}

	public String readFileToString(String filepath) throws FileNotFoundException {
		try {
			String fileData = new String(Files.readAllBytes(Paths.get(filepath)));
			return fileData;
		} catch (IOException e) {
			System.err.println("DesktopFileIO:readFileToString: Could not open file: " + filepath + " | " + e.getMessage());
			return null;
		}
	}

	public void writeStringToFile(String data, String filepath) {
		try {
			Path path = Paths.get(filepath);
			System.out.println(path);
			Files.createFile(path);
			Files.writeString(path, data, StandardOpenOption.WRITE);
		} catch (IOException e) {
			System.err.println("DesktopFileIO:writeStringToFile: Could not create file: " + filepath);
			e.printStackTrace();
		}
	}
}
