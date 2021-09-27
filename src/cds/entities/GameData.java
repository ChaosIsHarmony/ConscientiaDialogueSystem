package cds.entities;

import java.io.FileNotFoundException;
import java.util.Map;
import java.util.HashMap;

import cds.config.ConfigManager;
import cds.utils.JsonValue;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class GameData {

	private ConfigManager configManager;
	private String saveFilepath;

	private Map<String, JsonValue> savedVariables;
	private Map<String, Boolean> triggeredEvents;

	// New Game Constructor
	public GameData(ConfigManager configManager, String startingBook) {
		this.configManager = configManager;
		JsonObject saveData = createNewSaveFile(startingBook);
		if (saveData != null)	parseSaveData(saveData);
		else System.err.println("GameData:<Constructor>: Could not load game data from new save file: " + saveFilepath);
	}

	// Saved Game Constructor
	public GameData(ConfigManager configManager, String startingBook, String saveFilepath) {
		this.configManager = configManager;
		this.saveFilepath = saveFilepath;
	}

	private JsonObject createNewSaveFile(String startingBook) {
		// create new save file
		saveFilepath = configManager.getConfig().addNewSaveFile(startingBook);

		// load data from save file
		JsonObject saveData = null;
		try {
			saveData = configManager.getFileIO().readJsonFileToJsonObject(saveFilepath);
		} catch (FileNotFoundException e) {
			System.err.println("GameData:createNewSaveFile: Could not load new save file: " + saveFilepath + " | " + e.getMessage());
			e.printStackTrace();
		}

		return saveData;
	}

	private void parseSaveData(JsonObject saveData) {
		loadSavedVariables(saveData);
		loadTriggeredEvents(saveData);
	}

	private void loadSavedVariables(JsonObject saveData) {
		savedVariables = new HashMap<>();

		// TODO: outsource this to game-specific class
		for (String savedVar : saveData.keySet()){
			switch (savedVar) {
				case "current_location":
				case "current_npc":
				case "mindscape_prior_location":
				case "mindscape_current_npc":
				case "volatile_acquirables":
				case "awareness":
				case "personality_affinities":
					System.out.println(saveData.get(savedVar));
			}
		}
	}

	private void loadTriggeredEvents(JsonObject saveData) {
		triggeredEvents = new HashMap<>();

		for (JsonElement event : (JsonArray) saveData.get("triggered_events")) {
			JsonObject eventsJson = event.getAsJsonObject();
			String[] keys = new String[eventsJson.keySet().size()];
			eventsJson.keySet().toArray(keys);
			String event_num = keys[0];	// there's only one key, but as it's in a set, it cannot be
																	// accessed directly via index; hence the conversion

			JsonObject eventContentJson = (JsonObject) eventsJson.get(event_num);
			Boolean event_val = eventContentJson.get("value").getAsBoolean();

			triggeredEvents.put(event_num, event_val);
		}
	}

	public void setVariableValue(String varName, JsonValue varValue) { savedVariables.put(varName, varValue); }
	public JsonValue getVariableValue(String varName) { return savedVariables.get(varName); }

}
