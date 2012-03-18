package network;

import java.io.*;
import java.net.*;
import java.util.*;

import phyObj.Vector2f;

public class Peer extends Observable{
	/**
	 * The default port number for incoming network connections.
	 */
	public static final int DEFAULT_SERVER_PORT = 5507;
	/**
	 * The size of the ID/key space in bits.
	 */
	public static final int ID_SPACE = 10;
	/**
	 * The largest valid ID/key + 1.
	 */
	public static final long ID_LIMIT = 1 << ID_SPACE;
	/**
	 * The number of entries in the finger table, 1 <= n <= ID_SPACE.
	 */
	public static final int FINGER_ENTRIES = ID_SPACE;
	/**
	 * The period between invocations of background tasks, e.g., stabilize, fix fingers.
	 */
	public static final int BACKGROUND_TASK_PERIOD = 10000;
	
	public static final int TIME_OUT = 5;
	/**
	 * IP address detection method:
	 * (1) InetAddress.getLocalHost()
	 * (2) Querying whatismyip.org
	 * (3) First peer connection
	 */
	private static final int IP_DETECTION = 3;

	private PeerInformation myInfo;
	private ServerSocket serverSocket;
	private Thread incomingThread;
	private Thread periodicThread;
	public boolean logEnabled;
	private PeerInformation[] finger;
	private PeerInformation successor;
	private PeerInformation predecessor;
	private int next;
	private int my_port;

	
	
	
	
	
	
	public Peer() {
		myInfo = new PeerInformation();
//		logEnabled = true;
		finger = new PeerInformation[FINGER_ENTRIES];
	}
	
	
	
	

	public boolean createNetwork() {
		return createNetwork((long)(ID_LIMIT * Math.random()));
	}

	public boolean createNetwork(long id) {
		if (serverSocket == null)
			if (!startServer())
				return false;
		myInfo.id = id;
		successor = myInfo;
		if (logEnabled)
			logMessage("Established network @ " + myInfo.id);
		//else
			//System.out.println("Established network @ " + myInfo.id);
		return true;
	}

	
	
	
	
	
	public boolean connectToNetwork(String host) throws IOException, InterruptedException {
		return connectToNetwork(host, DEFAULT_SERVER_PORT);
	}

	public boolean connectToNetwork(String host, long id) throws IOException, InterruptedException {
		return connectToNetwork(host, DEFAULT_SERVER_PORT, id);
	}

	public boolean connectToNetwork(String host, int port) throws IOException,
			InterruptedException {

		return connectToNetwork(InetAddress.getByName(host), port);
	}

	public boolean connectToNetwork(String host, int port, long id)
			throws IOException, InterruptedException {

		return connectToNetwork(InetAddress.getByName(host), port, id);

	}

	public boolean connectToNetwork(InetAddress host) throws IOException, InterruptedException {
		return connectToNetwork(host, DEFAULT_SERVER_PORT);
	}

	public boolean connectToNetwork(InetAddress host, long id) throws IOException, InterruptedException {
		return connectToNetwork(host, DEFAULT_SERVER_PORT, id);
	}

	public boolean connectToNetwork(InetAddress host, int port) throws IOException, InterruptedException {
		return connectToNetwork(host, port, (long)(ID_LIMIT * Math.random()));
	}


	public boolean connectToNetwork(InetAddress host, int port, long id) throws IOException, InterruptedException {
		myInfo.id = id;
		if (serverSocket == null)
			if (!startServer())
				return false;
		Socket socket = new Socket();
		try {
			socket.connect(new InetSocketAddress(host, port), 10000);
		} catch (IOException e) {
			throw new IOException("Unable to connect to " + host + ":" + port,
					e);
		}
		ObjectOutputStream socketOut = new ObjectOutputStream(
				socket.getOutputStream());
		PeerMessage mesg = new PeerMessage(PeerMessage.Type.FIND_SUCCESSOR,
				myInfo);
		mesg.idToFindSuccessorOf = myInfo.id;
		mesg.indexToFix = -1;
		socketOut.writeObject(mesg);
		socket.close();
		int i;
		for (i = 0; i < TIME_OUT && successor == null; i++)	Thread.sleep(1000);
		if (i == TIME_OUT) {
			return false;
		}
		if (logEnabled)
			logMessage("Joined network @ " + myInfo.id);
		else
			//System.out.println("Joined network @ " + myInfo.id);
		startPeriodicThread();
		return true;
		
	}

	
	
	
	
	
	
	public void disconnectFromNetwork() {
		if (periodicThread != null)
			periodicThread.interrupt();
		if (incomingThread != null) {
			incomingThread.interrupt();
			incomingThread = null;
		}
	}

	
	
	
	
	
	
	private boolean startServer() {
		return startServer(DEFAULT_SERVER_PORT);
	}

