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

	// states
	private final int LOADING_DIALOGUE = 0;
	private final int WAITING_FOR_INPUT = 1;
	private int gameState = LOADING_DIALOGUE;

	public CDS(ConfigManager configManager, GameDataManager gameDataManager) {
		this.configManager = configManager;
		this.gameDataManager = gameDataManager;
		gameLoopActive = true;
		while (gameLoopActive) update();
	}


	public void update() {
		switch (gameState) {
			case LOADING_DIALOGUE:
				// load relevant dialogue and choices
				Dialogue currentDialogue = configManager.getDialogueProcessor().getDialogue(gameDataManager);

				// display dialogue and choices
				System.out.println("CDS:update: " + currentDialogue.getNpcText());

				// switch to waiting for player input
				gameState = WAITING_FOR_INPUT;
				break;
			case WAITING_FOR_INPUT:
				gameState = -1;
				break;
			default:
				gameLoopActive = false;
				break;
			}
	}
}
