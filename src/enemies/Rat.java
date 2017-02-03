package enemies;

/*
 * Classic dungeon enemy!
 * Not dangerous in the slightest!
 */
public class Rat extends Enemy {
	public Rat(int playerLvl) {
		this.enemyType = "Rat";
		this.setMaxHP(25 + playerLvl * 3);
		this.setHP(25 + playerLvl * 3);
		this.setArmor(0);
		this.setDmg(8 + playerLvl * 1.5);
		this.setCritChance(0.03);
		this.setIntel(2);
		this.setDex(2);
		this.setXPGain(7 + playerLvl * 3);
		this.setGold(playerLvl * 2);
		addRandomItems(playerLvl, "rawmeat");
	}
}