package cds.entities;

import cds.config.ConfigManager;

public class GameData {

	private ConfigManager configManager;
	private String saveFilepath;
	private Area currentArea;

	// New Game Constructor
	public GameData(ConfigManager configManager) {
		this.configManager = configManager;
		createNewSaveFile();
	}

	// Saved Game Constructor
	public GameData(ConfigManager configManager, String saveFilepath) {
		this.configManager = configManager;
		this.saveFilepath = saveFilepath;
	}

	private void createNewSaveFile() {

	}

	public void setCurrentArea(Area newArea) { currentArea = newArea; }
	public Area getCurrentArea() { return currentArea; }
}
