package cds.gameData;

import cds.config.ConfigManager;
import cds.utils.JsonValue;

public class GameDataManager {

	protected ConfigManager configManager;
	private IGameData gameData;

	// New Game Constructor
	public GameDataManager(ConfigManager configManager, String startingBook) {
		this.configManager = configManager;
		this.gameData = loadGameData(startingBook, null);
	}

	// Saved Game Constructor
	public GameDataManager(ConfigManager configManager, String startingBook, String saveFilepath) {
		this.configManager = configManager;
		this.gameData = loadGameData(startingBook, saveFilepath);
	}

	private IGameData loadGameData(String startingBook, String saveFilepath) {
		switch (configManager.getConfigStrategy()) {
			case "conscientia":
				return new ConscientiaGameData(this, startingBook, saveFilepath);
			default:
				System.err.println("GameDataManager:loadGameData: Illegal strategy: " + configManager.getConfigStrategy());
				return null;
		}
	}

	public void saveCurrentState() { gameData.saveCurrentState(); };
	public void setVariableValue(String varName, JsonValue<?> varValue) { gameData.setVariableValue(varName, varValue); }
	public JsonValue<?> getVariableValue(String varName) { return gameData.getVariableValue(varName); }



}
