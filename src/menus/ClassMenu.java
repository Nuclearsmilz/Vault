package menus;

import entities.*;
import main.*;

/**
 * Menu called when creating a new Player instance
 * @author Jon
 */
public class ClassMenu extends Menus {
	public ClassMenu() throws DeathException {
		this.menuTypes.add(new MenuType("Soldier", "A soldier on the brink of abandonment by his city."));
		this.menuTypes.add(new MenuType("Archer", "A lone hunter who lives to surive."));

		while (true) {
			VaultLogger.say("Choose a class: ");
			MenuType selectedItem = displayMenu(this.menuTypes);
			if (option(selectedItem)) {
				break;
			}
		}
	}

	private static boolean option( MenuType mt ) throws DeathException {
		String key = mt.getKey();
		if (key.equals("soldier")) {
			Player player = Player.getInstance("soldier");
			new Main(player, "new");
			return true;
		} else if (key.equals("archer")) {
			Player player = Player.getInstance("archer");
			new Main(player, "new");
			return true;
		} else {
			return false;
		}
	}
}