package bananabank.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.text.html.HTMLDocument.Iterator;

public class BananaBankServer {

	public static final String ACCOUNTFILE = "accounts.txt";
	public static final int PORT = 2000;
	
	//The thread that receives the Shutdown command will
	//assign itself to this variable.
	public static BBServerThread shutdownThread = null;
	
	public static void main(String[] Args) throws IOException {
		
		BananaBank bananaBank = new BananaBank(ACCOUNTFILE);
		Collection<Account> accountCollection = bananaBank.getAllAccounts();
		
		//Since a lock is needed for every account, a map is used to keep track
		//of the locks
		Map<Integer, ReentrantLock> locks = new HashMap<Integer, ReentrantLock>();
		for( Account account : accountCollection)
		{
			locks.put(account.getAccountNumber(), new ReentrantLock());
		}
		
		//This serves as the shutdown lock
		locks.put(0, new ReentrantLock());
		
		ArrayList<BBServerThread> threads = new ArrayList<BBServerThread>();
		ServerSocket ss = new ServerSocket(PORT);
		System.out.println("MAIN: ServerSocket created");
		
		//The shutdown lock is acquired so the shutdown thread does
		//not continue until all other threads are finished.
		locks.get(0).lock();
		
		try {
			while (true) {
				System.out.println("MAIN: Waiting for client connection on port "
						+ PORT);
				Socket cs = ss.accept();
				System.out.println("MAIN: Client connected");
				BBServerThread t = new BBServerThread(bananaBank, locks, cs, ss);
				t.start();
				threads.add(t);
			}
			
		//The shutdown thread closes the ServerSocket, causing the
		//SocketException to be thrown.
		} catch (SocketException e) {
			
			//Every thread that is not the shutdown thread
			//is stopped
			for (BBServerThread serverThread : threads)
			{
				if(serverThread != shutdownThread)
				{
					try {
						serverThread.join();
					} catch (InterruptedException e1) {}
				}
			}
			
			//Now that every other thread is done, the 
			//shutdown thread should be allowed to continue.
			locks.get(0).unlock();
		}
		
	}
}
