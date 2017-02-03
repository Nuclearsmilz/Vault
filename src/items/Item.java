package items;

import java.util.*;

import entities.*;
import main.*;

/**
 * These new fangled things named items drop from enemies
 * and you can take them with you,
 * and store in your inventory
 * @author Jon
 */
public class Item {
	private final String id;
	private final String type;
	private final String name;
	private final String description;
	private final int level;
	private final Map<String, Integer> properties;
	private final EquipmentLocation position;

	public Item(String id, String type, String name, String description, int level, Map<String, Integer> properties) {
		this(id, type, name, description, null, level, properties);
	}

	public Item(String id, String type, String name, String description, EquipmentLocation position, int level,
	        Map<String, Integer> properties)
	{
		this.id = id;
		this.type = type;
		this.name = name;
		this.description = description;
		this.position = position;
		this.level = level;
		if (properties != null) {
			this.properties = properties;
		} else {
			this.properties = new TreeMap<>();
		}
	}

	public String getID() {
		return id;
	}

	public String getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public int getLvl() {
		return level;
	}

	public EquipmentLocation getPosition() {
		return position;
	}

	public Integer getWeight() {
		if (properties.containsKey("weight")) { return properties.get("weight"); }
		return Integer.valueOf(0); 
	}

	public Map<String, Integer> getProperties() {
		return properties;
	}

	public int getProperty( String property ) {
		if (!properties.containsKey(property)) { return 0; }
		return properties.get(property);
	}

	public boolean containsProperty( String key ) {
		return properties.containsKey(key);
	}

	public boolean equals( Object obj ) {
		if (obj == null) { return false; }
		if (obj instanceof Item) {
			Item i = (Item) obj;
			return name.equals(i.name);
		}
		return false;
	}

	public void display() {
		VaultLogger.say("Name: " + name + "\nDescription: " + description + "\nLevel: " + level);
		for (Map.Entry<String, Integer> entry : properties.entrySet()) {
			VaultLogger.say(entry.getKey() + ": " + entry.getValue());
		}
	}
}