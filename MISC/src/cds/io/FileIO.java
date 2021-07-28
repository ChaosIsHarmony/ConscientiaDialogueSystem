package cds.io;

import java.io.FileNotFoundException;

public interface FileIO {

	String readFileToString(String filepath) throws FileNotFoundException;
	
	void writeStringToFile(String data, String filepath);

}