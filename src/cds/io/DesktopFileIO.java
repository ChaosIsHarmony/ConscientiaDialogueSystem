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

	public DesktopFileIO() {}

	/*
	 * ------------
	 * READ METHODS
	 * ------------
	 */
	public JsonObject readJsonFileToJsonObject(String filepath) throws FileNotFoundException {
		try {
			JsonObject jsonObject = JsonParser.parseReader(new FileReader(filepath)).getAsJsonObject();
			return jsonObject;
		} catch (IOException e) {
			System.err.println("DesktopFileIO:readJsonFileToJsonObject: Could not open file: " + filepath + " | " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	public String readFileToString(String filepath) throws FileNotFoundException {
		try {
			String fileData = new String(Files.readAllBytes(Paths.get(filepath)));
			return fileData;
		} catch (IOException e) {
			System.err.println("DesktopFileIO:readFileToString: Could not open file: " + filepath + " | " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	/*
	 * -------------
	 * WRITE METHODS
	 * -------------
	 */
	public void writeStringToFile(String data, String filepath) {
		Path path = Paths.get(filepath);

		try {
			Files.createFile(path);
		} catch (IOException e) {
			System.err.println("DesktopFileIO:writeStringToFile: Could not create file: " + filepath + " | " + e.getMessage());
			e.printStackTrace();
		}

		try {
			Files.writeString(path, data, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			System.err.println("DesktopFileIO:writeStringToFile: Could not write to file: " + filepath + " | " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void writeObjectToFile(Object obj, String filepath) {
		Path path = Paths.get(filepath);

		try {
			Files.createFile(path);
		} catch (IOException e) {
			System.err.println("DesktopFileIO:writeObjectToFile: Could not create file: " + filepath + " | " + e.getMessage());
			e.printStackTrace();
		}

		// this disableHtmlEscaping is essential, or else some characters will be encoded with numbers
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();;
		String data = gson.toJson(obj);

		try {
			Files.writeString(path, data, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			System.err.println("DesktopFileIO:writeObjectToFile: Could not write to file: " + filepath + " | " + e.getMessage());
			e.printStackTrace();
		}
	}
}
