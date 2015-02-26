public class Worker extends Thread {
	private Controller controller;
	private boolean job = false;
	private Message message;

	public Worker(Controller controller) {
		this.controller = controller;
	}

	public synchronized void run() {
		while (true) {
			while (!job) {
				try {
					wait();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			job = false;
			controller.messageDecoder(message);
		}
	}
	
	public synchronized void setJob(Message message){
		this.job = true;
		this.message = message;
		notifyAll();
	}
}
