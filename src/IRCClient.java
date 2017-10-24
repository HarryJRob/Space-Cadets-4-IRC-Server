import java.net.*;
import java.io.*;

public class IRCClient {
	private ServerSocket serverSocket;
	
	public static void main(String[] args) {
		System.out.println("Running Client");
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
	
}