import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.security.Guard;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.rmi.CORBA.Util;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import net.miginfocom.swing.MigLayout;

import com.sun.awt.*;

import layout.*;

public class Pigi extends JFrame {
	int posX = 0, posY = 0;
	private JFrame thisFrame = this;
	private JLayeredPane contentWrapper;
	private ArrayList<NetworkCard> networkCards;
	private ImageToggleButtonSeries cardsSelector[];
	public void initGUI() {
		this.setTitle("Pigi");
		this.setSize(800, 600);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setUndecorated(true);
		

		contentWrapper = new JLayeredPane();
		contentWrapper.setBorder(new LineBorder(new Color(160,160,160)));
		contentWrapper.setBounds(0, 0, this.getWidth(), this.getHeight());

		// make the window middle of the screen
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height
				/ 2 - this.getSize().height / 2);

		ImagePanel backgroundPanel = new ImagePanel(
				Utils.image("background.png"));
		ImagePanel logoPanel = new ImagePanel(Utils.image("logo.png"));

		logoPanel.setOpaque(false);

		JLayeredPane body = new JLayeredPane();
		this.add(body);
		// body.setBounds(0, 0, this.getWidth(), this.getHeight());
		body.add(backgroundPanel, new Integer(0), 0);

		JPanel logoWrapper = new JPanel();
		logoWrapper.setOpaque(false);
		logoWrapper.setBounds(0, 0, this.getWidth(), this.getHeight());
		logoWrapper.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		logoWrapper.add(logoPanel, gbc);

		int cardsCount = networkCards.size();
		String[] networkCardNames = new String[cardsCount];
		for (int i = 0; i < cardsCount; i++) {
			NetworkCard current = networkCards.get(i);
			String name = current.name.substring(0, Math.min(current.name.length(), Config.networkCardNameMaxLength));
			if(name.length() != current.name.length()) name += ".."; 
			String ip = current.ip;
			networkCardNames[i] = name + " - " + ip;
		}

		
		JPanel content = new JPanel();
		content.setSize(new Dimension(this.getWidth(), this
				.getHeight()));
		content.setLayout(new MigLayout(
				"align center, center, insets 0"));
		if (cardsCount == 0) {
			// Client not connect to the network
			System.out.println("You're not connecting to the network.");
			content.add(logoPanel, "center, wrap 20");
			JLabel text = new JLabel("You're not connected to network.", JLabel.CENTER);
			text.setFont(new Font("Tahoma", Font.PLAIN, 14));
			content.add(text, "center");
		} else {
			cardsSelector = new ImageToggleButtonSeries[cardsCount];
			for (int i = 0; i < cardsCount; i++) {
				cardsSelector[i] = new ImageToggleButtonSeries(
						Utils.image("selector_network.png"),
						Utils.image("selector_network_selected.png"),
						cardsSelector);
				JLabel text = new JLabel(networkCardNames[i]);
				text.setFont(new Font("Tahoma", Font.PLAIN, 10));
				text.setForeground(new Color(66,66,66));
				text.setOpaque(false);
				cardsSelector[i].setLayout(new MigLayout("align center, center, insets 0"));
				cardsSelector[i].add(text);
			}
			cardsSelector[0].setSelected(true);

			ImageButton goButton = new ImageButton(
					Utils.image("button_go.png"),
					Utils.image("button_go_clicked.png"));
			goButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					for(int i = 0; i < cardsSelector.length; i++) {
						if(cardsSelector[i].isSelected()) {
							go(networkCards.get(i));
							return ;
						}
					}
					System.out.println("No cards selected! but shouldn't happened.");
				}
			});
			
			content.add(logoPanel, "align center, wrap 25, gaptop 115");
			for (int i = 0; i < cardsCount; i++) {
				content.add(cardsSelector[i], "align center, wrap 10");
			}
			content.add(goButton, "align center, wrap, gaptop 10");
		}
		content.setOpaque(false);
		contentWrapper.add(content, new Integer(0), 0);
		body.add(contentWrapper, new Integer(1), 0);
		

		ImageButton exitButton = new ImageButton(Utils.image("exit.png"),
				Utils.image("exit_clicked.png"));
		exitButton
				.setPosition(this.getWidth() - exitButton.getWidth() - 15, 15);
		exitButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				thisFrame.dispose();
				System.exit(0);
			}
		});
		body.add(exitButton, new Integer(100), 0);

		// Get the window location when mouse clicked.
		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				posX = e.getX();
				posY = e.getY();
			}
		});
		// Make the window draggable
		this.addMouseMotionListener(new MouseAdapter() {
			public void mouseDragged(MouseEvent e) {
				setLocation(e.getXOnScreen() - posX, e.getYOnScreen() - posY);
			}
		});
	}

	public Pigi() {

		networkCards = Utils.getNetworkCardsList();
		initGUI();
		this.setVisible(true);

	}
	public void go(NetworkCard networkCard) {
		System.out.println("go!!");
		Controller controller = new Controller(contentWrapper, networkCard);
		
	}

	public static void main(String[] args) {

		new Pigi();
		System.out.println("Finish!");
	}
}
