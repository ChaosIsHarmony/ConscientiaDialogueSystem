package cds.gameData;

import cds.config.IConfig;
import cds.entities.Dialogue;
import cds.entities.ConscientiaNpc;
import cds.entities.MulticheckerBlock;
import cds.entities.Personality;
import cds.io.IFileIO;
import cds.utils.Constants;
import cds.utils.Functions;
import cds.utils.JsonValue;

import java.io.FileNotFoundException;
import java.util.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;


public class ConscientiaGameData implements IGameData {

	private IConfig config;
	private IFileIO fileio;

	private String[] saveFilepaths;
	// Uni Save
	private HashMap<String, Object> uniSaveData;
	// Player Save
	private HashMap<String, JsonValue<?>> playerSaveVariables;
	private HashMap<String, TriggeredEvent> triggeredEvents;
	// Npc Save
	private HashMap<String, ConscientiaNpc> npcsData;
	// Multicheckers
	private HashMap<String, MulticheckerBlock> multichecker;


	public ConscientiaGameData(
			GameDataManager gameDataManager,
			String startingBook,
			String[] saveFilepathsToLoad) {

		this.fileio = gameDataManager.configManager.getFileIO();
		this.config = gameDataManager.configManager.getConfig();

		JsonObject[] saveData = null;

		// new game
		if (saveFilepathsToLoad == null)	saveData = createNewSaveFiles(startingBook);
		// saved game
		else															saveData = loadOldSaveFiles(saveFilepathsToLoad);

		// parse save files
		if (saveData != null)	{
			parseUniSaveData(saveData[Constants.UNI_SAVE]);
			parsePlayerSaveData(saveData[Constants.PLAYER_SAVE]);
			parseNpcSaveData(saveData[Constants.NPC_SAVE]);
			parseMultichecker();
		} else {
			System.err.println(
					"ConscientiaGameData:<Constructor>: Could not load game data from save file: "
					+ saveData);
		}
	}

	private JsonObject[] createNewSaveFiles(String startingBook) {
		// create new save files
		this.saveFilepaths = new String[Constants.N_ACTIVE_SAVE_FILE_TYPES];

		// create the new player and npc save file's filepath
		String uniSaveFilepath = this.config.getUniSaveFilepath();
		String baseSaveFilepath = this.config.getBaseSaveFilepath();

		JsonObject uniSaveDataJson = getUniSaveJsonObject(uniSaveFilepath);
		int nSaveFiles = uniSaveDataJson.get(Constants.UNI_N_SAVE_FILES).getAsInt();

		this.saveFilepaths[Constants.UNI_SAVE] = uniSaveFilepath;
		buildNewSaveFilepaths(baseSaveFilepath, this.saveFilepaths, nSaveFiles);
		setAndSaveNumberOfSaveFiles(uniSaveFilepath, uniSaveDataJson, nSaveFiles+1);

		// copy default save files to new save files
		createNewFiles(startingBook, this.saveFilepaths);

		// load saved data
		return loadSavedData();
	}

	private JsonObject[] loadOldSaveFiles(String[] playerNpcSaveFilepaths) {
		// create new save files
		this.saveFilepaths = new String[Constants.N_ACTIVE_SAVE_FILE_TYPES];

		// create the new player and npc save file's filepath
		this.saveFilepaths[Constants.UNI_SAVE] = this.config.getUniSaveFilepath();
		this.saveFilepaths[Constants.PLAYER_SAVE] =
			this.config.getBaseSaveFilepath() + playerNpcSaveFilepaths[0];
		this.saveFilepaths[Constants.NPC_SAVE] =
			this.config.getBaseSaveFilepath() + playerNpcSaveFilepaths[1];

		// load saved data
		return loadSavedData();
	}

	private JsonObject[] loadSavedData() {
		// load data from save files
		JsonObject[] saveData = new JsonObject[Constants.N_ACTIVE_SAVE_FILE_TYPES];

		try {
			saveData[Constants.UNI_SAVE] =
				this.fileio.readJsonFileToJsonObject(this.saveFilepaths[Constants.UNI_SAVE]);
			saveData[Constants.PLAYER_SAVE] =
				this.fileio.readJsonFileToJsonObject(this.saveFilepaths[Constants.PLAYER_SAVE]);
			saveData[Constants.NPC_SAVE] =
				this.fileio.readJsonFileToJsonObject(this.saveFilepaths[Constants.NPC_SAVE]);
		} catch (FileNotFoundException e) {
			System.err.println(
					"ConscientiaGameData:loadSavedData: Could not load save files: "
					+ e.getMessage());
			e.printStackTrace();
			saveData = null;
		}

		return saveData;
	}

