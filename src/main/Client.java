package main;

import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
	DataInputStream in;
	DataOutputStream out;

	public Client(String serverName, int port) {
		Socket client = null;
		try {
			client = new Socket(serverName, port);
			in = new DataInputStream(client.getInputStream());
			out = new DataOutputStream(client.getOutputStream());

			while (true) {
				String serverMsg = in.readUTF();
				if (serverMsg.endsWith("END")) {
					serverMsg = serverMsg.substring(0, serverMsg.length() - 3);
					if (serverMsg.endsWith("QUERY")) {
						getInput();
					} else if (serverMsg.equals("EXIT")) {
						break;
					} else {
						VaultLogger.say(serverMsg);
						//System.out.println(serverMsg);
					}
				} else {
					String msg = "";
					while (!serverMsg.endsWith("END")) {
						msg += serverMsg;
						serverMsg = in.readUTF();
					}
					msg = serverMsg.substring(0, serverMsg.length() - 3);
					if (msg.equals("QUERY")) {
						getInput();
					} else if (serverMsg.equals("EXIT")) {
						break;
					} else {
						VaultLogger.say(serverMsg);
						//System.out.println(serverMsg);
					}
				}
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				client.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	public void getInput() {
		Scanner in;
		try {
			in = new Scanner(System.in);
			String userIn = in.nextLine();
			out.writeUTF(userIn);
			in.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}