package enemies;

/*
 * Heavy Damage, Easy to kill
 */
public class Assassin extends Enemy {
	public Assassin(int playerLvl) {
		this.enemyType = "Assassin";
		this.setMaxHP(60 + playerLvl * 8);
		this.setHP(60 + playerLvl * 8);
		this.setArmor(6 + playerLvl * 3);
		this.setDmg(40 + playerLvl * 3);
		this.setIntel(3);
		this.setDex(1);
		this.setCritChance(0.03);
		this.setXPGain(50 + playerLvl * 3);
		this.setGold(15 + playerLvl * 11);
		addRandomItems(playerLvl, "dagger", "shiv");
	}
}