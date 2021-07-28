package cds.parsers;

import cds.entities.Area;
import cds.entities.AreaDialogue;
import cds.entities.GameData;
import cds.io.IFileIO;
import cds.io.FileIOFactory;

public interface IParser {
	
	IFileIO fileio = FileIOFactory.createFileIO();
	
	AreaDialogue parseAreaDialogueFile(String filepath);
	
	GameData parseSaveFile(String filepath);
	
}