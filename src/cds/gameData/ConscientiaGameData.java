package cds.gameData;

import cds.config.ConfigManager;
import cds.entities.Dialogue;
import cds.utils.JsonValue;

import java.io.FileNotFoundException;
import java.util.HashMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;


public class ConscientiaGameData implements IGameData {

	GameDataManager gameDataManager;

	private String saveFilepath;
	private HashMap<String, JsonValue<?>> playerSaveVariables;
	private HashMap<String, Boolean> triggeredEvents;


	public ConscientiaGameData(GameDataManager gameDataManager, String startingBook, String saveFilepath) {
		this.gameDataManager = gameDataManager;

		// new game
		if (saveFilepath == null) {
			JsonObject saveData = createNewSaveFile(startingBook);
			if (saveData != null)	parseSaveData(saveData);
			else System.err.println("ConscientiaGameData:<Constructor>: Could not load game data from new save file: " + saveFilepath);
		}
		// TODO: load from saveFilepath
	}

	private JsonObject createNewSaveFile(String startingBook) {
		// create new save file
		saveFilepath = gameDataManager.configManager.getConfig().addNewSaveFile(startingBook);

		// load data from save file
		JsonObject saveData = null;
		try {
			saveData = gameDataManager.configManager.getFileIO().readJsonFileToJsonObject(saveFilepath);
		} catch (FileNotFoundException e) {
			System.err.println("ConscientiaGameData:createNewSaveFile: Could not load new save file: " + saveFilepath + " | " + e.getMessage());
			e.printStackTrace();
		}

		return saveData;
	}

	private void parseSaveData(JsonObject saveData) {
		loadSavedVariables(saveData);
		loadTriggeredEvents(saveData);
	}

	private void loadSavedVariables(JsonObject saveData) {
		playerSaveVariables = new HashMap<>();

		// parse all non-triggered event variables
		// must determine type: String, Integer, or Integer[]
		for (String savedVar : saveData.keySet()){
			if (!savedVar.equals("triggered_events")) {
				JsonElement jsonValue = saveData.get(savedVar).getAsJsonObject().get("value");

				if (jsonValue.isJsonArray())
					playerSaveVariables.put(savedVar, new JsonValue<>(copyArray(jsonValue.getAsJsonArray())));
				else {
					JsonPrimitive jsonPrimitive = jsonValue.getAsJsonPrimitive();
					if (jsonPrimitive.isString())
						playerSaveVariables.put(savedVar, new JsonValue<>(jsonPrimitive.getAsString()));
					else
						playerSaveVariables.put(savedVar, new JsonValue<>(jsonPrimitive.getAsInt()));
				}
			}
		}
	}

	private Integer[] copyArray(JsonArray array) {
		Integer[] array_cpy = new Integer[array.size()];
		for (int i = 0; i < array.size(); i++)
			array_cpy[i] = array.get(i).getAsInt();
		return array_cpy;
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


	public void saveCurrentState() {
		// TODO: save the current state of the game
		// This will entail rewriting all changed variables and triggeredEvents
	};
	public void setVariableValue(String varName, JsonValue<?> varValue) { playerSaveVariables.put(varName, varValue); }
	public JsonValue<?> getVariableValue(String varName) { return playerSaveVariables.get(varName); }

}
