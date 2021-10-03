package cds.dialogueProcessors;

import cds.config.ConfigManager;
import cds.entities.ConscientiaNpc;
import cds.entities.Dialogue;
import cds.entities.MulticheckerBlock;
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

	public String handleEvents(String newAddress) {
		boolean updateNpcAddress = true;
		boolean updateCurrentAddress = true;
		String newLocation = parseLocation(newAddress);
		currentNpc = parseNewNpc(newAddress);

		// check if location has changed
		if (changedLocations(newLocation))	{
			System.out.println("ConscientiaDialogueProcessor:handleEvents: Changed location.");
			gameDataManager.saveCurrentState();
			switchLocations(newLocation);
			updateNpcAddress = false;
			return handleEvents(newAddress);
		}

		// check if address is an event address
		if (newAddress.contains("X")) {
			updateNpcAddress = false;
			// trigger an event
			if (eventsJson.keySet().contains(newAddress)) {
				System.out.println("ConscientiaDialogueProcessor:handleEvents: Triggered Event.");
				String destinationAddress = setTriggeredEvent((JsonObject) eventsJson.get(newAddress));
				gameDataManager.saveCurrentState();
				updateCurrentAddress = false;
				return handleEvents(destinationAddress);
			}
			// fight
			else if (fightingWordsJson.keySet().contains(newAddress)) {
				System.out.println("ConscientiaDialogueProcessor:handleEvents: Fighting.");
				System.out.println("ConscientiaDialogueProcessor:handleEvents: Unimplemented Section - FIGHTING WORDS.");
				gameDataManager.saveCurrentState();
				updateCurrentAddress = false;
			}
			// switch npcs
			else if (npcSwitchersJson.keySet().contains(newAddress)) {
				System.out.println("ConscientiaDialogueProcessor:handleEvents: Switch NPC.");
				String destinationAddress = switchNpcs(npcSwitchersJson.get(newAddress).getAsInt(), newLocation);
				gameDataManager.saveCurrentState();
				updateCurrentAddress = false;
				return handleEvents(destinationAddress);
			}
			// use multichecker
			else if (gameDataManager.getMultichecker().keySet().contains(newAddress)) {
				System.out.println("ConscientiaDialogueProcessor:handleEvents: Multichecker.");
				MulticheckerBlock mb = gameDataManager.getMultichecker().get(newAddress);
				updateCurrentAddress = false;
				return handleEvents(mb.getDestinationAddress(gameDataManager));
			}
			// event checker, at-forcer, affinity checker, cues
			else {
				JsonObject dialogueBlockJson = (JsonObject) dialogueJson.get(newAddress);
				JsonObject actionJson = (JsonObject) dialogueBlockJson.get(Constants.DIALOGUE_ACTION);

				String actionSymbol = actionJson.get(Constants.ACTION_TYPE).getAsString();
				// @-forcer
				if (actionSymbol.equals(Constants.ACTION_TYPE_DIALOGUE_ADDRESS_FORCER_SYMBOL)) {
					System.out.println("ConscientiaDialogueProcessor:handleEvents: @-Forcer.");
					String targetAddress = actionJson.get(Constants.ACTION_TARGET_ADDRESS).getAsString();
					currentNpc.setAddress(newLocation, targetAddress);
				}
				// event checker
				else if (actionSymbol.equals(Constants.ACTION_TYPE_EVENT_CHECKER_SYMBOL)) {
					System.out.println("ConscientiaDialogueProcessor:handleEvents: Check Event.");
					int eventNum = actionJson.get(Constants.ACTION_EVENT).getAsInt();
					if (gameDataManager.getTriggeredEvent(eventNum)) {
						System.out.println("ConscientiaDialogueProcessor:handleEvents: Event True: " + eventNum);
						String destinationAddress = actionJson.get(Constants.ACTION_DESTINATION_ADDRESS).getAsString();
						updateCurrentAddress = false;
						return handleEvents(destinationAddress);
					}
				} else {
					System.out.println("ConscientiaDialogueProcessor:handleEvents: ????.");
					System.out.println("ConscientiaDialogueProcessor:handleEvents: Unimplemented Section - checking for X-addresses [affinity checker, cues, others(?)].");
				}
			}
		}


		// change npc's last address for the given location if necessary
		if (updateNpcAddress) {
			System.out.println("ConscientiaDialogueProcessor:handleEvents: Changed last NPC address.");
			currentNpc.setAddress(currentLocation, newAddress);
		}
		// update currentAddress
		if (updateCurrentAddress) {
			currentAddress = newAddress;
		}
		System.out.println("ConscientiaDialogueProcessor:handleEvents: " + currentNpc.getName());
		System.out.println("ConscientiaDialogueProcessor:handleEvents: " + newAddress);

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
}
