package cds;

import cds.config.ConfigManager;
import cds.entities.CombatBlock;
import cds.entities.ConscientiaNpc;
import cds.entities.Dialogue;
import cds.entities.Response;
import cds.gameData.GameDataManager;
import cds.utils.Constants;
import cds.utils.JsonValue;

public class CDS {

	private ConfigManager configManager;
	private GameDataManager gameDataManager;
	private boolean gameLoopActive;
	private Dialogue currentDialogue;
	private String nextAddress;

	// states for fsm
	private int gameState = Constants.LOADING_DIALOGUE;

	public CDS(ConfigManager configManager, GameDataManager gameDataManager) {
		this.configManager = configManager;
		this.gameDataManager = gameDataManager;

		// determine initial dialogue address
		String currentNpcName =
			(String) gameDataManager.getPlayerValue(Constants.PLAYER_CURRENT_NPC).getValue();
		ConscientiaNpc initialNpc = gameDataManager.getNpcByName(currentNpcName);
		String currentLocation =
			(String) gameDataManager.getPlayerValue(Constants.PLAYER_CURRENT_LOCATION).getValue();
		this.nextAddress = initialNpc.getDialogueAddress(currentLocation);

		// start game loop
		this.gameLoopActive = true;
		while (this.gameLoopActive) update();
	}

	public void update() {
		switch (this.gameState) {
			case Constants.LOADING_DIALOGUE:
				// handle events before getting dialogue
				String address = configManager.getDialogueProcessor().preprocessNewAddress(nextAddress);

				// check for mode switch
				if (address.contains("COMBAT")) {
					gameState = Constants.IN_COMBAT;
					this.nextAddress = address;
					break;
				}

				// load relevant dialogue and choices
				currentDialogue = configManager.getDialogueProcessor().getDialogue(address);

				// display dialogue and choices (up to max allowable choices, ordered by affinity)
				configManager.getRenderer().show(currentDialogue.getNpcText());
				int ind = 0;
				for (Response response : currentDialogue.getResponses())
					if (ind < Constants.MAX_VISIBLE_RESPONSES)
						configManager.getRenderer().show((ind++) + ": " + response.getText());
					else
						break;

				// switch to waiting for player input
				gameState = Constants.WAITING_FOR_INPUT;
				break;
			case Constants.WAITING_FOR_INPUT:
				int responseInd = configManager.getInputHandler().selectResponse();

				// ensure valid input
				if (responseInd < currentDialogue.getResponses().size()) {
					configManager
						.getRenderer()
						.show("CHOSEN RESPONSE: " + currentDialogue.getResponses().get(responseInd).getText());

					// Add to player affinity
					String personality = currentDialogue.getResponses().get(responseInd).getPersonality();
					int affinityPoints = currentDialogue.getResponses().get(responseInd).getAffinityPoints();
					int currentAffinity = (Integer) gameDataManager.getPlayerValue(personality).getValue();
					gameDataManager
						.setPlayerValue(personality, new JsonValue<Integer>(currentAffinity + affinityPoints));

					// Move to next address
					nextAddress = currentDialogue.getResponses().get(responseInd).getDestinationAddress();
					gameState = Constants.LOADING_DIALOGUE;
				} else {
					configManager.getRenderer().show("Invalid Selection.");
				}
				break;
			case Constants.IN_COMBAT:
				System.out.println("CDS:update: Switched to COMBAT mode.");
				CombatBlock cb = configManager.getDialogueProcessor().getCombatDescription(this.nextAddress);
				break;
			default:
				gameLoopActive = false;
				break;
			}
	}
}
