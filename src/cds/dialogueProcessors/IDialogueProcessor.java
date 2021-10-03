package cds.dialogueProcessors;

import cds.entities.Dialogue;
import cds.gameData.GameDataManager;

public interface IDialogueProcessor {

	void setupProcessor(GameDataManager gameDataManager);
	String handleEvents(String nextAddress);
	Dialogue getDialogue(String address);
}
