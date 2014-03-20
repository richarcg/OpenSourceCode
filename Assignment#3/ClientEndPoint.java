//Author: Caleb Richard
import java.net.InetAddress;
import java.util.Random;

//Used to store Client information
//including their current IP and port,
//the name of the client, and the client's
//id
public class ClientEndPoint {
	protected InetAddress address;
	protected int port;
	protected final String name;
	protected final int id;
	
	/**
	 * Constructor for ClientEndPoint
	 * @param addr - Client's IP address
	 * @param port - Client's Port
	 * @param name - Client's Name
	 */
	public ClientEndPoint(InetAddress addr, int port, String name) {
		this.address = addr;
		this.port = port;
		this.name= name;
		Random r = new Random();
		this.id = r.nextInt(1000) + 1;
	}
	
	/**
	 * Accessor method for Client name
	 * @return - Client's name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Accessor method for Client's id
	 * @return - Client's id
	 */
	public int getID() {
		return this.id;
	}

	/**
	 * Provides a hashcode for the client based on the current IP
	 * address and port of Client.
	 */
	public int hashCode() {
		// the hashcode is the exclusive or (XOR) of the port number and the hashcode of the address object
		return this.port ^ this.address.hashCode();
	}
	
	/**
	 * Changes the clients' IP address and Port
	 * @param addr - new IP address
	 * @param port - new Port
	 */
	public void changePortIP(InetAddress addr, int port) {
		this.address = addr;
		this.port = port;
	}
	
	
}
