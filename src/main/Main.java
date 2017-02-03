package main;

import java.util.*;

import commands.*;
import databanks.*;
import enemies.*;
import entities.*;

/**
 * Following class hold main loop,
 * Stores input, and does the relative jobs
 * @author Jon
 */
public class Main {
	public ArrayList<Enemy> enemyList = new ArrayList<Enemy>();
	public EnemyGenerator enemyGen = new EnemyGenerator();
	public CommandReader parser;
	public Enemy enemy;
	Player player = null;

	public Main(Player player, String playerType) throws DeathException {
		this.player = player;
		this.parser = new CommandReader(player);

		switch (playerType) {
			case "new": {
				newGame(player);
				break;
			}
			case "old": {
				VaultLogger.say("Welcome back, " + player.getName() + "!");
				VaultLogger.say("");
				player.getLocation().print();
				break;
			}
			default:
				VaultLogger.say("Invalid player type");
				break;
		}
	}

	/**
	* Starts a new game.
	* Prints the introduction text first and asks for the
	* desired name of the user's character and welcomes them.
	*/
	public void newGame( Player player ) throws DeathException {
		VaultLogger.say(player.getIntro());
		String userIn = VaultLogger.recieve();
		player.setName(userIn);
		WorldBank worldBank = Beans.getLocationRepository(player.getName());
		this.player.setLocation(worldBank.getStartingLocation());
		player.save();
		VaultLogger.say("Welcome to the Vault, " + player.getName() + ".");
		player.getLocation().print();
		prompt(player);
	}

	/**
	 * Main loop.
	 * Gets input from command line
	 * and checks if the command is in the registry
	 * Will loop forever unless exit command is written
	 */
	public void prompt( Player player ) throws DeathException {
		boolean prompt = true;

		try {
			while (prompt) {
				VaultLogger.say("\n>Prompt:");
				String command = VaultLogger.recieve().toLowerCase();
				prompt = parser.parse(player, command);
			}
		} catch (DeathException de) {
			if (de.getLocalizedMessage().equals("replay")) return;
			else throw de;
		}
	}
}