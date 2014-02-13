//Author: Caleb Richard
//Date: 2/12/2014
//CS 283 Assignment 1

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class SimpleClient {
	
	private static final int PORT = 4444;

	public static void main(String[] args) throws UnknownHostException, IOException {
		Socket s = new Socket("localhost", PORT);
		System.out.println("Client is connected to the server");
		PrintStream ps = new PrintStream(s.getOutputStream());
		BufferedReader r = new BufferedReader(new InputStreamReader(
				s.getInputStream()));
		ps.println("Hello World!!!!");
		String upper;
		upper = r.readLine();
		ps.close();
		r.close();
		System.out.println("Recieved " + upper);
	}
}
