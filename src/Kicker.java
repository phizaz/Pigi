public class Kicker extends Thread {
	private AliveList aliveList;
	private AliveDrawer aliveDrawer;

	public Kicker(AliveList aliveList, AliveDrawer aliveDrawer) {
		this.aliveList = aliveList;
		this.aliveDrawer = aliveDrawer;
	}

	public void run() {
		while (true) {
			if (aliveList.kickDisconnected()) {
				aliveDrawer.wake();
			}
			try {
				Thread.sleep(Config.kickerCooldown);
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
