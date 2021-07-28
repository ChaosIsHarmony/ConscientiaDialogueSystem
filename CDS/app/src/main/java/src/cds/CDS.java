package cds;

import cds.entities.AreaDialogue;
import cds.entities.GameData;
import cds.entities.DialogueProcessor;
import cds.parsers.IParser;
import cds.parsers.ParserFactory;
import cds.renderers.IRenderer;
import cds.renderers.RendererFactory;

public class CDS {

	DialogueProcessor dialogueProcessor;
	GameData gameData;
	IParser parser;
	IRenderer renderer;

	public CDS(String saveGameFilepath) {
		parser = ParserFactory.createParser();
		gameData = parser.parseSaveFile(saveGameFilepath);
		AreaDialogue areaDialogue = parser.parseAreaDialogueFile(gameData.getCurrentArea().getFilepath());
		dialogueProcessor = new DialogueProcessor(areaDialogue);
		renderer = RendererFactory.createRenderer();
	}
	
	public void update() {
		// check updated event info
		
		// load relevant dialogue
		
		// load relevant choices
		
		// display dialogue and choices
		
		// update event info
	}
}