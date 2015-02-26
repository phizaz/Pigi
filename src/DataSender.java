import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

//The sender holds sending task only not for preparing task
public class DataSender extends Thread {
	private boolean job = false;
	private boolean working = false;
	private boolean stop = false;
	private String host;
	private String filePath;
	private String fileName;
	private long fileSize;
	private int receiverClientNumber;
	private Controller controller;
	private AliveList aliveList;

	public DataSender(Controller controller, AliveList aliveList) {
		this.aliveList = aliveList;
		this.controller = controller;
	}

	public synchronized void run() {
		while (true) {
			while (!job) {
				try {
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			working = true;
			job = false;
			sendWorker();
			working = false;
		}
	}

	public synchronized void send(int receiverClientNumber, String host,
			String filePath, String fileName, long fileSize) {
		this.host = host;
		this.receiverClientNumber = receiverClientNumber;
		this.filePath = filePath;
		this.fileName = fileName;
		this.fileSize = fileSize;
		job = true;
		notifyAll();
	}

	public void askToStop() {
		stop = true;
	}

	public void sendWorker() {
		if (stop)
			stop = false; // stop at the first place is not correct.
		try {
			Socket socket = new Socket(host, Config.dataPort);
			FileInputStream fileReader = new FileInputStream(filePath);
			OutputStream outFile = socket.getOutputStream();

			byte[] buffer = new byte[Config.dataTransferBuffer];
			int bytesRead = 0;
			long totalSend = 0;

			System.out.println("Sending...");
			long startTime = System.currentTimeMillis();
			controller
					.messageProgress(receiverClientNumber, fileName, fileSize);
			controller.messageUpdateProgress(receiverClientNumber, fileSize,
					totalSend);
			boolean success = true;
			long lastUpdate = 0;
			try {
				while ((bytesRead = fileReader.read(buffer)) != -1) {
					if (stop) {
						break;
					}
					// If the connection is long.. Update the aliveList
					long now = System.currentTimeMillis();
					if (now - lastUpdate >= Config.senderReceiverUpdateLastActivityCooldown) {
						lastUpdate = now;
						OtherComputer a = aliveList.get(host);
						a.setLastActive(now);
						aliveList.put(host, a);
					}

					if (bytesRead > 0) {
						outFile.write(buffer, 0, bytesRead);
						totalSend += bytesRead;
						controller.messageUpdateProgress(receiverClientNumber,
								fileSize, totalSend);
						System.out.println("sent " + totalSend);
					}
				}
			} catch (SocketException e) {
				success = false;
			}

			socket.close();
			fileReader.close();
			if (fileSize != totalSend && !stop) {
				success = false;
			}
			if (!success || stop) {
				stop = false;
				if (!success) {
					controller.messageUploadError(receiverClientNumber);
				}
			} else {
				controller.messageUploadFinished(receiverClientNumber);
			}
			System.out.println("Send in :"
					+ (System.currentTimeMillis() - startTime));
		} catch (UnknownHostException e) {
			System.out.println("Sending file to noone.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isWorking() {
		return working;
	}
}
