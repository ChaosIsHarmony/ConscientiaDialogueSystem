package cds.entities;

import cds.gameData.IGameData;

import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class MulticheckerBlock {

	private class MulticheckerCase {
		final static int AND = 0, OR = 1, SIMPLE = 3;
		int type;
		ArrayList<String> events;
		String destinationAddress;

		public MulticheckerCase(JsonObject multicheckerCase) {
			if (multicheckerCase.keySet().contains("and"))		type = ADD;
			if (multicheckerCase.keySet().contains("or"))			type = OR;
			if (multicheckerCase.keySet().contains("simple"))	type = SIMPLE;
			System.out.println(multicheckerCase);
		}

		public boolean getVerity(IGameData gameData) {
			switch(type) {
				case AND:
					return getVerityAnd(gameData);
				case OR:
					return getVerityOr(gameData);
				case SIMPLE:
					return getVeritySimple(gameData);
				default:
					// Error
					return null;
			}

		}

		private boolean getVerityAnd(IGameData gameData) {}
			for (String event : events) {
				// Event must not be true
				if (event.contains("*")) {
					int eventNum = Integer.parseInt(event.substring(1));
					if (gameData.getTriggeredEvent(eventNum)) return false;
				}
				// must reset event
				else if (event.contains("#")) {

				}
				// check normally
				else {

				}
			}

			return true;
		}

		private boolean getVerityOr(IGameData gameData) {}
			for (String event : events) {
				// Event must not be true
				if (event.contains("*")) {
					int eventNum = Integer.parseInt(event.substring(1));
					if (gameData.getTriggeredEvent(eventNum)) return true;
				}
				// must reset event
				else if (event.contains("#")) {

				}
				// check normally
				else {

				}
			}

			return false;
		}

		private boolean getVeritySimple(IGameData gameData) {}
			// should only be length 1
			for (String event : events) {
				// Event must not be true
				if (event.contains("*")) {
					int eventNum = Integer.parseInt(event.substring(1));
					if (gameData.getTriggeredEvent(eventNum)) return true;
				}
				// must reset event
				else if (event.contains("#")) {

				}
				// check normally
				else {

				}
			}

			return false;
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

	public String getDestinationAddress(IGameData gameData) {
		for (MulticheckerCase multicheckerCase : multicheckerCases)
			if (multicheckerCase.getVerity(gameData)) return multicheckerCase.getDestinationAddress();

		// Failed
		System.err.println("MulticheckerBlock:getDestinationAddress: Triggered event cases are not exhaustive for " + address);

		return "";
	}
}
