package layout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JToggleButton;


public class ImageToggleButton extends JToggleButton {
	BufferedImage idle;
	BufferedImage selected;
	public ImageToggleButton (Image idle, Image selected) {
		this.idle = (BufferedImage) idle;
		this.selected = (BufferedImage) selected; 
		this.setBorder(null);
		Dimension size = new Dimension(this.idle.getWidth(), this.idle.getHeight());
		this.setSize(size);
		this.setPreferredSize(size);
		this.setMinimumSize(size);
		this.setMaximumSize(size);
		this.setBorder(null);
		this.setContentAreaFilled(false);
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(!this.isSelected()) 
			g.drawImage(idle, 0, 0, null);
		else 
			g.drawImage(selected,0,0,null);
	}
}
