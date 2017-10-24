import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class IRCServer {

	private ServerSocket serverSocket;
	private String serverName;
	
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
	
	public void start() {
		int count = 0;
		while (count != 5) {
            try {
				new clientConnection(serverSocket.accept()).run();
				count++;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Server ending");
	}
	
	private class clientConnection implements Runnable {
		
		private Socket clientSocket;
		private PrintWriter clientOutput;
		private BufferedReader clientInput;
		private String nickname;
		
		public clientConnection(Socket socket) {
			this.clientSocket = socket;

		}

		@Override
		public void run() {
			try {
				this.clientOutput = new PrintWriter(clientSocket.getOutputStream(), true);
				this.clientInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				
				clientOutput.println(serverName);
				System.out.println("Connection from: " + clientSocket.getInetAddress() + " : " + clientSocket.getPort());
				
				
				
			} catch (IOException e) {
				System.out.println(e.toString());
			}
		}
	}
	
	public static void main(String[] args) {
		System.out.println("Running Server");
		if (args.length == 1) {
			IRCServer myServer = new IRCServer();
			myServer.makeSocket(Integer.parseInt(args[0]));
			myServer.start();
		} else { System.out.println("Usage: IRCServer <Port>"); }
	}
}
