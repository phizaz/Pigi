
import java.io.Serializable;


public class Message implements Serializable {
	public String request;
	public String ip;
	public FileInfo fileInfo = null;
	public Computer computerInfo = null;
	
	public Message(String request, String ip, FileInfo fileInfo, Computer computerInfo) {
		this.request = request;
		this.ip = ip;
		this.fileInfo = fileInfo;
		this.computerInfo = computerInfo;
	}
}
