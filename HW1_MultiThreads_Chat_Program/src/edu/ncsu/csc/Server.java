package edu.ncsu.csc;

import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.ArrayList;

/**
 * ChatServer delievers the message from users to users. Server side code
 * 
 * @author Yoonsoo Park
 * 
 */
public class Server {

	// socket of SERVER.
	private static ServerSocket serverSocket = null;
	// socket of Client
	private static Socket clientSocket = null;

	private static final int maxClientsNum = 10;
	private static final workingThread[] threads = new workingThread[maxClientsNum];

	// private static final ArrayList<clientThread> thread = new
	// ArrayList<clientThread>();

	public static void main(String args[]) {

		// port number
		int portNum = 8080;
		if (args.length < 1) {
			System.out
					.println("Usage: java MultiThreadChatServerSync <portNumber>\n"
							+ "Now using port number=" + portNum);
		} else {
			portNum = Integer.valueOf(args[0]).intValue();
		}

		/*
		 * Open a server socket on the portNumber (default 2222). Note that we
		 * can not choose a port less than 1023 if we are not privileged users
		 * (root).
		 */
		try {
			serverSocket = new ServerSocket(portNum);
		} catch (IOException e) {
			System.out.println(e);
		}

		/*
		 * Create a client socket for each connection
		 * 
		 * and pass it to a new client thread.
		 */
		while (true) {
			try {
				clientSocket = serverSocket.accept();
				int i = 0;
				for (i = 0; i < maxClientsNum; i++) {
					if (threads[i] == null) {
						(threads[i] = new workingThread(clientSocket, threads))
								.start();

						break;
					}
				}
				if (i == maxClientsNum) {
					PrintStream os = new PrintStream(
							clientSocket.getOutputStream());
					os.println("Server too busy. Try later.");
					os.close();
					clientSocket.close();
				}
			} catch (IOException e) {
				System.out.println(e);
			}
		}
	}
}

class workingThread extends Thread {

	private String clientName = null;
	private DataInputStream is = null;
	private PrintStream os = null;
	private Socket clientSocket = null;
	private final workingThread[] threads;
	// private ArrayList<clientThread> thread;
	private int peopleInChat;

	// constructor of clientThread
	public workingThread(Socket clientSocket, workingThread[] threads) {
		this.clientSocket = clientSocket;
		this.threads = threads;
		peopleInChat = threads.length;

	}

	public void run() {

		workingThread[] threads = this.threads;

		int maxClientsNum = this.peopleInChat;

		try {
			/*
			 * Create input and output streams for this client.
			 */
			is = new DataInputStream(clientSocket.getInputStream());
			os = new PrintStream(clientSocket.getOutputStream());
			String name;
			while (true) {
				os.println("Give me your name please");
				name = is.readLine().trim();

				break;

			}

			/* Welcome the new the client. */
			os.println("Welcome " + name
					+ " \nTo leave enter /q in a new line please.");
			
			
			synchronized (this) {
				for (int i = 0; i < maxClientsNum; i++) {
					if (threads[i] != null && threads[i] == this) {
						clientName = "@" + name;
						break;
					}
				}
				for (int i = 0; i < maxClientsNum; i++) {
					if (threads[i] != null && threads[i] != this) {
						threads[i].os.println(name
								+ " entered the chat room :)");
					}
				}
			}
			/* Start the conversation. */
			while (true) {
				// getting input from Client.
				String line = is.readLine();
				if (line.startsWith("/q")) {
					break;
				}

				// inform to other client, but let just one thread enter this statement at a time. 
				synchronized (this) {
					for (int i = 0; i < maxClientsNum; i++) {
						if (threads[i] != null && threads[i].clientName != null) {
							threads[i].os.println(":::" + name + "::: " + line);
						}
					}
				}
			}

			//out from while loop which mean user left! then we need to inform this to other users and
			//say good bye to user leaves (same rule with above section JUST ONE THREAD AT A TIME)
			synchronized (this) {
				for (int i = 0; i < maxClientsNum; i++) {
					if (threads[i] != null && threads[i] != this
							&& threads[i].clientName != null) {
						threads[i].os.println(name
								+ " leaves the room");
					}
				}
			}
			os.println("Have a nice DAY!");

			// clean the thread up
			synchronized (this) {
				for (int i = 0; i < maxClientsNum; i++) {
					if (threads[i] == this) {
						threads[i] = null;
					}
				}
			}
			// close this particular socket.
			is.close();
			os.close();
			clientSocket.close();
		} catch (IOException e) {
		}
	}
}
