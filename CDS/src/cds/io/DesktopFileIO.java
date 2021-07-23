package cds.io;

import java.nio.file.Files;
import java.nio.file.Paths;

public class DesktopFileIO implements FileIO {

	public String readFileToString(String filepath) throws Exception {
		try {
			String fileData = new String(Files.readAllBytes(Paths.get(filepath)));
			return fileData;
		} catch (Exception e) {
			System.err.println("Could not open file: " + filepath + " | " + e.getMessage());
			return null;
		}
	}
	
	public void writeStringToFile(String data, String filepath) { 
		// TODO implement writing
	}
	
}