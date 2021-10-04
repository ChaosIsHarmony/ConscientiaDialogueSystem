package cds.entities;

import cds.utils.Constants;

import com.google.gson.JsonObject;

public class Action {

	private int type;
	private int eventNum;
	private String actionAddress;

	public Action(JsonObject actionBlock) {
		String typeStr = actionBlock.get(Constants.ACTION_TYPE).getAsString();
		// event checker
		if (typeStr.equals(Constants.ACTION_TYPE_EVENT_CHECKER_SYMBOL)) {
			type = Constants.ACTION_TYPE_EVENT_CHECKER;
			eventNum = actionBlock.get(Constants.ACTION_EVENT).getAsInt();
			actionAddress = actionBlock.get(Constants.ACTION_DESTINATION_ADDRESS).getAsString();
		}
		// affinity checker
		else if (typeStr.equals(Constants.ACTION_TYPE_AFFINITY_CHECKER)) {
			type = Constants.ACTION_TYPE_AFFINITY_CHECKER;
		}
		// address forcer
	  else {
			type = Constants.ACTION_TYPE_DIALOGUE_ADDRESS_FORCER;
			actionAddress = actionBlock.get(Constants.ACTION_TARGET_ADDRESS).getAsString();
		}
	}

	public int getType() { return type; }
	public int getEventNum() { return eventNum; }
	public String getActionAddress() { return actionAddress; }
}
