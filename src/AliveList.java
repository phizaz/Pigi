import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Map.Entry;

interface AliveListIteration {
	public void run(OtherComputer each) ;
}
public class AliveList {
	private TreeMap<String, OtherComputer> aliveList = new TreeMap<String, OtherComputer>();
	private boolean clientNumberSelected [] = new boolean[Config.maxClient];

	public void put(String key, OtherComputer val) {
		synchronized (aliveList) {
			aliveList.put(key, val);
		}
	}
	
	public OtherComputer get(String key) {
		synchronized (aliveList){
			return aliveList.get(key);
		}
	}
	
	public void remove(String key) {
		synchronized (aliveList) {
			aliveList.remove(key);
		}
	}
	
	public int availableClientNumber() {
		synchronized (aliveList) {
			ArrayList<Integer> availableList = new ArrayList<Integer>();
			
			for(int i = 0; i < clientNumberSelected.length; i++) {
				if(clientNumberSelected[i] != true) {
					availableList.add(new Integer(i));
				}
			}
			
			int size = availableList.size();
			if(size > 0) {
				int rand = (int) (Math.random() * size);
				int idx = availableList.get(rand);
				clientNumberSelected[idx] = true; 
				return idx;
			} else return -1;
		}
	}
	public void iterate(AliveListIteration code){
		synchronized (aliveList){
			Iterator entries = aliveList.entrySet().iterator();
			long Now = System.currentTimeMillis();
			boolean result = false;
			while(entries.hasNext()) {
				Entry thisEntry = (Entry) entries.next();
				String ip = (String) thisEntry.getKey();
				OtherComputer otherComputer = (OtherComputer) thisEntry.getValue();
				if(Now - otherComputer.getLastActive() <= Config.delayConsideredDisconnected) {
					//Alive
					code.run(otherComputer);
				}
			}
		}
	}
	public boolean kickDisconnected() {
		synchronized (aliveList) {
			Iterator entries = aliveList.entrySet().iterator();
			long Now = System.currentTimeMillis();
			boolean result = false;
			ArrayList<String> removeList = new ArrayList<String>();
			while (entries.hasNext()) {
				Entry thisEntry = (Entry) entries.next();
				String ip = (String) thisEntry.getKey();
				OtherComputer otherComputer = (OtherComputer) thisEntry
						.getValue();
				//System.out.println("Identify host to kick : " + ip + " Time : " + Now + " LastAC : " + otherComputer.getLastActive());
				if (Now - otherComputer.getLastActive() > Config.delayConsideredDisconnected) {
					// Has someone to be kicked!!!
					//Kick!!
					System.out.println("Kick!!");
					//aliveList.remove(ip);
					removeList.add(ip);
					int clientNumber = otherComputer.getClientId() - 1;
					clientNumberSelected[clientNumber] = false;
					result = true;
				}
			}
			//Remove IPs from the remove list
			for(int i = 0; i < removeList.size(); i++) {
				aliveList.remove(removeList.get(i));
			}
			return result;
		}
	}
}
