import java.net.*;
import java.io.*;

public class IRCClient {
	
	private Socket clientSocket;
	private PrintWriter outputToServer;
	private BufferedReader inputFromServer;
	private listener serverInputListener;
	private BufferedReader inputFromConsole;
	
	public static void main(String[] args) {
		IRCClient myClient = new IRCClient();
		myClient.run();
	}

	public void run() {
		setSocket("localhost", 2004);
		try {
			outputToServer = new PrintWriter(clientSocket.getOutputStream(), true);
			inputFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			serverInputListener = new listener();
			inputFromConsole = new BufferedReader(new InputStreamReader(System.in));
			
			System.out.println("**************************************************\n                   Welcome to \n   " + inputFromServer.readLine() + "\n**************************************************");
			outputToServer.println("Harry");
			
			serverInputListener.run();

			
			/* Testing
			outputToServer.println("Hello, World");
			outputToServer.println("!help");
			outputToServer.println("!changeNick 123");
			outputToServer.println("!adminLogin test");
			outputToServer.println("!setServerName test123");
			outputToServer.println("Hello, World");
			*/

			
			while(true) {
				System.out.println("Waiting for input");
				String curInput = inputFromConsole.readLine();
				if (curInput == "!quit" || curInput == null) {
					System.out.println("IRCClient exiting");
					System.exit(0);
				} else {
					outputToServer.println(curInput);
				}
			}
			
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
	
	private class listener implements Runnable {

		@Override
		public void run() {
			while(true)
				try {
					String curLine = inputFromServer.readLine();
					if (curLine != "" && curLine != null) {
						System.out.println(curLine);
					} 
				} catch (IOException e) {
					e.printStackTrace();
					break;
				}
		}
		
	}
	
}