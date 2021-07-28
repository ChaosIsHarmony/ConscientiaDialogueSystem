package cds.config;

import org.json.simple.JSONObject;

public interface IConfig {

	void loadData(JSONObject configData);

	String getSavedGameFilePath();
}