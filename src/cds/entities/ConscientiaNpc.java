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
	private HashMap<String, String> addresses;

	public ConscientiaNpc() { this.id = -1; }

	public ConscientiaNpc(String name, JsonObject npcData) {
		this.name = name;
		this.id = npcData.get(Constants.NPC_ID).getAsInt();
		this.imgFilepath = npcData.get(Constants.NPC_IMG).getAsString();
		parseWeakenesses((JsonArray) npcData.get(Constants.NPC_WEAKNESSES));
		parseAddresses((JsonObject) npcData.get(Constants.NPC_ADDRESSES));
	}

	private void parseWeakenesses(JsonArray weaknessesJson) {
		weaknesses = new HashSet<>();

		for (JsonElement weakness : weaknessesJson)
			weaknesses.add(weakness.getAsInt());
	}

	private void parseAddresses(JsonObject addressesJson) {
		addresses = new HashMap<>();

		for (String location : addressesJson.keySet())
			addresses.put(location, addressesJson.get(location).getAsString());
	}


	public String getName() { return name; }
	public int getId() { return id; }
	public String getImgFilepath() { return imgFilepath; }
	public boolean isWeakTo(int attackType) { return weaknesses.contains(attackType); }
	public String getAddress(String location) { return addresses.get(location); }
	public String setAddress(String location, String address) {
		return addresses.put(location, address);
	}

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
				+ ((addresses == null) ? 0 : addresses.hashCode());
		return result;
	}
}
