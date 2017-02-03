package databanks;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import com.google.gson.*;
import com.google.gson.reflect.*;

import items.*;
import main.*;
import nav.*;

/**
 * Holds and Loads the locations from locations.json file
 * Also allows to find the intial and current location of the player
 * @author Jon
 */
public class WorldBank {
	private ItemBank itemBank = Beans.getItemRepository();
	private String fileName;
	private Map<Coordinate, ILocation> locs;
	private static WorldBank instance;

	public WorldBank(String name) {
		locs = new HashMap<Coordinate, ILocation>();
		fileName = "json/profiles/" + name + "/locations.json";
		load();
	}

	public static WorldBank createRepo( String profileName ) {
		if ("".equals(profileName)) return instance;
		if (instance == null) {
			instance = new WorldBank(profileName);
		} else if (!instance.getFileName().contains(profileName)) {
			instance = new WorldBank(profileName);
		}
		return instance;
	}

	private void load() {
		JsonParser parser = new JsonParser();
		File f = new File(fileName);
		if (!f.exists()) {
			copyLocationsFile();
		}
		try {
			Reader reader = new FileReader(fileName);
			JsonObject json = parser.parse(reader).getAsJsonObject();
			for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
				locs.put(new Coordinate(entry.getKey()), loadLocation(entry.getValue().getAsJsonObject()));
			}
			reader.close();
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
			System.exit(-1);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	private ILocation loadLocation( JsonObject json ) {
		Coordinate coord = new Coordinate(json.get("coordinate").getAsString());
		String title = json.get("title").getAsString();
		String description = json.get("description").getAsString();
		Locations locationType = Locations.valueOf(json.get("locationType").getAsString()); // ERROR (maybe fixed)
		ILocation location = new Location(coord, title, description, locationType); // ERROR (maybe fixed)
		location.setDangerRating(json.get("danger").getAsInt());
		if (json.has("items")) {
			List<String> items = new Gson().fromJson(json.get("items"), new TypeToken<List<String>>() {}.getType());
			for (String id : items) {
				location.addItem(itemBank.getItem(id));
			}
		}
		if (json.has("npcs")) {
			List<String> npcs = new Gson().fromJson(json.get("npcs"), new TypeToken<List<String>>() {}.getType());
			for (String npc : npcs) {
				location.addNpc(npc);
			}
		}
		return location;
	}

	public void writeLocations() {
		try {
			JsonObject jsonObject = new JsonObject();
			for (Map.Entry<Coordinate, ILocation> entry : locs.entrySet()) {
				ILocation loc = entry.getValue();
				JsonObject locationJsonElement = new JsonObject();
				locationJsonElement.addProperty("title", loc.getTitle());
				locationJsonElement.addProperty("coordinate", loc.getCoordinate().toString());
				locationJsonElement.addProperty("description", loc.getDescription());
				locationJsonElement.addProperty("locationType", loc.getLocationType().toString());
				locationJsonElement.addProperty("danger", String.valueOf(loc.getDangerRating()));
				JsonArray itemList = new JsonArray();
				List<Item> items = loc.getItems();
				if (items.size() > 0) {
					for (Item item : items) {
						JsonPrimitive itemJson = new JsonPrimitive(item.getID());
						itemList.add(itemJson);
					}
					locationJsonElement.add("items", itemList);
				}
				jsonObject.add(loc.getCoordinate().toString(), locationJsonElement);
			}
			Writer writer = new FileWriter(fileName);
			Gson gson = new Gson();
			gson.toJson(jsonObject, writer);
			writer.close();
			VaultLogger.say("The game locations were saved.");
		} catch (IOException ioe) {
			VaultLogger.say("Unable to save locations to file: " + fileName + ".");
		}
	}

	public ILocation getStartingLocation() {
		String profileName = fileName.split("/")[2];
		instance = null;
		WorldBank.createRepo(profileName);
		load();
		Coordinate coord = new Coordinate(0, 0, -1);
		return getLocation(coord);
	}

	public ILocation getLocation( Coordinate coord ) {
		if (coord == null) return null;
		if (!locs.containsKey(coord)) { throw new BankException(
		        "Argument 'coordinate' with value '" + coord.toString() + "' not found in repository"); }
		return locs.get(coord);
	}

	private void copyLocationsFile() {
		File source = new File("json/data/locations.json");
		File destination = new File(fileName);
		destination.mkdirs();
		try {
			Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public void addLocation( ILocation loc ) {
		locs.put(loc.getCoordinate(), loc);
	}

	public String getFileName() {
		return fileName;
	}
}