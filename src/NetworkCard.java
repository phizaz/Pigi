import java.net.NetworkInterface;


public class NetworkCard {
	String name;
	String ip;
	NetworkInterface networkInterface;
	
	public NetworkCard(String name, String ip, NetworkInterface networkInterface) {
		this.networkInterface = networkInterface;
		this.ip = ip;
		this.name = name;
	}
}
