package cds.parsers;

import com.google.gson.JsonObject;
import cds.entities.Area;
import cds.entities.AreaDialogue;
import cds.entities.GameData;

import java.util.Set;

public class JsonFormatParser implements IParser {

  public AreaDialogue parseAreaDialogueFile(String filepath) {
    try {
      JsonObject areaDialogueFileText = fileio.readJsonFileToJsonObject(filepath);
      return parseDialogue(areaDialogueFileText);
    } catch (Exception e) {
      System.err.println(
          "JsonFormatParser:parseAreaDialogueFile: Could not load dialogue file" + e.getMessage());
      return null;
    }
  }

  private AreaDialogue parseDialogue(JsonObject jsonData) {
    AreaDialogue areaDialogue = new AreaDialogue();
    // parse json format
    JsonObject dialogue = (JsonObject) jsonData.get("dialogue");

//    for (String key : (Set<String>) dialogue.keySet()) {
//      System.out.println(key);
//      JsonArray bookFiles = (JsonArray) dialogue.get(key);
//      System.out.println(bookFiles);
//    }

    return areaDialogue;
  }

  public GameData parseSaveFile(String filepath) {
    try {
      JsonObject saveFileText = (JsonObject) fileio.readJsonFileToJsonObject(filepath);
      return parseSave(saveFileText);
    } catch (Exception e) {
      System.err.println(
          "JsonFormatParser:parseSaveFile: Could not load save file" + e.getMessage());
      return null;
    }
  }

  private GameData parseSave(JsonObject jsonData) {
    GameData gameData = new GameData();
    Area area = new Area();
    area.setAreaName(""); // jsonData);
    gameData.setCurrentArea(area);

    System.out.println(gameData.getCurrentArea().getAreaName());

    return gameData;
  }
}
