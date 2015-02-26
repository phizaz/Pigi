import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;

public class Receiver extends Thread {
	private ServerSocket serverSocket;
	private Socket socket;
	private Controller controller;
	private Worker worker;

	public Receiver(Controller controller) {
		this.controller = controller;
		this.worker = new Worker(controller);
		this.worker.start();
		try {
			serverSocket = new ServerSocket(Config.communicationPort);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Start the worker, the Receiver's servant
	}

	public void run() {
		while (true) {
			try {
				socket = serverSocket.accept();
				ObjectInputStream ois = new ObjectInputStream(
						socket.getInputStream());
				Message message = (Message) ois.readObject();
				System.out.printf("From host : %s message : %s\n", message.ip, message.request);
				worker.setJob(message);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
