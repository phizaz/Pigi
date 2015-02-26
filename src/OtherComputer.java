import java.awt.Image;
import java.io.Serializable;
import java.net.Socket;


public class OtherComputer extends Computer{
	private long lastActive;
	private int clientId; // Start with 1


	public long getLastActive() {
		return lastActive;
	}

	public void setLastActive(long lastActive) {
		this.lastActive = lastActive;
	}

	public int getClientId() {
		return clientId;
	}

	public void setClientId(int clientId) {
		this.clientId = clientId;
	}
	
}
