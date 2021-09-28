package cds;

import cds.config.ConfigManager;
import cds.gameData.GameDataManager;

public class CDS {

	ConfigManager configManager;
	GameDataManager gameDataManager;

	public CDS(ConfigManager configManager, GameDataManager gameDataManager) {
		this.configManager = configManager;
		this.gameDataManager = gameDataManager;
	}

	public void update() {
		// check updated event info

		// load relevant dialogue

		// load relevant choices

		// display dialogue and choices

		// update event info
	}
}
