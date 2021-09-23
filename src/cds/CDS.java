package cds;

import cds.config.ConfigManager;
import cds.entities.GameData;

public class CDS {

	ConfigManager configManager;
	GameData gameData;

	public CDS(ConfigManager configManager, GameData gameData) {
		this.configManager = configManager;
		this.gameData = gameData;
	}

	public void update() {
		// check updated event info

		// load relevant dialogue

		// load relevant choices

		// display dialogue and choices

		// update event info
	}
}
