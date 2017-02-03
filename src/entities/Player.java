package entities;

import java.io.*;
import java.util.*;
import java.util.Map.*;

import com.google.gson.*;
import com.google.gson.reflect.*;

import databanks.*;
import enemies.*;
import items.*;
import main.*;
import menus.*;
import nav.*;

/**
 * This class deals with the player and all of its properties.
 * Any method that changes a character or interacts with it goes here.
 */
public class Player extends Entity {
	// @Resources
	protected static ItemBank itemBank = Beans.getItemRepository();
	protected static WorldBank worldBank = Beans.getLocationRepository();
	private ILocation loc;

	private int xp;

	private String type;
	private static HashMap<String, Integer> characterLvls = new HashMap<String, Integer>();

	// singleton pattern. only 1 instance allowed (of the player)
	private static Player player;

	public Player() {}

	public static Player getInstance( String playerClass ) {
		player = new Player();
		JsonParser parser = new JsonParser();
		String fileName = "json/npcs.json";
		Random rand = new Random();
		int luck = rand.nextInt(3) + 1;

		try {
			Reader reader = new FileReader(fileName);
			JsonObject npcs = parser.parse(reader).getAsJsonObject().get("npcs").getAsJsonObject();
			JsonObject json = new JsonObject();
			for (Map.Entry<String, JsonElement> entry : npcs.entrySet()) {
				if (entry.getKey().equals(playerClass)) {
					json = entry.getValue().getAsJsonObject();
				}
			}
			// setting all of the player's stats & info
			player.setName(json.get("name").getAsString());
			player.setXP(json.get("xp").getAsInt());
			player.setMaxHP(json.get("maxHP").getAsInt());
			player.setHP(json.get("HP").getAsInt());
			player.setArmor(json.get("armor").getAsInt());
			player.setDmg(json.get("dmg").getAsDouble());
			player.setStrength(json.get("strength").getAsInt());
			player.setDex(json.get("dex").getAsInt());
			player.setIntel(json.get("intel").getAsInt());
			setUpVariables(player);

			JsonArray items = json.get("items").getAsJsonArray();
			for (JsonElement item : items) {
				//player.addItemToStorage(item);
				player.addItemToStorage(itemBank.getItem(item.getAsString()));
			}

			player.setLuck(luck);
			player.setIntro(json.get("intro").getAsString());
			if (player.getName().equals("Soldier")) {
				player.type = "Soldier";
			} else if (player.getName().equals("Archer")) {
				player.type = "Archer";
			} else {
				VaultLogger.say("Not a valid class!!!");
			}
			reader.close();

			setupCharacterLvls();
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return player;
	}

	public void save() {
		Gson gson = new Gson();
		JsonObject jsonObject = new JsonObject();

		jsonObject.addProperty("name", getName());
		jsonObject.addProperty("maxHP", getMaxHP());
		jsonObject.addProperty("HP", getHP());
		jsonObject.addProperty("armor", getArmor());
		jsonObject.addProperty("dmg", getDmg());
		jsonObject.addProperty("lvl", getLvl());
		jsonObject.addProperty("xp", getXP());
		jsonObject.addProperty("strength", getStrength());
		jsonObject.addProperty("intel", getIntel());
		jsonObject.addProperty("dex", getDex());
		jsonObject.addProperty("luck", getLuck());
		jsonObject.addProperty("weapon", getWeapon());
		jsonObject.addProperty("type", getCurrentCharacterType());

		HashMap<String, Integer> items = new HashMap<String, Integer>();
		for (ItemStack item : getStorage().getItemStack()) {
			/**
			 * MAJOR ERROR
			 */
//			if(item != null) {
//				items.put(item.getItem().getID(), item.getAmount());
//			}

		}

		JsonElement itemsJsonObject = gson.toJsonTree(items);
		jsonObject.add("items", itemsJsonObject);
		Map<EquipmentLocation, String> locs = new HashMap<>();
		locs.put(EquipmentLocation.HEAD, "head");
		locs.put(EquipmentLocation.FACE, "face");
		locs.put(EquipmentLocation.CHEST, "chest");
		locs.put(EquipmentLocation.BACK, "back");
		locs.put(EquipmentLocation.LEFT_ARM, "leftArm");
		locs.put(EquipmentLocation.LEFT_HAND, "leftHand");
		locs.put(EquipmentLocation.RIGHT_ARM, "rightArm");
		locs.put(EquipmentLocation.RIGHT_HAND, "rightHand");
		locs.put(EquipmentLocation.BOTH_HANDS, "BothHands");
		locs.put(EquipmentLocation.BOTH_ARMS, "bothArms");
		locs.put(EquipmentLocation.LEGS, "legs");
		locs.put(EquipmentLocation.FEET, "feet");

		HashMap<String, String> equipment = new HashMap<>();
		Item hands = itemBank.getItem("hands");
		for (Map.Entry<EquipmentLocation, Item> item : getEquipment().entrySet()) {
			if (item.getKey() != null & !hands.equals(item.getValue()) && item.getValue() != null) {
				equipment.put(locs.get(item.getKey()), item.getValue().getID());
			}
		}

		JsonElement equipmentJsonObject = gson.toJsonTree(equipment);
		jsonObject.add("equipment", equipmentJsonObject);
		JsonElement typesJsonObject = gson.toJsonTree(getCharacterLvls());
		jsonObject.add("type", typesJsonObject);
		Coordinate coord = getLocation().getCoordinate(); // ERROR (maybe fixed)
		String coordLoc = coord.x + "," + coord.y + "," + coord.z;
		jsonObject.addProperty("location", coordLoc);

		String fileName = getProfileFileName(getName());
		new File(fileName).getParentFile().mkdirs();
		try {
			Writer writer = new FileWriter(fileName);
			gson.toJson(jsonObject, writer);
			writer.close();
			worldBank = Beans.getLocationRepository(getName());
			worldBank.writeLocations();
			VaultLogger.say("\nYour game data was saved.");
		} catch (IOException ioe) {
			VaultLogger.say("\nUnable to save your game data to file '" + fileName + "'.");
		}
	}

	private String getNullAsEmptyString( JsonElement jsonElement ) {
		return jsonElement.isJsonNull() ? "" : jsonElement.getAsString();
	}

	public static Player load( String name ) {
		player = new Player();
		JsonParser parser = new JsonParser();
		String fileName = getProfileFileName(name);
		try {
			Reader reader = new FileReader(fileName);
			JsonObject json = parser.parse(reader).getAsJsonObject();

			player.setName(json.get("name").getAsString());
			player.setMaxHP(json.get("maxHP").getAsInt());
			player.setHP(json.get("HP").getAsInt());
			player.setArmor(json.get("armor").getAsInt());
			player.setDmg(json.get("dmg").getAsInt());
			player.setLvl(json.get("lvl").getAsInt());
			player.setXP(json.get("xp").getAsInt());
			player.setStrength(json.get("strength").getAsInt());
			player.setIntel(json.get("intel").getAsInt());
			player.setDex(json.get("dex").getAsInt());
			player.setLuck(json.get("luck").getAsInt());

			player.setCurrentCharacterType(json.get("type").toString());

			HashMap<String, Integer> charLvls = new Gson().fromJson(json.get("types"),
			        new TypeToken<HashMap<String, Integer>>()
					{}.getType());

			player.setCharacterLvls(charLvls);
			if (json.has("equipment")) {
				Map<String, EquipmentLocation> locations = new HashMap<>();
				locations.put("head", EquipmentLocation.HEAD);
				locations.put("face", EquipmentLocation.FACE);
				locations.put("chest", EquipmentLocation.CHEST);
				locations.put("back", EquipmentLocation.BACK);
				locations.put("leftArm", EquipmentLocation.LEFT_ARM);
				locations.put("leftHand", EquipmentLocation.LEFT_HAND);
				locations.put("rightArm", EquipmentLocation.RIGHT_ARM);
				locations.put("rightHand", EquipmentLocation.RIGHT_HAND);
				locations.put("bothHands", EquipmentLocation.BOTH_HANDS);
				locations.put("bothArms", EquipmentLocation.BOTH_ARMS);
				locations.put("legs", EquipmentLocation.LEGS);
				locations.put("feet", EquipmentLocation.FEET);
				HashMap<String, String> equipment = new Gson().fromJson(json.get("equipment"),
				        new TypeToken<HashMap<String, String>>()
						{}.getType());

				Map<EquipmentLocation, Item> equipmentMap = new HashMap<>();
				for (Map.Entry<String, String> entry : equipment.entrySet()) {
					EquipmentLocation equipmentLoc = locations.get(entry.getKey());
					Item item = itemBank.getItem(entry.getValue());
					equipmentMap.put(equipmentLoc, item);
				}
				player.setEquipment(equipmentMap);
			}
			if (json.has("items")) {
				HashMap<String, Integer> items = new Gson().fromJson(json.get("items"),
				        new TypeToken<HashMap<String, Integer>>()
						{}.getType());
				ArrayList<ItemStack> itemList = new ArrayList<>();
				for (Map.Entry<String, Integer> entry : items.entrySet()) {
					String itemID = entry.getKey();
					int amount = entry.getValue();
					Item item = itemBank.getItem(itemID);
					ItemStack itemStack = new ItemStack(amount, item);
					itemList.add(itemStack);
				}
				float maxWeight = (float) Math.sqrt(player.getStrength() * 150);
				player.setStorage(new Storage(maxWeight, itemList));
			}
			Coordinate coord = new Coordinate(json.get("location").getAsString());
			worldBank = Beans.getLocationRepository(player.getName());
			player.setLocation(worldBank.getLocation(coord));
			reader.close();
			//setupCharacterLvls();
			//Main.prompt(player); 
		} catch (FileNotFoundException fnfe) {
			VaultLogger.say("Unable to open file '" + fileName + "'.");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return player;
	}

	public List<Item> searchItem( String itemName, List<Item> itemList ) {
		List<Item> items = new ArrayList<>();
		for (Item item : itemList) {
			String testItemName = item.getName();
			if (testItemName.equalsIgnoreCase(itemName)) {
				items.add(item);
			}
		}
		return items;
	}

	public List<Item> searchItem( String itemName, Storage storage ) {
		return storage.search(itemName);
	}

	public List<Item> searchEquipment( String itemName, Map<EquipmentLocation, Item> equipment ) {
		List<Item> items = new ArrayList<>();
		for (Item item : equipment.values()) {
			if (item != null && item.getName().equals(itemName)) {
				items.add(item);
			}
		}
		return items;
	}

	public void pickUpItem( String itemName ) {
		List<Item> items = searchItem(itemName, getLocation().getItems());
		if (!items.isEmpty()) {
			Item item = items.get(0);
			addItemToStorage(item);
			loc.removeItem(item);
			VaultLogger.say("You picked up " + item.getName() + ".");
		}
	}

	public void dropItem( String itemName ) {
		List<Item> itemMap = searchItem(itemName, getStorage());
		if (itemMap.isEmpty()) {
			itemMap = searchEquipment(itemName, getEquipment());
		}
		if (!itemMap.isEmpty()) {
			Item item = itemMap.get(0);
			Item itemToDrop = itemBank.getItem(item.getID());
			Item weapon = itemBank.getItem(getWeapon());
			String Name = weapon.getName();

			if (itemName.equals(Name)) {
				dequipItem(Name);
			}
			removeItemFromStorage(itemToDrop);
			loc.addItem(itemToDrop);
			VaultLogger.say("You dropped " + item.getName() + ".");
		}
	}

	public void equipItem( String itemName ) {
		List<Item> items = searchItem(itemName, getStorage());
		if (!items.isEmpty()) {
			Item item = items.get(0);
			if (getLvl() >= item.getLvl()) {
				Map<String, String> change = equipItem(item.getPosition(), item);
				VaultLogger.say("You equipped " + item.getName() + ".");
				printStatChange(change);
			} else {
				VaultLogger.say("You do not have the required level to use this item!");
			}
		} else {
			VaultLogger.say("You do not have that item!");
		}
	}

	public void dequipItem( String itemName ) {
		List<Item> items = searchEquipment(itemName, getEquipment());
		if (!items.isEmpty()) {
			Item item = items.get(0);
			Map<String, String> change = unequipItem(item);
			VaultLogger.say("You unequipped " + item.getName() + ".");
			printStatChange(change);
		}
	}

	private void printStatChange( Map<String, String> stats ) {
		Set<Entry<String, String>> set = stats.entrySet();
		Iterator<Entry<String, String>> iter = set.iterator();

		while (iter.hasNext()) {
			Entry<String, String> me = iter.next();
			double value = Double.parseDouble((String) me.getValue());
			switch ((String) me.getKey()) {
				case "dmg": {
					if (value >= 0.0) {
						VaultLogger.say(me.getKey() + ": " + this.getDmg() + " [+" + me.getValue() + "]");
					} else {
						VaultLogger.say(me.getKey() + ": " + this.getDmg() + " [" + me.getValue() + "]");
					}
					break;
				}
				case "armor": {
					if (value >= 0) {
						VaultLogger.say(me.getKey() + ": " + this.getArmor() + " [+" + me.getValue() + "]");
					} else {
						VaultLogger.say(me.getKey() + ": " + this.getArmor() + " [" + me.getValue() + "]");
					}
					break;
				}
				case "hp": {
					if (value >= 0) {
						VaultLogger.say(me.getKey() + ": " + this.getHP() + " [+" + me.getValue() + "]");
					} else {
						VaultLogger.say(me.getKey() + ": " + this.getHP() + " [" + me.getValue() + "]");
					}
					break;
				}
				case "maxHP": {
					if (value >= 0) {
						VaultLogger.say(me.getKey() + ": " + this.getMaxHP() + " [+" + me.getValue() + "]");
					} else {
						VaultLogger.say(me.getKey() + ": " + this.getMaxHP() + " [" + me.getValue() + "]");
					}
					break;
				}
			}
		}
	}

	public void inspectItem( String itemName ) {
		List<Item> itemMap = searchItem(itemName, getStorage());
		if (itemMap.isEmpty()) {
			itemMap = searchItem(itemName, getLocation().getItems());
		}
		if (!itemMap.isEmpty()) {
			Item item = itemMap.get(0);
			item.display();
		} else {
			VaultLogger.say("Item doesn't exist within your reach.");
		}
	}

	public void attack( String oppponentName ) throws DeathException {
		Enemy enemyOpponent = null;
		NPC npcOpponent = null;
		List<Enemy> enemies = getLocation().getEnemies();
		List<NPC> npcs = getLocation().getNPCs();

		for (int i = 0; i < enemies.size(); i++) {
			if (enemies.get(i).enemyType.equalsIgnoreCase(oppponentName)) {
				enemyOpponent = enemies.get(i);
			}
		}
		for (int i = 0; i < npcs.size(); i++) {
			if (npcs.get(i).getName().equalsIgnoreCase(oppponentName)) {
				npcOpponent = npcs.get(i);
			}
		}
		if (enemyOpponent != null) {
			enemyOpponent.setName(enemyOpponent.enemyType);
			new AttackMenu(enemyOpponent, this);
		} else if (npcOpponent != null) {
			new AttackMenu(npcOpponent, this);
		} else {
			VaultLogger.say("Opponent not found.");
		}
	}

	public boolean hasItem( Item item ) {
		List<Item> searchEquipment = searchEquipment(item.getName(), getEquipment());
		List<Item> searchStorage = searchItem(item.getName(), getStorage());

		return !(searchEquipment.size() == 0 && searchStorage.size() == 0);
	}

	public int getXP() {
		return xp;
	}

	public void setXP( int xp ) {
		this.xp = xp;
	}

	public void getStats() {
		Item weapon = itemBank.getItem(getWeapon());
		String weaponName = weapon.getName();
		if (weaponName.equals(null)) {
			weaponName = "hands";
		}
		String message = "\nPlayer name: " + getName();
		message += "\nType: " + type;
		message += "\nCurrent weapon: " + weaponName;
		message += "\nGold: " + getGold();
		message += "\nHealth/Max: " + getHP() + "/" + getMaxHP();
		message += "\nDamage/Armor: " + getDmg() + "/" + getArmor();
		message += "\nStrength: " + getStrength();
		message += "\nIntelligence: " + getIntel();
		message += "\nDexterity: " + getDex();
		message += "\nLuck: " + getLuck();
		message += "\nXP: " + getXP();
		message += "\n" + getName() + "'s level: " + getLvl();
		VaultLogger.say(message);
	}

	public void printBackpack() {
		storage.display();
	}

	public ILocation getLocation() {
		return loc;
	}

	public void setLocation( ILocation location ) {
		this.loc = location;
	}

	public Locations getLocationType() {
		return getLocation().getLocationType();
	}

	public static void setUpVariables( Player player ) {
		float maxWeight = (float) Math.sqrt(player.getStrength() * 150);
		player.setStorage(new Storage(maxWeight));
	}

	protected static void setupCharacterLvls() {
		characterLvls.put("Soldier", 1);
		characterLvls.put("Archer", 1);
		characterLvls.put("Mage", 1);
	}

	public HashMap<String, Integer> getCharacterLvls() {
		return characterLvls;
	}

	public String getCurrentCharacterType() {
		return this.type;
	}

	public int getCharacterLvl( String characterType ) {
		int characterLvl = Player.characterLvls.get(characterType);
		return characterLvl;
	}

	public void setCharacterLvls( HashMap<String, Integer> newCharacterLvls ) {
		Player.characterLvls = newCharacterLvls;
	}

	public void setCurrentCharacterType( String newType ) {
		this.type = newType;
	}

	public void setCharacterLvl( String characterType, int level ) {
		Player.characterLvls.put(characterType, level);
	}

	protected static String getProfileFileName( String name ) {
		return "json/profiles" + name + "_profile.json";
	}

	public static boolean profileExists( String name ) {
		File file = new File(getProfileFileName(name));
		return file.exists();
	}
}