/*
 * ConfigManager.java
 *
 * Responsible for managing system-specific & game-specific configurations.
 * Made to be easily extensible with different strategies for:
 * 	- IO
 * 	- Rendering
 * 	- GameConfig
 * 	- DialogueProcessors
 *
 * Class Responsibilities:
 *	- Load/Store all strategies for use in-game
 *
 */
package cds.config;

import cds.io.IFileIO;
import cds.io.FileIOManager;
import cds.renderers.IRenderer;
import cds.renderers.ConsoleRenderer;
import cds.dialogueProcessors.IDialogueProcessor;
import cds.dialogueProcessors.ConscientiaDialogueProcessor;

import com.google.gson.JsonObject;

public class ConfigManager {

	IFileIO fileio;
	IConfig config;
	IRenderer renderer;
	IDialogueProcessor dialogueProcessor;

	public ConfigManager(String fileioType, String configFilepath) {
		fileio = FileIOManager.createFileIO(fileioType);
		loadConfiguration(configFilepath);
	}

	private void loadConfiguration(String configFilepath) {
		try {
			// load config file
			JsonObject configData = fileio.readJsonFileToJsonObject(configFilepath);

			// load all configurable strategies
			config = determineConfigStrategy(configData);
			renderer = determineRendererStrategy(configData);
			dialogueProcessor = determineDialogueProcessorStrategy(configData);

			// load gamebook-specific data
			config.loadData(configData);
		} catch (Exception e) {
			System.err.println("ConfigManager:loadConfiguration: Could not load config file" + e.getMessage());
		}
	}

	private IConfig determineConfigStrategy(JsonObject configData) {
		String configStrategy = configData.get("config_strategy").getAsString();

		if (configStrategy.equals("conscientia"))
			return new ConscientiaConfig(this);
		else
			return null;
	}

	private IRenderer determineRendererStrategy(JsonObject configData) {
		String rendererStrategy = configData.get("renderer_strategy").getAsString();

		if (rendererStrategy.equals("console"))
			return new ConsoleRenderer();
		else
			return null;
	}

	private IDialogueProcessor determineDialogueProcessorStrategy(JsonObject configData) {
		String dialogueProcessorStrategy = configData.get("dialogue_processor_strategy").getAsString();

		if (dialogueProcessorStrategy.equals("conscientia"))
			return new ConscientiaDialogueProcessor();
		else
			return null;
	}

	public IConfig getConfig() { return config; }
	public IFileIO getFileIO() { return fileio; }
	public IRenderer getRenderer() { return renderer; }
	public IDialogueProcessor getDialogueProcessor() { return dialogueProcessor; }

}
