package enemies;

import java.util.*;

import databanks.*;
import entities.*;
import items.*;
import main.*;

/*
 * Holds a type of enemy that will be
 * fluffed out more in its respective class
 */
public abstract class Enemy extends Entity {
	public String enemyType;
	private int xpGain;
	private ItemBank itemBank = Beans.getItemRepository();

	public int getXPGain() {
		return xpGain;
	}

	public void setXPGain( int xpGain ) {
		this.xpGain = xpGain;
	}

	@Override
	public boolean equals( Object obj ) {
		if (obj == null) return false;
		if (obj instanceof Enemy) {
			Enemy e = (Enemy) obj;
			return e.enemyType.equals(this.enemyType);
		}
		return false;
	}

	public void addRandomItems( int playerLevel, String ... children ) {
		List<String> itemList = Arrays.asList(children);
		Random rand = new Random();

		int numItems = 1;
		int i = 0;
		int j = rand.nextInt(5) + 1;
		while (i != numItems) {
			for (String itemName : itemList) {
				if (i == numItems) {
					break;
				}
				if (j == 1) {
					Item item = itemBank.getItem(itemName);
					addItemToStorage(item);
					i++;
				}
			}
		}
	}
}