package entities;

import java.util.*;

import databanks.*;
import items.*;
import main.*;

/**
 * SUPERclass for everything within an entity
 * @author Jon
 */
public abstract class Entity {
	protected ItemBank itemBank = Beans.getItemRepository();
	/**
	 * 1. Can Attack
	 * 2. Have Health
	 * 3. Have Names
	 */
	private int maxHP;
	private int HP;
	private int lvl;
	private String name;
	private String intro;

	// Extra stats
	private double dmg = 30;
	private double critChance = 0.0;
	private int strength, intel, dex, luck, gold, armor;
	private Map<EquipmentLocation, Item> equipment;
	protected Storage storage;

	// Default Weapon 
	private String weapon = "hands";

	public Entity() {
		this(100, 100, "default", 0, null, new HashMap<EquipmentLocation, Item>());
	}

	public Entity(int maxHP, int HP, String name, int gold, Storage storage, Map<EquipmentLocation, Item> equipment) {
		this.maxHP = maxHP;
		this.HP = HP;
		this.name = name;
		this.gold = gold;
		this.equipment = equipment;
		if (storage != null) {
			this.storage = storage;
		} else {
			this.storage = new Storage(150);
		}
	}

	/*
	 * Getters
	 */
	public int getHP() {
		return HP;
	}

	public int getMaxHP() {
		return maxHP;
	}

	public int getGold() {
		return gold;
	}

	public double getDmg() {
		return dmg;
	}

	public double getCritChance() {
		return critChance;
	}

	public int getArmor() {
		return armor;
	}

	public String getName() {
		return name;
	}

	public String getIntro() {
		return intro;
	}

	public int getLvl() {
		return lvl;
	}

	public Map<EquipmentLocation, Item> getEquipment() {
		return Collections.unmodifiableMap(equipment);
	}

	public int getStrength() {
		return strength;
	}

	public int getDex() {
		return dex;
	}

	public int getIntel() {
		return intel;
	}

	public int getLuck() {
		return luck;
	}

	public Storage getStorage() {
		return storage;
	}

	public String getWeapon() {
		return weapon;
	}

	/*
	 * Setters
	 */
	public void setHP( int hP ) {
		this.HP = hP;
		if (HP > maxHP) {
			HP = maxHP;
		}
	}

	public void setMaxHP( int maxHP ) {
		this.maxHP = maxHP;
		if (HP > maxHP) {
			HP = maxHP;
		}
	}

	public void setGold( int gold ) {
		this.gold = gold;
	}

	public void setDmg( double dmg ) {
		this.dmg = dmg;
	}

	public void setCritChance( double critChance ) {
		this.critChance = critChance;
	}

