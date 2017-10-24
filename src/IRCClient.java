import java.net.*;
import java.io.*;

public class IRCClient {
	
	private Socket clientSocket;
	private PrintWriter clientOutput;
	private BufferedReader clientInput;
	
	public static void main(String[] args) {
		System.out.println("Running Client");
		IRCClient myClient = new IRCClient();
		myClient.run();
	}

	public void run() {
		setSocket("localhost", 2004);
		try {
			clientOutput = new PrintWriter(clientSocket.getOutputStream(), true);
			clientInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			
			//clientOutput.println("Hello, World");
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