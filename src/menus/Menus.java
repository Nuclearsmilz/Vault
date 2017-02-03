package menus;

import java.util.*;

import main.*;

/**
 * Menu SUPERCLASS
 * All menus extend this
 * Add Items to MenuType, and
 * CALL DISPLAYMENU or else it WILL NOT WORK!!!
 * @author Jon
 */
public class Menus {
	protected List<MenuType> menuTypes = new ArrayList<>();
	protected Map<String, MenuType> commandMap = new HashMap<String, MenuType>();

	public MenuType displayMenu( List<MenuType> mt ) {
		int i = 1;
		for (MenuType menuType : mt) {
			commandMap.put(String.valueOf(i), menuType);
			commandMap.put(menuType.getKey(), menuType);
			for (String command : menuType.getAltCommands()) {
				commandMap.put(command.toLowerCase(), menuType);
			}
			i++;
		}
		MenuType selectedItem = selectMenu(mt);
		return selectedItem;
	}

	protected MenuType selectMenu( List<MenuType> mt ) {
		this.printItemsMenu(mt);
		String command = VaultLogger.recieve();
		if (commandMap.containsKey(command.toLowerCase())) {
			return commandMap.get(command.toLowerCase());
		} else {
			VaultLogger.say("I don't know what '" + command + "' means.");
			return this.displayMenu(mt);
		}
	}

	private void printItemsMenu( List<MenuType> mt ) {
		int i = 1;
		for (MenuType menuType : mt) {
			if (menuType.getDescription() != null) {
				VaultLogger.say("[" + i + "]" + menuType.getCommand() + " - " + menuType.getDescription());
			} else {
				VaultLogger.say("[" + i + "]" + menuType.getCommand());
			}
			i++;
		}
	}
}