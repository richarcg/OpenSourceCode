package bananabank.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class BananaBankClient {

	
	private static final int PORT = 2000;
	private static final String HOST = "localhost";
	
	public static void main(String[] args) throws IOException{
		Socket s = new Socket(HOST, PORT);
		System.out.println("Client is connected to the server");
		PrintStream ps = new PrintStream(s.getOutputStream());
		BufferedReader r = new BufferedReader(new InputStreamReader(
				s.getInputStream()));
		ps.print("50 11111 22222\n");
		String confirmation;
		confirmation = r.readLine();
		System.out.println(confirmation);
		ps.print("100 33333 55555\n");
		confirmation = r.readLine();
		System.out.println(confirmation);
		ps.print("4 44444 11111\n");
		confirmation = r.readLine();
		System.out.println(confirmation);
		ps.print("SHUTDOWN\n");
		confirmation = r.readLine();
		System.out.println(confirmation);
		ps.close();
		r.close();	}
}
