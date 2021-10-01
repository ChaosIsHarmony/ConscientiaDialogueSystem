package cds.gameData;

import cds.entities.ConscientiaNpc;
import cds.entities.Dialogue;
import cds.utils.JsonValue;

import java.util.HashMap;

public interface IGameData {

	void saveCurrentState();

	void setPlayerValue(String varName, JsonValue<?> varValue);
	JsonValue<?> getPlayerValue(String varName);

	void setNpcValue(String varName, ConscientiaNpc varValue);
	ConscientiaNpc getNpcValue(String varName);

	void setTriggeredEvent(int eventNum);
	Boolean getTriggeredEvent(int eventNum);
}
