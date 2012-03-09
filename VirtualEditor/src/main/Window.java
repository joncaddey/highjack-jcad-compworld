package main;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import phyObj.PhyCircle;
import phyObj.PhyComposite;
import phyObj.PhyPolygon;


/**
 * 
 * @author Steven Cozart Jonathan Caddey
 *
 */
@SuppressWarnings("serial")
public class Window extends JFrame {
	
	
	
	private VirtualCanvas my_canvas;
	

	public Window(String the_title, VirtualCanvas the_canvas){
		super(the_title);
		my_canvas = the_canvas;
		setup();
	}

	private void setup() {
		//sets the icon to the hulk..... hulk smash
		Image icon = Toolkit.getDefaultToolkit().createImage(
		"src/hulk.jpg");
		this.setIconImage(icon);
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.add(my_canvas.getCanvas());
		
		this.setMinimumSize(new Dimension(256, 256));
		this.setLocationRelativeTo(null);
		if (Toolkit.getDefaultToolkit().isFrameStateSupported(JFrame.MAXIMIZED_BOTH))
			this.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		this.setVisible(true);
	}
}
