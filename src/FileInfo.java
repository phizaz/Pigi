import java.io.Serializable;


public class FileInfo implements Serializable {
	public String fileName;
	public String filePath;
	long fileSize;
	
	public FileInfo(String fileName, String filePath, long fileSize){
		this.fileName = fileName;
		this.filePath = filePath;
		this.fileSize = fileSize;
	}
}
