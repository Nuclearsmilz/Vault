package entities;

import java.util.*;

import com.google.gson.*;

/**
 * Handles ALL of the NPC's properties and methods.
 * ANYTHING that cannot be controlled by the user is a 
 * NON PLAYABLE CHARACTER (NPC)
 * Extends Entity superclass.
 * @author Jon
 */
public class NPC extends Entity {
	private int xpGain;
	private String id;
	private List<String> allies;
	private List<String> enemies;

	public NPC() {

	}

	public NPC(String entityID) {
		allies = new ArrayList<>();
		enemies = new ArrayList<>();
		this.id = entityID;
	}

	public void setItems( JsonObject json, int itemLimit, int i ) {
		JsonArray items = json.get("items").getAsJsonArray();
		JsonArray itemTypes = json.get("tradingEmphasis").getAsJsonArray();
		boolean cont;
		for (JsonElement item : items) {
			if (i == itemLimit) {
				break;
			}

			cont = false;
			if (item.getAsString().length() > 0) {
				char itemType = item.getAsString().charAt(0); // ERROR (maybe fixed)
				for (JsonElement type : itemTypes) {
					if (itemType == type.getAsString().charAt(0)) {
						cont = true;
					}
				}
			} else {
				cont = false;
			}
			Random rand = new Random();
			int j = rand.nextInt(100) + 1;
			if (cont) {
				if ((j > 0) && (j <= 95)) {
					addItemToStorage(itemBank.getItem(item.getAsString()));
					i++;
				}
			} else {
				if ((j > 95) && (j <= 100)) {
					addItemToStorage(itemBank.getItem(item.getAsString()));
					i++;
				}
			}
		}
		if (i != itemLimit) {
			setItems(json, itemLimit, i);
		}
	}

	public List<String> getAllies() {
		return allies;
	}

	public List<String> getEnemies() {
		return enemies;
	}

	public int getXPGain() {
		return xpGain;
	}

	public void setAllies( List<String> allies ) {
		this.allies = allies;
	}

	public void setEnemies( List<String> enemies ) {
		this.enemies = enemies;
	}

	public void setXPGain( int xpGain ) {
		this.xpGain = xpGain;
	}

	public String getId() {
		return id;
	}

	@Override
	public boolean equals( Object obj ) {
		if (obj == null) return false;
		if (obj instanceof NPC) {
			NPC npc = (NPC) obj;
			return npc.getId().equals(id);
		}
		return false;
	}
}