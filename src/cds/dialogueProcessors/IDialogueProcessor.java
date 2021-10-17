package cds.dialogueProcessors;

import cds.entities.CombatBlock;
import cds.entities.Dialogue;
import cds.gameData.GameDataManager;

public interface IDialogueProcessor {

	void setupProcessor(GameDataManager gameDataManager);

	String preprocessNewAddress(String nextAddress);

	Dialogue getDialogue(String address);

	CombatBlock handleCombat();

}
