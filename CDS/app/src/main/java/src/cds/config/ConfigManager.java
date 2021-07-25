package cds.config;

import cds.io.FileIO;
import cds.io.FileIOFactory;

import org.json.simple.JSONObject;

public class ConfigManager {
		
	Config config;
	final String CONFIG_FILENAME = "config.json";
	
	public ConfigManager() {
		loadConfiguration();
	}
		
	private void loadConfiguration() {
		FileIO fileio = FileIOFactory.createFileIO();
		try {
			JSONObject configData = fileio.readJsonFileToJsonObject(CONFIG_FILENAME);
			config = ConfigFactory.createConfig();
			config.loadData(configData);
		} catch (Exception e) {
			System.err.println("ConfigManager:loadConfiguration: Could not load config file" + e.getMessage());
		}
	}
	
	public String getSavedGameFilePath() {
		return config.getSavedGameFilePath();
	}
}