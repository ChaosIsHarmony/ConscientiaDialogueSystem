package cds.gameData;

import cds.config.ConfigManager;
import cds.entities.ConscientiaNpc;
import cds.entities.MulticheckerBlock;
import cds.entities.Personality;
import cds.utils.JsonValue;

import java.util.HashMap;

public class GameDataManager {

	protected ConfigManager configManager;
	private IGameData gameData;

	public GameDataManager(
			ConfigManager configManager,
			String startingBook,
			String[] savedGameFilepaths) {

		this.configManager = configManager;
		gameData = new ConscientiaGameData(this, startingBook, savedGameFilepaths);

	}

	/*
	 * --------------------
	 * ACCESSORS & MUTATORS
	 * --------------------
	 */
	public void saveCurrentState() { gameData.saveCurrentState(); };

	public HashMap<String, MulticheckerBlock> getMultichecker() { return gameData.getMultichecker(); }

	public Personality[] getTopAffinities(int range) { return gameData.getTopAffinities(range); }

	public void setUniValue(String varName, Object varValue) {
		gameData.setUniValue(varName, varValue);
	}
	public Object getUniValue(String varName) { return gameData.getUniValue(varName); }

	public void setPlayerValue(String varName, JsonValue<?> varValue) {
		gameData.setPlayerValue(varName, varValue);
	}
	public JsonValue<?> getPlayerValue(String varName) { return gameData.getPlayerValue(varName); }

	public void setNpc(ConscientiaNpc varValue) { gameData.setNpc(varValue); }
	public ConscientiaNpc getNpcByName(String varName) { return gameData.getNpcByName(varName); }
	public ConscientiaNpc getNpcById(int varId) { return gameData.getNpcById(varId); }

	public void setTriggeredEvent(int eventNum, boolean value) {
		gameData.setTriggeredEvent(eventNum, value);
	}
	public Boolean getTriggeredEvent(int eventNum) { return gameData.getTriggeredEvent(eventNum); }
	public Boolean isTriggeredEventPersistent(int eventNum) { return gameData.isTriggeredEventPersistent(eventNum); }

}
