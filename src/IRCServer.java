import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;

public class IRCServer {

	private ServerSocket serverSocket;
	private String serverName;
	private LinkedList<String> chatLog;
	
	public IRCServer() {
		this.serverName = "Unnamed Server";
		chatLog = new LinkedList<String>();
	}
	
	public IRCServer(String serverName) {
		this.serverName = serverName;
		chatLog = new LinkedList<String>();
	}

	public void makeSocket(int port) {
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void start() {
		while (true) {
            try {
				new clientConnection(serverSocket.accept()).run();
			} catch (IOException e) {
				System.out.println(e.toString());
			}
		}
	}
	
	public static void main(String[] args) {
		if (args.length == 1) {
			IRCServer myServer = new IRCServer();
			myServer.makeSocket(Integer.parseInt(args[0]));
			myServer.start();
		} else { System.out.println("Usage: IRCServer <Port>"); }
	}
	
	private class clientConnection implements Runnable {
		
		private Socket clientSocket;
		private PrintWriter outputToClient;
		private BufferedReader inputFromClient;
		private String connection = "";
		
		public clientConnection(Socket socket) {
			this.clientSocket = socket;
			connection = socket.getInetAddress().toString().replaceAll("/", "") + ":" + socket.getPort();
		}

		@Override
		public void run() {
			try {
				this.outputToClient = new PrintWriter(clientSocket.getOutputStream(), true);
				this.inputFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				outputToClient.println(serverName);
				
				System.out.println("\nConnection from - " + connection);
				String curLine = "";
				
				while(true) {
					curLine = inputFromClient.readLine();
					System.out.printf("%-32s%s%n",connection,"\"" + curLine + "\"");
				}
				
			} catch (SocketException e) {
				System.out.println("Connection Exiting - " + connection);
			} catch (Exception e) {
				System.out.println(e.toString());
			}
		}
	}

	
}
