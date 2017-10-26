import java.net.*;
import java.io.*;

public class IRCClient {
	
	private Socket clientSocket;
	private PrintWriter outputToServer;
	private BufferedReader inputFromServer;
	
	public static void main(String[] args) {
		IRCClient myClient = new IRCClient();
		myClient.run();
	}

	public void run() {
		setSocket("localhost", 2004);
		try {
			outputToServer = new PrintWriter(clientSocket.getOutputStream(), true);
			inputFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			
			
			System.out.println("**************************************************\n                   Welcome to \n   " + inputFromServer.readLine() + "\n**************************************************");
			
			outputToServer.println("Harry");
			outputToServer.println("Hello, World");
			outputToServer.println("!help");
			
			while(true) 
				System.out.println(inputFromServer.readLine());
			
		} catch (IOException e) {
			System.out.println("Cannot find server");
			System.out.println(e.toString());
		}
	}
	
	private void setSocket(String ip, int port) {
		try {
			clientSocket = new Socket(ip,port);
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}
	
}