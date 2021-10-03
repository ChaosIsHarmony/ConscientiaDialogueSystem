package cds.gameData;

import cds.entities.ConscientiaNpc;
import cds.entities.Dialogue;
import cds.entities.MulticheckerBlock;
import cds.utils.JsonValue;

import java.util.HashMap;

public interface IGameData {

	void saveCurrentState();

	HashMap<String, MulticheckerBlock> getMultichecker();

	void setPlayerValue(String varName, JsonValue<?> varValue);
	JsonValue<?> getPlayerValue(String varName);

	void setNpc(ConscientiaNpc varValue);
	ConscientiaNpc getNpcByName(String varName);
	ConscientiaNpc getNpcById(int varId);

	void setTriggeredEvent(int eventNum);
	Boolean getTriggeredEvent(int eventNum);
}
