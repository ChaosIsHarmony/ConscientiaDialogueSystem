package cds.parsers;

import cds.entities.Area;
import cds.entities.AreaDialogue;
import cds.entities.GameData;
import cds.io.FileIO;

public class OriginalFormatParser implements Parser {

	public AreaDialogue parseAreaDialogueFile(String filepath) {
		try {
			String areaDialogueFileText = fileio.readFileToString(filepath);
			return parseDialogue(areaDialogueFileText);
		} catch (Exception e) {
			return null;
		}
	}
	
	private AreaDialogue parseDialogue(String text) {
		AreaDialogue areaDialogue = new AreaDialogue();
		// parse original format
		
		return areaDialogue;
	}
	
	public GameData parseSaveFile(String filepath) {
		try {
			String saveFileText = fileio.readFileToString(filepath);
			return parseSave(saveFileText);
		} catch (Exception e) {
			return null;
		}
	}
	
	private GameData parseSave(String text) {
		GameData gameData = new GameData();
		Area area = new Area();
		area.setAreaName(text);
		gameData.setCurrentArea(area);
		
		System.out.println(gameData.getCurrentArea().getAreaName());
			
		return gameData;
	}
}