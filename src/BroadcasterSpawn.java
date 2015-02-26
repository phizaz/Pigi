public class BroadcasterSpawn extends Thread {
	private MyComputer myComputer;
	private AliveList aliveList;
	private Broadcaster[] broadcasters;
	private boolean job = false;
	private String request;

	public BroadcasterSpawn(MyComputer myComputer, AliveList aliveList) {
		this.myComputer = myComputer;
		this.aliveList = aliveList;
		broadcasters = new Broadcaster[Config.broadcasterCount];
		for (int i = 0; i < broadcasters.length; i++) {
			broadcasters[i] = new Broadcaster();
			broadcasters[i].start();
		}
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
			if (request.equals("busy") || request.equals("available")
					|| request.equals("nameChanged")) {
				aliveList.iterate(new AliveListIteration() {

					@Override
					public void run(OtherComputer each) {
						// when request = available or
						// request = nameChanged the exception should be null -
						// no exception

						// Find the least job.
						int min = Integer.MAX_VALUE;
						int minPos = 0;
						for (int i = 0; i < broadcasters.length; i++) {
							int jobsCount = broadcasters[i].getJobsCount();
							if (jobsCount < min) {
								min = jobsCount;
								minPos = i;
							}
						}
						// Got the minPos
						broadcasters[minPos].addJob(request, each.ip,
								myComputer);
						// Done!
					}
				});
			} else {
				System.out.println("Wrong request on Broadcaster Spawn.");
			}
		}
	}

	public synchronized void setJob(String request) {
		this.job = true;
		this.request = request;
		notifyAll();
	}
}
