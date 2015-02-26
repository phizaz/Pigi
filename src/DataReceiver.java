import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class DataReceiver extends Thread {
	private boolean job = false;
	private boolean working = false;
	private boolean stop = false;
	private int senderClientNumber;
	private String senderIp;
	private String fileName;
	private long fileSize;
	private ServerSocket serverSocket;
	private Controller controller;
	private AliveList aliveList;

	public DataReceiver(Controller controller, AliveList aliveList) {
		this.aliveList = aliveList;
		this.controller = controller;
		
		try {
			serverSocket = new ServerSocket(Config.dataPort);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
			receiveWorker();
			working = false;
		}
	}

	public void receiveWorker() {
		if(stop) stop = false; // stop at the first place is not correct.
		try {
			Socket socket = serverSocket.accept();
			InputStream inFile = socket.getInputStream();
			String downloadDir = Utils.getDownloadFolder();
			String downloadFileDestination = downloadDir + "/" + fileName;
			FileOutputStream fileWriter = new FileOutputStream(
					downloadFileDestination);

			byte[] buffer = new byte[Config.dataTransferBuffer];
			int bytesRead = 0;
			long bytesReceived = 0;

			System.out.println("Receiving..");
			controller.messageProgress(senderClientNumber, fileName, fileSize);
			controller.messageUpdateProgress(senderClientNumber, fileSize,
					bytesReceived);
			boolean success = true;
			long lastUpdate = 0;
			try {
				while ((bytesRead = inFile.read(buffer)) != -1) {
					if (stop) {
						break;
					}
					//If the connection is long.. update the aliveList always.
					long now = System.currentTimeMillis();
					if(now - lastUpdate >= Config.senderReceiverUpdateLastActivityCooldown) {
						lastUpdate = now;
						OtherComputer a = aliveList.get(senderIp);
						a.setLastActive(now);
						aliveList.put(senderIp, a);
					}
					
					bytesReceived += bytesRead;
					// Update the progress bar
					controller.messageUpdateProgress(senderClientNumber,
							fileSize, bytesReceived);
					fileWriter.write(buffer, 0, bytesRead);
				}
			} catch (SocketException e) {
				//Connection Broken!
				success = false;
			}
			socket.close();
			fileWriter.close();
			if(fileSize != bytesReceived && !stop) {
				success = false;
			}
			if (!success || stop) {
				stop = false;
				if(!success) {
					controller.messageDownloadError(senderClientNumber);
				}
				// CLEAN THEFILE
				File remover = new File(downloadFileDestination);
				if (remover.delete()) {
					System.out
							.println("the unsuccessful downloaded file deleted.");
				} else {
					// WHY ??
					System.out
							.println("the unsuccessful downloaded file couldn't be deleted.");
				}
			} else {
				controller.messageDownloadFinished(senderClientNumber);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void askToStop() {
		stop = true;
	}

	public synchronized void receive(int senderClientNumber, String senderIp, String filename,
			long fileSize) {
		this.senderClientNumber = senderClientNumber;
		this.senderIp = senderIp;
		job = true;
		this.fileSize = fileSize;
		this.fileName = filename;
		notifyAll();
	}

	public boolean isWorking() {
		return working;
	}
	
	public int getSenderClientNumber() {
		return senderClientNumber;
	}
}