	private void parseUniSaveData(JsonObject saveData) {
		this.uniSaveData = new	HashMap<>();

		Integer nSaveFiles = saveData.get(Constants.UNI_N_SAVE_FILES).getAsInt();

		HashSet<Integer> persistentAcquirables = new HashSet<>();
		for (JsonElement acq : saveData.get(Constants.UNI_PERSISTENT_ACQ).getAsJsonArray())
			persistentAcquirables.add(acq.getAsInt());

		HashSet<Integer> persistentEvents = new HashSet<>();
		for (JsonElement event : saveData.get(Constants.UNI_PERSISTENT_EVENTS).getAsJsonArray())
			persistentEvents.add(event.getAsInt());

		this.uniSaveData.put(Constants.UNI_N_SAVE_FILES, nSaveFiles);
		this.uniSaveData.put(Constants.UNI_PERSISTENT_ACQ, persistentAcquirables);
		this.uniSaveData.put(Constants.UNI_PERSISTENT_EVENTS, persistentEvents);
	}

	private void parsePlayerSaveData(JsonObject saveData) {
		loadSavedVariables(saveData);
		loadTriggeredEvents(saveData);
	}

	private void loadSavedVariables(JsonObject saveData) {
		playerSaveVariables = new HashMap<>();

		// parse all non-triggered event variables
		// must determine type: String, Integer, or Integer[]
		for (String savedVar : saveData.keySet()) {
			if (!savedVar.equals(Constants.PLAYER_TRIGGERED_EVENTS)) {
				JsonElement jsonValue = saveData.get(savedVar).getAsJsonObject().get(Constants.TAG_VALUE);

				if (jsonValue.isJsonArray())
					playerSaveVariables.put(
							savedVar, new JsonValue<>(Functions.jsonArrayToSet(jsonValue.getAsJsonArray())));
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

	private void loadTriggeredEvents(JsonObject saveData) {
		this.triggeredEvents = new HashMap<>();

		for (JsonElement event : (JsonArray) saveData.get(Constants.PLAYER_TRIGGERED_EVENTS)) {
			JsonObject eventsJson = event.getAsJsonObject();

			String[] keys = new String[eventsJson.keySet().size()];
			eventsJson.keySet().toArray(keys);

			for (String event_num : keys) {
				JsonObject eventContentJson = (JsonObject) eventsJson.get(event_num);
				Boolean event_val = eventContentJson.get(Constants.TAG_VALUE).getAsBoolean();

				this.triggeredEvents.put(event_num, new TriggeredEvent(event_val));
			}
		}
	}

	private void parseNpcSaveData(JsonObject saveData) {
		this.npcsData = new HashMap<>();

		for (String key : saveData.keySet())
			this.npcsData.put(key, new ConscientiaNpc(key, (JsonObject) saveData.get(key)));
	}

	private void parseMultichecker() {
		this.multichecker = new HashMap<>();
		String filepath = this.config.getMulticheckerFilepath();

		JsonObject multicheckerJson = null;
		try {
			multicheckerJson = this.fileio.readJsonFileToJsonObject(filepath);
		} catch (FileNotFoundException e) {
			System.err.println(
					"ConscientiaGameData:parseMultichecker: Could not load multichecker file: "
					+ e.getMessage());
			e.printStackTrace();
		}

		for (String address : multicheckerJson.keySet()) {
			MulticheckerBlock mb =
				new MulticheckerBlock(address, multicheckerJson.get(address).getAsJsonObject());
			this.multichecker.put(address, mb);
		}
	}

	/*
	 * --------------------
	 * ACCESSORS & MUTATORS
	 * --------------------
	 */
	public void saveCurrentState() {
		// save all player variables
		HashMap<String, Object> playerData = new HashMap<>();

		for (String key : playerSaveVariables.keySet())
			playerData.put(key, playerSaveVariables.get(key));

		Object[] trigArr = new Object[1];
		trigArr[0] = this.triggeredEvents;
		playerData.put(Constants.PLAYER_TRIGGERED_EVENTS, trigArr);

		this.fileio.writeObjectToFile(playerData, this.saveFilepaths[Constants.PLAYER_SAVE]);

		// save all npc variables
		this.fileio.writeObjectToFile(this.npcsData, this.saveFilepaths[Constants.NPC_SAVE]);

		// save all unisave variables
		this.fileio.writeObjectToFile(this.uniSaveData, this.saveFilepaths[Constants.UNI_SAVE]);
	}

	public HashMap<String, MulticheckerBlock> getMultichecker() { return multichecker; }

	public Personality[] getTopAffinities(int range) {
		Personality[] topAffinities = new Personality[range];
		Personality[] affinities = new Personality[Constants.N_PERSONALITIES];

		// populate list of personalities
		for (int i = 0; i < affinities.length; i++)
			affinities[i] =	new Personality(
					Constants.PERSONALITY_SYMBOLS[i],
					Constants.PERSONALITY_LABELS[i],
					(Integer) playerSaveVariables.get(Constants.PERSONALITY_SYMBOLS[i]).getValue());

		// sort by affinity (hi->lo)
		sort(affinities);

		// Determine which affinities are the strongest
		for (int i = 0; i < range; i++)
			topAffinities[i] = affinities[i];

		return topAffinities;
	}

	// basic selection sort because the list is always so short
	private void sort(Personality[] affinities) {
		for (int i = 0; i < affinities.length; i++) {
			int highest = i;
			for (int j = i+1; j < affinities.length; j++) {
				if (affinities[j].getAffinity() > affinities[highest].getAffinity()) highest = j;
			}
			// swap
			Personality tmp = affinities[i];
			affinities[i] = affinities[highest];
			affinities[highest] = tmp;
		}
	}

	public void setUniValue(String varName, Object varValue) {
		uniSaveData.put(varName, varValue);
	}
	public Object getUniValue(String varName) { return uniSaveData.get(varName); }

	public void setPlayerValue(String varName, JsonValue<?> varValue) {
		playerSaveVariables.put(varName, varValue);
	}
	public JsonValue<?> getPlayerValue(String varName) { return playerSaveVariables.get(varName); }

	public void setNpc(ConscientiaNpc varValue) { this.npcsData.put(varValue.getName(), varValue); }
	public ConscientiaNpc getNpcByName(String varName) { return this.npcsData.get(varName); }
	public ConscientiaNpc getNpcById(int varId) {
		for (String npcName : this.npcsData.keySet())
			if (this.npcsData.get(npcName).getId() == varId)	return this.npcsData.get(npcName);
		System.out.println(
				"ConscientiaGameData:getNpcById: Unimplemented Section - Handle NPC not found error.");
		return null;
	}

	// event numbers are parsed as Strings because they are json keys,
	// but when stored as part of dialogue actions, they are ints
	public void setTriggeredEvent(int eventNum, boolean value) {
		triggeredEvents.put(""+eventNum, new TriggeredEvent(value));
	}
	public Boolean getTriggeredEvent(int eventNum) {
		return triggeredEvents.get(""+eventNum).value; }

	/*
	 * ----------------------
	 * HELPER METHODS/CLASSES
	 * ----------------------
	 */
	private class TriggeredEvent {
		public Boolean value;

		public TriggeredEvent(Boolean value) {
			this.value = value;
		}
	}

	private JsonObject getUniSaveJsonObject(String uniSaveFilepath) {
		try {
			return this.fileio.readJsonFileToJsonObject(uniSaveFilepath);
		} catch (Exception e) {
			System.err.println(
					"ConscientiaGameData:getUniSaveJsonObject: failed to load unisave file: "
					+ e.getMessage());
		}

		return null;
	}

	private void setAndSaveNumberOfSaveFiles(
			String uniSaveFilepath,
			JsonObject uniSaveDataJson,
			int nSaveFiles) {
		uniSaveDataJson.add(Constants.UNI_N_SAVE_FILES, new JsonPrimitive (nSaveFiles));
		this.fileio.writeObjectToFile(uniSaveDataJson, uniSaveFilepath);
	}

	private void buildNewSaveFilepaths(String baseSaveFilepath,
			String[] newSaveFilepaths,
			int nSaveFiles) {
		newSaveFilepaths[Constants.PLAYER_SAVE] =
			baseSaveFilepath + "playerSave" + nSaveFiles + ".json";
		newSaveFilepaths[Constants.NPC_SAVE] =
			baseSaveFilepath + "npcsSave" + nSaveFiles + ".json";
	}

	private void createNewFiles(String startingBook, String[] newSaveFilepaths) {
		try {
			// copy default save file contents
			String playerSaveTemplateFilepath =
				this.config.getTemplateFilepath(Constants.PLAYER_SAVE_TEMPLATE);
			String npcSaveTemplateFilepath =
				this.config.getTemplateFilepath(Constants.NPC_SAVE_TEMPLATE);

			JsonObject defaultPlayerSaveContents =
				this.fileio.readJsonFileToJsonObject(playerSaveTemplateFilepath);
			JsonObject defaultNpcsSaveContents =
				this.fileio.readJsonFileToJsonObject(npcSaveTemplateFilepath);

			// get the starting address for the startingBook
			String startingAddress = this.config.getStartingAddress(startingBook);
			// set the starting address to the one corresponding to the starting book
			((JsonObject) defaultPlayerSaveContents.get(Constants.PLAYER_CURRENT_LOCATION))
				.addProperty(Constants.TAG_VALUE, startingAddress);

			// write to new files
			this.fileio.writeObjectToFile(
					defaultPlayerSaveContents,
					newSaveFilepaths[Constants.PLAYER_SAVE]);
			this.fileio.writeObjectToFile(
					defaultNpcsSaveContents,
					newSaveFilepaths[Constants.NPC_SAVE]);
		} catch (Exception e) {
			System.err.println(
					"ConscientiaGameData:createNewFiles: failed to load default save files: "
					+ e.getMessage());
		}
	}
}
