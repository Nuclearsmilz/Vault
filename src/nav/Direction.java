package nav;

/**
 * This enum contains 4 singular properties
 * In-Game description via text 
 * and three integers
 * The integers are added to a coordinate via the coordinate class
 * to recieve a NEW direction
 * @author Jon
 */
public enum Direction {
	UP("Up", 0, 0, 1), 
	DOWN("Down", 0, 0, -1), 
	NORTH("North", 0, 1, 0), 
	SOUTH("South", 0, -1, 0), 
	EAST("East", 1, 0, 0),
	WEST("West", -1, 0, 0);

	private final int dx, dy, dz;
	private final String desc;

	private Direction(String description, int dx, int dy, int dz) {
		this.desc = description;
		this.dx = dx;
		this.dy = dy;
		this.dz = dz;
	}

	public String getDesc() {
		return desc;
	}

	public int getDx() {
		return dx;
	}

	public int getDy() {
		return dy;
	}

	public int getDz() {
		return dz;
	}
}