import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.print.DocFlavor.URL;
import javax.swing.ImageIcon;

public class Config {
	public static final String OS = System.getProperty("os.name").toLowerCase();
	public static final int pingTimeout = 333;
	public static final int pingTries = 3;
	public static final int communicationTimeout = 1000;
	public static final int senderTries = 3;
	public static final int broadcasterTries = 3;
	public static final int connectorCooldown = 3000;
	public static final int finderRoutine = 79000;
	public static final int finderCooldown = 15000;
	public static final int earlyConnectedTimeout = 1000; // Time to be considered as early connected.
	public static final int kickerCooldown = 1500;
	public static final int kickerTurn = 15000;
	public static final int delayConsideredDisconnected = 7000;
	public static final int communicationPort = 3333;
	public static final int dataPort = 3334;
	public static final String downloadPathWin = "";
	public static final String downloadPathMac = "~/Downloads/";
	public static final int maxClient = 8;
	public static final int dataTransferBuffer = 256 * 1024;
	public static final int finderCount = 30;
	public static final int broadcasterCount = 8;
	public static final int fileNameMaxLength = 17;
	public static final int networkCardNameMaxLength = 10;
	public static final int	senderReceiverUpdateLastActivityCooldown = 1500;
	
	
	public static final int messageBoxWidth = 171; 
}
