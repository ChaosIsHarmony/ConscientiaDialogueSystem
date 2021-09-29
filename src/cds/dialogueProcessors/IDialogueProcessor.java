package cds.dialogueProcessors;

import cds.entities.ConscientiaNpc;

public interface IDialogueProcessor {

	String getDialogue(String currentLocation, ConscientiaNpc currentNpc);
}
