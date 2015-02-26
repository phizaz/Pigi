import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;

import layout.ImagePanel;

import org.omg.CosNaming.NamingContextExtPackage.AddressHelper;

import net.miginfocom.swt.MigLayout;

public class AliveDrawer extends Thread {
	private boolean job = false;
	private JPanel[] grids;
	private ImagePanel[] messageWindows;
	private AliveList aliveList;
	private Controller controller;

	public AliveDrawer(Controller controller, JPanel[] grids,
			ImagePanel[] messageWindows, AliveList aliveList) {
		this.grids = grids;
		this.messageWindows = messageWindows;
		this.aliveList = aliveList;
		this.controller = controller;
	}

	public synchronized void run() {
		while (true) {
			// System.out.println("In");
			while (!job) {
				try {
					// System.out.println("Wait.");
					wait();
					// System.out.println("Wait finish.");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// REDRAW
			// System.out.println("draw.");
			redrawAlive();
			job = false;
		}
	}

	public void redrawAlive() {
		// BETA
		// System.out.println("called!");
		// removeAll the Grids
		final ArrayList<Integer> survivor = new ArrayList<Integer>();
		aliveList.iterate(new AliveListIteration() {
			@Override
			public void run(OtherComputer each) {
				// BETAAAA
				final int clientId = each.getClientId();
				final String clientIp = each.getIp();
				ImagePanel clientLogo = null;
				if (!each.isBusy() && 
					!controller.dataReceiver.isWorking() &&
					!controller.dataSender.isWorking()) {
					clientLogo = new ImagePanel(Utils.image("client_" + (clientId) + ".png"));
					new FileDrop(clientLogo, new FileDrop.Listener() {

						@Override
						public void filesDropped(File[] files) {
							// TODO Auto-generated method stub
							if (files.length > 1) {
								// BETA
								System.out.println("drop a file only!");
							} else {
								controller.promptFileSend(clientIp, clientId - 1,
										files[0]);
							}
						}
					});
				} else {
					clientLogo = new ImagePanel(Utils.image("client_" + (clientId)
							+ "_black.png"));
				}
				
				survivor.add(new Integer(clientId - 1));

				JLabel displayName = new JLabel(each.getDisplayName());
				displayName.setFont(new Font("Tahoma", Font.BOLD, 12));
				displayName.setForeground(new Color(88, 88, 88));

				JLabel displayIp = new JLabel(each.getIp());
				displayIp.setFont(new Font("Tahoma", Font.ITALIC, 9));
				displayIp.setForeground(new Color(99, 99, 99));

				grids[clientId - 1].removeAll();
				grids[clientId - 1].add(clientLogo, "wrap 6");
				grids[clientId - 1].add(displayName, "center, wrap 2");
				grids[clientId - 1].add(displayIp, "center");
				grids[clientId - 1].doLayout();
				grids[clientId - 1].repaint();
			}
		});
		// Kill the timeout
		for (int i = 0; i < Config.maxClient; i++) {
			int found = survivor.indexOf(new Integer(i));
			if (found == -1) {
				// died
				messageWindows[i].setVisible(false);
				//Try to stop the dataReceiver
				if(controller.dataReceiver.isWorking() && 
					controller.dataReceiver.getSenderClientNumber() == i) {
					//The current receiving is from the disconnected, just stop.
					controller.dataReceiver.askToStop();
				}
				grids[i].removeAll();
				grids[i].repaint();
			}
		}
	}

	public synchronized void wake() {
		System.out.println("alive wake called!");
		job = true;
		notifyAll();
	}
}
