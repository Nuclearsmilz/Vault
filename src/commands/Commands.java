package commands;

import java.lang.reflect.*;
import java.util.*;

import org.slf4j.*;

import chats.*;
import databanks.*;
import enemies.*;
import entities.*;
import main.*;
import nav.*;

/**
 * Holds the declaration of the methods for the game
 * 
 * To add a new command, add a method to this class and be sure
 * to Annotate it with @Command or it WILL NOT WORK
 * @author Jon
 */
public enum Commands {
	INSTANCE;

	private Logger logger = LoggerFactory.getLogger(Commands.class);

	public Player player;

	private final static Map<String, String> DIRECTIONS = new HashMap<String, String>();
	static {
		DIRECTIONS.put("u", "up");
		DIRECTIONS.put("d", "down");
		DIRECTIONS.put("n", "north");
		DIRECTIONS.put("s", "south");
		DIRECTIONS.put("e", "east");
		DIRECTIONS.put("w", "west");
	}

	public void initializePlayer( Player player ) {
		this.player = player;
	}

	public static Commands getInstance() {
		return INSTANCE;
	}

	// default command methods go here. all known by player

	@Command(command = "help", aliases = "h", description = "Prints help", debug = false)
	public void command_help() {
		Method[] methods = Commands.class.getMethods();
		int commandW = 0;
		int descriptionW = 0;
		VaultLogger.say("");
		for (Method method : methods) {
			if (!method.isAnnotationPresent(Command.class)) {
				continue;
			}
			Command annotation = method.getAnnotation(Command.class);
			String command = annotation.command() + "( " + annotation.aliases() + "):";
			String description = annotation.description();
			if (command.length() > commandW) {
				commandW = command.length();
			}
			if (description.length() > descriptionW) {
				descriptionW = description.length();
			}
		}
		for (Method method : methods) {
			if (!method.isAnnotationPresent(Command.class)) {
				continue;
			}
			Command annotation = method.getAnnotation(Command.class);
			String command = (annotation.aliases().length() == 0) ? annotation.command()
			        : annotation.command() + " (" + annotation.aliases() + "):";
			String message = String.format("%-" + commandW + "s %-" + descriptionW + "S", command,
			        annotation.description());
			if (annotation.debug()) {
				if ("test".equals(player.getName())) {
					VaultLogger.say(message);
				}
			} else {
				VaultLogger.say(message);
			}
		}
	}

	@Command(command = "save", aliases = "s", description = "Save the game.", debug = false)
	public void command_save() {
		logger.info("Saving in progress...");
		player.save();
		logger.info("Saving complete.");
	}

	@Command(command = "inspect", aliases = "i", description = "Inspect an item.", debug = false)
	public void command_inspect( String arg ) {
		player.inspectItem(arg.trim());
	}

	@Command(command = "equip", aliases = "e", description = "Equip an item.", debug = false)
	public void command_equip( String arg ) {
		player.equipItem(arg.trim());
	}

	@Command(command = "unequip", aliases = "ue", description = "Unequip an item.", debug = false)
	public void command_unequip( String arg ) {
		player.dequipItem(arg.trim());
	}

	@Command(command = "pickup", aliases = "pu", description = "Pickup an Item.", debug = false)
	public void command_pickup( String arg ) {
		player.pickUpItem(arg.trim());
	}

	@Command(command = "drop", aliases = "d", description = "Drop an item.", debug = false)
	public void command_drop( String arg ) {
		player.dropItem(arg.trim());
	}

	@Command(command = "attack", aliases = "att", description = "Attack an Entity. (NPC, Enemy)", debug = false)
	public void command_attack( String arg ) throws DeathException {
		player.attack(arg.trim());
	}

	@Command(command = "look", aliases = "l", description = "Look around. (Description of room)", debug = false)
	public void command_look( String arg ) {
		player.getLocation().print();
	}

	@Command(command = "talk", aliases = "t", description = "Talks to a character. (NPC)", debug = false)
	public void command_talk( String arg ) throws DeathException {
		ChatManager cm = new ChatManager();
		List<NPC> npcs = player.getLocation().getNPCs();
		NPC npc = null;
		for (NPC n : npcs) {
			if (n.getName().equalsIgnoreCase(arg)) {
				npc = n;
			}
		}
		if (npc != null) {
			cm.startConversation(npc, player);
		} else {
			VaultLogger.say("Unable to to talk to '" + arg + "'.");
		}
	}

	@Command(command = "view", aliases = "v", description = "View details. (stats, equipped, backpack)", debug = false)
	public void command_view( String arg ) {
		arg = arg.trim();
		switch (arg) {
			case "s":
			case "stats":
				player.getStats();
				break;
			case "e":
			case "equipped":
				player.printEquipment();
				break;
			case "b":
			case "backpack":
				player.printStorage();
				break;
			default:
				VaultLogger.say("That is not a valid option.");
				break;
		}
	}

	@Command(command = "enemies", aliases = "e", description = "Find any enemies around you.", debug = false)
	public void command_enemies() {
		List<Enemy> enemyList = player.getLocation().getEnemies();
		if (enemyList.size() > 0) {
			VaultLogger.say("Enemies around you are: ");
			VaultLogger.say("------------------------------");
			for (Enemy enemy : enemyList) {
				VaultLogger.say(enemy.enemyType);
			}
			VaultLogger.say("------------------------------");
		} else {
			VaultLogger.say("There are no enemies around you\n");
		}
	}

