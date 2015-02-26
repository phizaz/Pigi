import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;


public class Connector extends Thread {
	private ArrayList<String> jobs = new ArrayList<String>();
	private MyComputer myComputer;
	public Connector (MyComputer myComputer) {
		this.myComputer = myComputer;
	}
	
	public synchronized void run () {
		while(true){
			while(jobs.size() == 0) {
				try {
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			String host = jobs.get(0);
			ping(host, myComputer);
			jobs.remove(0);
		}
	}
	
	public synchronized void addJob(String host) {
		jobs.add(host);
		notifyAll();
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
}
