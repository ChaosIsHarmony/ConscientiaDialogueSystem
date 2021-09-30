package cds.dialogueProcessors;

import cds.entities.Dialogue;
import cds.gameData.GameDataManager;

public interface IDialogueProcessor {

	Dialogue getDialogue(GameDataManager gameDataManager);
}
