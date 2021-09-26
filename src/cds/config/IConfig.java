package cds.config;

import com.google.gson.*;

public interface IConfig {

	void loadData(JsonObject configData);

	String addNewSaveFile(String startingBook);
}
