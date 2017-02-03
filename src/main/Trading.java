package main;

import java.util.*;

import databanks.*;
import entities.*;
import items.*;
import menus.*;

/**
 * Lmao it says trading 
 * what more do you want
 * @author Jon
 */
public class Trading {
	NPC npc;
	Player player;
	ItemBank itemBank = Beans.getItemRepository();

	public Trading(NPC npc, Player player) {
		this.npc = npc;
		this.player = player;
	}

	/*
	 * The actual TRADE part of trading
	 */
	public void trade( boolean purchase, boolean sell ) {
		List<MenuType> tradeList = new ArrayList<>();
		String buyCommand = "Buy from " + npc.getName() + ".";
		String sellCommand = "Sell to " + npc.getName() + ".";
		if (purchase) {
			tradeList.add(new MenuType(buyCommand, null));
		}
		if (sell) {
			tradeList.add(new MenuType(sellCommand, null));
		}
		tradeList.add(new MenuType("Exit", null));
		Menus tradeMenu = new Menus();
		MenuType answer = tradeMenu.displayMenu(tradeList);
		String command = answer.getCommand();
		if (command.equals(buyCommand) && purchase) {
			playerPurchase();
		} else if (command.equals(sellCommand) && sell) {
			playerSell();
		} else if (command.equals("Exit")) return;
		trade(purchase, sell);
	}

	public void playerPurchase() {
		VaultLogger.say(npc.getName() + "'s items:\t" + npc.getName() + "'s gold:" + npc.getGold() + "\n");
		VaultLogger.say(npc.getStorage().displayWithValue(0, 0));

		VaultLogger.say("You have " + player.getGold() + " gold charms.");
		VaultLogger.say("What would you like to purchase?");
		VaultLogger.say("Type 'back' or 'exit' to leave.");
		String itemName = VaultLogger.recieve();

		if ("exit".equalsIgnoreCase(itemName) || "back".equalsIgnoreCase(itemName)) return;

		Item item = tradeItem(npc, player, itemName);
		if (item != null) {
			if (item != itemBank.getItem("empty")) {
				VaultLogger.say("You bought a " + item.getName() + " for " + item.getProperties().get("value")
				        + " gold charms.");
				VaultLogger.say("You now have " + player.getGold() + " gold charms remaining.");
			} else {
				VaultLogger.say("You do not have enough moolah!");
			}
		} else {
			VaultLogger.say("Either this item does not exist or this seller does not own that item.");
		}
	}

	public void playerSell() {
		VaultLogger.say(player.getName() + "'s items:\t" + npc.getName() + "'s gold:" + npc.getGold() + "\n");
		VaultLogger.say(player.getStorage().displayWithValue(player.getLuck(), player.getIntel()));

		VaultLogger.say("You have " + player.getGold() + " gold coins.");
		VaultLogger.say("What do you want to sell?");
		VaultLogger.say("Type 'back' or 'exit' to leave.");
		String itemName = VaultLogger.recieve();

		if ("exit".equalsIgnoreCase(itemName) || "back".equalsIgnoreCase(itemName)) return;

		Item item = tradeItem(npc, player, itemName);
		if (item != null) {
			if (item != itemBank.getItem("empty")) {
				VaultLogger.say("You have sold a " + item.getName() + " for "
				        + (int) ((0.5 + 0.045 * (player.getIntel() + player.getLuck()))
				                * item.getProperties().get("value"))
				        + " gold charms.");
				VaultLogger.say("You now have " + player.getGold() + " gold charms.");
			} else {
				VaultLogger.say(npc.getName() + " does not have enough moolah!");
			}
		} else {
			VaultLogger.say("Either this item does not exist or you do not own that item.");
		}
	}

	public Item tradeItem( Entity seller, Entity buyer, String itemName ) {
		List<Item> itemList = seller.getStorage().getItems();
		Map<String, String> itemIDs = new HashMap<>();
		Map<String, Integer> itemValues = new HashMap<>();
		Map<String, Item> itemID2Item = new HashMap<>();

		for (Item item : itemList) {
			String name = item.getName();
			String id = item.getID();
			int value = item.getProperties().get("value");
			itemIDs.put(name, id);
			itemValues.put(id, value);
			itemID2Item.put(id, item);
		}

		if (itemIDs.containsKey(itemName)) {
			int itemValue = itemValues.get(itemIDs.get(itemName));
			Item item = itemID2Item.get(itemIDs.get(itemName));
			if (seller instanceof Player) {
				itemValue = (int) ((0.5 + 0.015 * (seller.getIntel() + seller.getLuck())) * itemValue);
			}
			if (buyer.getGold() < itemValue) return itemBank.getItem("empty");
			buyer.addItemToStorage(item);
			buyer.setGold(buyer.getGold() - itemValue);

			seller.setGold(seller.getGold() + itemValue);
			seller.removeItemFromStorage(item);
			return item;
		} else {
			return null;
		}
	}
}