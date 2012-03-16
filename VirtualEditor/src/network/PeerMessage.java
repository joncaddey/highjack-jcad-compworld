package network;

import java.io.Serializable;

public class PeerMessage implements Serializable {
	private static final long serialVersionUID = -6083927382821646944L;

	public enum Type {
		FIND_SUCCESSOR, SUCCESSOR, FIND_PREDECESSOR, PREDECESSOR, NOTIFY, PAYLOAD
	}

	public Type type;
	public PeerInformation sender;
	public long idToFindSuccessorOf;	// FIND_SUCCESSOR
	public int indexToFix;	// FIND_SUCCESSOR, SUCCESSOR
	public PeerInformation peer;		// SUCCESSOR, PREDECESSOR
	public long idOfPayloadDestination;	// PAYLOAD
	public String payload;				// PAYLOAD

	public PeerMessage(Type type, PeerInformation sender) {
		this.type = type;
		this.sender = sender;
	}
}
