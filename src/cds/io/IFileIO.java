package cds.io;

import java.io.FileNotFoundException;

import com.google.gson.JsonObject;

public interface IFileIO {

	JsonObject readJsonFileToJsonObject(String filepath) throws FileNotFoundException;

	String readFileToString(String filepath) throws FileNotFoundException;
	
	void writeStringToFile(String data, String filepath);

}