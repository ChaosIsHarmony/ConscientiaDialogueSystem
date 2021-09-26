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

	ConfigManager configManager;

	String baseSaveFilepath;
	ArrayList<String> saveFiles;
	int nSaveFiles;

	Map<String, ArrayList<String>> dialogueFiles = new HashMap<>();
	Map<String, String> multicheckerFiles = new HashMap<>();
	Map<String, String> nonDialogueTextFiles = new HashMap<>();
	Map<String, String> structuralFiles = new HashMap<>();
	Map<String, String> templateFiles = new HashMap<>();
	Map<String, String> startingAddresses = new HashMap<>();

	public ConscientiaConfig(ConfigManager configManager) {
		this.configManager = configManager;
	}

	public void loadData(JsonObject configData) {
		parseSaveFiles(configData);
		parseDialogueFiles(configData);
		parseMulticheckerFiles(configData);
		parseNonDialogueTextFiles(configData);
		parseStructuralFiles(configData);
		parseTemplateFiles(configData);
		parseStartingAddresses(configData);
	}

	private void parseSaveFiles(JsonObject configData) {
		JsonObject saveFilesJson = (JsonObject) configData.get("save_files");
		JsonArray filenamesJson = (JsonArray) saveFilesJson.get("filenames");

		// parse number of saved files
		nSaveFiles = saveFilesJson.get("n_files").getAsInt();

		// parse filenames
		String dirPath = saveFilesJson.get("base").getAsString();
		saveFiles = new ArrayList<>();
		for (JsonElement filenameJson : filenamesJson.getAsJsonArray()) {
			String filename = (String) filenameJson.getAsString();
			String path = buildFilePath(dirPath, filename);
			saveFiles.add(path);
		}
		baseSaveFilepath = "resources" + "\\" + dirPath;
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
		JsonObject textJson = (JsonObject) configData.get("text");
		JsonObject templateFilesJson = (JsonObject) textJson.get("templates");

		String dirPath = textJson.get("base").getAsString() + "\\" + templateFilesJson.get("base").getAsString();
		for (JsonElement filenameJson : (JsonArray) templateFilesJson.get("files").getAsJsonArray()) {
			String filename = filenameJson.getAsString();
			String filepath = buildFilePath(dirPath, filename);
			templateFiles.put(filename, filepath);
		}
	}


	private void parseStartingAddresses(JsonObject configData) {
			 JsonObject startingAddressesJson = (JsonObject) configData.get("start");

			 for (String book : startingAddressesJson.keySet()) {
				 String startingAddress = startingAddressesJson.get(book).getAsString();
				 startingAddresses.put(book, startingAddress);
			 }
	}

	private String buildFilePath(String dir, String filename) {
		String basePath = "resources\\" + dir + "\\";
		String filepath = basePath + filename + ".json";
		return filepath;
	}


	public String addNewSaveFile(String startingBook) {
		// create the save file's filepath
		String newSaveFilepath = "";
		if (nSaveFiles == 0) newSaveFilepath = baseSaveFilepath + "\\" + "consc0.json";
		else {
			newSaveFilepath = baseSaveFilepath + "\\" + "consc" + nSaveFiles + ".json";
			nSaveFiles++;
		}

		// copy default string to new save file
		try {
			// copy default save file contents
			JsonObject defaultSaveContents = configManager.getFileIO().readJsonFileToJsonObject(templateFiles.get("PlayerSaveTemplate"));

			// set the starting address to the one corresponding to the starting book
			((JsonObject) defaultSaveContents.get("current_location")).addProperty("address", startingAddresses.get(startingBook));

			// write to new file
			configManager.getFileIO().writeStringToFile(defaultSaveContents.toString(), newSaveFilepath);
		} catch (Exception e) {
			System.err.println("ConscientiaConfig:addNewSaveFile: failed to load default save file: " + templateFiles.get("PlayerSaveTemplate"));
		}

			return newSaveFilepath;
	}
}
