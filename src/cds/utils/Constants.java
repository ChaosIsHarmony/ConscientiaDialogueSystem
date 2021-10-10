package cds.utils;

public class Constants {
	// CONFIG
	public static final String BASE_DIR = "base";
	public static final String SAVE_FILES = "save_files";
	public static final String TEXT_FILES = "text_files";
	public static final String DIALOGUE_FILES = "dialogue_files";
	public static final String BOOK_FILES = "book_files";
	public static final String STRUCTURAL_FILES = "structural_files";
	public static final String TEMPLATE_FILES = "template_files";
	public static final String START_ADDRESSES = "start_addresses";
	public static final String PERSONALITIES_LIST = "personalities";
	public static final String FILE_LIST = "files";
	public static final String UNI_SAVE_FILE = "uni_save";
	public static final String MULTICHECKER_FILE = "Multichecker";
	public static final String PLAYER_SAVE_TEMPLATE = "PlayerSaveTemplate";
	public static final String NPC_SAVE_TEMPLATE = "NpcsSaveTemplate";

	// AREAS
	public static final String AREA_ENCLAVE = "ENCLAVE";
	public static final String AREA_JER = "JER";
	public static final String AREA_KABU = "KABU";
	public static final String AREA_KAVU = "KAVU";
	public static final String AREA_MIND = "MIND";
	public static final String AREA_THIUDA = "THIUDA";
	public static final String AREA_TURROK = "TURROK";

	// DIALOGUE FILES
	public static final String BIRACUL = "BookOfBiracul";
	public static final String EIDOS = "BookOfEidos";
	public static final String RIKHARR = "BookOfRikharr";
	public static final String THETIAN = "BookOfThetian";
	public static final String TORMA = "BookOfTorma";
	public static final String WULFIAS = "BookOfWulfias";
	public static final String MIND = "Mindscape";

	// SAVE FILES
	public static final int UNI_SAVE = 0;						// different types of save files
	public static final int PLAYER_SAVE = 1;
	public static final int NPC_SAVE = 2;
	public static final int N_ACTIVE_SAVE_FILE_TYPES = 3; // total number of types of save file

	// SAVE JSON TAGS
	public static final String ACTION_TYPE = "type";
	public static final String ACTION_EVENT = "event_num";
	public static final String ACTION_DESTINATION_ADDRESS = "dest_add";
	public static final int ACTION_TYPE_EVENT_CHECKER = 0;
	public static final String ACTION_TYPE_EVENT_CHECKER_SYMBOL = "^";
	public static final int ACTION_TYPE_DIALOGUE_ADDRESS_FORCER = 1;
	public static final String ACTION_TYPE_DIALOGUE_ADDRESS_FORCER_SYMBOL = "@";
	public static final int ACTION_TYPE_AFFINITY_CHECKER = 2;
	public static final String ACTION_TYPE_AFFINITY_CHECKER_SYMBOL = "*";

	public static final String DIALOGUE_DIALOGUE = "dialogue";
	public static final String DIALOGUE_EVENTS = "events";
	public static final String DIALOGUE_NPC_SWITCHERS = "npc_switchers";
	public static final String DIALOGUE_FIGHTING_WORDS = "fighting_words";
	public static final String DIALOGUE_TEXT = "dialogue_text";
	public static final String DIALOGUE_RESPONSES = "responses";
	public static final String DIALOGUE_ACTION = "action";

	public static final String EVENTS_EVENT_NUMBER = "event_num";
	public static final String EVENTS_DESTINATION_ADDRESS = "dest_add";

	public static final String PLAYER_CURRENT_LOCATION = "current_location";
	public static final String PLAYER_CURRENT_NPC = "current_npc";
	public static final String PLAYER_TRIGGERED_EVENTS = "triggered_events";

	public static final String RESPONSE_TEXT = "response_text";
	public static final String RESPONSE_POINTS = "points";
	public static final String RESPONSE_DESTINATION_ADDRESS = "dest_add";

	public static final String TAG_VALUE = "value";
	public static final String TAG_DESCRIPTION = "description";

	public static final String UNI_N_SAVE_FILES = "n_save_files";
	public static final String UNI_PERSISTENT_ACQ = "persistent_acquirables";
	public static final String UNI_PERSISTENT_EVENTS = "persistent_events";

	// NPC JSON TAGS
	public static final String NPC_ID = "id";
	public static final String NPC_IMG = "imgFilepath";
	public static final String NPC_WEAKNESSES = "weaknesses";
	public static final String NPC_ADDRESSES = "addresses";

	// PERSONALITIES
	public static final int N_PERSONALITIES = 6;
	public static final String[] PERSONALITY_SYMBOLS =	{ "A",
																												"B",
																												"C",
																												"D",
																												"E",
																												"F" };
	public static final String[] PERSONALITY_LABELS =	{ "Diplomat",
																											"Truthseeker",
																											"Equivocator",
																											"Schemer",
																											"Tyrant",
																											"Loon" };

	// DISPLAY
	public static final int MAX_VISIBLE_RESPONSES = 3;
}
