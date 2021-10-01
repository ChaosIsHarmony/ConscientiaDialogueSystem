package cds;

import cds.config.ConfigManager;
import cds.entities.ConscientiaNpc;
import cds.entities.Dialogue;
import cds.entities.Response;
import cds.gameData.GameDataManager;
import cds.utils.Constants;
import cds.utils.JsonValue;

public class CDS {

	ConfigManager configManager;
	GameDataManager gameDataManager;
	boolean gameLoopActive;
	Dialogue currentDialogue;
	String nextAddress;

	// states
	private final int LOADING_DIALOGUE = 0;
	private final int WAITING_FOR_INPUT = 1;
	private int gameState = LOADING_DIALOGUE;

	public CDS(ConfigManager configManager, GameDataManager gameDataManager) {
		this.configManager = configManager;
		this.gameDataManager = gameDataManager;

		// determine initial dialogue address
		String currentNpcName = (String) gameDataManager.getPlayerValue(Constants.PLAYER_CURRENT_NPC).getValue();
		ConscientiaNpc initialNpc = gameDataManager.getNpcByName(currentNpcName);
		String currentLocation = (String) gameDataManager.getPlayerValue(Constants.PLAYER_CURRENT_LOCATION).getValue();
		nextAddress = initialNpc.getAddress(currentLocation);

		// start game loop
		gameLoopActive = true;
		while (gameLoopActive) update();
	}


	public void update() {
		switch (gameState) {
			case LOADING_DIALOGUE:
				// handle events before getting dialogue
				configManager.getDialogueProcessor().handleEvents(nextAddress);

				// load relevant dialogue and choices
				currentDialogue = configManager.getDialogueProcessor().getDialogue();

				// display dialogue and choices
				configManager.getRenderer().show(currentDialogue.getNpcText());
				int ind = 0;
				for (Response response : currentDialogue.getResponses())
					configManager.getRenderer().show((ind++) + ": " + response.getText());

				// switch to waiting for player input
				gameState = WAITING_FOR_INPUT;
				break;
			case WAITING_FOR_INPUT:
				int responseInd = configManager.getInputHandler().selectResponse();

				if (responseInd < currentDialogue.getResponses().size()) {
					configManager.getRenderer().show("CHOSEN RESPONSE: " + currentDialogue.getResponses().get(responseInd).getText());

					// Add to player affinity
					String personality = currentDialogue.getResponses().get(responseInd).getPersonality();
					int affinityPoints = currentDialogue.getResponses().get(responseInd).getAffinityPoints();
					int currentAffinity = (Integer) gameDataManager.getPlayerValue(personality).getValue();
					gameDataManager.setPlayerValue(personality, new JsonValue<Integer>(currentAffinity + affinityPoints));

					// Move to next address
					nextAddress = currentDialogue.getResponses().get(responseInd).getDestinationAddress();
					gameState = LOADING_DIALOGUE;
				} else {
					configManager.getRenderer().show("Invalid Selection.");
				}
				break;
			default:
				gameLoopActive = false;
				break;
			}
	}
}
