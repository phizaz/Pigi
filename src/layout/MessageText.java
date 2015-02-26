package layout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;


public class MessageText extends JLabel {
	public MessageText(String s) {
		super(s);
		
		setFont(new Font("Tahoma", Font.BOLD, 11));
		setForeground(new Color(216,216,216));
	}
}
