package cds.gameData;

import cds.config.ConfigManager;
import cds.entities.Dialogue;
import cds.entities.ConscientiaNpc;
import cds.utils.Constants;
import cds.utils.JsonValue;

import java.io.FileNotFoundException;
import java.util.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;


public class ConscientiaGameData implements IGameData {

	GameDataManager gameDataManager;

	private String[] saveFilepaths;
	// Uni Save
	private HashMap<String, HashSet<Integer>> uniSaveData;
	// Player Save
	private HashMap<String, JsonValue<?>> playerSaveVariables;
	private HashMap<String, Boolean> triggeredEvents;
	// Npc Save
	private HashMap<String, ConscientiaNpc> npcsData;


	public ConscientiaGameData(GameDataManager gameDataManager, String startingBook, String saveFilepath) {
		this.gameDataManager = gameDataManager;

		// new game
		if (saveFilepath == null) {
			JsonObject[] saveData = createNewSaveFiles(startingBook);
			if (saveData != null)	{
				parseUniSaveData(saveData[Constants.UNI_SAVE]);
				parsePlayerSaveData(saveData[Constants.PLAYER_SAVE]);
				parseNpcSaveData(saveData[Constants.NPC_SAVE]);
			} else System.err.println("ConscientiaGameData:<Constructor>: Could not load game data from new save file: " + saveFilepath);
		}
		// TODO: else - load from saveFilepath
	}

	private JsonObject[] createNewSaveFiles(String startingBook) {
		// create new save files
		saveFilepaths = gameDataManager.configManager.getConfig().addNewSaveGame(startingBook);

		// load data from save files
		JsonObject[] saveData = new JsonObject[Constants.N_ACTIVE_SAVE_FILE_TYPES];
		try {
			saveData[Constants.UNI_SAVE] = gameDataManager.configManager.getFileIO().readJsonFileToJsonObject(saveFilepaths[Constants.UNI_SAVE]);
			saveData[Constants.PLAYER_SAVE] = gameDataManager.configManager.getFileIO().readJsonFileToJsonObject(saveFilepaths[Constants.PLAYER_SAVE]);
			saveData[Constants.NPC_SAVE] = gameDataManager.configManager.getFileIO().readJsonFileToJsonObject(saveFilepaths[Constants.NPC_SAVE]);
		} catch (FileNotFoundException e) {
			System.err.println("ConscientiaGameData:createNewSaveFile: Could not load new save files: " + e.getMessage());
			e.printStackTrace();
			saveData = null;
		}

		return saveData;
	}

	private void parseUniSaveData(JsonObject saveData) {
		HashSet<Integer> persistentAcquirables = new HashSet<>();
		for (JsonElement acq : saveData.get(Constants.UNI_PERSISTENT_ACQ).getAsJsonArray())
			persistentAcquirables.add(acq.getAsInt());

		HashSet<Integer> persistentEvents = new HashSet<>();
		for (JsonElement event : saveData.get(Constants.UNI_PERSISTENT_EVENTS).getAsJsonArray())
			persistentEvents.add(event.getAsInt());
	}

	private void parsePlayerSaveData(JsonObject saveData) {
		loadSavedVariables(saveData);
		loadTriggeredEvents(saveData);
	}

	private void loadSavedVariables(JsonObject saveData) {
		playerSaveVariables = new HashMap<>();

		// parse all non-triggered event variables
		// must determine type: String, Integer, or Integer[]
		for (String savedVar : saveData.keySet()){
			if (!savedVar.equals(Constants.PLAYER_TRIGGERED_EVENTS)) {
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

		for (JsonElement event : (JsonArray) saveData.get(Constants.PLAYER_TRIGGERED_EVENTS)) {
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

	private void parseNpcSaveData(JsonObject saveData) {
		npcsData = new HashMap<>();

		for (String key : saveData.keySet())
			npcsData.put(key, new ConscientiaNpc(key, (JsonObject) saveData.get(key)));
	}


	// ACCESSORS & MUTATORS
	public void saveCurrentState() {
		// TODO: save the current state of the game
		// This will entail rewriting all changed variables and triggeredEvents
		System.out.println("ConscientiaGameData: saveCurrentState: Unimplemented Method.");
	}

	public void setPlayerValue(String varName, JsonValue<?> varValue) { playerSaveVariables.put(varName, varValue); }
	public JsonValue<?> getPlayerValue(String varName) { return playerSaveVariables.get(varName); }

	public void setNpcValue(String varName, ConscientiaNpc varValue) { npcsData.put(varName, varValue); }
	public ConscientiaNpc getNpcValue(String varName) { return npcsData.get(varName); }

	// event numbers are parsed as Strings because they are json keys,
	// but when stored as part of dialogue actions, they are ints
	public void setTriggeredEvent(int eventNum) { triggeredEvents.put(""+eventNum, true); }
	public Boolean getTriggeredEvent(int eventNum) { return triggeredEvents.get(""+eventNum); }

}
