package enemies;

import java.util.*;

import entities.*;

/**
 * Quite literally generates random enemies
 * to where the player is standing based on
 * level and location
 * @author Jon
 */
public class EnemyGenerator {
	Random rand = new Random();

	public Enemy generateEnemy( Player player ) {
		int randomInt = rand.nextInt(5) + 1;
		if (randomInt <= player.getLocation().getDangerRating()) {
			switch (player.getLocationType()) {
				case SWAMP:
					return getSwampMonster(player.getLvl());
				case PLAINS:
					return getPlainsMonster(player.getLvl());
				case CAVE:
					return getCaveMonster(player.getLvl());
					/**
					 * These only return null because the enemies 
					 * haven't been created yet for these areas.
					 */
				case FOREST:
					return null;
				case MOUNTAINS:
					return null;
				case ROAD:
					return null;
				case STAIRS:
					return null;
				case WALL:
					return null;
				default: // non-hostile locations
					return null;
			}
		} else {
			return null;
		}
	}

	private Enemy getSwampMonster( int playerLvl ) {
		int randInt = rand.nextInt(2);
		return (randInt == 1) ? new Witch(playerLvl) : new Rat(playerLvl);
	}

	private Enemy getPlainsMonster( int playerLvl ) {
		int randInt = rand.nextInt(2);
		return (randInt == 1) ? new Assassin(playerLvl) : new Slime(playerLvl);
	}

	private Enemy getCaveMonster( int playerLvl ) {
		int randInt = rand.nextInt(4);
		if (randInt == 0) return new Skeleton(playerLvl);
		else if (randInt == 1) return new Assassin(playerLvl);
		else return new Rat(playerLvl);
	}
}