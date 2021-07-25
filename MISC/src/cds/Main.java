package cds;

import cds.config.ConfigManager;

public class Main {
	
	public static void main(String[] args) {
		ConfigManager configManager = new ConfigManager();
		String saveGameFilepath = ".\\bin\\testData\\test.txt";
		CDS cds = new CDS(saveGameFilepath);
	}

}