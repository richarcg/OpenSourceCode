//Author: Caleb Richard
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;


public class MessageWorkerThread extends Thread {


	//Used to get IP address and port to respond to
	private DatagramPacket rxPacket;
	
	//used to close socket if shutdown request is recieved
	private DatagramSocket socket;

	/**
	 * Constructor for MessageWorkerThread
	 * @param packet - packet sent to thread
	 * @param socket - socket that recieved packet
	 */
	public MessageWorkerThread(DatagramPacket packet, DatagramSocket socket) {
		this.rxPacket = packet;
		this.socket = socket;
	}
	
	public void run() {
		
		// convert the rxPacket's payload to a string
		String payload = new String(rxPacket.getData(), 0, rxPacket.getLength())
				.trim();
		
		
		//The following set of if statements allow the server to react
		//to varying kinds of requests. Once a request is received from
		//the client, the appropriate method is called in order to serve
		//the request

		
		if(payload.startsWith("CHANGEIP"))
		{
			changeIP(payload);
			return;
		}
		
		if(payload.startsWith("JOIN"))
		{
			registerUser(payload);
			return;
		}
		
		if(payload.startsWith("MSG"))
		{
			sendMessage(payload);
			return;
		}
		
		if(payload.startsWith("POLL"))
		{
			try {
				getMessages();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return;
		}
		
		if(payload.startsWith("ACK"))
		{
			acknowledge(payload);
			return;
		}
		
		if(payload.startsWith("END"))
		{
			end(payload);
		}

		
		if(payload.startsWith("SHUTDOWN"))
		{
			try {
				shutdown();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}
				
		onBadRequest(payload);
	}
	
	// send a string, wrapped in a UDP packet, to the specified remote endpoint
	public void send(String payload, InetAddress address, int port)
			throws IOException {
		DatagramPacket txPacket = new DatagramPacket((payload).getBytes(),
				payload.length(), address, port);
		this.socket.send(txPacket);
	}
	
	/**
	 * Shuts down server if request is sent from the
	 * loopback request, otherwise, sends a message
	 * saying client is not authorized to do so
	 * @throws IOException
	 */
	private void shutdown() throws IOException {
		if(this.rxPacket.getAddress().isLoopbackAddress())
		{
			send("Server shutting down." + "\n",this.rxPacket.getAddress(),
					this.rxPacket.getPort());
			this.socket.close();
		} else {
			send("You are not authorized to shutdown the server." + "\n",this.rxPacket.getAddress(),
					this.rxPacket.getPort());
		}
	}
	
	/**
	 * Changes the IP Address and Port associated
	 * with the Client's ClientEndPoint object.
	 * The client must send the original id they
	 * were sent when they registered. Sends an invalid key 
	 * message if id does not match any client.
	 * @param payload - payload of packet
	 */
	private void changeIP(String payload) {
		StringTokenizer st = new StringTokenizer(payload);
		st.nextToken();
		String group = st.nextToken();
		int id = Integer.parseInt(group);
		ClientEndPoint change=null;
		
		//searches clientEndPoints for client with associated id.
		for(Map.Entry<Integer, ClientEndPoint> entry : MessageServer.clientEndPoints.entrySet())
		{
			if(entry.getValue().getID() == id)
			{
				change = entry.getValue();
			}
		}
		
		if(change == null)
		{
			try {
				send("-FAILURE: not a valid key" + "\n", this.rxPacket.getAddress(),
						this.rxPacket.getPort());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else
		{
			//uses ClientEndPoint method to change clients port and IP. 
			//id is not changed, so the user can rejoin later and not lose
			///any messages.
			change.changePortIP(this.rxPacket.getAddress(),
						this.rxPacket.getPort());
			try {
				send("+SUCCESS: IP and Port changed" + "\n", this.rxPacket.getAddress(),
						this.rxPacket.getPort());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * Registers user with name they pass in.
	 * The user is then told what their id is.
	 * @param payload - payload of packet
	 */
	private void registerUser(String payload) {
		StringTokenizer st = new StringTokenizer(payload);
		//st.nextToken();
		//String name = st.nextToken();
		//creates new ClientEndPoint that will be keyed with the clients current
		//ip and port number using an exclusive or.
		ClientEndPoint newClient = new ClientEndPoint(this.rxPacket.getAddress(), 
				this.rxPacket.getPort());
		int key = newClient.hashCode();
		MessageServer.clientEndPoints.put(key, newClient);
		int id=findID(key);
		boolean groupFound=false;
		int first=0;
		int size = MessageServer.groups.size();

		
		for (Map.Entry<String,MessageGroup> entry : MessageServer.groups.entrySet())
		{
			if(entry.getValue().size() < 2)
			{
				entry.getValue().addMember(id);
				groupFound=true;
				first=1;
				size-=1;
			}
		}
		

		
		if(!groupFound)
		{
			MessageGroup newGroup = new MessageGroup(""+size,2);
			MessageServer.groups.put(""+size,newGroup);
			MessageServer.groups.get(""+size).addMember(id);			
		}

		try {
			send("GAME "+ size+" "+first,
					this.rxPacket.getAddress(),
					this.rxPacket.getPort());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(groupFound)
		{
			sendMessage("MSG " + size + " TURN");		
		}
		
		
	}
	
	public static void end(String payload)
	{
		StringTokenizer st = new StringTokenizer(payload);
		st.nextToken();
		String game = st.nextToken();
		MessageServer.groups.remove(game);
	}
	
	/**
	 * Unregisters the user from the MessageServer.
	 * This also deletes them from all the groups they
	 * were previously in.
	 * @param payload - payload of packet
	 */
//	private void unregisterUser(String payload) {
//		
//		ClientEndPoint newClient = new ClientEndPoint(this.rxPacket.getAddress(), 
//				this.rxPacket.getPort(), "");
//		int key = newClient.hashCode();
//		int id=findID(key);
//		
//		for (Map.Entry<String,MessageGroup> entry : MessageServer.groups.entrySet())
//		{
//			if(entry.getValue().inGroup(id))
//			{
//				entry.getValue().deleteMember(id);
//			}
//		}
//		
//		//Deletes the client's ClientEndPoint object
//		MessageServer.clientEndPoints.remove(key);
//		
//		try {
//			send("+SUCCESS: Unregistered" + "\n", this.rxPacket.getAddress(),
//					this.rxPacket.getPort());
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
	
	/**
	 * Sends message to a group
	 * @param payload - payload of packet
	 */
	private void sendMessage(String payload){
		StringTokenizer st = new StringTokenizer(payload);
		st.nextToken();
		String group = st.nextToken();
		//System.out.println(group);
		String message = payload.substring(("MSG " + group).length() + 1,
				payload.length()).trim();
		
		ClientEndPoint newClient = new ClientEndPoint(this.rxPacket.getAddress(), 
				this.rxPacket.getPort());
		int key = newClient.hashCode();
		
		//calls the addMessage function of the MessageGroup associated
		//with the message.
		MessageServer.groups.get(group).addMessage(key, message);
		
//		try {
//			send("+SUCCESS: SENT " + message + " TO "  + group + "\n", this.rxPacket.getAddress(),
//					this.rxPacket.getPort());
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
	
	/**
	 * Sends messages waiting for a client to them
	 * Once POLL is requested. This is done with a TimerTask
	 * which will repeat every 10 seconds until the message is 
	 * recieved
	 * @throws InterruptedException
	 */
	private void getMessages () throws InterruptedException {
		ClientEndPoint newClient = new ClientEndPoint(this.rxPacket.getAddress(), 
				this.rxPacket.getPort());
		int key = newClient.hashCode();
		int id=findID(key);
		
		final Timer timer = new Timer();        
		final InetAddress address = this.rxPacket.getAddress();
		final int port = this.rxPacket.getPort();
		final int iden = id;
		//for every group the user is in a TimerTask is created in turn for each message
		//waiting for the user. If the message isn't acknowledged within 10 seconds, the
		//server sends the message again. It should be noted that one could also 
		//implement functionality such that if the message isn't acknowledged after so long
		//the message is deleted anyways.
		for (final Map.Entry<String,MessageGroup> entry : MessageServer.groups.entrySet())
		{
			if(entry.getValue().inGroup(id))
			{String messageLeft = entry.getValue().getMessage(id);
				if(messageLeft!=null)
				{
					final String message = entry.getValue().getMessage(id);
					if(message != null)
					{
						//Anonymous TimerTask class that sends the message every
						//10 seconds until it is acknowledged.
						timer.scheduleAtFixedRate(new TimerTask() {
							public void run() {
								if(message == entry.getValue().getMessage(iden))
								{
									try {
										send(message + "\n", address, port);
									} catch (IOException e) {
										e.printStackTrace();
									}
								} else
								{
									timer.cancel();
								}
							}
						}, 0, 6000);	
					}
					messageLeft = entry.getValue().getMessage(id);
				}
			}
		}
	}
	
	/**
	 * Acknowledges a message sent to the client,
	 * deletes that message from the particular client's
	 * list of messages.
	 * @param payload - payload of packet
	 */
	private void acknowledge(String payload) {
		StringTokenizer st = new StringTokenizer(payload);
		st.nextToken();
		String group = st.nextToken();
		String message = payload.substring(("ACK " + group).length() + 1,
				payload.length()).trim();
		ClientEndPoint newClient = new ClientEndPoint(this.rxPacket.getAddress(), 
				this.rxPacket.getPort());
		int key = newClient.hashCode();
		
		int id = MessageServer.clientEndPoints.get(key).getID();
		MessageServer.groups.get(group).deleteMessage(id ,message);
		
		
	}
	
	/**
	 * Tells the client that their request
	 * was not understood by the server.
	 * @param payload - payload of packet
	 */
	private void onBadRequest(String payload) {
		try {
			send("BAD REQUEST\n", this.rxPacket.getAddress(),
					this.rxPacket.getPort());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Given the key to a ClientEndPoint,
	 * returns the id of said ClientEndPoint
	 * @param key - key of ClientEndPoint
	 * @return - id of ClientEndPoint
	 */
	private int findID(int key) {
		for(Map.Entry<Integer, ClientEndPoint> entry : MessageServer.clientEndPoints.entrySet())
		{
			if(entry.getValue().hashCode() == key)
			{
				return entry.getValue().getID();
			}
		}
		return 0; 
	}

}

