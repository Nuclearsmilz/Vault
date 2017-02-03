package enemies;

/*
 * No armor, low health enemy
 * Overall easy to deal
 */
public class Skeleton extends Enemy {
	public Skeleton(int playerLvl) {
		this.enemyType = "Skele";
		this.setMaxHP(45 + (int) Math.pow(playerLvl, 3));
		this.setHP(45 + (int) Math.pow(playerLvl, 3));
		this.setArmor(0);
		this.setDmg(15 + Math.pow(playerLvl, 1.5));
		this.setCritChance(0.05);
		this.setIntel(3);
		this.setDex(3);
		this.setXPGain(10 + playerLvl * 3);
		this.setGold(playerLvl * 3);
		addRandomItems(playerLvl, "rusthelmet");
	}
}