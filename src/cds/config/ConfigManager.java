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
import cds.io.IInputHandler;
import cds.io.ConsoleInputHandler;
import cds.renderers.IRenderer;
import cds.renderers.ConsoleRenderer;
import cds.dialogueProcessors.IDialogueProcessor;
import cds.dialogueProcessors.ConscientiaDialogueProcessor;

import com.google.gson.JsonObject;

public class ConfigManager {

	IFileIO fileio;
	IConfig config;
	IRenderer renderer;
	IInputHandler inputHandler;
	IDialogueProcessor dialogueProcessor;
	String configStrategy;

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
			inputHandler = determineInputHandlerStrategy(configData);
			dialogueProcessor = determineDialogueProcessorStrategy(configData);

			// load gamebook-specific data
			config.loadData(configData);
		} catch (Exception e) {
			System.err.println("ConfigManager:loadConfiguration: Could not load config file " + e.getMessage());
		}
	}

	private IConfig determineConfigStrategy(JsonObject configData) {
		String configStrategy = configData.get("config_strategy").getAsString();

		switch (configStrategy) {
			case "conscientia":
				this.configStrategy = configStrategy;
				return new ConscientiaConfig(this);
			default:
				System.err.println("ConfigManager:determineConfigStrategy: Illegal strategy: " + configStrategy);
				return null;
		}
	}

	private IRenderer determineRendererStrategy(JsonObject configData) {
		String rendererStrategy = configData.get("renderer_strategy").getAsString();

		switch (rendererStrategy) {
			case "console":
				return new ConsoleRenderer();
			default:
				System.err.println("ConfigManager:determineRendererStrategy: Illegal strategy: " + rendererStrategy);
				return null;
		}
	}

	private IInputHandler determineInputHandlerStrategy(JsonObject configData) {
		String inputHandlerStrategy = configData.get("input_strategy").getAsString();

		switch (inputHandlerStrategy) {
			case "console":
				return new ConsoleInputHandler();
			default:
				System.err.println("ConfigManager:determineInputHandlerStrategy: Illegal strategy: " + inputHandlerStrategy);
				return null;
		}
	}

	private IDialogueProcessor determineDialogueProcessorStrategy(JsonObject configData) {
		String dialogueProcessorStrategy = configData.get("dialogue_processor_strategy").getAsString();

		switch (dialogueProcessorStrategy) {
			case "conscientia":
				return new ConscientiaDialogueProcessor(this);
			default:
				System.err.println("ConfigManager:determineDialogueProcessorStrategy: Illegal strategy: " + dialogueProcessorStrategy);
				return null;
		}
	}

	public IConfig getConfig() { return config; }
	public IFileIO getFileIO() { return fileio; }
	public IRenderer getRenderer() { return renderer; }
	public IInputHandler getInputHandler() { return inputHandler; }
	public IDialogueProcessor getDialogueProcessor() { return dialogueProcessor; }
	public String getConfigStrategy() { return configStrategy; }

}