	@Command(command = "go", aliases = "g", description = "Go in a certain direction.", debug = false)
	public void command_go( String arg ) throws DeathException {
		ILocation loc = player.getLocation();

		try {
			arg = DIRECTIONS.get(arg);
			Direction dir = Direction.valueOf(arg.toUpperCase());
			Map<Direction, ILocation> exits = loc.getExits();
			if (exits.containsKey(dir)) {
				ILocation newLoc = exits.get(Direction.valueOf(arg.toUpperCase()));
				if (!newLoc.getLocationType().equals(Locations.WALL)) {
					player.setLocation(newLoc);
					if ("test".equals(player.getName())) {
						VaultLogger.say(player.getLocation().getCoordinate().toString());
					}
					player.getLocation().print();
					Random rand = new Random();
					if (player.getLocation().getEnemies().size() == 0) {
						EnemyGenerator enemyGen = new EnemyGenerator();
						int goUp = rand.nextInt(player.getLocation().getDangerRating() + 1);
						for (int i = 0; i < goUp; i++) {
							Enemy enemy = enemyGen.generateEnemy(player);
							player.getLocation().addEnemy(enemy);
						}
					}
					if (player.getLocation().getItems().size() == 0) {
						int chance = rand.nextInt(100);
						if (chance < 60) {
							addItemToLocation();
						}
					}
					if (rand.nextDouble() < 0.5) {
						List<Enemy> enemies = player.getLocation().getEnemies();
						if (enemies.size() > 0) {
							int enemyPosition = rand.nextInt(enemies.size());
							String enemy = enemies.get(enemyPosition).enemyType;
							VaultLogger.say("A " + enemy + " is attacking you!!");
							player.attack(enemy);
						}
					}
				} else {
					VaultLogger.say("You cannot walk through a wall!");
				}
			} else {
				VaultLogger.say("The exit is not this way.");
			}
		} catch (IllegalArgumentException e) {
			VaultLogger.say("That direction does not exist!");
		} catch (NullPointerException e) {
			VaultLogger.say("That direction does not exist!");
		}
	}

	// debug commands go here. only available if player's name is test
	@Command(command = "attack", aliases = "", description = "Adjusts player's damage level.", debug = true)
	public void command_attackChange( String arg ) {
		double damage = Double.parseDouble(arg);
		player.setDmg(damage);
	}

	@Command(command = "maxhealth", aliases = "", description = "Adjusts player's maximum HP.", debug = true)
	public void command_maxHP( String arg ) {
		int maxHP = Integer.parseInt(arg);
		if (maxHP > 0) {
			player.setMaxHP(maxHP);
		} else {
			VaultLogger.say("Maximum HP must be positive!");
		}
	}

	@Command(command = "health", aliases = "", description = "Adjusts the player's HP levels.", debug = true)
	public void command_health( String arg ) {
		int HP = Integer.parseInt(arg);
		if (HP > 0) {
			player.setHP(HP);
		} else {
			VaultLogger.say("HP must be positive!");
		}
	}

	@Command(command = "armor", aliases = "", description = "Adjusts the player's armor levels.", debug = true)
	public void command_armor( String arg ) {
		int armour = Integer.parseInt(arg);
		player.setArmor(armour);
	}

	@Command(command = "level", aliases = "", description = "Adjusts the player's level.", debug = true)
	public void command_level( String arg ) {
		int lvl = Integer.parseInt(arg);
		player.setLvl(lvl);
	}

	@Command(command = "gold", aliases = "", description = "Adjusts the player's gold level.", debug = true)
	public void command_gold( String arg ) {
		int gold = Integer.parseInt(arg);
		player.setGold(gold);
	}

	@Command(command = "backpack", aliases = "", description = "Opens the backpack debug menu.", debug = true)
	public void command_backpack( String arg ) {
		new PackDebugMenu(player);
	}

	@Command(command = "teleport", aliases = "", description = "Moves the player to a certain location. (Specify coordinates)", debug = true)
	public void command_teleport( String arg ) {
		WorldBank worldBank = Beans.getLocationRepository(player.getName());
		ILocation newLoc = worldBank.getLocation(new Coordinate(arg));
		ILocation oldLoc = player.getLocation();
		try {
			player.setLocation(newLoc);
			player.getLocation().print();
		} catch (NullPointerException npe) {
			player.setLocation(oldLoc);
			VaultLogger.say("Location with specified coordinates does not exist!");
		}
	}

	// not commands. extra methods.
	private void addItemToLocation() {
		ItemBank itemBank = Beans.getItemRepository();
		if (player.getHP() < player.getMaxHP() / 3) {
			player.getLocation().addItem(itemBank.getRandomFood(player.getLvl()));
		} else {
			Random rand = new Random();
			int startIndex = rand.nextInt(3);
			switch (startIndex) {
				case 0:
					player.getLocation().addItem(itemBank.getRandomWeapon(player.getLvl()));
					break;
				case 1:
					player.getLocation().addItem(itemBank.getRandomFood(player.getLvl()));
					break;
				case 2:
					player.getLocation().addItem(itemBank.getRandomArmor(player.getLvl()));
					break;
				case 3:
					player.getLocation().addItem(itemBank.getRandomPotion(player.getLvl()));
					break;
			}
		}
	}
}