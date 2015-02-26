import java.util.TreeMap;
import java.util.TreeSet;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.*;

import javax.print.attribute.standard.MediaSize.Other;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import layout.ImageButton;
import layout.ImagePanel;
import layout.MessageText;
import net.miginfocom.swing.MigLayout;

public class Controller {
	private JLayeredPane contentWrapper;
	private NetworkCard networkCard;
	private AliveList aliveList = new AliveList();
	private MyComputer myComputer;
	// Finder
	private FinderSpawn finderSpawn;
	//Connectors
	private ConnectorSpawn connectorSpawn;
	// Kicker
	private Kicker kicker;
	// AliveDrawer
	private AliveDrawer aliveDrawer;
	// Broadcaster
	private BroadcasterSpawn broadcasterSpawn;
	// Communications
	private Receiver receiver;
	private Sender sender;
	// Data Communications
	public DataReceiver dataReceiver;
	public DataSender dataSender;

	// Layouts
	private JPanel[] grids;
	private ImagePanel[] messageWindows = new ImagePanel[Config.maxClient];
	private JPanel[] progressBars = new JPanel[Config.maxClient];
	private JLabel[] progressBarSents = new JLabel[Config.maxClient];

	public Controller(JLayeredPane contentWrapper, NetworkCard networkCard) {
		/*
		 * This only for testing UI purposes. for (int i = 0; i < 8; i++) {
		 * OtherComputer otherComputer = new OtherComputer();
		 * otherComputer.setClientId(i + 1);
		 * otherComputer.setDisplayName("Annonymous");
		 * otherComputer.setIp("192.168.1.1"); aliveList.put("" + i,
		 * otherComputer); }
		 */
		this.contentWrapper = contentWrapper;
		this.networkCard = networkCard;

		myComputer = new MyComputer();
		myComputer.setDisplayName("Annonymous");
		// IP should update ?
		myComputer.setIp(Utils.getIp(networkCard.networkInterface));
		myComputer.setSubnet(Utils.getSubnet(myComputer.getIp()));

		// Start the communication receiver.
		sender = new Sender();
		sender.start();
		receiver = new Receiver(this);
		receiver.start();

		// Start data communicators
		dataSender = new DataSender(this, aliveList);
		dataSender.start();
		dataReceiver = new DataReceiver(this, aliveList);
		dataReceiver.start();
		
		// Start the finder to discover clients in the same subnet.
		finderSpawn = new FinderSpawn(aliveList, myComputer, dataReceiver, dataSender);
		finderSpawn.start();
		// Start the connector to long the connection
		connectorSpawn = new ConnectorSpawn(aliveList, myComputer);
		connectorSpawn.start();

		// Start the broadcaster
		broadcasterSpawn = new BroadcasterSpawn(myComputer, aliveList);
		broadcasterSpawn.start();

		initGUI();

		// Start the Drawer Worker.
		aliveDrawer = new AliveDrawer(this, grids, messageWindows, aliveList); // Give them
																// somepanel
		aliveDrawer.start();
		aliveDrawer.wake();

		// Start kicker.
		kicker = new Kicker(aliveList, aliveDrawer);
		kicker.start();		
	}

