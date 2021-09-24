package cds;

import cds.config.ConfigManager;

public class Main {

	public static void main(String[] args) {
		// Setup
		String fileioType = args[0];
		String configFilepath = args[1];
		ConfigManager configManager = new ConfigManager(fileioType, configFilepath);

		// TODO: Query for which saved game to load, or load new game
		GameData gameData = new GameData(configManager);


		// Start game
		CDS cds = new CDS(configManager, gameData);
	}

}
