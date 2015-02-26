import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.EventSetDescriptor;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class NameField extends JTextField {
	private NameField th = this;
	private Color normalColor = new Color(255,0,109);
	private Color hoverColor = new Color(255,60,168);
	private Color editingColor = new Color(255,60,168);
	private JPanel parent;
	
	public NameField(String text, JPanel parent){
		this.parent = parent;
		Dimension size = new Dimension(115,15);
		this.setSize(size);
		this.setPreferredSize(size);
		this.setHorizontalAlignment(JTextField.CENTER);
		this.setFont(new Font("Tahoma", Font.BOLD, 15));
		this.setForeground(normalColor);
		this.setOpaque(false);
		this.setBorder(null);
		this.setText(text);
	
		this.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				th.parent.requestFocusInWindow();
			}
		});
		this.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent arg0) {
				// TODO Auto-generated method stub
				th.setForeground(normalColor);
			}
			
			@Override
			public void focusGained(FocusEvent arg0) {
				// TODO Auto-generated method stub
				th.setForeground(editingColor);
			}
		});
	}
	
}
