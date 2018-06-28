package server;
import java.io.*;
import java.net.*;
import java.util.*;
import model.*;

/**
 * A Server for the NetPaint Project that can handle multiple Clients. It
 * collects PaintObjects from the Clients, adds them to its Vector, and
 * continuously sends the updated Vector to the Clients.
 * 
 * @author Rajeev Ram, SL: Junting Lye, 02/27/18
 */
public class Server {

	// These are the instance variables
	private final static int PORTNUMBER = 4001;
	private ServerSocket myServerSocket;
	private Vector<PaintObject> paintObjectVector;
	private Vector<ObjectOutputStream> clientOutputStreams;
	
	// This is the constructor
	public Server() {
		try {
			myServerSocket = new ServerSocket(PORTNUMBER);
			paintObjectVector = new Vector<PaintObject>();
			clientOutputStreams = new Vector<ObjectOutputStream>();
			//System.out.println("The server has been started.");
		} 
		catch (IOException e) {
			System.err.println("Something went wrong in the server constructor.");
			e.printStackTrace();
		}
	}

	
	public static void main(String args[]) {
		
		// Instantiate a new server object
		Server mainServer = new Server();
		
		// Continuously wait for new clients
		while (true) {
			try {
				//System.out.println("Waiting...");
				Socket newClient = mainServer.myServerSocket.accept();
				//System.out.println("A new client has been accepted.");
				
				ObjectOutputStream outputToClient = new ObjectOutputStream(newClient.getOutputStream());
				outputToClient.writeObject(mainServer.paintObjectVector);
				//System.out.println("The current paint object vector has been written to the new client.");
				mainServer.clientOutputStreams.add(outputToClient);
				
				ObjectInputStream inputFromClient = new ObjectInputStream(newClient.getInputStream());
				ClientHandler runnable = mainServer.new ClientHandler(inputFromClient);
				Thread thread = new Thread(runnable);
				thread.start();
				//System.out.println("A new client reader thread has been started.");
			}
			catch (IOException e) {
				System.err.println("Something went wrong in the server main method.");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * This runnable is used to receive new PaintObjects from all of the clients. Each
	 * client gets its own dedicated handler that is started in a new thread.
	 * @author Rajeev Ram
	 */
	private class ClientHandler implements Runnable {
		
		ObjectInputStream inputFromClient;
		
		ClientHandler(ObjectInputStream newStream) {
			this.inputFromClient = newStream;
		}

		@Override
		public void run() {
			while (true) {
				// While true loop receives and sends out new paints object continuously
				try {
					PaintObject newPaintObject = (PaintObject) this.inputFromClient.readObject();
					if ( !paintObjectVector.contains(newPaintObject) ) {
						paintObjectVector.add(newPaintObject);
					}
					//System.out.println("A new paint object from a client has been added to the vector.");
					for (ObjectOutputStream newStream : clientOutputStreams) {
						newStream.reset();
						newStream.writeObject(paintObjectVector);
					}
					//System.out.println("The updated paint object vector has been sent out to all clients.");
				} 
				catch (ClassNotFoundException | IOException e) {
					System.err.println("Something went wrong in the client handler.");
					e.printStackTrace();
				}
			}
			
		}
		
	}

}
