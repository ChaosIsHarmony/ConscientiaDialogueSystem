package cds.entities;

import cds.utils.Constants;

import java.util.ArrayList;

import com.google.gson.JsonObject;

public class Dialogue {

	private String npcText;
	private ArrayList<Response> responses;

	public Dialogue(JsonObject dialogueContentsJson) {
		npcText = dialogueContentsJson.get(Constants.DIALOGUE_TEXT).getAsString();
		JsonObject responsesJson = (JsonObject) dialogueContentsJson.get(Constants.DIALOGUE_RESPONSES);

		responses = new ArrayList<>();
		for (String key : responsesJson.keySet())
			responses.add(new Response(key, (JsonObject) responsesJson.get(key)));
	}

	public String getNpcText() { return npcText; }
	public ArrayList<Response> getResponses() { return responses; }

}
