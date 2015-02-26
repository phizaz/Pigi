package layout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;


public class ImagePanel extends JPanel {
	private BufferedImage img = null;
	
	public ImagePanel(String img) {
		this(new ImageIcon(img).getImage());
	}
	public ImagePanel(InputStream input) throws IOException{
		this(ImageIO.read(input));
	}
	public ImagePanel(Image img) {
		this.img = (BufferedImage) img;
		Dimension size = new Dimension(this.img.getWidth(), this.img.getHeight());
		
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		setSize(size);
		setLayout(null);
	}
	
	public void setPosition(int x, int y) {
		setBounds(x, y, this.getWidth(), this.getHeight());
	}
	
	public void paintComponent(Graphics g) {
		g.drawImage(img, 0, 0, null);
	}
}
