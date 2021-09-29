package cds;

import cds.config.ConfigManager;
import cds.entities.ConscientiaNpc;
import cds.entities.Dialogue;
import cds.entities.Response;
import cds.gameData.GameDataManager;
import cds.utils.Constants;

public class CDS {

	ConfigManager configManager;
	GameDataManager gameDataManager;
	boolean gameLoopActive;
	boolean loadingDialogue;

	public CDS(ConfigManager configManager, GameDataManager gameDataManager) {
		this.configManager = configManager;
		this.gameDataManager = gameDataManager;
		gameLoopActive = true;
		loadingDialogue = true;
		while (gameLoopActive) update();
	}

	public void update() {
		if (loadingDialogue) {
			// check updated event info

			// load relevant dialogue
			Dialogue currentDialogue = loadDialogue();

			// System.out.println(dialogue);
			// load relevant choices

			// display dialogue and choices

			// update event info

			loadingDialogue = false;
		}

		// Wait for player input
	}

	private Dialogue loadDialogue() {
			String currentNpcName = (String) gameDataManager.getPlayerValue(Constants.PLAYER_CURRENT_NPC).getValue();
			ConscientiaNpc currentNpc = gameDataManager.getNpcValue(currentNpcName);
			String currentLocation = (String) gameDataManager.getPlayerValue(Constants.PLAYER_CURRENT_LOCATION).getValue();
			String currentAddress = currentNpc.getAddress(currentLocation);
			String dialogue = configManager.getDialogueProcessor().getDialogue(currentAddress, currentNpc);
			return null;
	}
}
