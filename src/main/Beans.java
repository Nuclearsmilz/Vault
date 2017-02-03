package main;

import databanks.*;

/**
 * There is no Dependency Injection framework for creating Beans (runtime singletons / prototypes). 
 * So this class will contain those Beans.
 */
public class Beans {
	public static ItemBank getItemRepository() {
		return ItemBank.createRepo();
	}

	public static WorldBank getLocationRepository() {
		return WorldBank.createRepo("");
	}

	public static WorldBank getLocationRepository( String profile ) {
		return WorldBank.createRepo(profile);
	}

	public static NPCBank getNPCRepository() {
		return NPCBank.createRepo();
	}
}