	@SuppressWarnings("unused")
	private boolean startServer(int port) {
		while (true) {
			try {
				serverSocket = new ServerSocket(port);
			} catch (IOException e) {
				port++;
				continue;
			} catch (SecurityException e) {
				System.err.println(e);
				return false;
			}
			try {
				if (IP_DETECTION == 1)
					myInfo.address = InetAddress.getLocalHost();
				else if (IP_DETECTION == 2) {
					URLConnection urlc = new URL("http://whatismyip.org/").openConnection();
					BufferedReader socketIn = new BufferedReader(new InputStreamReader(urlc.getInputStream()));
					myInfo.address = InetAddress.getByName(socketIn.readLine());
				}
			} catch (IOException e) {
				System.err.println(e);
				try {
					serverSocket.close();
				} catch (IOException e1) {
				}
				serverSocket = null;
				return false;
			}
			myInfo.port = port;
			break;
		}
		
		my_port = port;

		//System.out.println("Listening on " + (myInfo.address != null ? myInfo.address : "") + ":" + myInfo.port);

		incomingThread = new Thread() {
			{
				setDaemon(true);
			}

			public void run()  {
				while (!isInterrupted()) {
					logMessage("Waiting for peer connection...");
					try {
						Socket socket = serverSocket.accept();
						processConnection(socket);
					} catch (IOException e) {
					    System.err.println(e);
					}
				}
			}
		};
		incomingThread.start();
		startPeriodicThread();

		return true;
	}

	
	
	
	
	
	
	private void processConnection(Socket socket) {
		try {
			if (myInfo.address == null) {
				myInfo.address = socket.getLocalAddress();
				if (logEnabled)
					logMessage("Updated local IP to " + myInfo.address);
				//else
					//System.out.println("Updated local IP to " + myInfo.address);
			}
			ObjectInputStream socketIn = new ObjectInputStream(socket.getInputStream());
			PeerMessage mesg = (PeerMessage)socketIn.readObject();
			if (mesg.sender.address == null)
				mesg.sender.address = socket.getInetAddress();
			processMessage(mesg);
		} catch (Exception e) {
//			System.err.println(e);
			// Broken connection expected for precessor alive test
		}
	}

	
	
	
	
	
	

	private void processMessage(PeerMessage mesg) {
		logMessage("Received message " + mesg.type + " from " + mesg.sender.id);
		switch (mesg.type) {
		case FIND_SUCCESSOR:
			findSuccessor(mesg);
			break;
		case SUCCESSOR:
			if (mesg.indexToFix == -1) {
				successor = mesg.peer;
				logMessage("** Updated successor @ " + successor.id);
				logMessage('\n' + internalState());
			} else {
				finger[mesg.indexToFix] = mesg.peer;
				logMessage("** Updated finger[" + mesg.indexToFix + "] @ " + finger[mesg.indexToFix].id);
				logMessage('\n' + internalState());
			}
			break;
		case FIND_PREDECESSOR:
			mesg.type = PeerMessage.Type.PREDECESSOR;
			PeerInformation sender = mesg.sender;
			mesg.sender = myInfo;
			mesg.peer = predecessor;
			sendMessage(mesg, sender);
			break;
		case PREDECESSOR:
			if (mesg.peer != null && withinOpen(mesg.peer.id, myInfo.id, successor.id)) {
				successor = mesg.peer;
				logMessage("** Updated sucessor @ " + successor.id);
				logMessage('\n' + internalState());
			}
			mesg.type = PeerMessage.Type.NOTIFY;
			mesg.sender = myInfo;
			sendMessage(mesg, successor);
			break;
		case NOTIFY:
			if (predecessor == null || withinOpen(mesg.sender.id, predecessor.id, myInfo.id)) {
				predecessor = mesg.sender;
				logMessage("** Updated predecessor @ " + predecessor.id);
				logMessage('\n' + internalState());
			}
			break;
		case PAYLOAD:
			handlePayload(mesg);
		}
	}

	
			
			
			
			
			
	private boolean sendMessage(PeerMessage mesg, PeerInformation destination) {
		if (destination.address == null)	// silently fail when IP_DETECTION == 3 and no other peer has connected
			return false;
		logMessage("Sending message " + mesg.type + " to " + destination.id);
		try {
			Socket socket = new Socket(destination.address, destination.port);
			ObjectOutputStream socketOut = new ObjectOutputStream(socket.getOutputStream());
			socketOut.writeObject(mesg);
			socket.close();
		} catch (IOException e) {
			invalidatePeer(destination);
			if (successor == myInfo) {
				PeerMessage mesg2 = new PeerMessage(PeerMessage.Type.FIND_SUCCESSOR, myInfo);
				mesg2.idToFindSuccessorOf = myInfo.id;
				mesg2.indexToFix = -1;
				findSuccessor(mesg2);
			}
			return false;
		}
		return true;
	}

	
	
	
	
	
	
