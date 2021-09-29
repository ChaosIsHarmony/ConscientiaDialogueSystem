package cds.utils;

public class Constants {

	// SAVE FILES
	public static final int UNI_SAVE = 0;						// different types of save files
	public static final int PLAYER_SAVE = 1;
	public static final int NPC_SAVE = 2;
	public static final int N_ACTIVE_SAVE_FILE_TYPES = 3; // total number of types of save file

	// SAVE JSON TAGS
	public static final String UNI_PERSISTENT_ACQ = "persistent_acquirables";
	public static final String UNI_PERSISTENT_EVENTS = "persistent_events";
	public static final String PLAYER_CURRENT_LOCATION = "current_location";
	public static final String PLAYER_CURRENT_NPC = "current_npc";
	public static final String PLAYER_TRIGGERED_EVENTS = "triggered_events";

	// NPC JSON TAGS
	public static final String NPC_ID = "id";
	public static final String NPC_IMG = "img";
	public static final String NPC_WEAKNESSES = "weaknesses";
	public static final String NPC_ADDRESSES = "addresses";
}
