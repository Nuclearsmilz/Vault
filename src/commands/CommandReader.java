package commands;

import java.lang.reflect.*;
import java.util.*;

import entities.*;
import main.*;

/**
 * This class does what it says.
 * Parses the game commands inputted by the user, automatically
 * DO NOT add a command here, add it in the CommandRepo Enum
 * @author Jon
 */
public class CommandReader {
	Player player;
	private TreeMap<String, Method> commandMap;

	public CommandReader(Player player) {
		this.player = player;
		commandMap = new TreeMap<String, Method>();
		initiateCommandMap();
	}

	// adds the command to the command map
	public void initiateCommandMap() {
		Method[] methods = Commands.class.getMethods();

		for (Method method : methods) {
			if (!method.isAnnotationPresent(Command.class)) {
				continue;
			}
			Command annotation = method.getAnnotation(Command.class);
			this.commandMap.put(annotation.command(), method);
			for (String alias : annotation.aliases().split(",")) {
				if (alias.length() == 0) {
					break;
				}
				this.commandMap.put(alias, method);
			}
		}
	}

	public boolean parse( Player player, String userCommand ) throws DeathException {
		Commands commands = Commands.getInstance();
		commands.initializePlayer(player);

		if (userCommand.equalsIgnoreCase("exit") || userCommand.equalsIgnoreCase("back")) return false;
		String command = removeNaturalText(userCommand);

		for (String key : commandMap.descendingKeySet()) {
			if (command.startsWith(key)) {
				Method method = commandMap.get(key);
				if (method.getParameterTypes().length == 0) {
					if (command.equals(key)) {
						try {
							if (method.getAnnotation(Command.class).debug()) {
								if ("test".equals(player.getName())) {
									method.invoke(command);
								} else {
									VaultLogger.say("Must be using 'test' profile to debug!");
								}
							} else {
								method.invoke(commands);
							}
						} catch (IllegalAccessException | InvocationTargetException iaeite) {
							if (iaeite.getCause() instanceof DeathException) {
								throw (DeathException) iaeite.getCause();
							} else {
								iaeite.getCause().printStackTrace();
							}
						}
					} else {
						VaultLogger.say("I don't know what '" + userCommand + "' means.");
						return true;
					}
				} else if (method.getParameterTypes()[0] == String.class) {
					String arg = command.substring(key.length()).trim();
					try {
						if (method.getAnnotation(Command.class).debug()) {
							if ("test".equals(player.getName())) {
								method.invoke(commands, arg);
							} else {
								VaultLogger.say("Must be using 'test' profile to debug!");
							}
						} else {
							method.invoke(commands, arg);
						}
					} catch (IllegalAccessException | InvocationTargetException e) {
						if (e.getCause() instanceof DeathException) {
							throw (DeathException) e.getCause();
						} else {
							e.getCause().printStackTrace();
						}
					}
				}
				return true;
			}
		}
		VaultLogger.say("I don't know what '" + userCommand + "' means.");
		return true;
	}

	private String removeNaturalText( String command ) {
		command = command.replaceAll(" to ", " ");
		command = command.replaceAll(" a ", " ");
		return command;
	}
}