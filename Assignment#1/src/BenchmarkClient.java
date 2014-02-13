//Author: Caleb Richard
//Date: 2/12/2014
//CS 283 Assignment 1

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;


public class BenchmarkClient {

	private static final int PORT = 4444;
	private static int SAMPLE_SIZE = 100;

	
	public static void main(String[] args) throws UnknownHostException, IOException {
		
		long longDelay=0;
		long shortDelay=0;
		long averageDelay=0;
		long timeBefore, timeAfter, timeTaken;
		for(int i=0; i<SAMPLE_SIZE; i++)
		{
			timeBefore = System.nanoTime();
			Socket s = new Socket("localhost", PORT);

			System.out.println("Client is connected to the server");
			PrintStream ps = new PrintStream(s.getOutputStream());
			BufferedReader r = new BufferedReader(new InputStreamReader(
					s.getInputStream()));

			ps.println("Hello World!!!!");
			String upper;
			upper = r.readLine();
			System.out.println("Recieved " + upper);
			ps.close();
			r.close();
			
			timeAfter = System.nanoTime();
			timeTaken = timeAfter-timeBefore;
			
			if(shortDelay == 0 || timeTaken < shortDelay)
			{
				shortDelay = timeTaken;
			}
			
			if(timeTaken > longDelay)
			{
				longDelay = timeTaken;
			}
			
			averageDelay += timeTaken;
			

		}

		averageDelay = averageDelay/SAMPLE_SIZE;
		
		System.out.println("Shortest time to serve request:" + shortDelay + " ns");
		System.out.println("Longest time to serve request:" + longDelay + " ns");
		System.out.println("Average time to serve request:" + averageDelay + " ns");
		
		
	
	}

}
