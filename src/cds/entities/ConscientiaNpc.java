package cds.entities;

import java.util.*;

import com.google.gson.JsonObject;

public class ConscientiaNpc {

	private String npcName;
	private int npcId;
	private String imgFilepath;
	private HashSet<Integer> weaknesses;
	private HashMap<String, String> addresses;

	public ConscientiaNpc(String name, JsonObject npcData) {

	}


	public String getNpcName() { return npcName; }
	public int getNpcId() { return npcId; }
	public String getImgFilepath() { return imgFilepath; }
	public boolean isWeakTo(int attackType) { return weaknesses.contains(attackType); }
	public String getAddress(String location) { return addresses.get(location); }
}