	private void logMessage(String text) {
		if (logEnabled)
			System.err.println(new Date() + " -- " + text);
	}

	
	
	
	
	
	/**
	 * My successor is the successor of the querying node if the id to find the successor of is
	 * between me, exclusive, and my successor, inclusive.  That is, if the id is of my successor,
	 * then my successor is its own successor.
	 * @param mesg
	 */
	private void findSuccessor(PeerMessage mesg) {
		if (successor != null && withinHalfClosed(mesg.idToFindSuccessorOf, myInfo.id, successor.id)) {
			mesg.type = PeerMessage.Type.SUCCESSOR;
			PeerInformation sender = mesg.sender;
			mesg.sender = myInfo;
			mesg.peer = successor;
			sendMessage(mesg, sender);
		} else {
			PeerInformation closest = closestPrecedingNode(mesg.idToFindSuccessorOf);
			if (!myInfo.equals(closest)) {
				sendMessage(mesg, closest);
			} else if (successor == myInfo && predecessor != null) {
				sendMessage(mesg, predecessor);
			}

		}
	}

	
	
	
	
	
	private PeerInformation closestPrecedingNode(long id) {
		for (int i = finger.length - 1; i >= 0; i--)
			if (finger[i] != null && withinOpen(finger[i].id, myInfo.id, id))
				return finger[i];
		return myInfo;
	}

	
	
	
	
	
	
	private void fixFingers() {
		PeerMessage mesg = new PeerMessage(PeerMessage.Type.FIND_SUCCESSOR, myInfo);
		mesg.idToFindSuccessorOf = (myInfo.id + (1 << next)) % ID_LIMIT;
		mesg.indexToFix = next;
		findSuccessor(mesg);
		next = (next + 1) % finger.length;
	}

	
	
	
	
	
	
	private static boolean withinOpen(long id, long start, long end) {
		return id != start && (start == end || distance(start, id) < distance(start, end));
	}

	private static boolean withinHalfClosed(long id, long start, long end) {
		return id == end || withinOpen(id, start, end);
	}

	private static long distance(long start, long end) {
		long dist = end - start;
		if (dist < 0)
			dist += ID_LIMIT;
		return dist;
	}

	
	
	
	
	
	
	private void startPeriodicThread() {
		periodicThread = new Thread() {
			{
				setDaemon(true);
			}

			public void run() {
				while (!isInterrupted()) {
					try {
						synchronized (this) {
							wait(BACKGROUND_TASK_PERIOD);
						}
					} catch (InterruptedException e) {
//						System.err.println(e);
						break;
					}
					// initiate a stabilize operation
					if (successor != null) {
						PeerMessage mesg = new PeerMessage(PeerMessage.Type.FIND_PREDECESSOR, myInfo);
						sendMessage(mesg, successor);
					}
					// fix an entry in the finger table
					fixFingers();
					// check whether predecessor is alive
					if (predecessor != null) {
						try {
							new Socket(predecessor.address, predecessor.port).close();
						} catch (IOException e) {
//							System.err.println(e);
							invalidatePeer(predecessor);
						}
					}
				}
			}
		};
		periodicThread.start();
	}

	
	
	
	
	
	private void invalidatePeer(PeerInformation invalid) {
		if (invalid.equals(predecessor)) {
			predecessor = null;
		}
		if (invalid.equals(successor)) {
			successor = myInfo;
		}
		for (int i = 0; i < finger.length; i++) {
			if (invalid.equals(finger[i])) {
				finger[i] = null;
			}
		}
		logMessage('\n' + internalState());
	}

	
	
	
	
	
	
	private String internalState() {
		StringBuilder sb = new StringBuilder();
		sb.append("Predecessor: ");
		sb.append(predecessor == null ? predecessor : predecessor.id);
		sb.append("\nSuccessor: ");
		sb.append(successor.id);
		for (int i = 0; i < finger.length; i++) {
			sb.append("\nFinger for ");
			sb.append((myInfo.id + (1 << i)) % ID_LIMIT);
			sb.append(" @ ");
			sb.append(finger[i] == null ? finger[i] : finger[i].id);
		}
		return sb.toString();
	}

	
	
	
	
	
	
	public void sendObject(Serializable text, long id) {
		PeerMessage mesg = new PeerMessage(PeerMessage.Type.PAYLOAD, myInfo);
		mesg.payload = text;
		mesg.idOfPayloadDestination = id;
		handlePayload(mesg);
	}

	
	
	
	
	
	
	private void handlePayload(PeerMessage mesg) {
		if (mesg.idOfPayloadDestination == -1) {
			setChanged();
			notifyObservers(mesg.payload);
		}
		else if (successor != null && withinHalfClosed(mesg.idOfPayloadDestination, myInfo.id, successor.id)) {
			mesg.idOfPayloadDestination = -1;
			sendMessage(mesg, successor);
		} else {
			PeerInformation closest = closestPrecedingNode(mesg.idOfPayloadDestination);
			if (!myInfo.equals(closest))
				sendMessage(mesg, closest);
			else if (successor == myInfo && predecessor != null)
				sendMessage(mesg, predecessor);
		}
	}

	
	public long getID() {
		return myInfo.id;
	}

	public int getPort() {
		return my_port;
	}
	
}
