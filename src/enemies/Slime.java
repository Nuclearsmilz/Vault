package enemies;

public class Slime extends Enemy {
	public Slime(int playerLvl) {
		this.enemyType = "Slime";
		this.setMaxHP(20 + playerLvl * 8);
		this.setHP(20 + playerLvl * 8);
		this.setArmor(2 + playerLvl * 3);
		this.setDmg(10 + playerLvl * 3);
		this.setIntel(1);
		this.setDex(0);
		this.setCritChance(0.01);
		this.setXPGain(15 + playerLvl * 3);
		this.setGold(5 + playerLvl * 11);
		addRandomItems(playerLvl, "elixir");
	}
}