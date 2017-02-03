package main;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

import org.slf4j.*;

public class VaultLogger {
	private static Logger logger = LoggerFactory.getLogger(VaultLogger.class);

	public static BlockingQueue<String> queue = new LinkedBlockingQueue<>();
	public static BlockingQueue<String> inputQ = new LinkedBlockingQueue<>();

	public static DataInputStream in;
	public static DataOutputStream out;
	public static Socket server;
	
	public static GameModeType mode;

	public static void startMessenger( GameModeType modeIncoming, Socket socketIncoming ) {
		logger.debug("startMessenger( " + modeIncoming + "  , " + socketIncoming + " )");
		mode = modeIncoming;
		server = socketIncoming;
	}

	public static void startMessenger( GameModeType modeInc ) {
		logger.debug("startMessenger( " + modeInc + " )");
		mode = modeInc;
	}

	public static BlockingQueue<String> getQueue() {
		return queue;
	}

	public static void say( String message ) {
		logger.debug("offer( " + message + " )");

		if (GameModeType.SERVER == mode) {
			try {
				out = new DataOutputStream(server.getOutputStream());
				in = new DataInputStream(server.getInputStream());
			} catch (IOException ioe) {
				logger.debug("Inside offer( " + message + ") ", ioe);
			}
		}
		if (GameModeType.SERVER == mode) {
			sendToServer(message);
		} else {
			System.out.println(message);
		}
	}

	public static boolean sendToServer( String message ) {
		logger.debug("sendToServer( " + message + " )");
		try {
			out.writeUTF(message + "END");
		} catch (SocketException se) {
			logger.debug("Inside  sendToServer( " + message + " )", se);
		} catch (IOException ioe) {
			logger.debug("Inside  sendToServer( " + message + " )", ioe);
		}
		return true;
	}

	public static String getInput( String message ) {
		logger.debug("getInput( " + message + " )");
		String input = "";
		try {
			out.writeUTF(message + "END");
			input = in.readUTF();
		} catch (SocketException se) {
			logger.debug("Inside getInput( " + message + " )", se);
			input = "error";
		} catch (IOException ioe) {
			logger.debug("Inside getInput( " + message + " )", ioe);
			input = "error";
		}
		return input;
	}

	public static String recieve() {
		String message = null;
		if (GameModeType.SERVER == mode) {
			message = getInput("QUERY");
			if (message.equals("error")) {
				message = "exit";
			}
		} else {
			Scanner in = null;
			try {
				in = new Scanner(System.in);
				message = in.nextLine();
			} catch (NoSuchElementException nsee) {
				nsee.printStackTrace();
			} catch (IllegalStateException ise) {
				ise.printStackTrace();
			}
		}
		return message;
	}
}