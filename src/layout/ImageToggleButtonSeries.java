package layout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class ImageToggleButtonSeries extends ImageToggleButton {
	ImageToggleButtonSeries [] series;
	ImageToggleButtonSeries th = this;
	public ImageToggleButtonSeries (Image idle, Image selected, ImageToggleButtonSeries [] series) {
		super(idle, selected);
		this.series = series;
		this.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				ImageToggleButton button = (ImageToggleButton)e.getSource();
				if(!button.isSelected()) {
					for(int i = 0; i < th.series.length; i++) {
						if(th.series[i].isSelected()) return ;
					}
					button.setSelected(true);
				}
			}
		});
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		if(this.isSelected()) {
			for(int i = 0; i < series.length; i++) {
				if(series[i] != this) {
					series[i].setSelected(false);
				}
			}
		}
	}
}
