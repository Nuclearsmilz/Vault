package commands;

import databanks.*;
import entities.*;
import items.*;
import main.*;

/**
 * To edit the contents of the player's backpack during, well, debugging.
 * Items added and removed are by display name.
 * @author Jon
 *
 */
public class PackDebugMenu {
	// @Resources
	protected static ItemBank itemBank = Beans.getItemRepository();

	private static String helpText = "\nlist: Lists the curent item.\n" + "add: Add a new item.\n"
	        + "remove: Remove an item.\n" + "help: Prints this text box.\n" + "exit: Exits the Pack Debug Menu\n";

	public PackDebugMenu(Player player) {
		boolean prompt = true;
		while (prompt) {
			VaultLogger.say("Edit backpack: ");
			String command = VaultLogger.recieve();
			prompt = parse(player, command.toLowerCase());
		}
	}

	public static boolean parse( Player player, String command ) {
		boolean prompt = true;

		try {
			if (command.startsWith("add")) {
				try {
					Item appendItem = itemBank.getItem(command.substring(3).trim());
					if (appendItem.getName() != null) player.addItemToStorage(appendItem);
				} catch (BankException re) {
					VaultLogger.say(re.getMessage());
				}
			} else if (command.startsWith("remove")) {
				String removeItemName = command.substring(6).trim();
				player.dropItem(removeItemName);
			} else if (command.startsWith("list")) {
				player.printBackpack();
			} else if (command.startsWith("help")) {
				VaultLogger.say(helpText);
			} else if (command.startsWith("exit")) {
				prompt = false;
			}
		} catch (NumberFormatException nfe) {
			VaultLogger.say("Invalid item name.");
		}

		return prompt;
	}
}