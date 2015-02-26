import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Finder extends Thread {
	private MyComputer myComputer;
	private AliveList aliveList;
	private int startRange;
	private int endRange;
	private int jobs = 0;

	public Finder(AliveList aliveList, MyComputer myComputer, int start, int end) {
		this.aliveList = aliveList;
		this.myComputer = myComputer;
		this.startRange = start;
		this.endRange = end;
	}
	
	public void ping(String host, Computer myComputer) {
		//System.out.printf("To host : %s Send message : %s\n", host,
		//		message.request);
		for(int i = 0; i < Config.pingTries; i++) {
			try {
				int timeout = Config.pingTimeout;
				Message message = new Message("ping", myComputer.ip, null, null);
				Socket socket = new Socket();
				socket.connect(
						new InetSocketAddress(host, Config.communicationPort),
						timeout);
				ObjectOutputStream oos = new ObjectOutputStream(
						socket.getOutputStream());
				oos.writeObject(message);
				oos.flush();
				break;
			} catch (SocketTimeoutException e) {
				// Timed out
				continue;
			} catch (UnknownHostException e) {
				// Unknown host
				break;
			} catch (ConnectException e) {
				// NoRouteToHost
				break;
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
	}

	public synchronized void run() {
		// Scan
		while(true) {
			while(jobs == 0) {
				try {
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			jobs--;
			find();
		}
	}

	public synchronized void wake() {
		jobs++;
		notifyAll();
	}
	
	public void find() {
		String subnet = myComputer.getSubnet();
		// Brute force scan the network for the one who's online
		for (int i = startRange; i <= endRange; i++) {
			String host = subnet + "." + i;
			if (host.equals(myComputer.ip))
				continue;
			OtherComputer found = aliveList.get(host);
			if (found != null) {
				if (System.currentTimeMillis() - found.getLastActive() < Config.earlyConnectedTimeout) {
					// considered as early connected, no need to reping.
					continue;
				}
			}
			ping(host, (Computer) myComputer);		
		}
	}
}
