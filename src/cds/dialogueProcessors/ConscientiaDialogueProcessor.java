package cds.dialogueProcessors;

import cds.config.ConfigManager;
import cds.entities.ConscientiaNpc;
import cds.entities.Dialogue;
import cds.gameData.GameDataManager;
import cds.utils.Constants;

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

	public void handleEvents(String newAddress) {
		String newLocation = parseLocation(newAddress);

		// check if address is an event address
		if (newAddress.contains("X")) {
			System.out.println("Has X: " + newAddress);
			if (eventsJson.keySet().contains(newAddress)) {
				String destinationAddress = setTriggeredEvent((JsonObject) eventsJson.get(newAddress));
				gameDataManager.saveCurrentState();
				handleEvents(destinationAddress);
			} else if (fightingWordsJson.keySet().contains(newAddress)) {
				System.out.println("ConscientiaDialogueProcessor:handleEvents: Unimplemented Section - FIGHTING WORDS.");
				gameDataManager.saveCurrentState();
			} else if (npcSwitchersJson.keySet().contains(newAddress)) {
				String destinationAddress = switchNpcs(npcSwitchersJson.get(newAddress).getAsInt(), newLocation);
				gameDataManager.saveCurrentState();
				handleEvents(destinationAddress);
			} else {
				// TODO: handle error of not finding (maybe will be in cues? or somewhere else?)
				System.out.println("ConscientiaDialogueProcessor:handleEvents: Unimplemented Section - checking for X-addresses [multichecker, affinity checker, cues, others(?)].");
				currentAddress = newAddress;// will switch when I figure out what to do here
			}
		}
		// handle normal address
		else {
			// check if location has changed
			if (changedLocations(newLocation))	{
				gameDataManager.saveCurrentState();
				switchLocations(newLocation);
			}
			// update currentAddress
			System.out.println("THIS: " + newAddress);
			currentAddress = newAddress;
		}
	}

	public Dialogue getDialogue() {
		Dialogue newDialogue = null;

		// load dialogue for given npc by location
		if (dialogueJson != null) {
			System.out.println("HERE: " + currentAddress);
			JsonObject dialogueBlockJson = (JsonObject) dialogueJson.get(currentAddress);
			newDialogue = new Dialogue(dialogueBlockJson);
		}

		return newDialogue;
	}


	private boolean changedLocations(String newLocation) {
		// find index of 2nd ! and compare the values
		int endCurrent = currentLocation.indexOf('!', currentLocation.indexOf('!')+1);
		int endNew = newLocation.indexOf('!', newLocation.indexOf('!')+1);

		return !newLocation.substring(0,endNew).equals(currentLocation.substring(0,endCurrent));
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
		} catch (IOException e) {
			System.err.println("ConscientiaDialogueProcessor:switchLocations: Failed to load dialogue file: " + currentLocation + " | " + filepath);
			e.printStackTrace();
		}
	}

	private String parseLocation(String address) {
		int firstExclamation = address.indexOf('!')+1;
		int secondExclamation = address.indexOf('!', firstExclamation)+1;
		int endInd = address.indexOf('!', secondExclamation)+1;
		return address.substring(0, endInd);
	}

	private String setTriggeredEvent(JsonObject eventBlock) {
		int eventNum = eventBlock.get(Constants.EVENTS_EVENT_NUMBER).getAsInt();
		gameDataManager.setTriggeredEvent(eventNum);
		return eventBlock.get(Constants.EVENTS_DESTINATION_ADDRESS).getAsString();
	}

	private String switchNpcs(int newNpcId, String newLocation) {
		// set current npc's address to most recent one
		ConscientiaNpc newNpc = gameDataManager.getNpcById(newNpcId);
		// switch current npc to new npc
		currentNpc = newNpc;
		// find relevant dialogue address for new npc
		return currentNpc.getAddress(newLocation);
	}
}
