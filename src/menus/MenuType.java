package menus;

import java.util.*;

/**
 * Represents a single Menu Option in the Menus
 * @see Menus
 * @author Jon
 */
public class MenuType {
	protected String command;
	protected String description;
	protected Set<String> altCommands;

	public MenuType(String command, String description) {
		this.command = command;
		this.description = description;
		this.altCommands = new HashSet<String>();
	}

	public MenuType(String command, String description, String ... altCommands) {
		this(command, description);
		for (String altCommand : altCommands) {
			this.altCommands.add(altCommand);
		}
	}

	public String getCommand() {
		return command;
	}

	public String getDescription() {
		return description;
	}

	public Set<String> getAltCommands() {
		return altCommands;
	}

	public void setCommand( String command ) {
		this.command = command;
	}

	public void setDescription( String description ) {
		this.description = description;
	}

	public void setAltCommands( Set<String> altCommands ) {
		this.altCommands = altCommands;
	}

	// Used for switch in menus
	// Used in place of getCommand method for comparison against user input
	public String getKey() {
		return getCommand().toLowerCase();
	}
}