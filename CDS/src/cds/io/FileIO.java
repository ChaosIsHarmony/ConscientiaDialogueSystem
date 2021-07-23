package cds.io;

public interface FileIO {

	String readFileToString(String filepath) throws Exception;
	
	void writeStringToFile(String data, String filepath);

}