	public void initGUI() {
		System.out.println("Controller Init GUI.");

		JPanel content = new JPanel();
		content.setLayout(new MigLayout("align center, center, insets 0, gap 0"));
		content.setSize(new Dimension(800, 600));
		content.setPreferredSize(new Dimension(800, 600));
		content.setOpaque(false);
		// INIT
		grids = new JPanel[Config.maxClient];
		Dimension gridSize = new Dimension(150, 150);
		for (int i = 0; i < grids.length; i++) {
			grids[i] = new JPanel();
			grids[i].setSize(gridSize);
			grids[i].setPreferredSize(gridSize);
			grids[i].setOpaque(false);
			grids[i].setLayout(new MigLayout("align center, center, insets 0"));
		}

		JPanel center = new JPanel();
		Dimension centerSize = new Dimension(300, 300);
		center.setSize(centerSize);
		center.setPreferredSize(centerSize);
		center.setLayout(new MigLayout("align center, center, insets 0"));
		center.setOpaque(false);
		ImagePanel centerLogo = new ImagePanel(Utils.image("client_center.png"));
		
//		JLabel centerText = new JLabel(myComputer.displayName, JLabel.CENTER);
//		centerText.setFont(new Font("Tahoma", Font.BOLD, 15));
//		centerText.setForeground(new Color(66, 66, 66));
		
		NameField centerText = new NameField(myComputer.displayName, content);
		centerText.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				NameField field = (NameField) e.getSource();
				myComputer.displayName = field.getText();
				System.out.println(field.getText());
				broadcasterSpawn.setJob("nameChanged");
			}
		});
		
		
		JLabel centerIp = new JLabel(myComputer.ip, JLabel.CENTER);
		centerIp.setFont(new Font("Tahoma", Font.ITALIC, 11));
		centerIp.setForeground(new Color(77, 77, 77));

		center.add(centerLogo, "center, wrap 12");
		center.add(centerText, "center, wrap 3");
		center.add(centerIp, "center");

		content.add(grids[2]);
		content.add(grids[3]);
		content.add(grids[4]);
		content.add(grids[5], "wrap");
		content.add(grids[1]);
		content.add(center, "span 2 2");
		content.add(grids[6], "wrap");
		content.add(grids[0]);
		content.add(grids[7]);

		contentWrapper.removeAll();
		contentWrapper.add(content, new Integer(0), 0);

		JPanel logoWrapper = new JPanel();
		logoWrapper.setLayout(new MigLayout("align center, bottom, insets 0"));
		logoWrapper.setSize(new Dimension(800, 600));
		logoWrapper.setOpaque(false);
		ImageButton refresh = new ImageButton(Utils.image("refresh.png"),
				Utils.image("refresh_clicked.png"),
				Utils.image("refresh_clicked.png"));
		finderSpawn.setRefreshButton(refresh);
		refresh.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				//Recall the FinderSpawn
				finderSpawn.wake();
			}
		});
		logoWrapper.add(refresh, "wrap 15");
		contentWrapper.add(logoWrapper, new Integer(1), 0);

		for (int i = 0; i < Config.maxClient; i++) {
			messageWindows[i] = createMessageWindow(i);
		}
		// promptFileReceive("192.168.1.1", 1, new
		// FileInfo("The Lord of the Rings.", 999));
	}

	public ImagePanel createMessageWindow(int userNumber) {
		// TEST MESSAGE
		ImagePanel message = new ImagePanel(Utils.image("message.png"));

		Dimension dim = Utils.userNumberToDimension[userNumber];
		message.setPosition(100 + 150 * dim.width + 75 + 5 - message.getWidth()
				/ 2, 75 + 35 + 150 * dim.height - message.getHeight());
		message.setLayout(new MigLayout("align center, center, insets 0", "",
				"[]12"));
		message.setVisible(false);
		contentWrapper.add(message, new Integer(5), 0);
		return message;
	}

	public void promptFileSend(String receiverIp, int receiverClientNumber,
			File file) {
		// Tell the user which you wanna send this file.
		MessageText requestText = new MessageText("You wanna send me?");
		MessageText fileNameText = new MessageText(Utils.trimFileName(file
				.getName()));
		MessageText fileSize = new MessageText(Utils.convertFileSize(file
				.length()));
		fileSize.setForeground(new Color(255, 0, 182));
		ImageButton OK = new ImageButton(Utils.image("message_ok.png"),
				Utils.image("message_ok_hover.png"));
		ImageButton NO = new ImageButton(Utils.image("message_no.png"),
				Utils.image("message_no_hover.png"));
		JPanel buttons = new JPanel();
		buttons.setLayout(new MigLayout("align center, center, insets 0"));
		buttons.add(OK, "gapright 8");
		buttons.add(NO);
		buttons.setOpaque(false);

		final ImagePanel thisWindow = messageWindows[receiverClientNumber];
		final String filename = file.getName();
		final String filePath = file.getAbsolutePath();
		final long filesize = file.length();
		final String ip = receiverIp;
		final int senderNumber = receiverClientNumber;
		OK.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				// Tell the world that you're busy
				busy(ip); 
				// Show the pending window
				messagePending(senderNumber);
				// send the request fileReady
				FileInfo fileInfo = new FileInfo(filename, filePath, filesize);
				sender.addJob("fileReady", ip, myComputer, fileInfo);
			}
		});
		NO.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				thisWindow.setVisible(false);
				contentWrapper.repaint();
			}
		});

		thisWindow.removeAll();
		thisWindow.add(requestText, "center, span 2, wrap 2");
		thisWindow.add(fileNameText);
		thisWindow.add(fileSize, "wrap 8");
		thisWindow.add(buttons, "center, span 2");
		thisWindow.doLayout();
		contentWrapper.repaint();
		thisWindow.setVisible(true);
	}

	public void promptFileReceive(String senderIp, int senderClientNumber,
			FileInfo file) {
		// DOSOMeTHING
		// Prompt the user.
		MessageText requestText = new MessageText("I wanna send you");
		MessageText fileNameText = new MessageText(
				Utils.trimFileName(file.fileName));
		MessageText fileSize = new MessageText(
				Utils.convertFileSize(file.fileSize));
		fileSize.setForeground(new Color(255, 0, 182));
		ImageButton OK = new ImageButton(Utils.image("message_ok.png"),
				Utils.image("message_ok_hover.png"));
		ImageButton NO = new ImageButton(Utils.image("message_no.png"),
				Utils.image("message_no_hover.png"));
		JPanel buttons = new JPanel();
		buttons.setLayout(new MigLayout("align center, center, insets 0"));
		buttons.add(OK, "gapright 8");
		buttons.add(NO);
		buttons.setOpaque(false);

		final ImagePanel thisWindow = messageWindows[senderClientNumber];
		final String filename = file.fileName;
		final long filesize = file.fileSize;
		final String ip = senderIp;
		final int senderNumber = senderClientNumber;
		final FileInfo fileInfo = file;
		OK.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				// Start transmit
				// Show the pending window, and finally show the downloading
				// Tell the world that you're busy
				busy(ip); 
				// window.
				messagePending(senderNumber);
				// And wake the DataReceiver to receive file;
				dataReceiver.receive(senderNumber, ip, filename, filesize);
				// Tell the sender to start sending file
				sender.addJob("startTransmit", ip, myComputer, fileInfo);
			}
		});
		NO.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				thisWindow.setVisible(false);
				contentWrapper.repaint();
				// Just denied send the result back to sender
				sender.addJob("transmitDenied", ip, myComputer, null);
			}
		});

		thisWindow.removeAll();
		thisWindow.add(requestText, "center, span 2, wrap 2");
		thisWindow.add(fileNameText);
		thisWindow.add(fileSize, "wrap 8");
		thisWindow.add(buttons, "center, span 2");
		thisWindow.doLayout();
		contentWrapper.repaint();
		thisWindow.setVisible(true);
	}

	public void messagePending(int senderClientNumber) {
		ImagePanel thisWindow = messageWindows[senderClientNumber];
		MessageText pendingText = new MessageText("Pending..");

		thisWindow.removeAll();
		thisWindow.add(pendingText);
		thisWindow.doLayout();
		contentWrapper.repaint();
	}

	public void messageProgress(int clientNumber, String fileName, long fileSize) {
		final ImagePanel thisWindow = messageWindows[clientNumber];
		// draw the progress bar

		MessageText a = new MessageText(Utils.trimFileName(fileName));
		MessageText b = new MessageText(Utils.convertFileSize(fileSize));
		b.setForeground(new Color(255, 0, 182));
		JPanel textWrapper = new JPanel();
		textWrapper.setLayout(new MigLayout("align center, center, insets 0"));
		textWrapper.setOpaque(false);
		textWrapper.add(a);
		textWrapper.add(b);

		Dimension size = new Dimension(171, 12);
		MessageText sendSize = new MessageText(Utils.convertFileSize(0));
		sendSize.setForeground(new Color(255, 255, 255));
		sendSize.setFont(new Font("Tahoma", Font.BOLD, 9));
		sendSize.setOpaque(false);
		progressBarSents[clientNumber] = sendSize;

		JPanel sendSizeWrapper = new JPanel();
		sendSizeWrapper.setLayout(new MigLayout("align right, insets 0"));
		sendSizeWrapper.setSize(size);
		sendSizeWrapper.setPreferredSize(size);
		sendSizeWrapper.add(sendSize, "gapright 12");
		sendSizeWrapper.setOpaque(false);

		JPanel progressBar = new JPanel();
		Dimension barSize = new Dimension(0, 12);
		progressBar.setBackground(new Color(80, 80, 80));
		progressBar.setSize(barSize);
		progressBar.setPreferredSize(barSize);
		this.progressBars[clientNumber] = progressBar;

		JPanel progressWrapper = new JPanel();
		progressWrapper.setLayout(null);
		progressWrapper.setBackground(new Color(100, 100, 100));
		progressWrapper.setSize(size);
		progressWrapper.setPreferredSize(size);
		progressWrapper.add(progressBar);

		JLayeredPane progressLayer = new JLayeredPane();
		progressLayer.setSize(size);
		progressLayer.setPreferredSize(size);
		progressLayer.add(progressWrapper, new Integer(0), 0);
		progressLayer.add(sendSizeWrapper, new Integer(1), 0);
		ImageButton stop = new ImageButton(Utils.image("message_stop.png"),
				Utils.image("message_stop_hover.png"));
		stop.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				//Set available
				available();
				// When stop occurred, how to stop the file transferring
				System.out.println("not implemented yet!");
				// stop
				dataReceiver.askToStop();
				dataSender.askToStop();
				// and hide
				thisWindow.setVisible(false);
			}
		});

		thisWindow.removeAll();
		thisWindow.add(textWrapper, "center, wrap 6");
		thisWindow.add(progressLayer, "center, wrap 8");
		thisWindow.add(stop, "center");
		thisWindow.setVisible(true);

		thisWindow.doLayout();
		contentWrapper.repaint();
	}

	public void messageUpdateProgress(int clientNumber, long fileSize,
			long sentSize) {
		int newWidth = (int) ((double) sentSize / (double) fileSize * (double) Config.messageBoxWidth);
		System.out.println(newWidth);
		Dimension size = new Dimension(newWidth, 12);
		progressBars[clientNumber].setSize(size);
		progressBars[clientNumber].setPreferredSize(size);
		progressBarSents[clientNumber].setText(Utils.convertFileSize(sentSize));
		progressBarSents[clientNumber].repaint();
		progressBars[clientNumber].doLayout();
		progressBars[clientNumber].repaint();
	}
	
	public void messageDownloadError(int senderClientNumber) {
		//Tell the world that you are now available.
		available();
		
		final ImagePanel thisWindow = messageWindows[senderClientNumber];
		MessageText a = new MessageText("Something goes wrong");
		MessageText b = new MessageText("I couldn't finish sending");
		ImageButton OK = new ImageButton(Utils.image("message_ok.png"),
				Utils.image("message_ok_hover.png"));

		OK.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				thisWindow.setVisible(false);
			}
		});
		thisWindow.removeAll();
		thisWindow.add(a, "center, wrap 2");
		thisWindow.add(b, "center, wrap 8");
		thisWindow.add(OK, "center");
		thisWindow.doLayout();
		contentWrapper.repaint();
		thisWindow.setVisible(true);
		progressBars[senderClientNumber] = null;
		progressBarSents[senderClientNumber] = null;
	}

	public void messageUploadError(int receiverClientNumber) {
		//Tell the world that you are now available
		available();
		
		final ImagePanel thisWindow = messageWindows[receiverClientNumber];
		MessageText a = new MessageText("Something goes wrong");
		MessageText b = new MessageText("I can not receive it.");
		ImageButton OK = new ImageButton(Utils.image("message_ok.png"),
				Utils.image("message_ok_hover.png"));

		OK.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				thisWindow.setVisible(false);
			}
		});
		thisWindow.removeAll();
		thisWindow.add(a, "center, wrap 2");
		thisWindow.add(b, "center, wrap 8");
		thisWindow.add(OK, "center");
		thisWindow.doLayout();
		contentWrapper.repaint();
		thisWindow.setVisible(true);
		progressBars[receiverClientNumber] = null;
		progressBarSents[receiverClientNumber] = null;
	}

	public void messageDownloadFinished(int senderClientNumber) {
		//Tell the world that you are now available
		available();
		// DODDODODOD
		final ImagePanel thisWindow = messageWindows[senderClientNumber];
		MessageText a = new MessageText("You just received that file.");
		ImageButton OK = new ImageButton(Utils.image("message_ok.png"),
				Utils.image("message_ok_hover.png"));
		OK.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				thisWindow.setVisible(false);
			}
		});

		thisWindow.removeAll();
		thisWindow.add(a, "center, wrap 8");
		thisWindow.add(OK, "center");
		thisWindow.doLayout();
		contentWrapper.repaint();
		progressBars[senderClientNumber] = null;
		progressBarSents[senderClientNumber] = null;
	}

	public void messageUploadFinished(int receiverClientNumber) {
		//Tell the world that you are now available
		available();
		// DODDODODOD
		final ImagePanel thisWindow = messageWindows[receiverClientNumber];
		MessageText a = new MessageText("I just received that file.");
		ImageButton OK = new ImageButton(Utils.image("message_ok.png"),
				Utils.image("message_ok_hover.png"));
		OK.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				thisWindow.setVisible(false);
			}
		});

		thisWindow.removeAll();
		thisWindow.add(a, "center, wrap 8");
		thisWindow.add(OK, "center");
		thisWindow.doLayout();
		contentWrapper.repaint();
		progressBars[receiverClientNumber] = null;
		progressBarSents[receiverClientNumber] = null;
	}

	public void messageUploadDenied(int receiverClientNumber) {
		//Tell the world that you are now available
		available();
		
		final ImagePanel thisWindow = messageWindows[receiverClientNumber];
		MessageText a = new MessageText("No, thanks.");
		ImageButton OK = new ImageButton(Utils.image("message_ok.png"),
				Utils.image("message_ok_hover.png"));
		OK.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				thisWindow.setVisible(false);
			}
		});

		thisWindow.removeAll();
		thisWindow.add(a, "center, wrap 8");
		thisWindow.add(OK, "center");
		thisWindow.doLayout();
		contentWrapper.repaint();
		progressBars[receiverClientNumber] = null;
		progressBarSents[receiverClientNumber] = null;
	}

	public boolean addToAliveList(OtherComputer alive) {
		// Remove the duplicate and done
		// With the lastActive we can determine the disconnected one.
		aliveList.put(alive.ip, alive);
		return true;
	}

	public void messageDecoder(Message message) {
		String senderIp = message.ip;
		String request = message.request;
		Computer computer = message.computerInfo;
		if (request.equals("ping")) {
			// Just send the respond.
			System.out.println("Just got a ping from:" + senderIp);
			sender.addJob("pong", senderIp, myComputer, null);
			// If not know send the ping.
			OtherComputer found = aliveList.get(senderIp);
			if (found == null) {
				System.out.println("Reping to host:" + senderIp);
				sender.addJob("ping", senderIp, myComputer, null);
			}
		} else if (request.equals("pong")) {
			// Duplicate the computer to OtherComputer.
			// With the lastAcitve value.
			// Tell Kicker to kick someone.
			System.out.println("Received pong from :" + senderIp);
			kicker.wake();

			OtherComputer alive = new OtherComputer();
			
			if (aliveList.get(senderIp) == null) {
				// The new
				System.out.println("Add the new to list.");
				int clientNumber = aliveList.availableClientNumber();
				if (clientNumber != -1) {
					int clientId = clientNumber + 1;
					alive.setDisplayName(computer.getDisplayName());
					alive.setClientId(clientId);
					alive.setIp(computer.getIp());
					alive.setBusy(computer.isBusy());
					alive.setLastActive(System.currentTimeMillis());
					addToAliveList(alive);
					System.out.println("Added the client " + clientId);
				} else {
					// IF client full.
					// Report! This should never happened.
					System.out.println("Max Client is reached!");
				}
			}
			else {
				System.out.println("Pong from the known.");
				alive = aliveList.get(computer.getIp());
				//Update list
				alive.setBusy(computer.isBusy());
				alive.setLastActive(System.currentTimeMillis());
				// And add to the client list.
				addToAliveList(alive);
			}
			// wake AliveDrawer to redraw the list
			aliveDrawer.wake();
		} else if (request.equals("fileReady")) {
			// List the file and prompt user for accept
			if(myComputer.isBusy() && !myComputer.getBusyWith().equals(senderIp)) {
				//If we're busy with someone not him. just denied
				sender.addJob("transmitDenied", senderIp, myComputer, message.fileInfo);
				return ;
			}
			OtherComputer otherComputer = aliveList.get(senderIp);
			promptFileReceive(senderIp, otherComputer.getClientId() - 1,
					message.fileInfo);
		} else if (request.equals("startTransmit")) {
			// Tell the DataSender to start file transmission
			System.out.println("Start Transmit!");
			int clientNumber = aliveList.get(senderIp).getClientId() - 1;
			System.out.println("Sender ip : " + senderIp + " clientNumber : " + clientNumber);
			String filePath = message.fileInfo.filePath;
			String fileName = message.fileInfo.fileName;
			long fileSize = message.fileInfo.fileSize;
			dataSender.send(clientNumber, senderIp, filePath, fileName,
					fileSize);
		} else if (request.equals("transmitDenied")) {
			// Stop the transmission show the denied window
			int clientNumber = aliveList.get(senderIp).getClientId() - 1;
			messageUploadDenied(clientNumber);
			
		} else if (request.equals("busy")) {
			OtherComputer a = aliveList.get(senderIp);
			a.setBusy(true);
			a.setLastActive(System.currentTimeMillis());
			addToAliveList(a);
		} else if (request.equals("available")) {
			OtherComputer a = aliveList.get(senderIp);
			a.setBusy(false);
			a.setLastActive(System.currentTimeMillis());
			addToAliveList(a);
		} else if (request.equals("nameChanged")) {
			System.out.println("------nameChanged------" + computer.displayName);
			OtherComputer a = aliveList.get(senderIp);
			a.setDisplayName(computer.displayName);
			a.setLastActive(System.currentTimeMillis());
			addToAliveList(a);
			aliveDrawer.wake();
		} else {
			System.out.println("Wrong request on DecodingMessage");
		}
	}
	
	public void busy(String with) {
		myComputer.setBusy(true);
		myComputer.setBusyWith(with);
		broadcasterSpawn.setJob("busy");
	}
	
	public void available() {
		myComputer.setBusy(false);
		myComputer.setBusyWith(null);
		broadcasterSpawn.setJob("available");
	}
}
