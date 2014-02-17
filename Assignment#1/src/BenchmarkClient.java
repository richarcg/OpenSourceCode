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
	private static final int SAMPLE_SIZE = 1000;
	private static final int THREAD_COUNT = 3;

	private static ClientThread[] ctArray;

	public static void main(String[] args) throws UnknownHostException,
			IOException, InterruptedException {

		Socket cs;
		ctArray = new ClientThread[THREAD_COUNT];

		long timeBefore = System.nanoTime();

		for (int i = 0; i < THREAD_COUNT; i++) {
			cs = new Socket("localhost", PORT);
			ctArray[i] = new ClientThread(cs);
			ctArray[i].start();
		}

		for (int i = 0; i < THREAD_COUNT; i++) {
			ctArray[i].join();
		}

		long timeAfter = System.nanoTime();
		long timeTaken = timeAfter - timeBefore;

		long averageTime = timeTaken / (THREAD_COUNT * SAMPLE_SIZE);

		System.out.println("Total time taken:" + timeTaken + " ns");
		System.out.println("Average time to serve request:" + averageTime
				+ " ns");

	}

}
