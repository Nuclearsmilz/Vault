package chats;

import java.io.*;
import java.util.*;
import java.util.Map.*;

import com.google.gson.*;

import databanks.*;
import entities.*;
import items.*;
import main.*;

public class ChatManager {
	private static NPCBank npcBank = NPCBank.createRepo();
	private static final ChatManager instance = new ChatManager();
	private Map<NPC, List<Line>> lines = new HashMap<NPC, List<Line>>();
	private static final Map<String, Actions> ACTIONS_MAP = new HashMap<>();
	private static final Map<String, Conditions> CONDITIONS_MAP = new HashMap<>();

	static {
		ACTIONS_MAP.put("none", Actions.NONE);
		ACTIONS_MAP.put("attack", Actions.ATTACK);
		ACTIONS_MAP.put("buy", Actions.BUY);
		ACTIONS_MAP.put("give", Actions.GIVE);
		ACTIONS_MAP.put("sell", Actions.SELL);
		ACTIONS_MAP.put("take", Actions.TAKE);
		ACTIONS_MAP.put("trade", Actions.TRADE);

		CONDITIONS_MAP.put("none", Conditions.NONE);
		CONDITIONS_MAP.put("ally", Conditions.ALLY);
		CONDITIONS_MAP.put("enemy", Conditions.ENEMY);
		CONDITIONS_MAP.put("item", Conditions.ITEM);
		CONDITIONS_MAP.put("level", Conditions.LEVEL);
		CONDITIONS_MAP.put("char type", Conditions.CHAR_TYPE);
	}

	public ChatManager() {
		load();
	}

	public static ChatManager getInstance() {
		return instance;
	}

	private void load() {
		String fileName = "json/npcs.json";
		JsonParser parser = new JsonParser();
		File f = new File(fileName);
		try {
			Reader reader = new FileReader(fileName);
			JsonObject json = parser.parse(reader).getAsJsonObject();
			Set<Map.Entry<String, JsonElement>> entries = json.entrySet();
			for (Map.Entry<String, JsonElement> entry : entries) {
				NPC npc = npcBank.getNPC(entry.getKey());
				JsonObject info = entry.getValue().getAsJsonObject();
				if (info.get("conversations") != null) {
					JsonArray conversation = info.get("conversations").getAsJsonArray();
					addConversation(npc, conversation);
				}
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	private void addConversation( NPC npc, JsonArray conversation ) {
		List<Line> start = new ArrayList<>();
		int index = 0;
		for (JsonElement entry : conversation) {
			JsonObject info = entry.getAsJsonObject();
			start.add(getLine(index++, conversation));
		}
		lines.put(npc, start);
	}

	private Line getLine( int index, JsonArray conversation ) {
		JsonObject line = conversation.get(index).getAsJsonObject();
		List<Integer> answers = new ArrayList<>();
		if (line.get("answer") != null) {
			for (JsonElement i : line.get("answer").getAsJsonArray()) {
				answers.add(i.getAsInt());
			}
		}
		String prompt = line.get("player").getAsString();
		String text = line.get("text").getAsString();
		String[] conditionArray = line.get("condition").getAsString().split("=");
		Conditions condition = CONDITIONS_MAP.get(conditionArray[0]);
		String conditionParameter = (conditionArray.length == 1) ? "" : conditionArray[1];
		Actions action = ACTIONS_MAP.get(line.get("action").getAsString());
		return new Line(index, prompt, text, condition, conditionParameter, answers, action);
		//return null;
	}

	public void startConversation( NPC npc, Player player ) throws DeathException {
		List<Line> conversation = null;
		//lines.get(npc);
		Iterator<Entry<NPC, List<Line>>> iterator = lines.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<NPC, List<Line>> entry = (Map.Entry<NPC, List<Line>>) iterator.next();
			if (entry.getKey().equals(npc)) {
				conversation = entry.getValue();
			}
			iterator.remove();
		}
		if (conversation != null) {
			Line start = null;
			for (Line line : conversation) {
				if ("".equals(line.getPrompt()) && ChatManager.matchesConditions(npc, player, line)) {
					start = line;
					break;
				}
			}
			if (start != null) {
				VaultLogger.say(start.getText());
				Line answer = start.display(npc, player, conversation);
				trigger(start, npc, player);
				while (answer != null) {
					VaultLogger.say(answer.getText());
					trigger(answer, npc, player);
					Line tmpAnswer = answer.display(npc, player, conversation);
					answer = tmpAnswer;
				}
			}
		}
	}

	private void trigger( Line line, NPC npc, Player player ) throws DeathException {
		switch (line.getAction()) {
			case ATTACK:
				VaultLogger.say("\n" + npc.getName() + " is attacking you!\n");
				player.attack(npc.getName());
				break;
			case BUY:
				//TODO: Player buy 
				break;
			case GIVE:
				break;
			case SELL:
				//TODO: Player Sell
				break;
			case TAKE:
				break;
			case TRADE:
				Trading trading = new Trading(npc, player);
				trading.trade(true, true);
				break;
			//$CASES-OMITTED$
			default:
				break;
		}
	}

	public static boolean matchesConditions( NPC npc, Player player, Line line ) {
		switch (line.getCondition()) {
			case ALLY:
				return npc.getAllies().contains(player.getCurrentCharacterType());
			case CHAR_TYPE:
				String characterType = line.getConditionParameter();
				return characterType.equals(player.getCurrentCharacterType());
			case ENEMY:
				return npc.getEnemies().contains(player.getCurrentCharacterType());
			case ITEM:
				ItemBank itemBank = Beans.getItemRepository();
				Item requiredItem = itemBank.getItem(line.getConditionParameter());
			case LEVEL:
				int requiredLvl = Integer.parseInt(line.getConditionParameter());
				return player.getLvl() >= requiredLvl;
			//$CASES-OMITTED$
			default:
				return true;
		}
	}

}