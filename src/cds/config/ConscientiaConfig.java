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

import cds.entities.Personality;
import cds.utils.Constants;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.*;

public class ConscientiaConfig implements IConfig {

	ConfigManager configManager;

	String baseSaveFilepath;
	String uniSaveFilepath;
	ArrayList<String> saveFiles;
	int nSaveFiles;

	Map<String, ArrayList<String>> dialogueFiles = new HashMap<>();
	Map<String, String> nonDialogueTextFiles = new HashMap<>();
	Map<String, String> structuralFiles = new HashMap<>();
	Map<String, String> templateFiles = new HashMap<>();
	Map<String, String> startingAddresses = new HashMap<>();
	Map<String, Personality> personalities = new HashMap<>();

	public ConscientiaConfig(ConfigManager configManager) {
		this.configManager = configManager;
	}

	public void loadData(JsonObject configData) {
		parseSaveFiles(configData);
		parseDialogueFiles(configData);
		parseNonDialogueTextFiles(configData);
		parseStructuralFiles(configData);
		parseTemplateFiles(configData);
		parseStartingAddresses(configData);
		parsePersonalities(configData);
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
			String filename = filenameJson.getAsString();
			String path = buildFilePath(dirPath, filename);
			saveFiles.add(path);
		}

		// parse universal save filepath
		uniSaveFilepath = buildFilePath(dirPath, saveFilesJson.get("uni_save").getAsString());

