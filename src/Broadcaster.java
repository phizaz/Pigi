import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;

class Job {
	String request;
	String host;
	Computer myComputer;

	public Job(String request, String host, Computer myComputer) {
		this.request = request;
		this.host = host;
		this.myComputer = myComputer;
	}
}

public class Broadcaster extends Thread {
	private ArrayList<Job> jobs = new ArrayList<Job>();

	public Broadcaster() {

	}

	public synchronized void addJob(String request, String host,
			Computer myComputer) {
		jobs.add(new Job(request, host, myComputer));
		notifyAll();
	}

	public int getJobsCount() {
		return jobs.size();
	}

	public synchronized void run() {
		while (true) {
			while (jobs.size() == 0) {
				try {
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Job now = jobs.get(0);
			String request = now.request;
			String host = now.host;
			Computer myComputer = now.myComputer;

			if (request.equals("busy")) {
				Message a = new Message("busy", myComputer.ip, null, myComputer);
				send(host, a);
			} else if (request.equals("available")) {
				Message a = new Message("available", myComputer.ip, null,
						myComputer);
				send(host, a);
			} else if (request.equals("nameChanged")) {
				Message a = new Message("nameChanged", myComputer.ip, null,
						myComputer);
				send(host, a);
			} else {
				System.out.println("Wrong request on Broadcaster.");
			}
			jobs.remove(0);
		}
	}

	private void send(String host, Message message) {
		for (int i = 0; i < Config.broadcasterTries; i++) {
			try {
				System.out.println("++++Broadcast to : " + host);
				int timeout = Config.communicationTimeout;
				Socket socket = new Socket();
				socket.connect(new InetSocketAddress(host,
						Config.communicationPort), timeout);
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
