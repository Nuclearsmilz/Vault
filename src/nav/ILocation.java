package nav;

import java.util.*;

import enemies.*;
import entities.*;
import items.*;

/**
 * ILocation is an interface that holds
 * all the methods used for a specific location
 * @author Jon
 */
public interface ILocation {
	Coordinate getCoordinate();

	String getTitle();

	String getDescription();

	Locations getLocationType();

	List<Item> getItems();

	Storage getStorage();

	void addItem( Item item );

	Item removeItem( Item item );

	int getDangerRating();

	List<Enemy> getEnemies();

	List<NPC> getNPCs();

	void addNpcs( List<String> npcIDS );

	void addNpc( String npcID );

	void removeNpc( NPC npc );

	void addEnemy( Enemy enemy );

	void removeEnemy( Enemy enemy );

	void setDangerRating( int dangerRating );

	Map<Direction, ILocation> getExits();

	void print();
}