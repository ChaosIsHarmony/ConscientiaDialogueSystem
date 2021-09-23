package cds.config;

import cds.io.IFileIO;
import cds.io.FileIOFactory;

import com.google.gson.JsonObject;

public class ConfigManager {

	IConfig config;
	final String CONFIG_FILENAME = "resources\\content\\json\\Dialogue\\BookOfBiracul\\BIRACUL_THE_EMPYREAN.json";//"config.json";

	public ConfigManager() {
		loadConfiguration();
	}

	private void loadConfiguration() {
		IFileIO fileio = FileIOFactory.createFileIO();
		try {
			JsonObject configData = fileio.readJsonFileToJsonObject(CONFIG_FILENAME);

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
