package cds.config;

import cds.entities.Personality;
import cds.utils.Constants;
import cds.utils.Functions;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.*;

public class ConscientiaConfig implements IConfig {

	private ConfigManager configManager;

	private String baseSaveFilepath;
	private String uniSaveFilepath;

	private Map<String, ArrayList<String>> dialogueFiles = new HashMap<>();
	private Map<String, String> nonDialogueTextFiles = new HashMap<>();
	private Map<String, String> structuralFiles = new HashMap<>();
	private Map<String, String> templateFiles = new HashMap<>();
	private Map<String, String> startingAddresses = new HashMap<>();
	private Map<String, Personality> personalities = new HashMap<>();

	public ConscientiaConfig(ConfigManager configManager, JsonObject configData) {
		this.configManager = configManager;
		loadData(configData);
	}

	private void loadData(JsonObject configData) {
		parseSaveFiles(configData);
		parseStartingAddresses(configData);
		parsePersonalities(configData);

		JsonObject textFilesData = (JsonObject) configData.get(Constants.TEXT_FILES);
		parseDialogueFiles(textFilesData);
		parseNonDialogueTextFiles(textFilesData);
		parseStructuralFiles(textFilesData);
		parseTemplateFiles(textFilesData);
	}

	private void parseSaveFiles(JsonObject configData) {
		JsonObject saveFilesJson = (JsonObject) configData.get(Constants.SAVE_FILES);

		// parse universal save filepath
		String dirPath = saveFilesJson.get(Constants.BASE_DIR).getAsString();
		this.uniSaveFilepath =
			buildFilePath(dirPath, saveFilesJson.get(Constants.UNI_SAVE_FILE).getAsString(), ".json");

		// base save filepath for when saving game states
		this.baseSaveFilepath = buildFilePath(dirPath, "", "");
	}

	private void parseDialogueFiles(JsonObject textFilesData) {
		JsonObject dialogueFilesJson = (JsonObject) textFilesData.get(Constants.DIALOGUE_FILES);
		JsonArray bookFilesJson = (JsonArray) dialogueFilesJson.get(Constants.BOOK_FILES);

		// first parse by book, then by area
		String dirPath = 
      textFilesData.get(Constants.BASE_DIR).getAsString()
			+ "\\"
			+ dialogueFilesJson.get(Constants.BASE_DIR).getAsString()
			+ "\\";

		for (JsonElement bookJson : bookFilesJson) {
			String book = ((JsonObject) bookJson).get(Constants.BASE_DIR).getAsString();
			String bookDirPath = dirPath + book;
			JsonArray areaFilesJson = ((JsonObject) bookJson).get(Constants.FILE_LIST).getAsJsonArray();
			ArrayList<String> filenames = new ArrayList<>();
			for (JsonElement filenameJson : areaFilesJson) {
				String filename = filenameJson.getAsString();
				String path = buildFilePath(bookDirPath, filename, ".json");
				filenames.add(path);
			}
			this.dialogueFiles.put(book, filenames);
		}
	}

	private void parseNonDialogueTextFiles(JsonObject textFilesData) {
		JsonObject nonDialogueTextFilesJson =
			(JsonObject) textFilesData.get(Constants.NON_DIALOGUE_TEXT_FILES);
		JsonArray filesJson = (JsonArray) nonDialogueTextFilesJson.get(Constants.FILE_LIST);

		String dirPath =
			textFilesData.get(Constants.BASE_DIR).getAsString()
			+ "\\"
			+ nonDialogueTextFilesJson.get(Constants.BASE_DIR).getAsString();

		for (JsonElement filenameJson : filesJson) {
			String filename = filenameJson.getAsString();
			String filepath = buildFilePath(dirPath, filename, ".json");
			this.nonDialogueTextFiles.put(filename, filepath);
		}
	}

	private void parseStructuralFiles(JsonObject textFilesData) {
		JsonObject structuralJson = (JsonObject) textFilesData.get(Constants.STRUCTURAL_FILES);
		JsonArray filesJson = (JsonArray) structuralJson.get(Constants.FILE_LIST);

		String dirPath =
			textFilesData.get(Constants.BASE_DIR).getAsString()
			+ "\\"
			+ structuralJson.get(Constants.BASE_DIR).getAsString();

		for (JsonElement filenameJson : filesJson) {
			String filename = filenameJson.getAsString();
			String filepath = buildFilePath(dirPath, filename, ".json");
			this.structuralFiles.put(filename, filepath);
		}
	}

