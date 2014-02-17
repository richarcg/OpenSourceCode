//Author: Caleb Richard
//Date: 2/12/2014
//CS 283 Assignment 1

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread extends Thread {

	private Socket clientSocket;

	public ServerThread(Socket cs) {
		this.clientSocket = cs;
	}

	@Override
	public void run() {
		System.out.println("WORKER" + Thread.currentThread().getId()
				+ ": Worker thread starting");
		try {

			PrintStream ps = new PrintStream(clientSocket.getOutputStream());
			BufferedReader r = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));
			String line;

			while ((line = r.readLine()) != null) {
				System.out.println("Received: " + line);
				ps.println(line.toUpperCase());
			}
			System.out.println("Client disconnected");
			ps.close();
			r.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("WORKER" + Thread.currentThread().getId()
				+ ": Worker thread finished");
	}

}