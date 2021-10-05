package cds.entities;

public class Personality {

	private String key;
	private String label;
	private int affinity;

	public Personality(String key, String label, int affinity) {
		this.key = key;
		this.label = label;
		this.affinity = affinity;
	}

	public String getKey() { return key; }
	public String getLabel() { return label; }
	public int getAffinity() { return affinity; }
}
