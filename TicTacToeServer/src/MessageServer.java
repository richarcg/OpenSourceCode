//Author: Caleb Richard
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class MessageServer {

	// constants
	public static final int DEFAULT_PORT = 20000;
	public static final int MAX_PACKET_SIZE = 512;

	// port number to listen on
	protected int port;
	
	//This is the collection of groups that exist in the server
	// note that this is synchronized, i.e. safe to be read/written from
	// concurrent threads without additional locking
	public static final Map<String,MessageGroup> groups = Collections.
			synchronizedMap(new HashMap<String, MessageGroup>());
	
	
	
	// set of clientEndPoints
	// note that this is synchronized, i.e. safe to be read/written from
	// concurrent threads without additional locking
	protected static final Map<Integer, ClientEndPoint> clientEndPoints = Collections
			.synchronizedMap(new HashMap<Integer,ClientEndPoint>());

	// constructor
	MessageServer(int port) {
		this.port = port;
	}
	
	public void start() throws IOException {
		DatagramSocket socket = null;
		try {
			// create a datagram socket, bind to port port. See
			// http://docs.oracle.com/javase/tutorial/networking/datagrams/ for
			// details.

			socket = new DatagramSocket(port);

			// receive packets in an infinite loop
			while (true) {
				// create an empty UDP packet
				byte[] buf = new byte[MessageServer.MAX_PACKET_SIZE];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				// call receive (this will poulate the packet with the received
				// data, and the other endpoint's info)
				socket.receive(packet);
				// start up a worker thread to process the packet (and pass it
				// the socket, too, in case the
				// worker thread wants to respond)
				MessageWorkerThread t = new MessageWorkerThread(packet, socket);
				t.start();
			}
		} catch (SocketException se) {
			// we jump out here if there's an error, or if the worker thread (or
			// someone else) closed the socket
		} finally {
			if (socket != null && !socket.isClosed())
				socket.close();
		}
	}

	
	// main method
	public static void main(String[] args) throws IOException {
		int port = MessageServer.DEFAULT_PORT;

		// check if port was given as a command line argument
		if (args.length > 0) {
			try {
				port = Integer.parseInt(args[0]);
			} catch (Exception e) {
				System.out.println("Invalid port specified: " + args[0]);
				System.out.println("Using default port " + port);
			}
		}

		// instantiate the server
		MessageServer server = new MessageServer(port);

		System.out
				.println("Starting server. Connect with netcat (nc -u localhost "
						+ port
						+ ") or start multiple instances of the client app to test the server's functionality.");

		// start it
		server.start();

	}

}

