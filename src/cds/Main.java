package cds;

import cds.config.ConfigManager;

public class Main {

	public static void main(String[] args) {
		ConfigManager configManager = new ConfigManager();
		// TODO: Query for which saved game to load, or load new game
		CDS cds = new CDS(configManager);
	}

}
