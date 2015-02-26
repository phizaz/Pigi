import java.awt.Image;
import java.io.Serializable;

import javax.swing.ImageIcon;


public class Computer implements Serializable {
	protected String displayName;
	protected String ip;
	protected boolean busy = false;

	public Computer() {
	}

		
	public boolean isBusy() {
		return busy;
	}


	public void setBusy(boolean busy) {
		this.busy = busy;
	}


	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
}
