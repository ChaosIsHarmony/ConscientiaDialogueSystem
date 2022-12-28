package cds.dialogueProcessors;

import cds.config.ConfigManager;
import cds.entities.CombatBlock;
import cds.entities.ConscientiaNpc;
import cds.entities.Dialogue;
import cds.entities.MulticheckerBlock;
import cds.entities.Personality;
import cds.gameData.GameDataManager;
import cds.utils.Constants;
import cds.utils.Functions;
import cds.utils.JsonValue;

import java.io.IOException;
import java.util.*;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class ConscientiaDialogueProcessor implements IDialogueProcessor {

	private ConfigManager configManager;
	private GameDataManager gameDataManager;
	private JsonObject dialogueJson;
	private JsonObject eventsJson;
	private JsonObject npcSwitchersJson;
	private JsonObject fightingWordsJson;
	private JsonObject combatDescriptionsJson;
	private String currentLocation;
	private ConscientiaNpc currentNpc;

	public ConscientiaDialogueProcessor(ConfigManager configManager) {
		this.configManager = configManager;
	}

	public void setupProcessor(GameDataManager gameDataManager) {
		this.gameDataManager = gameDataManager;
		this.currentLocation =
			(String) gameDataManager.getPlayerValue(Constants.PLAYER_CURRENT_LOCATION).getValue();
		switchLocations(this.currentLocation); 	// loads dialogue for current location
		this.currentNpc = new ConscientiaNpc();	// loads empty npc for NPC change comparison
		try {
			this.combatDescriptionsJson =
				configManager
				.getFileIO()
				.readJsonFileToJsonObject(configManager.getConfig().getCombatDescriptionsFilepath());
		} catch (IOException e) {
			System.err.println("ConscientiaDialogueProcessor:setupProcessor: Failed to load combat descriptions - " + e.getMessage());
			e.printStackTrace();
		}
	}

	public String preprocessNewAddress(String newAddress) {
    System.out.println("ConscientiaDialogueProcessor: Address: " + newAddress);
		boolean updateNpcAddress = true;
		String newLocation = parseLocation(newAddress);
		this.currentNpc = parseNewNpc(newAddress);
    System.out.println("ConscientiaDialogueProcessor: NPC: " + this.currentNpc.getName() + " | " + this.currentNpc.getId());

		// check if location has changed
		if (changedLocations(newLocation))	{
			System.out.println("ConscientiaDialogueProcessor:preprocessNewAddress: Changed location: " + newLocation);
			switchLocations(newLocation);
			gameDataManager.saveCurrentState();
			updateNpcAddress = false;
			return preprocessNewAddress(newAddress);
		}

		// changed rooms
		if (changedRooms(newLocation)) {
			System.out.println("ConscientiaDialogueProcessor:preprocessNewAddress: Changed Room: " + newLocation);
			// update currentLocation
			currentLocation = newLocation;
			gameDataManager.setPlayerValue(
					Constants.PLAYER_CURRENT_LOCATION,
					new JsonValue<>(currentLocation));
			gameDataManager.saveCurrentState();
		}

		// check if address is an event address
		if (isolateAddressNumber(newAddress).contains(".X")) {
			String referenceAddress = new String(newAddress);
			newAddress = handleTriggers(newAddress, newLocation);

			// if address didn't change, then it involves an action
			if (newAddress.equals(referenceAddress))	newAddress = handleAction(newAddress, newLocation);

			updateNpcAddress = false;
		}

		// change npc's last address for the given location if necessary
		if (updateNpcAddress) {
			System.out.println("ConscientiaDialogueProcessor:preprocessNewAddress: Changed last NPC address: " + newAddress);
			this.currentNpc.setDialogueAddress(currentLocation, newAddress);
		}

		return newAddress;
	}

	private String handleTriggers(String newAddress, String newLocation) {
		// trigger an event
		if (eventsJson.keySet().contains(newAddress)) {
			System.out.println("ConscientiaDialogueProcessor:handleTriggers: Triggered Event.");
			String destinationAddress = setTriggeredEvent((JsonObject) eventsJson.get(newAddress));
			gameDataManager.saveCurrentState();
			return preprocessNewAddress(destinationAddress);
		}
		// fight
		if (fightingWordsJson.keySet().contains(newAddress)) {
			System.out.println("ConscientiaDialogueProcessor:handleTriggers: Fighting.");
			System.out.println("ConscientiaDialogueProcessor:handleTriggers: Unimplemented Section - FIGHTING WORDS.");
			// handle @-forcer
			JsonObject fightBlockJson = (JsonObject) fightingWordsJson.get(newAddress);
			JsonObject actionJson = (JsonObject) fightBlockJson.get(Constants.DIALOGUE_ACTION);
			String actionSymbol = actionJson.get(Constants.ACTION_TYPE).getAsString();
			System.out.println("ConscientiaDialogueProcessor:handleTriggers: @-Forcer.");
			String targetAddress = actionJson.get(Constants.ACTION_DESTINATION_ADDRESS).getAsString();
			this.currentNpc.setDialogueAddress(newLocation, targetAddress);
			gameDataManager.saveCurrentState();
			// TODO: Make sure current NPC = combat NPC

			// return triggered to switch to combat mode
			return "COMBAT";
		}
		// switch npcs
		if (npcSwitchersJson.keySet().contains(newAddress)) {
			System.out.println("ConscientiaDialogueProcessor:handleTriggers: Switch NPC: " + newAddress);
			String destinationAddress =
				switchNpcs(npcSwitchersJson.get(newAddress).getAsInt(), newLocation);
			gameDataManager.saveCurrentState();
			return preprocessNewAddress(destinationAddress);
		}
		// multichecker
		if (gameDataManager.getMultichecker().keySet().contains(newAddress)) {
			System.out.println("ConscientiaDialogueProcessor:handleTriggers: Multichecker: " + newAddress);
			MulticheckerBlock mb = gameDataManager.getMultichecker().get(newAddress);
			return preprocessNewAddress(mb.getDestinationAddress(gameDataManager));
		}
		// cues
    System.out.println("ConscientiaDialogueProcessor:handleTriggers: Unimplemented Section - checking for X-addresses [cues, others(?)].");
    return newAddress;
	}

	private String handleAction(String newAddress, String newLocation) {
    System.out.println("ConscientiaDialogueProcessor:handleAction: " + newAddress);
    // isolate action-related object
    JsonObject dialogueBlockJson = (JsonObject) dialogueJson.get(newAddress);
    if (dialogueBlockJson == null)  return newAddress; // if not a dialogue block, then return newAddress
    
		// parse out action symbol and addresses
		JsonObject actionJson = (JsonObject) dialogueBlockJson.get(Constants.DIALOGUE_ACTION);
    if (actionJson == null)  return newAddress; // if not action, then return newAddress
		String actionSymbol = actionJson.get(Constants.ACTION_TYPE).getAsString();

		// @-forcer
		if (actionSymbol.equals(Constants.ACTION_TYPE_DIALOGUE_ADDRESS_FORCER_SYMBOL)) {
			System.out.println("ConscientiaDialogueProcessor:handleAction: @-Forcer.");
			String targetAddress = actionJson.get(Constants.ACTION_DESTINATION_ADDRESS).getAsString();
			this.currentNpc.setDialogueAddress(newLocation, targetAddress);
			return newAddress;
		}
		// event checker
		else if (actionSymbol.equals(Constants.ACTION_TYPE_EVENT_CHECKER_SYMBOL)) {
			int eventNum = actionJson.get(Constants.ACTION_EVENT).getAsInt();
			System.out.println("ConscientiaDialogueProcessor:handleAction: Check Event - " + eventNum);
			if (gameDataManager.getTriggeredEvent(eventNum)) {
				System.out.println("ConscientiaDialogueProcessor:handleAction: Event True.");
				String destinationAddress =
					actionJson.get(Constants.ACTION_DESTINATION_ADDRESS).getAsString();
				return preprocessNewAddress(destinationAddress);
			}
		}
		// affinity checker
		else if (actionSymbol.equals(Constants.ACTION_TYPE_AFFINITY_CHECKER_SYMBOL)) {
			System.out.println("ConscientiaDialogueProcessor:handleAction: Check Affinity.");
			// get current highest affinity
			Personality topAffinity = gameDataManager.getTopAffinities(1)[0];
			JsonObject responsesJson = (JsonObject) dialogueBlockJson.get(Constants.DIALOGUE_RESPONSES);

			// get address with that affinity
			String destinationAddress =
				((JsonObject) responsesJson.get(topAffinity.getKey()))
					.get(Constants.RESPONSE_DESTINATION_ADDRESS)
					.getAsString();
			return preprocessNewAddress(destinationAddress);
		}

		return newAddress;
	}

	public CombatBlock handleCombat() {
		CombatBlock cb = new CombatBlock(this.currentNpc.getId());

		// get combat descriptions for current npc
		JsonObject descriptionsJson =
			(JsonObject) combatDescriptionsJson.get("" + this.currentNpc.getId());

		// check if player can kill current npc
		handlePlayerVictory(descriptionsJson, cb);

		// trigger events
		triggerCombatEvents(descriptionsJson, cb);

		// parse proper follow-up address
		handlePostCombatAddress(cb);

		// save game
		gameDataManager.saveCurrentState();

		return cb;
	}

	public Dialogue getDialogue(String address) {
		Dialogue newDialogue = null;

		// load dialogue for given npc by location
		if (dialogueJson != null) {
			System.out.println("ConscientiaDialogueProcessor:getDialogue: " + address);
			JsonObject dialogueBlockJson = (JsonObject) dialogueJson.get(address);
			newDialogue = new Dialogue(dialogueBlockJson);
		}

		return newDialogue;
	}

	/*
	 * --------------
	 * HELPER METHODS
	 * --------------
	 */
	private boolean changedLocations(String newLocation) {
		// find index of 2nd ! and compare the values
		int endCurrent = getExclamationIndex(this.currentLocation, 2);
		int endNew = getExclamationIndex(newLocation, 2);
		String currLoc = this.currentLocation.substring(0,endCurrent);
		String newLoc = newLocation.substring(0,endNew);

		return !newLoc.equals(currLoc);
	}

	private boolean changedRooms(String newLocation) {
		// find index of 3rd ! and compare the values
		int endCurrent = getExclamationIndex(this.currentLocation, 3);
		int endNew = getExclamationIndex(newLocation, 3);
		String currLoc = this.currentLocation.substring(0,endCurrent);
		String newLoc = newLocation.substring(0,endNew);

		return !newLoc.equals(currLoc);
	}

	private void switchLocations(String newLocation) {
		String filepath = configManager.getConfig().getDialogueFileFilepath(newLocation);

		// load dialogue file for new location
		try {
			JsonObject fileContentsJson = configManager.getFileIO().readJsonFileToJsonObject(filepath);

			// set the relevant sections of the dialogue file
			dialogueJson = (JsonObject) fileContentsJson.get(Constants.DIALOGUE_DIALOGUE);
			eventsJson = (JsonObject) fileContentsJson.get(Constants.DIALOGUE_EVENTS);
			npcSwitchersJson = (JsonObject) fileContentsJson.get(Constants.DIALOGUE_NPC_SWITCHERS);
			fightingWordsJson = (JsonObject) fileContentsJson.get(Constants.DIALOGUE_FIGHTING_WORDS);

			// update currentLocation
			currentLocation = newLocation;
			gameDataManager.setPlayerValue(
					Constants.PLAYER_CURRENT_LOCATION,
					new JsonValue<>(currentLocation));
		} catch (IOException e) {
			System.err.println(
					"ConscientiaDialogueProcessor:switchLocations: Failed to load dialogue file: "
					+ currentLocation
					+ " | "
					+ filepath);
			e.printStackTrace();
		}
	}

	private String parseLocation(String address) {
		int endInd = getExclamationIndex(address, 3);
		return address.substring(0, endInd);
	}

	private String setTriggeredEvent(JsonObject eventBlock) {
		int eventNum = eventBlock.get(Constants.EVENTS_EVENT_NUMBER).getAsInt();
		gameDataManager.setTriggeredEvent(eventNum, true);
		return eventBlock.get(Constants.EVENTS_DESTINATION_ADDRESS).getAsString();
	}

	private String switchNpcs(int newNpcId, String newLocation) {
		// set current npc's address to most recent one
		ConscientiaNpc newNpc = gameDataManager.getNpcById(newNpcId);
		//
		// switch current npc to new npc
		this.currentNpc = newNpc;
		gameDataManager.setPlayerValue(
				Constants.PLAYER_CURRENT_NPC,
				new JsonValue<>(this.currentNpc.getName()));

		// find relevant dialogue address for new npc
		return this.currentNpc.getDialogueAddress(newLocation);
	}

	private ConscientiaNpc parseNewNpc(String newAddress) {
		int startInd = getExclamationIndex(newAddress, 4);
		int endInd = getExclamationIndex(newAddress, 5) - 1;
		String npcName = newAddress.substring(startInd, endInd);
		return gameDataManager.getNpcByName(npcName);
	}

	private int getExclamationIndex(String address, int which) {
		int count = 0;
		int ind = 0;
		while (count < which) {
			ind = address.indexOf('!', ind) + 1;
			count++;
		}
		return ind;
	}

	private String isolateAddressNumber(String newAddress) {
		int startInd = getExclamationIndex(newAddress, 3);
		int endInd = getExclamationIndex(newAddress, 4)-1;
		return newAddress.substring(startInd, endInd);
	}

	private void handlePlayerVictory(JsonObject descriptionsJson, CombatBlock cb) {
		// get all acquirables player has collected
		HashSet<Integer> volatileAcqs =
			(HashSet<Integer>) gameDataManager.getPlayerValue(Constants.PLAYER_VOLATILE_ACQ).getValue();
		HashSet<Integer> persistentAcqs =
			(HashSet<Integer>) gameDataManager.getUniValue(Constants.UNI_PERSISTENT_ACQ);
		HashSet<Integer> acqs = new HashSet<>();
		acqs.addAll(volatileAcqs);
		acqs.addAll(persistentAcqs);

		// check for victory
		for (Integer i : acqs)
			if (descriptionsJson.keySet().contains(i.toString())) {
				cb.setText(descriptionsJson.get(i.toString()).getAsString());
				cb.setIsPlayerVictorious(true);
				break;
			} else {
				cb.setText(descriptionsJson.get(Constants.TAG_DEFAULT).getAsString());
				cb.setIsPlayerVictorious(false);
			}
	}

	private void triggerCombatEvents(JsonObject descriptionsJson, CombatBlock cb) {
		// populate set of all events to trigger
		HashSet<Integer> eventsToTrigger = new HashSet<>();
		if (cb.isPlayerVictorious())
			eventsToTrigger.addAll(
					Functions.jsonArrayToSet(
						descriptionsJson.get(Constants.COMBAT_PLAYER_VICTORIOUS).getAsJsonArray()));
		else
			eventsToTrigger.addAll(
					Functions.jsonArrayToSet(
						descriptionsJson.get(Constants.COMBAT_PLAYER_DEFEATED).getAsJsonArray()));

		// trigger them
		for (Integer event : eventsToTrigger)
				gameDataManager.setTriggeredEvent(event, true);
	}

	private void handlePostCombatAddress(CombatBlock cb) {
		// if player isPlayerVictorious, choose from npc death addresses
		if (cb.isPlayerVictorious()) {
		String nextAddress = this.currentNpc.getNpcDeathAddress(this.currentLocation);
		if (nextAddress == null)
			nextAddress = this.currentNpc.getNpcDeathAddress(Constants.TAG_DEFAULT);
		cb.setNextAddress(nextAddress);
		}
		// otherwise, process book specific addresses
		else {
			// TODO: process book-specific code
			cb.setNextAddress("KABU!SANCTUARY!AWAKENING CHAMBER!0.X999!DESCRIPTION!");
		}

	}
}
