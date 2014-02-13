//Author: Caleb Richard
//Date: 2/12/2014
//CS 283 Assignment 1

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleServer {

	private static final int PORT = 4444;

	public static void main(String[] args) throws IOException {
		
		ServerSocket ss = new ServerSocket(PORT);
		System.out.println("ServerSocket created");
		
		while (true) {
			System.out.println("Waiting for client connection on port " + PORT);
			Socket cs = ss.accept();
			System.out.println("Client connected");

			PrintStream ps = new PrintStream(cs.getOutputStream());
			BufferedReader r = new BufferedReader(new InputStreamReader(
					cs.getInputStream()));
			String line;
			
			//This code was added to simply make the server take longer
			//for each request. Without it, the overhead cost of 
			//spawning threads overshadowed the benifits, because the
			//server could handle requests faster than the client could
			//make them.
			for(int i=0;i<10000000;i++);
			while ((line = r.readLine()) != null) {
				System.out.println("Received: " + line);
				ps.println(line.toUpperCase());
			}
			System.out.println("Client disconnected");
			ps.close();
			r.close();
		}
	}
}
