package com.example.inarow;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.StringTokenizer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class TicTacToe extends Activity {

	// constants
	public static final int DEFAULT_PORT = 20000;
	public static final int MAX_PACKET_SIZE = 512;
	private static final String SERVER_ADDRESS = "54.186.216.144";
	private InetSocketAddress serverSocketAddress;
	// Determines whether or not this player is first
	private static int first;

	// The game this player is playing
	private static int gameNumber;
	private static boolean gameOver;

	// Used for tagging in log
	private final String TAG = this.getClass().getSimpleName();

	private Context con = this;

	// This array represents the tictactoe board
	private int[] board = new int[9];

	private Button[] buttons = new Button[9];
	private Button connect;

	private Toast toast1;

	private ProgressDialog waiting;

	private DatagramSocket socket;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tic_tac_toe);
		this.waiting = new ProgressDialog(this);
		waiting.setCancelable(false);
		waiting.setTitle("Waiting");
		waiting.setMessage("Wait for opponent");
		Arrays.fill(board, -1);

		buttons[0] = (Button) findViewById(R.id.button1);
		buttons[1] = (Button) findViewById(R.id.button2);
		buttons[2] = (Button) findViewById(R.id.button3);
		buttons[3] = (Button) findViewById(R.id.button4);
		buttons[4] = (Button) findViewById(R.id.button5);
		buttons[5] = (Button) findViewById(R.id.button6);
		buttons[6] = (Button) findViewById(R.id.button7);
		buttons[7] = (Button) findViewById(R.id.button8);
		buttons[8] = (Button) findViewById(R.id.button9);
		connect = (Button) findViewById(R.id.button10);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tic_tac_toe, menu);
		return true;
	}

	public void onConnect(View v) {
		gameOver = false;
		waiting.show();
		Log.d(TAG, "show it!");
		connect.setEnabled(false);
		try {

				socket = new DatagramSocket(DEFAULT_PORT);
				new AsyncTask<DatagramSocket, Void, String>() {

					protected void onPreExecute() {
						serverSocketAddress = new InetSocketAddress(
								SERVER_ADDRESS, DEFAULT_PORT);

					}

					protected String doInBackground(
							DatagramSocket... arg0) {
						Log.d(TAG, "firstATask");
						String command = "JOIN";
						try {
							send(command, serverSocketAddress, DEFAULT_PORT);
						} catch (IOException e) {
							e.printStackTrace();
							Log.d(TAG, "IOException");
						}
						DatagramPacket packet = null;
						String payload = null;
						try {
							// create an empty UDP packet
							byte[] buf = new byte[TicTacToe.MAX_PACKET_SIZE];
							packet = new DatagramPacket(buf, buf.length);
							// call receive (this will poulate the packet with
							// the received
							// data, and the other endpoint's info)
							arg0[0].receive(packet);
							Log.d(TAG, "gotPacket");
							payload = new String(packet.getData(), 0,
									packet.getLength()).trim();
							Log.d(TAG, "gotPacket2");
						} catch (SocketException e) {
							e.printStackTrace();
							Log.d(TAG, "socketException");

						} catch (IOException e) {
							e.printStackTrace();
							Log.d(TAG, "IOException");
						}
						Log.d(TAG, "gotPacket3");
						return payload;
					}
	
					protected void onPostExecute(String payload) {
						Log.d(TAG, "gotPacket4");
						StringTokenizer st = new StringTokenizer(payload);
						String title = st.nextToken();
						if (title.startsWith("GAME")) {
							String game = st.nextToken();
							String xoro = st.nextToken();

							gameNumber = Integer.parseInt(game);
							first = Integer.parseInt(xoro);
						}
						Log.d(TAG, "first set");
					}

				}.execute(socket);
				
			ReceiveSocketTask rst = new ReceiveSocketTask();
			rst.execute(socket);

		} catch (SocketException e) {
			e.printStackTrace();
			Log.d(TAG, "SocketException");
		}
	}

	public void onMove(int spot, int xoro) {
		board[spot] = xoro;
		buttons[spot].setEnabled(false);
		if (xoro == 0) {
			 buttons[spot].setText("X");
		} else {
			 buttons[spot].setText("O");
		}

	}

	public void onClick_1(View v) {
		onMove(0, first);
		sendMove(0);
	}

	public void onClick_2(View v) {
		onMove(1, first);
		sendMove(1);
	}

	public void onClick_3(View v) {
		onMove(2, first);
		sendMove(2);
	}

	public void onClick_4(View v) {
		onMove(3, first);
		sendMove(3);
	}

	public void onClick_5(View v) {
		onMove(4, first);
		sendMove(4);
	}

	public void onClick_6(View v) {
		onMove(5, first);
		sendMove(5);
	}

	public void onClick_7(View v) {
		onMove(6, first);
		sendMove(6);
	}

	public void onClick_8(View v) {
		onMove(7, first);
		sendMove(7);
	}

	public void onClick_9(View v) {
		onMove(8, first);
		sendMove(8);
	}

	// send a string, wrapped in a UDP packet, to the specified remote endpoint
	private void send(String payload, InetSocketAddress address, int port)
			throws IOException {
		DatagramPacket txPacket = new DatagramPacket((payload).getBytes(),
				payload.length(), address);
		this.socket.send(txPacket);
	}

	private void sendMove(int spot) {
		if (!waiting.isShowing()) {
			waiting.show();
		}
		new MessageTask().execute("MSG " + gameNumber + " MOVE " + spot);
		if (checkWin()) {
			endGame(1);
		}
	}

	private void endGame(int winner) {
		if(waiting.isShowing())
		{
			waiting.dismiss();
		}
		String message = "You";
		if (winner==1) {
			message += " Won!";
		} else if(winner==0){
			message += " Lost!";
		} else {
			message += " Tied!";
		}
		toast1 = Toast.makeText(con, message, Toast.LENGTH_SHORT);
		toast1.show();
		for (int i = 0; i < buttons.length; i++) {
			buttons[i].setEnabled(false);
		}

		new MessageTask().execute("END");

	}

	private boolean checkWin() {
		boolean gameOver = true;
		if ((board[0] == board[1] && board[1] == board[2] && board[0] != -1)
				|| (board[3] == board[4] && board[4] == board[5] && board[3] != -1)
				|| (board[6] == board[7] && board[7] == board[8] && board[6] != -1)
				|| (board[0] == board[3] && board[3] == board[6] && board[0] != -1)
				|| (board[1] == board[4] && board[4] == board[7] && board[1] != -1)
				|| (board[2] == board[5] && board[5] == board[8] && board[2] != -1)
				|| (board[0] == board[4] && board[4] == board[8] && board[0] != -1)
				|| (board[2] == board[4] && board[4] == board[6] && board[2] != -1)) {
			TicTacToe.gameOver = true;
			return gameOver;
		}
		
		for(int i=0;i<board.length;i++)
		{
			if(board[i] == -1)
			{
				gameOver=false;
			}
		}
		
		//This means cat won, or there are no spots left
		if(gameOver)
		{
			endGame(2);
		}
		return gameOver;
	}

	private class MessageTask extends AsyncTask<String, Void, DatagramPacket> {

		protected DatagramPacket doInBackground(String... arg0) {
			try {
				send(arg0[0], serverSocketAddress, DEFAULT_PORT);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

	}

	private class ReceiveSocketTask extends
			AsyncTask<DatagramSocket, Void, Integer> {

		protected void onPreExecute() {

		}

		protected Integer doInBackground(DatagramSocket... arg0) {
			int spot = -1;
			try {
				boolean received=false;
				arg0[0].setSoTimeout(3000);
					// create an empty UDP packet
					byte[] buf = new byte[TicTacToe.MAX_PACKET_SIZE];
					DatagramPacket packet = new DatagramPacket(buf, buf.length);
					// call receive (this will populate the packet with the
					// received
					// data, and the other endpoint's info)
						send("POLL", serverSocketAddress, DEFAULT_PORT);

						try {
							arg0[0].receive(packet);
							received = true;
						} catch (SocketTimeoutException ste) {
						}
						if(received){
							spot = -2;
							String payload = new String(packet.getData(), 0,
									packet.getLength()).trim();
							send("ACK " + gameNumber + " " + payload,
									serverSocketAddress, DEFAULT_PORT);
		
							StringTokenizer st = new StringTokenizer(payload);
							String message = st.nextToken();
						
							Log.d(TAG, "Got Move");
		
							if (message.startsWith("MOVE")) {
								spot = Integer.parseInt(st.nextToken());
		
								Log.d(TAG, "Found Move");
								}
						}
			
			} catch (IOException e) {
				e.printStackTrace();
			} 
			return spot;
		}

		protected void onPostExecute(Integer i) {
			if(i==-2)
			{
				if(waiting.isShowing())
				{
					waiting.dismiss();
				}
			}

			if(i >= 0 && i < board.length) 
			{
				if(waiting.isShowing())
				{
					waiting.dismiss();
				}
				onMove(i, Math.abs(first-1));
			}
			if (checkWin()) {
				endGame(0);
				socket.close();
			} else {
				new ReceiveSocketTask().execute(socket);
			}
		}

	}

}
