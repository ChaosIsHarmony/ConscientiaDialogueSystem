package cds.config;

import com.google.gson.JsonObject;

import java.util.*;

public class ConscientiaConfig implements IConfig {

	Map<String, ArrayList<String>> dialogue = new HashMap<>();

	public void loadData(JsonObject configData) {
		JsonObject text = (JsonObject) configData.get("content");
		JsonObject jsonFormatFiles = (JsonObject) text.get("json");

		parseDialogueFiles(jsonFormatFiles);
	}

	private void parseDialogueFiles(JsonObject jsonData) {
		JsonObject files = (JsonObject) jsonData.get("dialogue");
		System.out.println(files.entrySet());

//		for (String key :  files) {
//			System.out.println(key);
//			JsonArray bookFiles = (JsonArray) files.get(key);
//			System.out.println(bookFiles);
//		}
	}

	public String getSavedGameFilePath() {
		return "not.a.file.txt";
	}


}
