import java.awt.Image;


public class MyComputer extends Computer {
	private String subnet;
	private String busyWith;
	
	public String getBusyWith() {
		return busyWith;
	}

	public void setBusyWith(String busyWith) {
		this.busyWith = busyWith;
	}

	public String getSubnet() {
		return subnet;
	}

	public void setSubnet(String subnet) {
		this.subnet = subnet;
	}
	
}