	public void setArmor( int armor ) {
		this.armor = armor;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public void setIntro( String intro ) {
		this.intro = intro;
	}

	public void setLvl( int lvl ) {
		this.lvl = lvl;
	}

	public void setEquipment( Map<EquipmentLocation, Item> equipment ) {
		this.equipment = equipment;
	}

	public void setStrength( int strength ) {
		this.strength = strength;
	}

	public void setDex( int dex ) {
		this.dex = dex;
	}

	public void setIntel( int intel ) {
		this.intel = intel;
	}

	public void setLuck( int luck ) {
		this.luck = luck;
	}

	public void setStorage( Storage storage ) {
		this.storage = storage;
	}

	/**  
	 * Equipping an Item
	 */
	public Map<String, String> equipItem( EquipmentLocation place, Item item ) {
		double oldDmg = this.dmg;
		int oldArmor = this.armor;
		if (place == null) {
			place = item.getPosition();
		}
		if (equipment.get(place) != null) {
			unequipItem(equipment.get(place));
		}
		if (place == EquipmentLocation.BOTH_HANDS) {
			unequipTwoPlaces(EquipmentLocation.LEFT_HAND, EquipmentLocation.RIGHT_HAND);
		} else if (place == EquipmentLocation.BOTH_ARMS) {
			unequipTwoPlaces(EquipmentLocation.LEFT_ARM, EquipmentLocation.RIGHT_ARM);
		}
		Item bothHands = equipment.get(EquipmentLocation.BOTH_HANDS);
		if (bothHands != null && (EquipmentLocation.LEFT_HAND == place || place == EquipmentLocation.RIGHT_HAND)) {
			unequipItem(bothHands);
		}
		Item bothArms = equipment.get(EquipmentLocation.BOTH_ARMS);
		if (bothArms != null && (EquipmentLocation.LEFT_ARM == place || place == EquipmentLocation.RIGHT_ARM)) {
			unequipItem(bothArms);
		}
		equipment.put(place, item);
		removeItemFromStorage(item);
		Map<String, String> result = new HashMap<String, String>();
		switch (item.getID().charAt(0)) {
			/*
			 * Case for Random Weapon 
			 */
			case 'W': {
				this.weapon = item.getID();
				this.dmg += item.getProperty("damage");
				double differentDmg = this.dmg - oldDmg;
				result.put("damage", String.valueOf(differentDmg));
				break;
			}
			/*
			 * Case for Armor
			 */
			case 'A': {
				this.armor += item.getProperty("armor");
				int differentArmor = this.armor - oldArmor;
				result.put("armor", String.valueOf(differentArmor));
				break;
			}
			/*
			 * Case for Potion
			 */
			case 'P': {
				if (item.containsProperty("maxHP")) {
					int oldHP = this.getHP();
					this.maxHP += item.getProperty("maxHP");
					this.HP += item.getProperty("HP");
					this.HP = (this.HP > this.maxHP) ? this.maxHP : this.HP;
					int hpNew = this.HP;
					unequipItem(item); // One use item only
					removeItemFromStorage(item);
					if (hpNew != oldHP) {
						result.put("HP", String.valueOf(HP - oldHP));
					} else {
						result.put("HP", String.valueOf(item.getProperty("maxHP")));
					}
				}
				break;
			}
			/*
			 * Case for Food
			 */
			case 'F': {
				int oldHP = this.getHP();
				this.HP += item.getProperty("HP");
				this.HP = (this.HP > this.maxHP) ? this.maxHP : this.HP;
				unequipItem(item); // One use time only
				removeItemFromStorage(item);
				result.put("HP", String.valueOf(HP - oldHP));
				break;
			}
		}
		return result;
	}

	public Map<String, String> unequipItem( Item item ) {
		for (EquipmentLocation key : equipment.keySet()) {
			if (item.equals(equipment.get(key))) {
				equipment.put(key, null);
			}
		}
		Map<String, String> result = new HashMap<String, String>();
		if (item.containsProperty("damage")) {
			double oldDmg = dmg;
			weapon = "hands";
			dmg -= item.getProperty("damage");
			double differentDmg = dmg - oldDmg;
			result.put("damage", String.valueOf(differentDmg));
		}
		if (item.containsProperty("armor")) {
			int oldArmor = armor;
			armor -= item.getProperty("armor");
			int differentArmor = armor - oldArmor;
			result.put("armor", String.valueOf(differentArmor));
		}
		return result;
	}

	private void unequipTwoPlaces( EquipmentLocation leftLocation, EquipmentLocation rightLocation ) {
		Item left = equipment.get(leftLocation);
		Item right = equipment.get(rightLocation);
		if (left != null) {
			unequipItem(left);
		}
		if (right != null) {
			unequipItem(right);
		}
	}

	public void printEquipment() {
		VaultLogger.say("\n------------------------------------------------------------");
		VaultLogger.say("Equipped Items:");
		if (equipment.keySet().size() == 0) {
			VaultLogger.say("***Empty***");
		} else {
			int i = 0;
			Item hands = itemBank.getItem("hands");
			Map<EquipmentLocation, String> locs = new HashMap<>();
			locs.put(EquipmentLocation.HEAD, "Head");
			locs.put(EquipmentLocation.FACE, "Face");
			locs.put(EquipmentLocation.CHEST, "Chest");
			locs.put(EquipmentLocation.BACK, "Back");
			locs.put(EquipmentLocation.LEFT_ARM, "Left arm");
			locs.put(EquipmentLocation.LEFT_HAND, "Left hand");
			locs.put(EquipmentLocation.RIGHT_ARM, "Right arm");
			locs.put(EquipmentLocation.RIGHT_HAND, "Right hand");
			locs.put(EquipmentLocation.BOTH_HANDS, "Both hands");
			locs.put(EquipmentLocation.BOTH_ARMS, "Both arms");
			locs.put(EquipmentLocation.LEGS, "Legs");
			locs.put(EquipmentLocation.FEET, "Feet");
			for (Map.Entry<EquipmentLocation, Item> item : equipment.entrySet()) {
				if (item.getKey() != null && !hands.equals(item.getValue()) && item.getValue() != null) {
					VaultLogger.say(locs.get(item.getKey()) + " - " + item.getValue().getName());
				} else {
					i++;
				}
			}
			if (i == equipment.keySet().size()) {
				VaultLogger.say("***Empty***");
			}
		}
		VaultLogger.say("------------------------------------------------------------");
	}

	/*
	 * Extra Storage Shit
	 */
	public void printStorage() {
		storage.display();
	}

	public void addItemToStorage( Item item ) {
		storage.addItem(new ItemStack(1, item));
	}

	public void removeItemFromStorage( Item item ) {
		storage.removeItem(new ItemStack(1, item));
	}
}