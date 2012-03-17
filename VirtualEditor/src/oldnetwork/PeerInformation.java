package oldnetwork;

import java.io.Serializable;
import java.net.*;

public class PeerInformation implements Serializable {
	private static final long serialVersionUID = -451214736867200063L;
	
	public InetAddress address;
	public int port;
	public long id;
	
	public PeerInformation() {
		id = -1;
	}

	public PeerInformation(PeerInformation other) {
		this(other.address, other.port, other.id);
	}
	
	public PeerInformation(InetAddress address, int port, long id) {
		this.address = address;
		this.port = port;
		this.id = id;
	}
	
	public String toString() {
		return address + ":" + port + " @ " + id;
	}

	public boolean equals(Object other) {
		if (!(other instanceof PeerInformation))
			return false;
		
		return id == ((PeerInformation)other).id;
	}
}