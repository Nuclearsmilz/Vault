package menus;

import java.util.*;

import enemies.*;
import entities.*;
import items.*;
import main.*;

/**
 * when the player comes to attack an enemy
 * @author Jon
 */
public class AttackMenu extends Menus {
	private Enemy enemyOpponent;
	private NPC npcOpponent;
	private Player player;
	private Random rand;
	private int armor;
	private double dmg;
	private int escapeAttemptsSuccessful = 0;

	// attack enemy
	public AttackMenu(Enemy enemyOpponent, Player player) throws DeathException {
		this.rand = new Random();
		this.player = player;
		this.enemyOpponent = enemyOpponent;
		this.armor = player.getArmor();
		this.dmg = player.getDmg();

		this.menuTypes.add(new MenuType("Attack", "Attack the" + enemyOpponent.getName() + "!"));
		this.menuTypes
		        .add(new MenuType("Defend", "Defend yourself against the " + enemyOpponent.getName() + "'s attack!"));
		this.menuTypes.add(new MenuType("Equip", "Equip an item."));
		this.menuTypes.add(new MenuType("Unequip", "Unequip an item."));
		this.menuTypes.add(new MenuType("View", "View stats about your character."));
		this.menuTypes.add(new MenuType("Escape", "Try and escape from the " + enemyOpponent.getName() + "!"));

		while (enemyOpponent.getHP() > 0 && player.getHP() > 0 && (escapeAttemptsSuccessful <= 0)) {
			VaultLogger.say("\nWhat shall you do?");
			MenuType selectedItem = displayMenu(this.menuTypes);
			selected(selectedItem);
		}
		if (player.getHP() == 0) {
			VaultLogger.say("You died. Start again? y/n");
			String userIn = VaultLogger.recieve().toLowerCase();
			while (!userIn.startsWith("y") && !userIn.startsWith("n")) {
				VaultLogger.say("You died. Start again? y/n");
				userIn = VaultLogger.recieve().toLowerCase();
			}
			if (userIn.startsWith("y")) {
				throw new DeathException("restart");
			} else if (userIn.startsWith("n")) {
				throw new DeathException("close");
			} else if (enemyOpponent.getHP() <= 0) {
				int xp = enemyOpponent.getXPGain();
				this.player.setXP(this.player.getXP() + xp);
				int oldLvl = this.player.getLvl();
				int newLvl = (int) (0.070 * Math.sqrt(this.player.getXP() + 1));
				this.player.setLvl(newLvl);

				/*
				 *  Checks to see if the enemy has any items,
				 *  and drops them if they do.
				 *  Was an issue with removing any items from the enemy's inventory
				 *  while looping through it so adding a second loop 
				 *  removes the ConcurrentModification error.
				 */
				List<ItemStack> itemStacks = enemyOpponent.getStorage().getItemStack();
				List<String> itemIds = new ArrayList<>();
				for (ItemStack itemStack : itemStacks) {
					String itemId = itemStack.getItem().getID();
					itemIds.add(itemId);
				}
				for (String itemId : itemIds) {
					Item item = Beans.getItemRepository().getItem(itemId);
					enemyOpponent.removeItemFromStorage(item);
					this.player.getLocation().addItem(item);
					VaultLogger.say("Your opponent dropped a " + item.getName());
				}

				this.player.getLocation().removeEnemy(enemyOpponent);
				this.player.setGold(this.player.getGold() + enemyOpponent.getGold());
				VaultLogger.say("You killed the " + enemyOpponent.getName() + "!" + "\nYou gained " + xp + " XP and "
				        + enemyOpponent.getGold() + " gold!");
				if (newLvl > oldLvl) {
					VaultLogger.say("You are now level " + newLvl + "!");
				}
				//CharChange cc = new CharChange();
				//cc.trigger(this.player, "kill", enemyOpponent.getName());
			}
		}
	}

