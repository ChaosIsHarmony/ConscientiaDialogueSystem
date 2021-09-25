package cds;

import cds.config.ConfigManager;
import cds.entities.GameData;

public class Main {

	public static void main(String[] args) {
		// Setup
		String fileioType = args[0];
		String configFilepath = args[1];
		ConfigManager configManager = new ConfigManager(fileioType, configFilepath);

		// TODO: Query for which saved game to load, or load new game
		String startingBook = "eidos";
		GameData gameData = new GameData(configManager, startingBook);


		// Start game
		CDS cds = new CDS(configManager, gameData);
	}

}
