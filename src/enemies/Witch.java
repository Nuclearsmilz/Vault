package enemies;

/*
 * Matching armor, low hp, high dmg
 */
public class Witch extends Enemy {
	public Witch(int playerLvl) {
		this.enemyType = "Witch";
		this.setMaxHP(50 + playerLvl * 5);
		this.setHP(50 + playerLvl * 5);
		this.setArmor(playerLvl);
		this.setDmg(10 + playerLvl * 2);
		this.setIntel(1);
		this.setDex(1);
		this.setCritChance(0.02);
		this.setXPGain(30 + playerLvl * 3);
		this.setGold(playerLvl * 3);
		addRandomItems(playerLvl, "breastplate", "supermilk");
	}
}