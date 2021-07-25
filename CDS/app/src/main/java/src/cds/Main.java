package cds;

import cds.config.ConfigManager;

public class Main {
	
	public static void main(String[] args) {
		ConfigManager configManager = new ConfigManager();
		String saveGameFilepath = configManager.getSavedGameFilePath();
		CDS cds = new CDS(saveGameFilepath);
	}

}