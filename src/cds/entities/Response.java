package cds.entities;

import cds.utils.Constants;

import com.google.gson.JsonObject;

public class Response {

	private String personality;
	private int affinityPoints;
	private String responseText;
	private String destinationAddress;

	public Response(String personality, JsonObject responseJson) {
		this.personality = personality;

		affinityPoints = responseJson.get(Constants.RESPONSE_POINTS).getAsInt();
		responseText = responseJson.get(Constants.RESPONSE_TEXT).getAsString();
		destinationAddress = responseJson.get(Constants.RESPONSE_DESTINATION_ADDRESS).getAsString();
	}

	public String getPersonality() { return personality; }
	public int getAffinityPoints() { return affinityPoints; }
	public String getText() { return responseText; }
	public String getDestinationAddress() { return destinationAddress; }

}
