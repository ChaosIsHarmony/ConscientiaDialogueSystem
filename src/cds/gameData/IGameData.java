package cds.gameData;

import cds.entities.ConscientiaNpc;
import cds.entities.Dialogue;
import cds.entities.MulticheckerBlock;
import cds.entities.Personality;
import cds.utils.JsonValue;

import java.util.HashMap;

public interface IGameData {

	void saveCurrentState();

	HashMap<String, MulticheckerBlock> getMultichecker();

	Personality[] getTopAffinities(int range);

	void setUniValue(String varName, Object varValue);
	Object getUniValue(String varName);

	void setPlayerValue(String varName, JsonValue<?> varValue);
	JsonValue<?> getPlayerValue(String varName);

	void setNpc(ConscientiaNpc varValue);
	ConscientiaNpc getNpcByName(String varName);
	ConscientiaNpc getNpcById(int varId);

	void setTriggeredEvent(int eventNum, boolean value);
	Boolean getTriggeredEvent(int eventNum);
	Boolean isTriggeredEventPersistent(int eventNum);

}
