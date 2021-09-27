/*
 * ConscientiaVariables.java
 *
 * Houses all Conscientia-specific gameVariables
 */
public ConscientiaVariables {
	private String currentLocation;
	private String currentNpc;
	private String mindscapePriorLocation;
	private String mindscapeCurrentNpc;
	private Integer awareness;
	private Integer[] personalityAffinities;

	public String getCurrentLocation() { return currentLocation; }
	public String getCurrentNpc() { return currentNpc; }
	public String getMindscapePriorLocation() { return mindscapePriorLocation; }
	public String getMindscapeCurrentNpc() { return mindscapeCurrentNpc; }
	public Integer getAwareness() { return awareness; }
	public Integer[] getPersonalityAffinities() { return personalityAffinities; }

	public void setCurrentLocation(String location) { currentLocation = location; }
	public void setCurrentNpc(String npc) { currentNpc = npc; }
	public void setMindscapePriorLocation(String location) { mindscapePriorLocation = location; }
	public void setMindscapeCurrentNpc(String npc) { mindscapeCurrentNpc = npc; }
	public void incrementAwareness(int increment) { awareness += increment; }
	public void incrementPersonality(int personality, int increment) {
		personalityAffinities[personality] += increment;
	}

}
