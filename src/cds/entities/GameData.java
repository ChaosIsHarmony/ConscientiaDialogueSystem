package cds.entities;

import cds.config.ConfigManager;

public class GameData {

	private ConfigManager configManager;
	private String saveFilepath;
	private Area currentArea;

	// New Game Constructor
	public GameData(ConfigManager configManager, String startingBook) {
		this.configManager = configManager;
		createNewSaveFile(startingBook);
	}

	// Saved Game Constructor
	public GameData(ConfigManager configManager, String startingBook, String saveFilepath) {
		this.configManager = configManager;
		this.saveFilepath = saveFilepath;
	}

	private void createNewSaveFile(String startingBook) {
		// create new save file
		saveFilepath = configManager.getConfig().addNewSaveFile(startingBook);

	}

	public void setCurrentArea(Area newArea) { currentArea = newArea; }
	public Area getCurrentArea() { return currentArea; }
}
