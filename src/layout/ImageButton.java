package layout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ImageButton extends JButton {
	BufferedImage image;
	BufferedImage clicked;
	BufferedImage hover;
	BufferedImage preferred;
	ImageButton th = this;
	boolean hoverIn = false;

	public ImageButton(Image img) {
		/*
		 * image = new BufferedImage(img.getWidth(null), img.getHeight(null),
		 * BufferedImage.TYPE_INT_ARGB); Graphics2D g = image.createGraphics();
		 * g.drawImage(img, 0, 0, null); g.dispose();
		 */
		image = (BufferedImage) img;
		preferred = image;
		this.setBorder(null);
		this.setContentAreaFilled(false);
		this.setSize(new Dimension(image.getWidth(), image.getHeight()));
		this.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
		this.setMinimumSize(new Dimension(image.getWidth(), image.getHeight()));
		this.setMaximumSize(new Dimension(image.getWidth(), image.getHeight()));
	}
	public ImageButton(Image img, Image clicked) {
		this(img);
		this.clicked = (BufferedImage) clicked;
		this.getModel().addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				// TODO Auto-generated method stub
				ButtonModel model = (ButtonModel) e.getSource();
				if(model.isPressed()) {
					preferred = th.clicked;
					repaint();
				} else {
					if(!hoverIn) preferred = th.image;
					else preferred = th.hover;
					repaint();
				}
			}
		});
	}
	public ImageButton(Image img, Image clicked, Image hover) {
		this(img, clicked);
		this.hover = (BufferedImage) hover;
		this.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent e) {
				hoverIn = true;
				preferred = th.hover;
				repaint();
			}
			public void mouseExited(MouseEvent e){
				hoverIn = false;
				preferred = th.image;
				repaint();
			}
		});
	}
	public void setClicked () {
		this.preferred = clicked;
		this.repaint();
	}
	
	public void set(Image img){
		this.preferred = (BufferedImage) img;
		this.repaint();
	}
	
	public void setNormal(){
		if(!hoverIn) this.preferred = image;
		else this.preferred = hover;
		this.repaint();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(preferred, 0, 0, null);
	}
	
	public void setPosition(int x, int y) {
		this.setBounds(x, y, this.getWidth(), this.getHeight());
	}
}
