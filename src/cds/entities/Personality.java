package cds.entities;

public class Personality {

	private String key;
	private String label;

	public Personality(String key, String label) {
		this.key = key;
		this.label = label;
	}

	public String getKey() { return key; }
	public String getLabel() { return label; }
}
