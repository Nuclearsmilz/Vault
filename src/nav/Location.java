package nav;

import java.util.*;

import databanks.*;
import enemies.*;
import entities.*;
import items.*;
import main.*;

public class Location implements ILocation {
	// @Resources
	protected static ItemBank itemBank = Beans.getItemRepository();
	protected static NPCBank npcBank = Beans.getNPCRepository();

	private Coordinate coord;
	private String title;
	private String desc;
	private Locations locType;
	private int dangerRating;
	private Storage storage = new Storage();

	private List<Enemy> enemies = new ArrayList<>();
	private List<NPC> npcs = new ArrayList<>();

	public Location() {

	}

	public Location(Coordinate coordinate, String title, String description, Locations locationType) {
		this.coord = coordinate;
		this.title = title;
		this.desc = description;
		this.locType = locationType;
	}

	public Location(Coordinate coordinate, String title, String description) {
		this.coord = coordinate;
		this.title = title;
		this.desc = description;
	}

	public Coordinate getCoord() {
		return coord;
	}

	public Coordinate getCoordinate() {
		return coord;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return desc;
	}

	public Locations getLocationType() {
		return locType;
	}

	public Storage getStorage() {
		return storage;
	}

	public List<Item> getItems() {
		return storage.getItems();
	}

	public int getDangerRating() {
		return dangerRating;
	}

	public List<Enemy> getEnemies() {
		return enemies;
	}

	public void addNpcs( List<String> npcIDS ) {
		for (String npcID : npcIDS) {
			addNpc(npcID);
		}
	}

	public void addNpc( String npcID ) {
		npcs.add(npcBank.getNPC(npcID));
	}

	public void removeNpc( NPC npc ) {
		for (int i = 0; i < npcs.size(); i++) {
			if (npcs.get(i).equals(npc)) {
				npcs.remove(i);
			}
		}
	}

	public List<NPC> getNPCs() {
		return Collections.unmodifiableList(npcs);
	}

	public void setCoord( Coordinate coord ) {
		this.coord = coord;
	}

	public void setTitle( String title ) {
		this.title = title;
	}

	public void setDesc( String desc ) {
		this.desc = desc;
	}

	public void setLocType( Locations locType ) {
		this.locType = locType;
	}

	public void setDangerRating( int dangerRating ) {
		this.dangerRating = dangerRating;
	}

	public void addItem( Item item ) {
		storage.add(item);
	}

	public Item removeItem( Item item ) {
		return storage.remove(item);
	}

	// The following checks each direction for an exit and adds it to the exits hashmap if it exists
	public Map<Direction, ILocation> getExits() {
		Map<Direction, ILocation> exits = new HashMap<Direction, ILocation>();
		ILocation borderingLoc;
		WorldBank worldBank = Beans.getLocationRepository();
		for (Direction dir : Direction.values()) {
			try {
				borderingLoc = worldBank.getLocation(getCoordinate().getBorderingCoordinate(dir));
				if (borderingLoc.getCoordinate().getZ() == getCoordinate().getZ()) {
					exits.put(dir, borderingLoc);
				} else if (getLocationType().equals(Locations.STAIRS)) {
					exits.put(dir, borderingLoc);
				}
			} catch (BankException rex) {
				// nothing needed here because the exception exists by itself (already has outputs)
			}
		}
		return exits;
	}

	public void addEnemy( Enemy enemy ) {
		if (enemy != null) {
			enemies.add(enemy);
		}
	}

	public void removeEnemy( Enemy enemy ) {
		for (int i = 0; i < enemies.size(); i++) {
			if (enemies.get(i).equals(enemy)) {
				enemies.remove(i);
			}
		}
	}

	public void print() {
		VaultLogger.say("\n" + getTitle() + ":");
		VaultLogger.say("    " + getDescription());

		List<Item> items = getItems();
		if (!items.isEmpty()) {
			VaultLogger.say("Items: ");
			for (Item item : items) {
				VaultLogger.say("    " + item.getName());
			}
		}
		List<NPC> npcs = getNPCs();
		if (!npcs.isEmpty()) {
			VaultLogger.say("NPCs: ");
			for (NPC npc : npcs) {
				VaultLogger.say("    " + npc.getName());
			}
		}
		VaultLogger.say("");
		for (Map.Entry<Direction, ILocation> dir : getExits().entrySet()) {
			VaultLogger.say(dir.getKey().getDesc() + ": ");
			VaultLogger.say("    " + dir.getValue().getDescription());
		}
	}
}