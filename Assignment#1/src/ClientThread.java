import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class ClientThread extends Thread {

	private static final int SAMPLE_SIZE = 1000;
	private Socket clientSocket;

	public ClientThread(Socket cs) {
		this.clientSocket = cs;
	}

	public void run() {

			System.out.println("Client is connected to the server");
			PrintStream ps;
			BufferedReader r;
			try {
				ps = new PrintStream(clientSocket.getOutputStream());

				r = new BufferedReader(new InputStreamReader(
						clientSocket.getInputStream()));


				for (int i = 0; i < SAMPLE_SIZE; i++) {
	
					ps.println("Hello World!!!!");
					String upper;
					upper = r.readLine();
					System.out.println("Recieved " + upper);
				}
				ps.close();
				r.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}
