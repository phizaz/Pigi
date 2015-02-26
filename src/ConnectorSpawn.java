import javax.print.attribute.standard.JobSheets;


public class ConnectorSpawn extends Thread {
	private AliveList aliveList;
	private MyComputer myComputer;
	private Connector [] connectors = new Connector[Config.maxClient];
	private boolean job = false;
	public ConnectorSpawn(AliveList aliveList, MyComputer myComputer) {
		this.aliveList = aliveList;
		this.myComputer = myComputer;
		for(int i = 0; i < connectors.length; i++) {
			connectors[i] = new Connector(myComputer);
			connectors[i].start();
		}
	}
	
	public synchronized void run () {
		while(true) {
			aliveList.iterate(new AliveListIteration() {
				
				@Override
				public void run(OtherComputer each) {
					// TODO Auto-generated method stub
					int clientNumber = each.getClientId() - 1;
					connectors[clientNumber].addJob(each.ip);
				}
			});
			try {
				Thread.sleep(Config.connectorCooldown);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
