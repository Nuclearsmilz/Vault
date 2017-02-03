package databanks;

import java.io.*;
import java.util.*;

import com.google.gson.*;
import com.google.gson.stream.*;

import entities.*;
import items.*;

/**
 * CONTROLS ALL OF THE NPC SHIT 
 * THIS IS GONNA BE LONG CLASS
 * GET IT OUT OF MY FACE
 * UGH
 * @author Jon
 */
public class NPCBank {
	private static NPCBank npcBank = null;
	private Map<String, NPC> npcMap = new HashMap<String, NPC>();
	static String fileName = "json/npcs.json";

	public static NPCBank createRepo() {
		if (npcBank == null) {
			File dataFile = new File(fileName);
			if (!dataFile.exists()) { throw new RuntimeException("File '" + dataFile + "' does not exist."); }
			npcBank = new NPCBank();
			npcBank.load(dataFile);
		}
		return npcBank;
	}

	public NPC getNPC( String npcID ) {
		if (npcID == null || npcID.trim().length() == 0) return null;
		if (!npcMap.containsKey(npcID)) { throw new RuntimeException(
		        "Argument 'npcID' with value '" + npcID + "' not found in bank."); }
		return npcMap.get(npcID);
	}

	//Load ALL ITEMS from npcs.json with FILE
	protected void load( File repo ) {
		try {
			JsonReader reader = new JsonReader(new FileReader(repo));
			load(reader);
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		}
	}

	//Load ALL ITEMS from npcs.json with READER
	protected void load( JsonReader reader ) {
		JsonObject jsonNPCs = new JsonObject();
		try {
			JsonParser jparser = new JsonParser();
			JsonObject jsonObj = jparser.parse(reader).getAsJsonObject();
			jsonNPCs = jsonObj.get("npcs").getAsJsonObject();
			reader.close();
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		for (Map.Entry<String, JsonElement> entry : jsonNPCs.entrySet()) {
			String id = entry.getKey();
			JsonObject npcData = entry.getValue().getAsJsonObject();
			String name = npcData.get("name").getAsString();
			int HP = npcData.get("HP").getAsInt();
			int maxHP = npcData.get("maxHP").getAsInt();
			int dmg = npcData.get("dmg").getAsInt();
			int armor = npcData.get("armor").getAsInt();
			int lvl = npcData.get("lvl").getAsInt();
			int intel = npcData.get("intel").getAsInt();
			int dex = npcData.get("dex").getAsInt();
			int strength = npcData.get("strength").getAsInt();
			int gold = 0;
			if (npcData.has("gold")) {
				gold = npcData.get("gold").getAsInt();
			}

			//MAKE THE DAMN NPC. MAKE IT! MAKE. IT.
			NPC npc = new NPC(id);
			npc.setName(name);
			npc.setHP(HP);
			npc.setDmg(dmg);
			npc.setArmor(armor);
			npc.setMaxHP(maxHP);
			npc.setLvl(lvl);
			npc.setIntel(intel);
			npc.setDex(dex);
			npc.setStrength(strength);
			npc.setGold(gold);

			float maxWeight = (float) Math.sqrt(strength * 150);
			npc.setStorage(new Storage(maxWeight));
			if (npcData.has("sellLimit") && npcData.has("items")) {
				int itemLimit = npcData.get("sellLimit").getAsInt();
				int i = 0;
				npc.setItems(npcData, itemLimit, i);
			}
			JsonArray alliesJson = npcData.get("allies").getAsJsonArray();
			List<String> allies = new ArrayList<>();
			for (JsonElement ally : alliesJson) {
				allies.add(ally.getAsString());
			}
			npc.setAllies(allies);
			List<String> enemies = new ArrayList<>();
			JsonArray enemiesJson = npcData.get("enemies").getAsJsonArray();
			for (JsonElement enemy : enemiesJson) {
				enemies.add(enemy.getAsString());
			}
			npc.setEnemies(enemies);
			npcMap.put(id, npc);
		}
	}
}