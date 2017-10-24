import java.io.IOException;
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
		
	}
	
	private class clientConnection implements Runnable {
		
		private Socket clientSocket;
		
		public clientConnection(Socket socket) {
			this.clientSocket = socket;
		}

		@Override
		public void run() {
			
		}
	}
	
	public static void main(String[] args) {
		System.out.println("Running Server");
		if (args[0] != null) {
			IRCServer myServer = new IRCServer();
			myServer.makeSocket(Integer.parseInt(args[0]));
			myServer.start();
		} else { System.out.println("Usage: IRCServer <Port>"); }
	}
}