		// save for when saving game states
		baseSaveFilepath = "resources" + "\\" + dirPath;
	}

	private void parseDialogueFiles(JsonObject configData) {
		JsonObject textJson = (JsonObject) configData.get("text");
		JsonObject dialogueFilesJson = (JsonObject) textJson.get("dialogue");
		JsonArray bookFilesJson = (JsonArray) dialogueFilesJson.get("books");

		// first parse by book, then by area
		String dirPath = textJson.get("base").getAsString() + "\\" + dialogueFilesJson.get("base").getAsString() + "\\";
		for (JsonElement bookJson : bookFilesJson) {
			String book = ((JsonObject) bookJson).get("base").getAsString();
			String bookDirPath = dirPath + book;
			JsonArray areaFilesJson = ((JsonObject) bookJson).get("files").getAsJsonArray();
			ArrayList<String> filenames = new ArrayList<>();
			for (JsonElement filenameJson : areaFilesJson) {
				String filename = filenameJson.getAsString();
				String path = buildFilePath(bookDirPath, filename);
				filenames.add(path);
			}
			dialogueFiles.put(book, filenames);
		}
	}

	private void parseNonDialogueTextFiles(JsonObject configData) {}

	private void parseStructuralFiles(JsonObject configData) {
		JsonObject textJson = (JsonObject) configData.get("text");
		JsonObject structuralJson = (JsonObject) textJson.get("structural");
		JsonArray filesJson = (JsonArray) structuralJson.get("files");

		String dirPath = textJson.get("base").getAsString() + "\\" + structuralJson.get("base").getAsString();
		for (JsonElement filenameJson : filesJson) {
			String filename = filenameJson.getAsString();
			String filepath = buildFilePath(dirPath, filename);
			structuralFiles.put(filename, filepath);
		}
	}

	private void parseTemplateFiles(JsonObject configData) {
		JsonObject textJson = (JsonObject) configData.get("text");
		JsonObject templateFilesJson = (JsonObject) textJson.get("templates");

		String dirPath = textJson.get("base").getAsString() + "\\" + templateFilesJson.get("base").getAsString();
		for (JsonElement filenameJson : templateFilesJson.get("files").getAsJsonArray()) {
			String filename = filenameJson.getAsString();
			String filepath = buildFilePath(dirPath, filename);
			templateFiles.put(filename, filepath);
		}
	}

	// parses the starting address for each book
	// used when creating a new save file
	private void parseStartingAddresses(JsonObject configData) {
			 JsonObject startingAddressesJson = (JsonObject) configData.get("start");

			 for (String book : startingAddressesJson.keySet()) {
				 String startingAddress = startingAddressesJson.get(book).getAsString();
				 startingAddresses.put(book, startingAddress);
			 }
	}

	private void parsePersonalities(JsonObject configData) {
		JsonObject personalitiesJson = (JsonObject) configData.get("personalities");
		for (String key : personalitiesJson.keySet())
			personalities.put(key, new Personality(key, personalitiesJson.get(key).getAsString(), 0));
	}



	// ACCESSORS
	public String getDialogueFileFilepath(String location) {
		// parse out area & region to determine directories on file path
		int areaStartInd = 0;
		int areaEndInd = location.indexOf('!');
		int regionStartInd = areaEndInd+1;
		int regionEndInd = location.indexOf('!', regionStartInd);


		String area = location.substring(areaStartInd, areaEndInd);
		String region = location.substring(regionStartInd, regionEndInd);
		String[] splitRegion = region.split("\\s+");
		if (splitRegion.length > 1)
			region = splitRegion[0] + "_" + splitRegion[1];
		String book;

		// find relevant directory
		System.out.println("ConscientiaConfig:getDialogueFileFilepath: Unimplemented Section - find relevant directory for book by address.");
		switch (area) {
			case "MIND":
				book = Constants.MIND;
				break;
			case "KABU":
				book = Constants.EIDOS;
				break;
			default:
				book = "";
				break;
		}

		// find & return relevant filepath
		for (String filepath : dialogueFiles.get(book))
			if (filepath.contains(region))
				return filepath;

		// TODO: handle error for address not found
		return null;
	}

	public String getMulticheckerFilepath() {
		return structuralFiles.get("Multicheckers");
	}

	public String[] addNewSaveGame(String startingBook) {
		String[] newSaveFilepaths = new String[Constants.N_ACTIVE_SAVE_FILE_TYPES];

		// create the new player and npc save file's filepath
		buildNewSaveFilepaths(newSaveFilepaths);

		// copy default string to new save file
		createNewFiles(startingBook, newSaveFilepaths);

		return newSaveFilepaths;
	}


	// HELPER METHODS
	private String buildFilePath(String dir, String filename) {
		String basePath = "resources\\" + dir + "\\";
		String filepath = basePath + filename + ".json";
		return filepath;
	}

	private void buildNewSaveFilepaths(String[] newSaveFilepaths) {
		newSaveFilepaths[Constants.UNI_SAVE] = uniSaveFilepath;
		if (nSaveFiles == 0) {
			newSaveFilepaths[Constants.PLAYER_SAVE] = baseSaveFilepath + "\\" + "playerSave0.json";
			newSaveFilepaths[Constants.NPC_SAVE] = baseSaveFilepath + "\\" + "npcsSave0.json";
		} else {
			newSaveFilepaths[Constants.PLAYER_SAVE] = baseSaveFilepath + "\\" + "playerSave" + nSaveFiles + ".json";
			newSaveFilepaths[Constants.NPC_SAVE] = baseSaveFilepath + "\\" + "npcsSave" + nSaveFiles + ".json";
			nSaveFiles++;
			System.out.println("ConscientiaConfig: buildNewSaveFilepaths: nSaveFiles incremented, but config.json not saved, and thus keeps loading as nSaveFiles == 0");
		}
	}

	private void createNewFiles(String startingBook, String[] newSaveFilepaths) {
		try {
			// copy default save file contents
			JsonObject defaultPlayerSaveContents = configManager.getFileIO().readJsonFileToJsonObject(templateFiles.get("PlayerSaveTemplate"));
			JsonObject defaultNpcsSaveContents = configManager.getFileIO().readJsonFileToJsonObject(templateFiles.get("NpcsSaveTemplate"));

			// set the starting address to the one corresponding to the starting book
			((JsonObject) defaultPlayerSaveContents.get("current_location")).addProperty("value", startingAddresses.get(startingBook));

			// write to new file
			configManager.getFileIO().writeStringToFile(defaultPlayerSaveContents.toString(), newSaveFilepaths[Constants.PLAYER_SAVE]);
			configManager.getFileIO().writeStringToFile(defaultNpcsSaveContents.toString(), newSaveFilepaths[Constants.NPC_SAVE]);
		} catch (Exception e) {
			System.err.println("ConscientiaConfig:addNewSaveFile: failed to load default save files: " + e.getMessage());
		}
	}
}
