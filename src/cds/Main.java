package cds;

import cds.config.ConfigManager;
import cds.gameData.GameDataManager;

public class Main {

	public static void main(String[] args) {
		String configFilepath = args[0];

		// SETUP
		ConfigManager configManager = new ConfigManager(configFilepath);

		// select save file or start new file
		String[] savedGameFilepaths = configManager.getInputHandler().selectSaveFiles();
		String startingBook = null;
		if (savedGameFilepaths == null)
			startingBook = configManager.getInputHandler().selectStartingBook();

		// load data for game
		GameDataManager gameDataManager =
			new GameDataManager(configManager, startingBook, savedGameFilepaths);
		configManager.getDialogueProcessor().setupProcessor(gameDataManager);

		// START GAME
		CDS cds = new CDS(configManager, gameDataManager);
	}

}
