package cds.entities;

public class Response {

	// Personality as an enum? Or something else defined in the config.json file?
	private Personality personality;
	private int affinityPoints;
	private String responseText;
	private String destinationAddress;

	public Response() {}

	public Personality getPersonality() { return personality; }
	public int getAffinityPoints() { return affinityPoints; }
	public String getResponseText() { return responseText; }
	public String getDestinationAddress() { return destinationAddress; }

}