	public AttackMenu(NPC npcOpponent, Player player) throws DeathException {
		this.rand = new Random();
		this.npcOpponent = npcOpponent;
		this.player = player;
		this.menuTypes.add(new MenuType("Attack", "Attack " + npcOpponent.getName() + "."));
		this.menuTypes.add(new MenuType("Defend", "Defend against " + npcOpponent.getName() + "'s attack."));
		this.menuTypes.add(new MenuType("Escape", "Try and escape from " + npcOpponent.getName() + "."));
		this.menuTypes.add(new MenuType("Equip", "Equip an item."));
		this.menuTypes.add(new MenuType("Unequip", "Unequip an item."));
		this.menuTypes.add(new MenuType("View", "View stats. (about you)"));
		this.armor = player.getArmor();
		this.dmg = player.getDmg();
		while (npcOpponent.getHP() > 0 && player.getHP() > 0 && (escapeAttemptsSuccessful <= 0)) {
			VaultLogger.say("\nWhat is your choice?");
			MenuType selectedItem = displayMenu(this.menuTypes);
			selected(selectedItem);
		}
		if (player.getHP() == 0) {
			VaultLogger.say("You died... Start again? y/n");
			String reply = VaultLogger.recieve().toLowerCase();
			while (!reply.startsWith("y") && !reply.startsWith("n")) {
				VaultLogger.say("You died... Start again? y/n");
				reply = VaultLogger.recieve().toLowerCase();
			}
			if (reply.startsWith("y")) {
				throw new DeathException("restart");
			} else if (reply.startsWith("n")) { throw new DeathException("close"); }
		} else if (npcOpponent.getHP() == 0) {
			int xp = npcOpponent.getXPGain();
			this.player.setXP(this.player.getXP() + xp);
			int oldLevel = this.player.getLvl();
			int newLevel = (int) (0.075 * Math.sqrt(this.player.getXP()) + 1);
			this.player.setLvl(newLevel);

			/*
			 *  Checks to see if the NPC has any items,
			 *  and drops them if they do.
			 *  Was an issue with removing any items from the NPC's inventory
			 *  while looping through it so adding a second loop 
			 *  removes the ConcurrentModification error.
			 */
			List<ItemStack> itemStacks = npcOpponent.getStorage().getItemStack();
			List<String> itemIds = new ArrayList<>();
			for (ItemStack itemStack : itemStacks) {
				String itemId = itemStack.getItem().getID();
				itemIds.add(itemId);
			}
			for (String itemId : itemIds) {
				Item item = Beans.getItemRepository().getItem(itemId);
				npcOpponent.removeItemFromStorage(item);
				this.player.getLocation().addItem(item);
				VaultLogger.say("Your opponent dropped a " + item.getName());
			}

			this.player.getLocation().removeNpc(npcOpponent);
			this.player.setGold(this.player.getGold() + npcOpponent.getGold());
			VaultLogger.say("You killed a " + npcOpponent.getName() + "\nYou have gained " + xp + " XP and "
			        + npcOpponent.getGold() + " gold");
			if (oldLevel < newLevel) {
				VaultLogger.say("You've are now level " + newLevel + "!");
			}
			//CharChange cc = new CharChange();
			//cc.trigger(this.player, "kill", npcOpponent.getName());
		}
	}

	private void selected( MenuType mt ) {
		switch (mt.getKey()) {
			case "attack": {
				mutateStats(1, 0.5);
				if (npcOpponent == null) {
					attack(player, enemyOpponent);
					attack(enemyOpponent, player);
				} else {
					attack(player, npcOpponent);
					attack(npcOpponent, player);
				}

				resetStats();
				break;
			}
			case "defend": {
				mutateStats(0.5, 1);
				if (npcOpponent == null) {
					VaultLogger.say("\nYou ready yourself to defend against the " + enemyOpponent.getName() + ".");
					attack(player, enemyOpponent);
					attack(enemyOpponent, player);
				} else {
					VaultLogger.say("\nYou ready yourself to defend against the " + npcOpponent.getName() + ".");
					attack(player, npcOpponent);
					attack(npcOpponent, player);
				}
				resetStats();
				break;
			}
			case "escape": {
				if (npcOpponent == null) {
					escapeAttemptsSuccessful = escapeAttempt(player, enemyOpponent, escapeAttemptsSuccessful);
				} else {
					escapeAttemptsSuccessful = escapeAttempt(player, npcOpponent, escapeAttemptsSuccessful);
				}
				break;
			}
			case "equip": {
				equip();
				break;
			}
			case "unequip": {
				unequip();
				break;
			}
			case "view": {
				viewStats();
				break;
			}
			default: {
				break;
			}
		}
	}

