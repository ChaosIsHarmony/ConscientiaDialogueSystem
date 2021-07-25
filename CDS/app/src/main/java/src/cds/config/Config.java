package cds.config;

import org.json.simple.JSONObject;

public interface Config {

	void loadData(JSONObject configData);

	String getSavedGameFilePath();
}