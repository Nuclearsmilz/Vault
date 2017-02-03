package menus;

import java.io.*;
import java.net.*;
import java.nio.file.*;

import entities.*;
import main.*;

/**
 * The first menu displayed when game starts.
 * @see Vault.
 * This menu lets the player decide which menu to load.
 * (save, load, delete, exit)
 * @author Jon
 */
public class MainMenu extends Menus implements Runnable {
	//private static final long serialVersionUID = 1L;

	public MainMenu(Socket server, GameModeType mode) {
		VaultLogger.startMessenger(mode, server);
	}

	public MainMenu() {
		start();
	}

	public void run() {
		start();
	}

	public void start() {
		this.menuTypes.add(new MenuType("Start", "Starts a brand new game, straight off the shelves!", "new"));
		this.menuTypes.add(new MenuType("Load", "Loads an existing game!"));
		this.menuTypes.add(new MenuType("Delete", "Deletes an existing game save file."));
		this.menuTypes.add(new MenuType("Exit", null, "quit"));

		while (true) {
			try {
				MenuType selectedItem = displayMenu(this.menuTypes);
				boolean exit = option(selectedItem);
				if (!exit) {
					break;
				}
			} catch (DeathException de) {
				if (de.getLocalizedMessage().equals("close")) {
					break;
				}
			}
		}
		VaultLogger.say("EXIT");
	}

	private static boolean option( MenuType mt ) throws DeathException {
		String key = mt.getKey();
		switch (key) {
			case "start":
				try {
					Path original = Paths.get("json/data/locations.json");
					Path destination = Paths.get("json/locations.json");
					Files.copy(original, destination, StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException ioe) {
					VaultLogger.say("Unable to load the new locations file.");
					ioe.printStackTrace();
				}
				new ClassMenu();
				break;

			case "load":
				listProfiles();
				VaultLogger.say(
				        "\nWhat is the name of your player that you want to load? Type 'back' or 'exit' to go back.");
				Player player = null;
				boolean exit = false;
				while (player == null) {
					key = VaultLogger.recieve();
					if (Player.profileExists(key)) {
						player = Player.load(key);
					} else if (key.equals("exit") || key.equals("back")) {
						exit = true;
						break;
					} else {
						//VaultLogger.say("This is what you typed: '" + key + "'.");
						VaultLogger.say("That user does not exist. Try checking the spelling.");
					}
				}
				if (exit) return true;
				new Main(player, "old");
				break;

			case "delete":
				listProfiles();
				VaultLogger.say(
				        "\nWhat is the name of your player that you want to delete (case sensitive)? Type 'back' or 'exit' to go back.");
				exit = false;
				while (!exit) {
					key = VaultLogger.recieve();
					if (Player.profileExists(key)) {
						String profileName = key;
						VaultLogger.say("Are you sure you want to delete '" + profileName + "'? y/n.");
						key = VaultLogger.recieve();
						if (key.equalsIgnoreCase("y")) {
							File profile = new File("json/profiles/" + profileName);
							deleteDirectory(profile);
							VaultLogger.say("Profile deleted.");
							return true;
						} else {
							listProfiles();
							VaultLogger.say("\nWhich profile do you want to delete?");
						}
					} else if (key.equals("exit") || key.equals("back")) {
						exit = true;
						break;
					} else {
						//VaultLogger.say("This is what you typed: '" + key + "'.");
						VaultLogger.say("That user does not exist. Try checking the spelling.");
					}
				}
				break;
			case "exit":
				VaultLogger.say("Bye!");
				return false;
		}
		return true;
	}

	private static boolean deleteDirectory( File dir ) {
		if (dir.exists()) {
			File[] files = dir.listFiles();
			if (null != files) {
				for (int i = 0; i < files.length; i++) {
					if (files[i].isDirectory()) {
						deleteDirectory(files[i]);
					} else {
						files[i].delete();
					}
				}
			}
		}
		return (dir.delete());
	}

	private static void listProfiles() {
		VaultLogger.say("Profiles: ");
		File file = new File("json/profiles");
		String[] profiles = file.list();
		int index = 1;
		for (String name : profiles) {
			if (new File("json/profiles").isDirectory()) {
				VaultLogger.say("  " + index + ". " + name);
			}
			index += 1;
		}
	}
}