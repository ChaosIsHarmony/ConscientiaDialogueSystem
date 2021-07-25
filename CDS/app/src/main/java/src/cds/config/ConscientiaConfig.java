package cds.config;

import java.util.*;
import org.json.simple.JSONObject;

public class ConscientiaConfig implements Config {
	
	Map<String, String> dialogue = new HashMap<>();
	
	public void loadData(JSONObject configData) {
		JSONObject text = (JSONObject) configData.get("text");
		JSONObject original = (JSONObject) text.get("original");
		JSONObject dialogue = (JSONObject) original.get("dialogue");
		
		parseDialogue(dialogue);
	}
	
	private void parseDialogue(JSONObject dialogue) {
		System.out.println(dialogue);
	}
	
	public String getSavedGameFilePath() {
		return "not.a.file.txt";
	}
	
	
}