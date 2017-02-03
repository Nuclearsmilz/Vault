package main;

import java.io.*;
import java.net.*;

import org.slf4j.*;

import menus.*;

/**
 * Start the game.
 * Creates a Main Menu.
 * Handles everything after that. 
 * @author Jon
 */
public class Vault {
	private static Logger logger = LoggerFactory.getLogger(Vault.class);

	public static void main( String[] args ) {
		logger.info("Starting Vault. " + toString(args));

		GameModeType mode = getGameMode(args);
		
		logger.debug("Staring mode" + mode.name());

		String serverName = "localhost";
		int port = 7077;
		if (mode == GameModeType.SERVER) {
			port = Integer.parseInt(args[1]);
		} else if (mode == GameModeType.CLIENT) {
			serverName = args[2];
			port = Integer.parseInt(args[1]);
		}
		if (GameModeType.CLIENT == mode) {
			while (true) {
				ServerSocket listener = null;
				try {
					listener = new ServerSocket(port);
					while (true) {
						Socket server = listener.accept();
						Runnable run = new MainMenu(server, mode);
						new Thread(run).start();
					}
				} catch (SocketException se) {
					se.printStackTrace();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				} finally {
					try {
						listener.close();
					} catch (IOException ioe) {
						ioe.printStackTrace();
					}
				}
			}
		} else {
			VaultLogger.startMessenger(GameModeType.STAND_ALONE);
			new MainMenu();
		}
	}

	private static GameModeType getGameMode( String[] args ) {
		if (args == null || args.length == 0 || "".equals(args[0].trim())) return GameModeType.STAND_ALONE;

		try {
			return GameModeType.valueOf(args[0].toUpperCase());
		} catch (IllegalArgumentException iae) {
			logger.warn("No game mode '" + args[0].toUpperCase() + "' of that type known. Terminating.");
			System.exit(-1);
		}
		return GameModeType.STAND_ALONE;
	}

	public static String toString( String[] args ) {
		if (args.length == 0) return "";

		final StringBuilder builder = new StringBuilder();

		builder.append("[ ");
		for (int index = 0; index < args.length; index++) {
			if (index > 0) {
				builder.append(", ");
			}
			builder.append(args[index]);
		}
		builder.append(" ]");
		return builder.toString();
	}
}