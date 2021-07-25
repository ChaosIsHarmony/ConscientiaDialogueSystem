package cds.parsers;

import cds.entities.Area;
import cds.entities.AreaDialogue;
import cds.entities.GameData;
import cds.io.FileIO;
import cds.io.FileIOFactory;

public interface Parser {
	
	FileIO fileio = FileIOFactory.createFileIO();
	
	AreaDialogue parseAreaDialogueFile(String filepath);
	
	GameData parseSaveFile(String filepath);
	
}