package cds.dialogueProcessors;

import cds.config.ConfigManager;
import cds.entities.ConscientiaNpc;
import cds.entities.Dialogue;
import cds.entities.MulticheckerBlock;
import cds.entities.Personality;
import cds.gameData.GameDataManager;
import cds.utils.Constants;
import cds.utils.JsonValue;

import java.io.IOException;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class ConscientiaDialogueProcessor implements IDialogueProcessor {

	private ConfigManager configManager;
	private GameDataManager gameDataManager;
	private JsonObject dialogueJson;
	private JsonObject eventsJson;
	private JsonObject npcSwitchersJson;
	private JsonObject fightingWordsJson;
	private String currentLocation;
	private String currentAddress;
	private ConscientiaNpc currentNpc;

	public ConscientiaDialogueProcessor(ConfigManager configManager) {
		this.configManager = configManager;
	}

	public void setupProcessor(GameDataManager gameDataManager) {
		this.gameDataManager = gameDataManager;
		currentAddress = "";
		currentLocation = (String) gameDataManager.getPlayerValue(Constants.PLAYER_CURRENT_LOCATION).getValue();
		switchLocations(currentLocation); // loads dialogue for current location
		currentNpc = new ConscientiaNpc();
	}

	public String preprocessNewAddress(String newAddress) {
		boolean updateNpcAddress = true;
		boolean updateCurrentAddress = true;
		String newLocation = parseLocation(newAddress);
		currentNpc = parseNewNpc(newAddress);
		System.out.println("ConscientiaDialogueProcessor:preprocessNewAddress: NewAdd Beg. - " + newAddress);
		System.out.println("ConscientiaDialogueProcessor:preprocessNewAddress: NewLoc - " + newLocation);
		System.out.println("ConscientiaDialogueProcessor:preprocessNewAddress: CurrNPC Beg. - " + currentNpc.getName());

		// check if location has changed
		if (changedLocations(newLocation))	{
			System.out.println("ConscientiaDialogueProcessor:preprocessNewAddress: Changed location.");
			gameDataManager.saveCurrentState();
			switchLocations(newLocation);
			updateNpcAddress = false;
			return preprocessNewAddress(newAddress);
		}

		// check if address is an event address
		if (isolateNumber(newAddress).contains(".X")) {
			String referenceAddress = new String(newAddress);
			newAddress = handleEvents(newAddress, newLocation);

			// if address didn't change, then it involves an action
			if (newAddress.equals(referenceAddress))	newAddress = handleAction(newAddress, newLocation);
			else																			updateCurrentAddress = false;

			updateNpcAddress = false;
		}

		// change npc's last address for the given location if necessary
		if (updateNpcAddress) {
			System.out.println("ConscientiaDialogueProcessor:preprocessNewAddress: Changed last NPC address.");
			currentNpc.setAddress(currentLocation, newAddress);
		}
		// update currentAddress
		if (updateCurrentAddress) {
			currentAddress = newAddress;
		}
		System.out.println("ConscientiaDialogueProcessor:preprocessNewAddress: CurrNPC name - " + currentNpc.getName());
		System.out.println("ConscientiaDialogueProcessor:preprocessNewAddress: CurrNPC add - " + currentNpc.getAddress(newLocation));
		System.out.println("ConscientiaDialogueProcessor:preprocessNewAddress: CurrAdd - " + currentAddress);
		System.out.println("ConscientiaDialogueProcessor:preprocessNewAddress: NewAdd - " + newAddress);

		return newAddress;
	}

	private String handleEvents(String newAddress, String newLocation) {
		// trigger an event
		if (eventsJson.keySet().contains(newAddress)) {
			System.out.println("ConscientiaDialogueProcessor:handleEvents: Triggered Event.");
			String destinationAddress = setTriggeredEvent((JsonObject) eventsJson.get(newAddress));
			gameDataManager.saveCurrentState();
			return preprocessNewAddress(destinationAddress);
		}
		// fight
		else if (fightingWordsJson.keySet().contains(newAddress)) {
			System.out.println("ConscientiaDialogueProcessor:handleEvents: Fighting.");
			System.out.println("ConscientiaDialogueProcessor:handleEvents: Unimplemented Section - FIGHTING WORDS.");
			gameDataManager.saveCurrentState();
			return newAddress;
		}
		// switch npcs
		else if (npcSwitchersJson.keySet().contains(newAddress)) {
			System.out.println("ConscientiaDialogueProcessor:handleEvents: Switch NPC.");
			String destinationAddress = switchNpcs(npcSwitchersJson.get(newAddress).getAsInt(), newLocation);
			gameDataManager.saveCurrentState();
			return preprocessNewAddress(destinationAddress);
		}
		// use multichecker
		else if (gameDataManager.getMultichecker().keySet().contains(newAddress)) {
			System.out.println("ConscientiaDialogueProcessor:handleEvents: Multichecker.");
			MulticheckerBlock mb = gameDataManager.getMultichecker().get(newAddress);
			return preprocessNewAddress(mb.getDestinationAddress(gameDataManager));
		}
		// cues
		else {
			System.out.println("ConscientiaDialogueProcessor:handleEvents: ????.");
			System.out.println("ConscientiaDialogueProcessor:handleEvents: Unimplemented Section - checking for X-addresses [cues, others(?)].");
			return newAddress;
		}
	}

	private String handleAction(String newAddress, String newLocation) {
		JsonObject dialogueBlockJson = (JsonObject) dialogueJson.get(newAddress);
		JsonObject actionJson = (JsonObject) dialogueBlockJson.get(Constants.DIALOGUE_ACTION);
		String actionSymbol = actionJson.get(Constants.ACTION_TYPE).getAsString();

		// @-forcer
		if (actionSymbol.equals(Constants.ACTION_TYPE_DIALOGUE_ADDRESS_FORCER_SYMBOL)) {
			System.out.println("ConscientiaDialogueProcessor:handleAction: @-Forcer.");
			String targetAddress = actionJson.get(Constants.ACTION_TARGET_ADDRESS).getAsString();
			currentNpc.setAddress(newLocation, targetAddress);
			return newAddress;
		}
		// event checker
		else if (actionSymbol.equals(Constants.ACTION_TYPE_EVENT_CHECKER_SYMBOL)) {
			System.out.println("ConscientiaDialogueProcessor:handleAction: Check Event.");
			int eventNum = actionJson.get(Constants.ACTION_EVENT).getAsInt();
			if (gameDataManager.getTriggeredEvent(eventNum)) {
				System.out.println("ConscientiaDialogueProcessor:handleAction: Event True: " + eventNum);
				String destinationAddress = actionJson.get(Constants.ACTION_DESTINATION_ADDRESS).getAsString();
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

	// HELPER METHODS
	private boolean changedLocations(String newLocation) {
		// find index of 2nd ! and compare the values
		int endCurrent = getExclamationIndex(currentLocation, 2);
		int endNew = getExclamationIndex(newLocation, 2);
		String currLoc = currentLocation.substring(0,endCurrent);
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
			gameDataManager.setPlayerValue(Constants.PLAYER_CURRENT_LOCATION, new JsonValue<>(currentLocation));
		} catch (IOException e) {
			System.err.println("ConscientiaDialogueProcessor:switchLocations: Failed to load dialogue file: " + currentLocation + " | " + filepath);
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
		// switch current npc to new npc
		currentNpc = newNpc;
		gameDataManager.setPlayerValue(Constants.PLAYER_CURRENT_NPC, new JsonValue<>(currentNpc.getName()));
		// find relevant dialogue address for new npc
		return currentNpc.getAddress(newLocation);
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

	private String isolateNumber(String newAddress) {
		int startInd = getExclamationIndex(newAddress, 3);
		int endInd = getExclamationIndex(newAddress, 4)-1;
		return newAddress.substring(startInd, endInd);
	}
}
