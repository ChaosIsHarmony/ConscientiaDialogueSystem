package cds.dialogueProcessors;

import cds.config.ConfigManager;
import cds.entities.ConscientiaNpc;

import com.google.gson.JsonObject;

public class ConscientiaDialogueProcessor implements IDialogueProcessor {

	private ConfigManager configManager;

	private String mostRecentLocation;
	private ConscientiaNpc mostRecentNpc;

	public ConscientiaDialogueProcessor(ConfigManager configManager) {
		this.configManager = configManager;
		mostRecentLocation = "";
		mostRecentNpc = new ConscientiaNpc();
	}

	public String getDialogue(String newAddress, ConscientiaNpc currentNpc) {
		String currentLocation = parseCurrentLocation(newAddress);

		System.out.println(newAddress);
		System.out.println(currentLocation);
		System.out.println(currentNpc.getName());

		// make sure NPC is current
		if (mostRecentNpc != currentNpc) {

		}


		// make sure Area is current
		if (!currentLocation.equals(mostRecentLocation))	switchLocations(currentLocation);

		return "";
	}


	private String parseCurrentLocation(String address) {
		int endInd = address.indexOf("!")+1;
		endInd = address.indexOf("!", endInd)+1;
		endInd = address.indexOf("!", endInd)+1;
		return address.substring(0, endInd);
	}

	private void switchLocations(String currentLocation) {
		String newAreaDialogueFilepath = "";
		// JsonObject areaDialogueJson = configManager.getFileIO().readJsonFileToJsonObject(newAreaDialogueFilepath);
	}
}
