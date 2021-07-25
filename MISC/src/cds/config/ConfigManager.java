package cds.config;

import cds.io.FileIO;
import cds.io.FileIOFactory;
import java.io.FileNotFoundException;
import org.json.simple.parser.*;

public class ConfigManager {
		
	Config config;
	final String CONFIG_FILEPATH = ".\\resources\\config.yml";
	
	public ConfigManager() {
		loadConfiguration();
	}
	
	public Config getConfig() { return config; }
	
	private void loadConfiguration() {
		FileIO fileio = FileIOFactory.createFileIO();
		
		try {
			String configFileData = fileio.readFileToString(CONFIG_FILEPATH);
			config = parseConfigData(configFileData);
		} catch (FileNotFoundException e) {
			System.err.println("Config file not found: " + e.getMessage());
		}
	}
	
	private Config parseConfigData(String data) {
		
		return null;
	}
}