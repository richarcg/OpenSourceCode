//Author: Caleb Richard
//Date: 2/12/2014
//CS 283 Assignment 1

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MTServer {
	
	private static final int PORT = 4444;

	public static void main(String[] args) throws IOException {
		
		ServerSocket ss = new ServerSocket(PORT);
		System.out.println("MAIN: ServerSocket created");
		while(true) {
			System.out.println("MAIN: Waiting for client connection on port " + PORT);				
			Socket cs = ss.accept();
			System.out.println("MAIN: Client connected");
			new ServerThread(cs).start();
		}
	}
}
