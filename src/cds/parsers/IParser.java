package cds.parsers;

import cds.entities.AreaDialogue;
import cds.entities.GameData;
import cds.io.IFileIO;
import cds.io.FileIOManager;

public interface IParser {

	IFileIO fileio = FileIOManager.createFileIO();

	AreaDialogue parseAreaDialogueFile(String filepath);

	GameData parseSaveFile(String filepath);

}
