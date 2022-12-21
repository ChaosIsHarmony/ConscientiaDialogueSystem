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

	private IConfig config;
	private IDialogueProcessor dialogueProcessor;
	private IFileIO fileio;
	private IInputHandler inputHandler;
	private IRenderer renderer;

	public ConfigManager(String configFilepath) {
		this.fileio = new DesktopFileIO();
		loadConfiguration(configFilepath);
	}

	private void loadConfiguration(String configFilepath) {
		try {
			// load config file
			JsonObject configData = fileio.readJsonFileToJsonObject(configFilepath);

			// load all configurable strategies
			this.config = new ConscientiaConfig(this, configData);
			this.dialogueProcessor = new ConscientiaDialogueProcessor(this);
			this.inputHandler = new ConsoleInputHandler();
			this.renderer = new ConsoleRenderer();
		} catch (Exception e) {
			System.err.println(
					"ConfigManager:loadConfiguration: Could not load config file: "
					+ e.getMessage());
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

}
