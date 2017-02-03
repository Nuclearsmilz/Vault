package items;

/**
 * Tells me an item with a certain amount of it
 * @author Jon
 */
public class ItemStack {
	int amount;
	Item item;

	public ItemStack(int amount, Item item) {
		this.amount = amount;
		this.item = item;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount( int amount ) {
		this.amount = amount;
	}

	public Item getItem() {
		return item;
	}

	public void setItem( Item item ) {
		this.item = item;
	}
}