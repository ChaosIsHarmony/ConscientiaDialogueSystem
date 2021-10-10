package cds.config;

import com.google.gson.*;

public interface IConfig {

	String getDialogueFileFilepath(String location);

	String getMulticheckerFilepath();

	String getTemplateFilepath(String filename);

	String getUniSaveFilepath();

	String getBaseSaveFilepath();

	String getStartingAddress(String startingBook);

}
