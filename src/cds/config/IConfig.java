package cds.config;

import com.google.gson.*;

public interface IConfig {

	void loadData(JsonObject configData);

	String[] addNewSaveGame(String startingBook);

	String getDialogueFileFilepath(String location);
	String getMulticheckerFilepath();
}
