import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

import org.omg.CosNaming.NamingContextExtPackage.AddressHelper;

public class Utils {
	public static Dimension [] userNumberToDimension = {
		new Dimension(0,2),
		new Dimension(0,1),
		new Dimension(0,0),
		new Dimension(1,0),
		new Dimension(2,0),
		new Dimension(3,0),
		new Dimension(3,1),
		new Dimension(3,2)
	};
	
	public static Enumeration<NetworkInterface> getNetworkInterfaces() {
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface
					.getNetworkInterfaces();
			return interfaces;
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static ArrayList<NetworkCard> getNetworkCardsList() {
		ArrayList<NetworkCard> cards = new ArrayList<NetworkCard>();
		Enumeration<NetworkInterface> interfaceList = getNetworkInterfaces();
		try {
			while (interfaceList.hasMoreElements()) {
				NetworkInterface current = interfaceList.nextElement();
				if (!current.isUp() || current.isLoopback()
						|| current.isVirtual())
					continue;
				Enumeration<InetAddress> addresses = current.getInetAddresses();
				String ip = getIp(current);
				if (ip != null){
					cards.add(new NetworkCard(current.getDisplayName(), ip, current));
				}
					
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return cards;
	}

	public static String getIp(NetworkInterface networkInterface) {
		Enumeration<InetAddress> addresses = networkInterface
				.getInetAddresses();
		while (addresses.hasMoreElements()) {
			InetAddress currentAddress = addresses.nextElement();
			if (currentAddress instanceof Inet4Address) {
				return currentAddress.getHostAddress();
			}
		}
		return null;
	}

	public static String getSubnet(String ip) {
		// return the computer subnet
		String [] splits = ip.split("\\.");
		return splits[0] + "." + splits[1] + "." + splits[2]; 
	}

	public static InputStream file(String path) {
		return Utils.class.getClassLoader().getResourceAsStream("res/" + path);
	}

	public static void addFont(InputStream font){
		try {
			Font a = Font.createFont(Font.TRUETYPE_FONT, font);
			GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(a);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (FontFormatException e) {
			e.printStackTrace();
		}
	}
	
	public static Image image(String path) {
		try {
			return ImageIO.read(file(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getDownloadFolder(){
		JFileChooser fr = new JFileChooser();
		FileSystemView fw = fr.getFileSystemView();
		String defaultDir = fw.getDefaultDirectory().toString();
		String downloadDir = defaultDir + "/Downloads";
		
		File dir = new File(downloadDir);
		if(!dir.exists()) {
			dir.mkdir(); // Create the dir. if not exists
		}
		return downloadDir;
	}
	
	public static boolean isWindows() {
		return Config.OS.indexOf("win") >= 0;
	}
	
	public static boolean isMac() {
		return Config.OS.indexOf("mac") >= 0;
	}
	
	public static String convertFileSize(long fileSize){
		double size = (double) fileSize;
		int cnt = 0;
		String [] i = {
				"B", "KB", "MB", "GB", "TB", "PB"
		};
		while(size > 1000) {
			size /= 1000;
			cnt++;
		}
		return String.format("%.1f %s", size, i[cnt]);
	}
	
	public static String trimFileName(String fileName) {
		int len = fileName.length();
		String trimmedFileName = fileName.substring(0, len < Config.fileNameMaxLength ? len : Config.fileNameMaxLength);
		return trimmedFileName + (fileName.length() > Config.fileNameMaxLength ? ".." : "");
	}
}
