package cds.gameData;

import cds.entities.Dialogue;
import cds.utils.JsonValue;

import java.util.HashMap;

public interface IGameData {

	void saveCurrentState();
	void setVariableValue(String varName, JsonValue<?> varValue);
	JsonValue<?> getVariableValue(String varName);

}
