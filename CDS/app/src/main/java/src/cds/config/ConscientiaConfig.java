package cds.config;

import java.util.*;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

public class ConscientiaConfig implements IConfig {
	
	Map<String, ArrayList<String>> dialogue = new HashMap<>();
	
	public void loadData(JSONObject configData) {
		JSONObject text = (JSONObject) configData.get("content");
		JSONObject original = (JSONObject) text.get("original");
		
		parseDialogueFiles(original);
	}
	
	private void parseDialogueFiles(JSONObject jsonData) {
		JSONObject files = (JSONObject) jsonData.get("dialogue");
		
		for (String key : (Set<String>) files.keySet()) {
			System.out.println(key);
			JSONArray bookFiles = (JSONArray) files.get(key);
			System.out.println(bookFiles);
		}
	}
	
	public String getSavedGameFilePath() {
		return "not.a.file.txt";
	}
	
	
}