	private void parseTemplateFiles(JsonObject textFilesData) {
		JsonObject templateFilesJson = (JsonObject) textFilesData.get(Constants.TEMPLATE_FILES);

		String dirPath =
			textFilesData.get(Constants.BASE_DIR).getAsString()
			+ "\\"
			+ templateFilesJson.get(Constants.BASE_DIR).getAsString();

		for (JsonElement filenameJson : templateFilesJson.get(Constants.FILE_LIST).getAsJsonArray()) {
			String filename = filenameJson.getAsString();
			String filepath = buildFilePath(dirPath, filename, ".json");
			this.templateFiles.put(filename, filepath);
		}
	}

	// parses the starting address for each book
	// used when creating a new save file
	private void parseStartingAddresses(JsonObject configData) {
		JsonObject startingAddressesJson = (JsonObject) configData.get(Constants.START_ADDRESSES);

		for (String book : startingAddressesJson.keySet()) {
			String startingAddress = startingAddressesJson.get(book).getAsString();
			this.startingAddresses.put(book, startingAddress);
		}
	}

	// parses the unique personalities and their labels for each book
	private void parsePersonalities(JsonObject configData) {
		JsonObject personalitiesJson = (JsonObject) configData.get(Constants.PERSONALITIES_LIST);

		for (String key : personalitiesJson.keySet())
			this.personalities.put(key, new Personality(key, personalitiesJson.get(key).getAsString(), 0));
	}


	/*
	 * --------------------
	 * ACCESSORS & MUTATORS
	 * --------------------
	 */
	public String getDialogueFileFilepath(String location) {
		// parse out area & region to determine directories on file path
		int areaStartInd = 0;
		int areaEndInd = location.indexOf('!');
		int regionStartInd = areaEndInd+1;
		int regionEndInd = location.indexOf('!', regionStartInd);


		String area = location.substring(areaStartInd, areaEndInd);
		String region = location.substring(regionStartInd, regionEndInd);
		// given the filenames' formatting vs. the formatting in the dialogue files
		// it's necessary to add in the underscore
		String[] splitRegion = region.split("\\s+");
		if (splitRegion.length > 1)
			region = splitRegion[0] + "_" + splitRegion[1];
		String book = getBook(area);

		// find & return relevant filepath
		for (String filepath : dialogueFiles.get(book))
			if (filepath.contains(region))
				return filepath;

		// found no matches
		System.err.println(
				"ConscientiaConfig:getDialogueFileFilepath: Could not find filepath for given location - "
				+ location);
		return null;
	}

	public String getMulticheckerFilepath() {
		return structuralFiles.get(Constants.MULTICHECKER_FILE);
	}

	public String getTemplateFilepath(String filename) { return templateFiles.get(filename); }

	public String getUniSaveFilepath() { return uniSaveFilepath; }

	public String getBaseSaveFilepath() { return baseSaveFilepath; }

	public String getStartingAddress(String startingBook) {
		return startingAddresses.get(startingBook);
	}

	public String getCombatDescriptionsFilepath() {
		System.out.println(nonDialogueTextFiles.get(Constants.COMBAT_DESCRIPTIONS));

		return nonDialogueTextFiles.get(Constants.COMBAT_DESCRIPTIONS); }

	/*
	 * --------------
	 * HELPER METHODS
	 * --------------
	 */
	private String buildFilePath(String dir, String filename, String extension) {
		String basePath = "resources\\" + dir + "\\";
		String filepath = basePath + filename + extension;
		return Functions.getOSSpecificFilepath(filepath);
	}

	private String getBook(String area) {
		switch (area) {
			case Constants.AREA_ENCLAVE:
				return Constants.TORMA;
			case Constants.AREA_JER:
				return Constants.THETIAN;
			case Constants.AREA_KABU:
				return Constants.EIDOS;
			case Constants.AREA_KAVU:
				return Constants.RIKHARR;
			case Constants.AREA_MIND:
				return Constants.MIND;
			case Constants.AREA_THIUDA:
				return Constants.WULFIAS;
			case Constants.AREA_TURROK:
				return Constants.BIRACUL;
			default:
				return "";
		}
	}
}
