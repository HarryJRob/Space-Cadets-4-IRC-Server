import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IRCServer {

	private ServerSocket serverSocket;
	
	private LinkedList<String> chatLog = new LinkedList<String>();
	private LinkedList<clientConnection> clientList = new LinkedList<clientConnection>();
	
	private String serverName;
	private String adminPassword = "";
	
	//Parse the arguments given to the server
	public static void main(String[] args) {
		if (args.length >= 1) {
			String name = "Unnamed Server";
			int port = 2004;
			String pass = "";
			
			for(String curStr : args) {
				String[] parts = curStr.split(":");
				if(parts[0].equals("port")) {
					port = Integer.parseInt(parts[1]);
				} else if(parts[0].equals("adminPass")) { 
					pass = parts[1];
				} else if(parts[0].equals("name")) { 
					name = parts[1];
				}
			}
			
			IRCServer myServer = new IRCServer(name,port,pass);
			myServer.start();
		} else { System.out.println("Usage: IRCServer port:<Port Number> (Optional) adminPass:<admin password> (Optional) name:<server name>"); }
	}
	
	//Initialise the server
	public IRCServer(String serverName, int portNumber, String adminPassword) {
		this.serverName = serverName;
		makeSocket(portNumber);
		this.adminPassword = adminPassword;
	}

	//Create a server socket on a given port
	private void makeSocket(int port) {
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			System.out.println(e.toString());
		}
	}
	
	//Broadcast all messages in chatLog to all clients
	private void broadcastAll() {
		for(String message : chatLog) {
			for (clientConnection curConnection : clientList) {
				curConnection.sendMessage(message);
			}
		}
		chatLog.clear();
	}
	
	/* When a connection comes in accept the connection creating a socket.
	 * Add that connection to a list of all connections
	 * start that thread
	 */
	public void start() {
		while (true) {
            try {
            	clientConnection tempRef = new clientConnection(serverSocket.accept());
				clientList.add(tempRef);
            	new Thread(tempRef).start();
			} catch (IOException e) {
				System.out.println(e.toString());
			}
		}
	}
	
	/* A runnable class to handle a clients connection
	 * Also does most of the logic for commands etc.
	 */
	private class clientConnection implements Runnable {
		
		private Socket clientSocket;
		
		private PrintWriter outputToClient;
		private BufferedReader inputFromClient;
		
		private String connection = "";
		
		private String nickname = "[Anon]";
		private boolean isAdmin = false;
		
		//Constructor which passes the socket created by the ServerSocket
		public clientConnection(Socket socket) {
			this.clientSocket = socket;
			connection = socket.getInetAddress().toString().replaceAll("/", "") + ":" + socket.getPort();
		}

		
		//Read the input from the user and handle it
		@Override
		public void run() {
			try {
				//Create the input and output streams to and from the client
				this.outputToClient = new PrintWriter(clientSocket.getOutputStream(), true);
				this.inputFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				
				outputToClient.println(serverName);
				
				System.out.println("\nConnection from - " + connection);
				
				//Set the nickname of this client
				String nick = inputFromClient.readLine();
				if (!nick.equals("") && !nick.equals("Server") && !nick.equals("!quit")) {
					nickname = '[' + nick + ']';
				}
				
				chatLog.add("[Server] - " + nickname + " has joined");
				broadcastAll();
				
				while(true) {
					String curLine = inputFromClient.readLine();
					
					System.out.printf("%-32s%s%n",connection,"\"" + curLine + "\"");

					//If this line is a command do
					if (curLine.charAt(0) == '!') {
						
						Pattern cmdPattern = Pattern.compile("(!adminLogin|!changeNick|!help|!setServerName|!kick|!shutdown)(.*)");
						String command = "Invalid Command", args = "";
						Matcher m = cmdPattern.matcher(curLine);
						
						if (m.find()) {
							command = m.group(1).trim();
							args = m.group(2).trim();
						}

						parseCommands(command, args);
					
					//Else just send it as a normal message
					} else {
						
						chatLog.add(nickname + " - " + curLine);
						broadcastAll();
					}
				}
				
			} catch (SocketException e) {
				
				chatLog.add("[Server] - " + nickname + " has left");
				broadcastAll();
				System.out.println("Connection Exiting - " + connection);
				
			} catch (Exception e) {
				
				e.printStackTrace();
			}
		}
		
		//Separates the parsing of commands into a sub so the run method can be read easier.
		public void parseCommands(String command, String args) {
			switch (command) {
			
			case "!adminLogin":
				if (adminPassword != "") {
					if (adminPassword.equals(args)) {
						isAdmin = true;
						outputToClient.println("[Server] - You have successfully logged in");
					} else { outputToClient.println("[Server] - Incorrect password"); }
					
				} else { outputToClient.println("[Server] - This server may not be accessed remotely"); }
				
				break;
				
			case "!changeNick":
				if(args != "" && args != "Server" && args != "!quit") {
					chatLog.add("[Server] - " + nickname + " changed their name to [" + args + "]");
					nickname = '[' + args + ']';
					broadcastAll();
				}
				break;
				
			case "!help":
				if (isAdmin == false) {
					outputToClient.println("	Commands:\n    		!help\n    		!changeNick <nickname>\n    		!adminLogin <password>");
				} else if (isAdmin) {
					outputToClient.println("	Commands:\n    		!help\n    		!changeNick <nickname>\n    		!adminLogin <password>\n\n	Admin Commands:\n    		!setServerName <name>\n    		!shutdown");
				}
				break;
				
			case "!quit":
				chatLog.add("[Server] - " + nickname + " has left");
				broadcastAll();
				break;
				
			case "!setServerName":
				if(isAdmin) {
					serverName = args;
					chatLog.add("[Server] - Server name changed to \"" + serverName + "\"");
					broadcastAll();
				} else { outputToClient.println("[Server] - You are not authorized to use this command"); }
				break;
				
			case "!shutdown":
				if(isAdmin) {
					chatLog.add("[Server] - Shutting down");
					broadcastAll();
					System.exit(0);
				} else { outputToClient.println("[Server] - You are not authorized to use this command"); }
				break;
				
			default:
				outputToClient.println("[Server] - Command not recognised use \"!help\" to view the commands");
			}
			System.out.printf("%-32s%s%n",connection,"Running command: " + command);
		}
		
		//Allows me to send messages while outside this class
		public void sendMessage(String message) {
			outputToClient.println(message);
		}
	}
}
