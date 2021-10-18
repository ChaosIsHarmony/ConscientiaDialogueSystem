package cds.entities;

public class CombatBlock {

	private String combatDescription;
	private int npcId;
	private boolean isPlayerVictorious;
	private String nextAddress;

	public CombatBlock(int npcId) { this.npcId = npcId; }

	public void setText(String combatDescription) { this.combatDescription = combatDescription; }
	public String getText() { return combatDescription; }

	public void setIsPlayerVictorious(boolean isPlayerVictorious) {
		this.isPlayerVictorious = isPlayerVictorious;
	}
	public boolean isPlayerVictorious() { return isPlayerVictorious; }

	public void setNextAddress(String nextAddress) { this.nextAddress = nextAddress; }
	public String getNextAddress() { return nextAddress; }
}
