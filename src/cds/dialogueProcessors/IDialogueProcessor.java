package cds.dialogueProcessors;

import cds.entities.Dialogue;
import cds.gameData.GameDataManager;

public interface IDialogueProcessor {

	void setupProcessor(GameDataManager gameDataManager);
	void handleEvents(String nextAddress);
	Dialogue getDialogue();
}
