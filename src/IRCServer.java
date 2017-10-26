import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Pattern;

public class IRCServer {

	private ServerSocket serverSocket;
	private String serverName;
	private Queue<String> chatLog = new LinkedList<String>();
	private LinkedList<clientConnection> clientList = new LinkedList<clientConnection>();
	private String adminPassword = "";
	
	public IRCServer() {
		this.serverName = "Unnamed Server";
	}
	
	public IRCServer(String serverName) {
		this.serverName = serverName;
	}

	public void makeSocket(int port) {
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void broadcast() {
		for(String message : chatLog) {
			for (clientConnection curConnection : clientList) {
				curConnection.sendMessage(message);
			}
		}
		chatLog.clear();
	}
	
	public void start() {
		while (true) {
            try {
            	clientConnection tempRef = new clientConnection(serverSocket.accept());
				clientList.add(tempRef);
            	tempRef.run();
			} catch (IOException e) {
				System.out.println(e.toString());
			}
		}
	}
	
	public static void main(String[] args) {
		if (args.length == 1) {
			IRCServer myServer = new IRCServer("Test Server");
			myServer.makeSocket(Integer.parseInt(args[0]));
			myServer.start();
		} else { System.out.println("Usage: IRCServer <Port>"); }
	}
	
	private class clientConnection implements Runnable {
		
		private Socket clientSocket;
		private PrintWriter outputToClient;
		private BufferedReader inputFromClient;
		private String connection = "";
		private String nickname = "[Anon]";
		private boolean isAdmin = false;
		
		public clientConnection(Socket socket) {
			this.clientSocket = socket;
			connection = socket.getInetAddress().toString().replaceAll("/", "") + ":" + socket.getPort();
		}

		public void sendMessage(String message) {
			outputToClient.println(message);
		}
		
		@Override
		public void run() {
			try {
				this.outputToClient = new PrintWriter(clientSocket.getOutputStream(), true);
				this.inputFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				outputToClient.println(serverName);
				nickname = '[' + inputFromClient.readLine() + ']';
				
				System.out.println("\nConnection from - " + connection);
				
				while(true) {
					String curLine = inputFromClient.readLine();
					
					System.out.printf("%-32s%s%n",connection,"\"" + curLine + "\"");

					if (curLine.charAt(0) == '!') {
						/* Commands:
						 *    !adminLogin <password>
						 *    !changeNick <nickname>
						 *    !help
						 *    
						 *  Only possible with admin perms
						 *    !setServerName <name>
						 *    !kick <nickname>
						 *    !shutdown
						 */
						System.out.println(curLine);
						Pattern cmdPattern = Pattern.compile("(!adminLogin|!changeNick|!help|!setServerName|!kick|!shutdown)([.]*)", Pattern.DOTALL);
						
						System.out.println(cmdPattern.matcher(curLine));
						String command = "";
						String args = cmdPattern.matcher(curLine).group(1);
						
						System.out.println("Command: " +command+ " - " + args);
						
						switch (command.trim()) {
						case "!adminLogin":
							if (adminPassword != "") {
								//Password stuff
							} else { outputToClient.println("[Server] This server may not be accessed remotely"); }
							break;
						case "!changeNick":
							// nickname stuff
							break;
						case "!help":
							if (isAdmin) {
								outputToClient.println("Commands:\n    !help\n    !changeNick <nickname>\n    !adminLogin <password>");
							} else if (!isAdmin) {
								outputToClient.println("Commands:\n    !help\n    !changeNick <nickname>\n    !adminLogin <password>\n\nAdmin Commands:\n    !setServerName <name>\n    !kick <nickname>\n    !shutdown");
							}
							break;
						case "!setServerName":
							
						case "!kick":
							break;
						case "!shutdown":
							
							break;
						default:
							outputToClient.println("[Server] Command not recognised use \"!help\" to view the commands");
						}
						System.out.printf("%-32s%s%n",connection,"Running command: " + command);
						
					} else {
						chatLog.add(nickname + " - " + curLine);
						broadcast();
					}
				}
				
			} catch (SocketException e) {
				System.out.println("Connection Exiting - " + connection);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	
}
