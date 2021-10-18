/*
 * variable names are in snake_case to match json files when serializing
 */
package cds.entities;

import cds.utils.Constants;

import java.util.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ConscientiaNpc {

	private String name;
	private int id;
	private String img_filepath;
	private HashSet<Integer> weaknesses;
	private HashMap<String, String> dialogue_addresses;
	private HashMap<String, String> npc_death_addresses;

	public ConscientiaNpc() { this.id = -1; }

	public ConscientiaNpc(String name, JsonObject npcData) {
		this.name = name;
		this.id = npcData.get(Constants.NPC_ID).getAsInt();
		this.img_filepath = npcData.get(Constants.NPC_IMG).getAsString();
		parseWeakenesses((JsonArray) npcData.get(Constants.NPC_WEAKNESSES));
		parseDialogueAddresses((JsonObject) npcData.get(Constants.NPC_DIALOGUE_ADDRESSES));
		if (npcData.keySet().contains(Constants.NPC_DEATH_ADDRESSES))
			parseNpcDeathAddresses((JsonObject) npcData.get(Constants.NPC_DEATH_ADDRESSES));
	}

	private void parseWeakenesses(JsonArray weaknessesJson) {
		weaknesses = new HashSet<>();

		for (JsonElement weakness : weaknessesJson)
			weaknesses.add(weakness.getAsInt());
	}

	private void parseDialogueAddresses(JsonObject addressesJson) {
		dialogue_addresses = new HashMap<>();

		for (String location : addressesJson.keySet())
			dialogue_addresses.put(location, addressesJson.get(location).getAsString());
	}

	private void parseNpcDeathAddresses(JsonObject addressesJson) {
		npc_death_addresses = new HashMap<>();

		for (String location : addressesJson.keySet())
			npc_death_addresses.put(location, addressesJson.get(location).getAsString());
	}

	public String getName() { return name; }
	public int getId() { return id; }
	public String getImgFilepath() { return img_filepath; }
	public boolean isWeakTo(int attackType) { return weaknesses.contains(attackType); }
	public String getDialogueAddress(String location) { return dialogue_addresses.get(location); }
	public String setDialogueAddress(String location, String address) {
		return dialogue_addresses.put(location, address);
	}
	public String getNpcDeathAddress(String location) { return npc_death_addresses.get(location); }

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
				+ ((img_filepath == null) ? 0 : img_filepath.hashCode());
		return result;
	}
}
