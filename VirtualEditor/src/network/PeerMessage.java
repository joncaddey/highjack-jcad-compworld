package network;

import java.io.Serializable;

public class PeerMessage implements Serializable {
	private static final long serialVersionUID = -6083927382821646944L;

	public enum Type {
		FIND_SUCCESSOR, SUCCESSOR, FIND_PREDECESSOR, PREDECESSOR, NOTIFY
	}

	public Type type;
	public PeerInformation sender;
	public long idToFindSuccessorOf;	// used by FIND_SUCCESSOR
	public int successorDestination;	// used by FIND_SUCCESSOR, SUCCESSOR
	public PeerInformation peer;		// used by SUCCESSOR, PREDECESSOR

	public PeerMessage(Type type, PeerInformation sender) {
		this.type = type;
		this.sender = sender;
	}
}
