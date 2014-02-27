package bananabank.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.locks.ReentrantLock;

public class BBServerThread extends Thread {
	
	private static BananaBank bananaBank;
	private Socket clientSocket; 
	private static ServerSocket serSocket;
	private static Map<Integer, ReentrantLock> locks;
	
	/**
	 * Creates a new BBServerThread
	 * @param bb - BananaBank object that is used
	 * @param ls - Map of account number to ReentrantLock
	 * @param cs - ClientSocket that is used
	 * @param ss - ServerSocket that is used
	 */
	public BBServerThread (BananaBank bb, Map<Integer, ReentrantLock> ls, Socket cs, ServerSocket ss)
	{
		bananaBank = bb;
		clientSocket = cs;
		serSocket = ss;
		locks = ls;
	}
	
	public void run() {
		try {

			PrintStream ps = new PrintStream(clientSocket.getOutputStream());
			BufferedReader r = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));
			String line;

			while ((line = r.readLine()) != null) {
				
				StringTokenizer st = new StringTokenizer(line);
				String first = st.nextToken();
				
				//Client should only be able to issue shutdown command if it is from 127.0.0.1
				if(first.equals("SHUTDOWN") && clientSocket.getInetAddress().isLoopbackAddress())
				{
					BananaBankServer.shutdownThread = (BBServerThread) Thread.currentThread();
					serSocket.close();
					//acquires lock for shutdown sequence
					locks.get(0).lock();
					try {
						 shutdown(ps);
					} finally {
						locks.get(0).unlock();
					}
				}else if (first.equals("SHUTDOWN")) {
					ps.print("Command not Authorized\n");
				} else
				{

					int amount = Integer.parseInt(first);
					int source = Integer.parseInt(st.nextToken());
					int dest = Integer.parseInt(st.nextToken());
					
					
					
					Account srcAccount = bananaBank.getAccount(source);
					Account destAccount = bananaBank.getAccount(dest);
					
					//getAccount will return null if account 
					//number isn't valid.
					if(srcAccount == null || destAccount == null)
					{
						ps.print("Invalid Account number\n");
					} else if (srcAccount.getBalance() < amount)
					{
						ps.print("Insufficient funds\n");
					} else{
						
						//begins thread-safe transfer of funds.
						transfer(srcAccount, destAccount, amount);
						
						ps.print(amount + " transferred from account " + source +
								" to account " + dest + "\n");
					}
				}
				
			}
			ps.close();
			r.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method transfers funds between accounts, acquiring locks
	 * for each account that is being used to allow for concurrent
	 * transfers to happen across threads on different accounts.
	 * @param src - account money is being drawn from.
	 * @param dest - account money is being deposited in
	 * @param amount - amount of money being transfered
	 */
	public static void transfer(Account src, Account dest, int amount){
		ReentrantLock l1; 
		ReentrantLock l2; 
		
		if(src.getAccountNumber() < dest.getAccountNumber())
		{
			l1 = locks.get(src.getAccountNumber());
			l2 = locks.get(dest.getAccountNumber());
		} else {
			l1 = locks.get(dest.getAccountNumber());
			l2 = locks.get(src.getAccountNumber());
		}
		l1.lock();
		l2.lock();
		try {
			src.transferTo(amount, dest);
		} finally {
			l1.unlock();
			l2.unlock();
		}
	}
	

	/**
	 * This method begins the shutdown procedure, which consists of:
	 * 1: saving the bank's state
	 * 2:computing the bank's total amount
	 * 3: returning this sum to the client who requested shutdown. 
	 * @param ps - PrintStream to client that requested shutdown.
	 * @throws IOException
	 */
	public static void shutdown(PrintStream ps) throws IOException {
		System.out.println("Shutdown started");
		bananaBank.save(BananaBankServer.ACCOUNTFILE);
		int sum=0;	
		Collection<Account> accCollection = bananaBank.getAllAccounts();
		for (Account account: accCollection)
		{
			sum += account.getBalance();
		}
		ps.print(sum + "\n");
		
	}

}
