package cds.config;

import com.google.gson.JsonObject;

import java.util.*;

public class ConscientiaConfig implements IConfig {

	Map<String, ArrayList<String>> saveFiles = new HashMap<>();
	Map<String, ArrayList<String>> dialogueFiles = new HashMap<>();
	Map<String, ArrayList<String>> multicheckerFiles = new HashMap<>();
	Map<String, ArrayList<String>> nonDialogueTextFiles = new HashMap<>();
	Map<String, ArrayList<String>> structuralFiles = new HashMap<>();
	Map<String, ArrayList<String>> templateFiles = new HashMap<>();

	public void loadData(JsonObject configData) {
		parseSaveFiles(configData);
		parseDialogueFiles(configData);
		parseMulticheckerFiles(configData);
		parseNonDialogueTextFiles(configData);
		parseStructuralFiles(configData);
		parseTemplateFiles(configData);
	}

	private void parseSaveFiles(JsonObject configData) {}

	private void parseDialogueFiles(JsonObject configData) {
		JsonObject text = (JsonObject) configData.get("text");
		JsonObject jsonFormatFiles = (JsonObject) text.get("json");
		JsonObject dialogueFiles = (JsonObject) jsonFormatFiles.get("dialogue");

		System.out.println("PARSE DIALOGUE FILES" + configData.entrySet());

//		for (String key :  files) {
//			System.out.println(key);
//			JsonArray bookFiles = (JsonArray) files.get(key);
//			System.out.println(bookFiles);
//		}
	}

	private void parseMulticheckerFiles(JsonObject configData) {}
	private void parseNonDialogueTextFiles(JsonObject configData) {}
	private void parseStructuralFiles(JsonObject configData) {}
	private void parseTemplateFiles(JsonObject configData) {}
}
