package cds.entities;

public class CombatBlock {

	private String combatDescription;
	private int npcId;
	private boolean isVictorious;

	public CombatBlock(int npcId) { this.npcId = npcId; }

	public void setText(String combatDescription) { this.combatDescription = combatDescription; }
	public String getText() { return combatDescription; }

	public void setIsVictorious(boolean isVictorious) { this.isVictorious = isVictorious; }
	public boolean isVictorious() { return isVictorious; }
}
