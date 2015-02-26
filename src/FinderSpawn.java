import layout.ImageButton;

public class FinderSpawn extends Thread {
	private MyComputer myComputer;
	private AliveList aliveList;
	private Finder[] finders = new Finder[Config.finderCount];
	private DataReceiver dataReceiver;
	private DataSender dataSender;
	private ImageButton refresh;

	public FinderSpawn(AliveList aliveList, MyComputer myComputer,
			DataReceiver dataReceiver, DataSender dataSender) {
		this.dataReceiver = dataReceiver;
		this.dataSender = dataSender;
		this.aliveList = aliveList;
		this.myComputer = myComputer;
		int extraCount = 255 - (255 / finders.length) * finders.length;
		int partition = 255 / finders.length;
		int last = 0;
		for (int i = 0; i < finders.length; i++) {
			int start = last;
			int end = start + partition - 1;
			if (extraCount > 0) {
				extraCount--;
				end++;
			}
			System.out.println(start + " " + end);
			finders[i] = new Finder(aliveList, myComputer, start, end);
			finders[i].start();
			last = end + 1;
		}
	}
	
	public void setRefreshButton(ImageButton refresh) {
		this.refresh = refresh;
	}

	public synchronized void run() {
		long lastWorking = 0;
		while (true) {
			try {
				long now = System.currentTimeMillis();
				if (now - lastWorking > Config.finderCooldown) {
					if (!dataSender.isWorking() && !dataReceiver.isWorking()) {
						lastWorking = now;
						// Search only when idle
						for (int i = 0; i < finders.length; i++) {
							finders[i].wake();
						}
					}
				} else {
					refresh.set(Utils.image("refresh_false.png"));
					Thread.sleep(150);
					refresh.setNormal();
				}
				wait(Config.finderRoutine);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public synchronized void wake() {
		notifyAll();
	}
}
