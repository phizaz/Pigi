import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;

class workingQueue {
	String request;
	String host;
	FileInfo fileInfo;
	Computer myComputer;
	
	public workingQueue(String request, String host, Computer myComputer, FileInfo fileInfo) {
		this.request = request;
		this.host = host;
		this.fileInfo = fileInfo;
		this.myComputer = myComputer;
	}
}
public class Sender extends Thread {
	private ArrayList<workingQueue> q = new ArrayList<workingQueue>(); 

	public Sender() {

	}
	
	public synchronized void addJob(String request, String host, Computer myComputer, FileInfo fileInfo) {
		q.add(new workingQueue(request, host, myComputer, fileInfo));
		notifyAll();
	}

	public synchronized void run() {
		while (true) {
			while (q.size() == 0) {
				try {
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			workingQueue now = q.get(0);
			String request = now.request;
			String host = now.host;
			Computer myComputer = now.myComputer;
			FileInfo fileInfo = now.fileInfo;
			
			if (request.equals("ping")) {
				ping(host, myComputer);
			} else if (request.equals("pong")) {
				pong(host, myComputer);
			} else if (request.equals("fileReady")) {
				fileReady(host, myComputer, fileInfo);
			} else if (request.equals("startTransmit")) {
				startTransmit(host, myComputer, fileInfo);
			} else if (request.equals("transmitDenied")) {
				transmitDenied(host, myComputer);
			} else {
				System.out.println("Wrong using of sender!");
			}
			//pop out of queue
			q.remove(0);
		}
	}

	private void send(String host, Message message) {
		// System.out.printf("To host : %s Send message : %s\n", host,
		// message.request);
		for(int i = 0; i < Config.senderTries; i++) {
			try {
				int timeout = message.request.equals("ping") ? Config.pingTimeout
						: Config.communicationTimeout;
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
				continue; //Let's try again;
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

	private void ping(String host, Computer myComputer) {

		send(host, new Message("ping", myComputer.ip, null, null));

	}

	private void pong(String host, Computer myComputer) {

		send(host, new Message("pong", myComputer.ip, null, myComputer));

	}

	private void fileReady(String host, Computer myComputer, FileInfo fileInfo) {

		send(host, new Message("fileReady", myComputer.ip, fileInfo, null));

	}

	private void busy(String host, Computer myComputer) {

		send(host, new Message("busy", myComputer.ip, null, null));

	}

	private void available(String host, Computer myComputer) {

		send(host, new Message("available", myComputer.ip, null, null));

	}

	private void startTransmit(String host, Computer myComputer,
			FileInfo fileInfo) {

		send(host, new Message("startTransmit", myComputer.ip, fileInfo, null));

	}

	private void transmitDenied(String host, Computer myComputer) {

		send(host, new Message("transmitDenied", myComputer.ip, null, null));

	}
}