	private int escapeAttempt( Player player, Entity attacker, int escapeAttempts ) {
		if (escapeAttempts == -10) {
			escapeAttempts = 0;
		}

		double playerEscapeLevel = player.getIntel() + player.getDex();
		double attackerEscapeLevel = attacker.getIntel() + attacker.getDex() + (attacker.getDmg() / playerEscapeLevel);
		double escapeLevel = playerEscapeLevel / attackerEscapeLevel;

		Random rand = new Random();
		int luck = rand.nextInt(player.getLuck() * 2) + 1;
		int low = 60 - luck;
		int up = 80 - luck;
		double minEscapeLevel = (rand.nextInt((up - low) + 1) + low) / 100.0;

		if (escapeLevel > minEscapeLevel && (escapeAttempts == 0)) {
			VaultLogger.say("You managed to escape the " + attacker.getName());
			return 1;
		} else if (escapeAttempts < 0) {
			VaultLogger.say("You tried to escape too many times!");
			return escapeAttempts - 1;

		} else {
			VaultLogger.say("You failed to escape the " + attacker.getName());
			return escapeAttempts - 1;
		}
	}

	private void attack( Entity att, Entity def ) {
		if (att.getHP() == 0) return;
		double dmg = att.getDmg();
		double crit = rand.nextDouble();
		if (crit < att.getCritChance()) {
			dmg += dmg;
			VaultLogger.say("CRITICAL HIT!! Your damage has been multiplied 'x2'.");
		}
		int hpReduc = (int) ((((3 * att.getLvl() / 50 + 2) * dmg * dmg / (def.getArmor() + 1) / 100) + 2)
		        * rand.nextDouble() + 1);
		def.setHP(def.getHP() - hpReduc);
		if (def.getHP() <= 0) {
			def.setHP(0);
		}
		VaultLogger.say(hpReduc + " damage has been dealt!");
		if (att instanceof Player) {
			VaultLogger.say("The " + def.getName() + "'s HP is " + def.getHP() + ".");
		} else {
			VaultLogger.say("Your HP is " + def.getHP());
		}
	}

	private void mutateStats( double damageMutate, double armorMutate ) {
		armor = player.getArmor();
		dmg = player.getDmg();
		player.setArmor((int) (armor * armorMutate));
		player.setDmg(dmg * damageMutate);
	}

	private void resetStats() {
		player.setArmor(armor);
		player.setDmg(dmg);
	}

	private void equip() {
		player.printStorage();
		VaultLogger.say("What item do you want to equip?");
		String itemName = VaultLogger.recieve();
		if (!itemName.equalsIgnoreCase("back")) {
			player.equipItem(itemName);
		}
	}

	private void unequip() {
		player.printEquipment();
		VaultLogger.say("What item do you want to unequip?");
		String itemName = VaultLogger.recieve();
		if (!itemName.equalsIgnoreCase("back")) {
			player.dequipItem(itemName);
		}
	}

	private void viewStats() {
		VaultLogger.say("\nWhat is your choice? ex. View stats, View Backpack, View Equipment (vs, vb, ve)");
		String userIn = VaultLogger.recieve();
		switch (userIn) {
			case "vs":
			case "viewstats":
				player.getStats();
				break;
			case "ve":
			case "viewequipped":
				player.printEquipment();
				break;
			case "vb":
			case "viewbackpack":
				player.printStorage();
				break;
			case "back":
			case "exit":
				break;
			default:
				viewStats();
				break;
		}
	}
}