package databanks;

import java.io.*;
import java.util.*;

import com.google.gson.*;
import com.google.gson.stream.*;

import entities.*;
import items.*;

public class ItemBank {
	private static final Map<String, EquipmentLocation> EQUIPMENT_POSITION_MAP = new HashMap<>();
	private static ItemBank itemBank = null;
	private Map<String, Item> itemMap = new HashMap<>();

	static {
		EQUIPMENT_POSITION_MAP.put("head", EquipmentLocation.HEAD);
		EQUIPMENT_POSITION_MAP.put("face", EquipmentLocation.FACE);
		EQUIPMENT_POSITION_MAP.put("chest", EquipmentLocation.CHEST);
		EQUIPMENT_POSITION_MAP.put("back", EquipmentLocation.BACK);
		EQUIPMENT_POSITION_MAP.put("leftArm", EquipmentLocation.LEFT_ARM);
		EQUIPMENT_POSITION_MAP.put("leftHand", EquipmentLocation.LEFT_HAND);
		EQUIPMENT_POSITION_MAP.put("rightArm", EquipmentLocation.RIGHT_ARM);
		EQUIPMENT_POSITION_MAP.put("rightHand", EquipmentLocation.RIGHT_HAND);
		EQUIPMENT_POSITION_MAP.put("bothArms", EquipmentLocation.BOTH_ARMS);
		EQUIPMENT_POSITION_MAP.put("bothHands", EquipmentLocation.BOTH_HANDS);
		EQUIPMENT_POSITION_MAP.put("legs", EquipmentLocation.LEGS);
		EQUIPMENT_POSITION_MAP.put("feet", EquipmentLocation.FEET);
	}

	// Loads all items from the json file specified
	protected void load( File repo ) {
		try {
			JsonReader reader = new JsonReader(new FileReader(repo));
			load(reader);
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		}
	}

	// Loads all items from the JsonReader specified
	protected void load( JsonReader reader ) {
		JsonObject jsonItems = new JsonObject();
		try {
			JsonParser parser = new JsonParser();
			JsonObject json = parser.parse(reader).getAsJsonObject();
			jsonItems = json.get("items").getAsJsonObject();
			reader.close();
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		for (Map.Entry<String, JsonElement> entry : jsonItems.entrySet()) {
			String id = entry.getKey();
			JsonObject itemData = entry.getValue().getAsJsonObject();
			String type = itemData.get("type").getAsString();
			String name = itemData.get("name").getAsString();
			String description = itemData.get("description").getAsString();
			int level = itemData.get("level").getAsInt();
			JsonObject sProps = itemData.get("properties").getAsJsonObject();
			Map<String, Integer> properties = new TreeMap<>();
			EquipmentLocation position = EQUIPMENT_POSITION_MAP.get(itemData.get("position").getAsString());

			for (Map.Entry<String, JsonElement> entry2 : sProps.entrySet()) {
				Integer propValue = entry2.getValue().getAsInt();
				properties.put(entry2.getKey(), propValue);
			}
			addItem(new Item(id, type, name, description, position, level, properties));
		}
	}

	public Item getItem( String id ) {
		if (id == null || id.trim().length() == 0) { return null; }
		if (!itemMap.containsKey(id)) { throw new BankException(
		        "Argument 'id' with value '" + id + "' not found in repository."); }
		return itemMap.get(id);
	}

	void addItem( Item item ) {
		itemMap.put(item.getID(), item);
	}

	public static ItemBank createRepo() {
		if (itemBank == null) {
			File file = new File(new File(System.getProperty("user.dir")), "json");

			File dataFile = new File(new File(file, "data"), "items.json");
			if (!dataFile.exists()) { throw new RuntimeException("File '" + dataFile + "' does not exist."); }

			itemBank = new ItemBank();
			itemBank.load(dataFile);
		}
		return itemBank;
	}

	public void store( JsonWriter jsonWriter ) {
		GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
		Gson gson = builder.create();
		Map<String, Map<String, Item>> root = new TreeMap<>();
		root.put("items", itemMap);
		gson.toJson(root, Map.class, jsonWriter);
	}

	public void retrieve( JsonReader jsonReader ) {
		load(jsonReader);
	}

	public Item getRandomWeapon( int level ) {
		return getRandomItem("W", level);
	}

	public Item getRandomArmor( int level ) {
		return getRandomItem("A", level);
	}

	public Item getRandomPotion( int level ) {
		return getRandomItem("P", level);
	}

	public Item getRandomFood( int level ) {
		return getRandomItem("F", level);
	}

	public Item getRandomItem( String start, int level ) {
		Random rand = new Random();
		int chance = rand.nextInt(100);
		if (chance < 70) {
			Item item = null;
			do {
				item = getRandom(start);
			} while (item.getLvl() > level);
			return item;
		} else {
			return getRandom(start);
		}
	}

	public Item getRandom( String start ) {
		Random rand = new Random();
		Item item = null;
		do {
			int itemIndex = rand.nextInt(itemMap.size() - 2);
			List<Item> items = new ArrayList<>(itemMap.values());
			item = items.get(itemIndex + 2); // avoids empty and hands items
		} while (!item.getID().startsWith(start));
		return item;
	}
}