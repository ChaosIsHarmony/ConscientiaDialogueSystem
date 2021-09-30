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

	public ConscientiaDialogueProcessor(ConfigManager configManager) {
		this.configManager = configManager;
	}

	public Dialogue getDialogue(GameDataManager gameDataManager) {
		Dialogue dialogue = null;

		// extract npc and text info
		String currentNpcName = (String) gameDataManager.getPlayerValue(Constants.PLAYER_CURRENT_NPC).getValue();
		ConscientiaNpc currentNpc = gameDataManager.getNpcValue(currentNpcName);
		String currentLocation = (String) gameDataManager.getPlayerValue(Constants.PLAYER_CURRENT_LOCATION).getValue();
		String currentAddress = currentNpc.getAddress(currentLocation);

		// check updated event info

		// load dialogue for given npc by location
		String filepath = configManager.getConfig().getDialogueFileFilepath(currentLocation);
		JsonObject fileContentsJson = null;
		try {
			fileContentsJson = configManager.getFileIO().readJsonFileToJsonObject(filepath);
		} catch (IOException e) {
			System.err.println("ConscientiaDialogueProcessor:getDialogue: Failed to load dialogue file: " + currentAddress + " | " + currentLocation + " | " + filepath + " | ");
			e.printStackTrace();
		}

		if (fileContentsJson != null) {
			JsonObject dialogueContentsJson = (JsonObject) fileContentsJson.get("dialogue");
			JsonObject currentDialogueJson = (JsonObject) dialogueContentsJson.get(currentAddress);

			dialogue = new Dialogue(currentDialogueJson);
		}

		return dialogue;
	}

	// update event info
}
