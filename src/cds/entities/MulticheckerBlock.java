package cds.entities;

import cds.gameData.GameDataManager;

import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class MulticheckerBlock {

	private class MulticheckerCase {
		final static String AND = "and", OR = "or", SIMPLE = "simple";
		String type;
		ArrayList<String> events;
		String destinationAddress;

		public MulticheckerCase(JsonObject multicheckerCase) {
			if (multicheckerCase.keySet().contains("and"))					type = AND;
			else if (multicheckerCase.keySet().contains("or"))			type = OR;
			else if (multicheckerCase.keySet().contains("simple"))	type = SIMPLE;
			else																										type = "";

			events = new ArrayList<>();
			for (JsonElement event : multicheckerCase.get(type).getAsJsonArray())
				events.add(event.getAsString());

			destinationAddress = multicheckerCase.get("dest_add").getAsString();
		}

		public boolean getVerity(GameDataManager gameDataManager) {
			switch(type) {
				case AND:
					return getVerityAnd(gameDataManager);
				case OR:
					return getVerityOr(gameDataManager);
				case SIMPLE:
					return getVeritySimple(gameDataManager);
				default:
					// Error
					System.err.println("MulticheckerCase:getVerity: Unexpected type (not AND, OR, or SIMPLE): " + type);
					return false;
			}
		}

		private boolean getVerityAnd(GameDataManager gameDataManager) {
			for (String event : events) {
				// Event must not be true
				if (event.contains("*")) {
					int eventNum = Integer.parseInt(event.substring(1));
					if (gameDataManager.getTriggeredEvent(eventNum)) return false;
				}
				// must reset event
				else if (event.contains("#")) {
					if (!resetEvent(gameDataManager, event))	return false;
				}
				// check normally
				else {
					int eventNum = Integer.parseInt(event);
					if (!gameDataManager.getTriggeredEvent(eventNum)) return false;
				}
			}

			return true;
		}

		private boolean getVerityOr(GameDataManager gameDataManager) {
			for (String event : events) {
				// Event must not be true
				if (event.contains("*")) {
					int eventNum = Integer.parseInt(event.substring(1));
					if (!gameDataManager.getTriggeredEvent(eventNum)) return true;
				}
				// must reset event
				else if (event.contains("#")) {
					if (resetEvent(gameDataManager, event))	return true;
				}
				// check normally
				else {
					int eventNum = Integer.parseInt(event);
					if (gameDataManager.getTriggeredEvent(eventNum)) return true;
				}
			}

			return false;
		}

		private boolean getVeritySimple(GameDataManager gameDataManager) {
			// should only be length 1
			for (String event : events) {
				// Event must not be true
				if (event.contains("*")) {
					int eventNum = Integer.parseInt(event.substring(1));
					if (gameDataManager.getTriggeredEvent(eventNum)) return true;
				}
				// must reset event
				else if (event.contains("#")) {
					return resetEvent(gameDataManager, event);
				}
				// check normally
				else {
					int eventNum = Integer.parseInt(event);
					if (gameDataManager.getTriggeredEvent(eventNum)) return true;
				}
			}

			return false;
		}

		private boolean resetEvent(GameDataManager gameDataManager, String event) {
			int endInd = event.indexOf("#");
			int startResetEventInd = endInd + 1;
			int eventNum = Integer.parseInt(event.substring(0,endInd));
			int resetEventNum = Integer.parseInt(event.substring(startResetEventInd));
			boolean isTrue = gameDataManager.getTriggeredEvent(eventNum);
			gameDataManager.setTriggeredEvent(resetEventNum, false);
			return isTrue;
		}

		public String getDestinationAddress() { return destinationAddress; }
	}

	private ArrayList<MulticheckerCase> multicheckerCases;
	private String address;

	public MulticheckerBlock(String address, JsonObject multicheckerBlock) {
		multicheckerCases = new ArrayList<>();
		for (String key : multicheckerBlock.keySet())
			multicheckerCases.add(new MulticheckerCase(multicheckerBlock.get(key).getAsJsonObject()));
	}

	public String getDestinationAddress(GameDataManager gameDataManager) {
		for (MulticheckerCase multicheckerCase : multicheckerCases)
			if (multicheckerCase.getVerity(gameDataManager)) return multicheckerCase.getDestinationAddress();

		// Failed
		System.err.println("MulticheckerBlock:getDestinationAddress: Triggered event cases are not exhaustive for " + address);

		return "";
	}
}
