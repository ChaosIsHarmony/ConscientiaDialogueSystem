package cds;

import cds.config.ConfigManager;
import cds.gameData.GameDataManager;

public class Main {

	public static void main(String[] args) {
		String fileioType = args[0];
		String configFilepath = args[1];

		// Setup
		ConfigManager configManager = new ConfigManager(fileioType, configFilepath);
		String[] savedGameFilepaths = configManager.getInputHandler().selectSaveFiles();
		String startingBook = null;
		if (savedGameFilepaths == null) startingBook = configManager.getInputHandler().selectStartingBook();
		GameDataManager gameDataManager = new GameDataManager(configManager, startingBook, savedGameFilepaths);
		configManager.getDialogueProcessor().setupProcessor(gameDataManager);

		// Start game
		CDS cds = new CDS(configManager, gameDataManager);
	}

}
