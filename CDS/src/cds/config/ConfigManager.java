package cds.config;

import java.io.FileNotFoundException;
import cds.io.FileIO;
import cds.io.FileIOFactory;

public class ConfigManager {
		
	Config config;
	final String CONFIG_FILEPATH = ".\\resources\\config.yml";
	
	public ConfigManager() {
		loadConfiguration();
	}
	
	private void loadConfiguration() {
		FileIO fileio = FileIOFactory.createFileIO();
		
		try {
			String configFileData = fileio.readFileToString(CONFIG_FILEPATH);
			System.out.println(configFileData);
		} catch (FileNotFoundException e) {}
	}
}