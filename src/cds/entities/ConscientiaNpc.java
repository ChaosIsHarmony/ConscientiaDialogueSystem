package cds.entities;

import cds.utils.Constants;

import java.util.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ConscientiaNpc {

	private String name;
	private int id;
	private String imgFilepath;
	private HashSet<Integer> weaknesses;
	private HashMap<String, String> dialogueAddresses;
	private HashMap<String, String> postCombatAddresses;

	public ConscientiaNpc() { this.id = -1; }

	public ConscientiaNpc(String name, JsonObject npcData) {
		System.out.println(name);
		this.name = name;
		this.id = npcData.get(Constants.NPC_ID).getAsInt();
		this.imgFilepath = npcData.get(Constants.NPC_IMG).getAsString();
		parseWeakenesses((JsonArray) npcData.get(Constants.NPC_WEAKNESSES));
		parseDialogueAddresses((JsonObject) npcData.get(Constants.NPC_DIALOGUE_ADDRESSES));
		if (npcData.keySet().contains(Constants.NPC_POST_COMBAT_ADDRESSES))
			parsePostCombatAddresses((JsonObject) npcData.get(Constants.NPC_POST_COMBAT_ADDRESSES));
	}

	private void parseWeakenesses(JsonArray weaknessesJson) {
		weaknesses = new HashSet<>();

		for (JsonElement weakness : weaknessesJson)
			weaknesses.add(weakness.getAsInt());
	}

	private void parseDialogueAddresses(JsonObject addressesJson) {
		dialogueAddresses = new HashMap<>();

		for (String location : addressesJson.keySet())
			dialogueAddresses.put(location, addressesJson.get(location).getAsString());
	}

	private void parsePostCombatAddresses(JsonObject addressesJson) {
		postCombatAddresses = new HashMap<>();

		for (String location : addressesJson.keySet())
			postCombatAddresses.put(location, addressesJson.get(location).getAsString());
	}

	public String getName() { return name; }
	public int getId() { return id; }
	public String getImgFilepath() { return imgFilepath; }
	public boolean isWeakTo(int attackType) { return weaknesses.contains(attackType); }
	public String getDialogueAddress(String location) { return dialogueAddresses.get(location); }
	public String setDialogueAddress(String location, String address) {
		return dialogueAddresses.put(location, address);
	}
	public String getPostCombatAddress(String location) { return postCombatAddresses.get(location); }

	@Override
	public String toString() {
		return name + " | " + id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj.getClass() != this.getClass())	return false;

		final ConscientiaNpc that = (ConscientiaNpc) obj;
		return this.id == that.id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result =
			(prime * result)
				+ ((name == null) ? 0 : name.hashCode())
				+ ((imgFilepath == null) ? 0 : imgFilepath.hashCode())
				+ ((dialogueAddresses == null) ? 0 : dialogueAddresses.hashCode());
		return result;
	}
}
