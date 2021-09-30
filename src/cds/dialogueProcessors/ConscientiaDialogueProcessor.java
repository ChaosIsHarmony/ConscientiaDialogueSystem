package cds.dialogueProcessors;

import cds.config.ConfigManager;
import cds.entities.ConscientiaNpc;
import cds.entities.Dialogue;
import cds.gameData.GameDataManager;
import cds.utils.Constants;

import java.io.IOException;

import com.google.gson.JsonObject;

public class ConscientiaDialogueProcessor implements IDialogueProcessor {

	private ConfigManager configManager;
	private GameDataManager gameDataManager;
	private JsonObject currentLocationDialogueJson;
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
	  // extract npc and text info
		String currentNpcName = (String) gameDataManager.getPlayerValue(Constants.PLAYER_CURRENT_NPC).getValue();
		ConscientiaNpc newNpc = gameDataManager.getNpcValue(currentNpcName);

		currentAddress = newAddress;
		String newLocation = parseLocation(newAddress);

		// check updated event info

		// check if location or npc have changed
		if (changedLocations(newLocation))	{
			gameDataManager.saveCurrentState();
			switchLocations(newLocation);
		}
		if (!currentNpc.equals(newNpc)) {
			// TODO: upon changing npc, look up npc's current address for this location
			// currentAddress = newNpc.getAddress(newLocation);
			System.out.println("ConscientiaDialogueProcessor: handleEvents: Unimplemented Section - checking for NPC change.");
			currentNpc = newNpc;
		}
	}

	public Dialogue getDialogue() {
		Dialogue dialogue = null;

		// load dialogue for given npc by location
		if (currentLocationDialogueJson != null) {
			JsonObject currentDialogueJson = (JsonObject) currentLocationDialogueJson.get(currentAddress);

			dialogue = new Dialogue(currentDialogueJson);
		}

		return dialogue;
	}


	private boolean changedLocations(String newLocation) {
		// find index of 2nd ! and compare the values
		int endCurrent = currentLocation.indexOf('!', currentLocation.indexOf('!')+1);
		int endNew = newLocation.indexOf('!', newLocation.indexOf('!')+1);

		return !newLocation.substring(0,endNew).equals(currentLocation.substring(0,endCurrent));
	}

	private void switchLocations(String newLocation) {
		String filepath = configManager.getConfig().getDialogueFileFilepath(newLocation);
		try {
			JsonObject fileContentsJson = configManager.getFileIO().readJsonFileToJsonObject(filepath);
			currentLocation = newLocation;
			currentLocationDialogueJson = (JsonObject) fileContentsJson.get("dialogue");
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

	// update event info
}
