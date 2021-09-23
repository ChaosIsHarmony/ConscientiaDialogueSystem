/*
 * ConscientiaConfig.java
 *
 * Responsible for managing game-specific files.
 * One specific strategy that implements the IConfig interface for use with the dialogue system.
 * Other games may require different files to keep track of, which would require a different strategy.
 *
 * Class Responsibilities:
 *	- Load/Store all filepaths for relevant game files
 *		- SaveFiles
 *		- DialogueFiles
 *		- MulticheckerFiles
 *		- NonDialogueTextFiles
 *			- Combat
 *			- Acquirables
 *			- Credits
 *		- StructuralFiles
 *			- NPCs by Location/Num
 *			- Cues
 *			- Maps
 *		- Templates
 *			- SaveFile
 *			- NPC stats
 *
 */
package cds.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.*;

public class ConscientiaConfig implements IConfig {

	Map<String, String> saveFiles = new HashMap<>();
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

	private void parseSaveFiles(JsonObject configData) {
		JsonObject saveFilesJson = (JsonObject) configData.get("save_files");
		JsonArray filenamesJson = (JsonArray) saveFilesJson.get("filenames");

		String dirPath = saveFilesJson.get("base").getAsString();
		for (JsonElement filenameJson : filenamesJson.getAsJsonArray()) {
			String filename = (String) filenameJson.getAsString();
			String path = buildFilePath(dirPath, filename);
			saveFiles.put(filename, path);
		}
	}

	private void parseDialogueFiles(JsonObject configData) {
		JsonObject textJson = (JsonObject) configData.get("text");
		JsonObject dialogueFilesJson = (JsonObject) textJson.get("dialogue");
		JsonArray bookFilesJson = (JsonArray) dialogueFilesJson.get("books");

		String dirPath = textJson.get("base").getAsString() + "\\" + dialogueFilesJson.get("base").getAsString() + "\\";
		for (JsonElement bookJson : bookFilesJson) {
			String book = ((JsonObject) bookJson).get("base").getAsString();
			String bookDirPath = dirPath + book;
			JsonArray areaFilesJson = ((JsonObject) bookJson).get("files").getAsJsonArray();
			ArrayList<String> filenames = new ArrayList<>();
			for (JsonElement filenameJson : areaFilesJson) {
				String filename = (String) filenameJson.getAsString();
				String path = buildFilePath(bookDirPath, filename);
				filenames.add(path);
			}
			dialogueFiles.put(book, filenames);
		}
	}

	private void parseMulticheckerFiles(JsonObject configData) {}
	private void parseNonDialogueTextFiles(JsonObject configData) {}
	private void parseStructuralFiles(JsonObject configData) {}
	private void parseTemplateFiles(JsonObject configData) {

	}

	private String buildFilePath(String dir, String filename) {
		String basePath = "resources\\" + dir + "\\";
		String filepath = basePath + filename + ".json";
		return filepath;
	}
}
