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
 */
package cds.config;

import cds.io.IFileIO;
import cds.io.DesktopFileIO;
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

	public ConfigManager(String configFilepath) {
		fileio = new DesktopFileIO();
		loadConfiguration(configFilepath);
	}

	private void loadConfiguration(String configFilepath) {
		try {
			// load config file
			JsonObject configData = fileio.readJsonFileToJsonObject(configFilepath);

			// load all configurable strategies
			config = new ConscientiaConfig(this, configData);
			renderer = new ConsoleRenderer();
			inputHandler = new ConsoleInputHandler();
			dialogueProcessor = new ConscientiaDialogueProcessor(this);
		} catch (Exception e) {
			System.err.println("ConfigManager:loadConfiguration: Could not load config file " + e.getMessage());
		}
	}

	/*
	 * --------------------
	 * ACCESSORS & MUTATORS
	 * --------------------
	 */
	public IConfig getConfig() { return config; }
	public IFileIO getFileIO() { return fileio; }
	public IRenderer getRenderer() { return renderer; }
	public IInputHandler getInputHandler() { return inputHandler; }
	public IDialogueProcessor getDialogueProcessor() { return dialogueProcessor; }
	public String getConfigStrategy() { return configStrategy; }

}
