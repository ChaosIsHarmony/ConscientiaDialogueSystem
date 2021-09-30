package cds.entities;

import cds.utils.Constants;

import com.google.gson.JsonObject;

public class Dialogue {

	private String npcText;
	private Response[] responses;

	public Dialogue(JsonObject dialogueContentsJson) {
		npcText = dialogueContentsJson.get(Constants.DIALOGUE_TEXT).getAsString();
		JsonObject responsesJson = (JsonObject) dialogueContentsJson.get(Constants.DIALOGUE_RESPONSES);

			System.out.println("Dialogue:<Constructor>: " + responsesJson);
	}

	public String getNpcText() { return npcText; }
	public Response[] getResponses() { return responses; }

}
