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
		
		
		//Creates a new jMenu bar
		addShapesPanel();
		
	
		//sets up panesl
		JPanel properties = new PropertiesPanel(my_canvas);
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(my_canvas.getCanvas(), BorderLayout.CENTER);
		panel.add(properties, BorderLayout.SOUTH);
		this.add(panel);
		
		
		
		this.pack();
		this.setMinimumSize(new Dimension(getWidth(), 256));
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	
	
	private void addShapesPanel(){
		JPanel a_panel = new JPanel();
		a_panel.setLayout(new GridLayout(0,1));
		
		final JButton circle_item = new JButton("Circle");
		a_panel.add(circle_item);
		
		final JButton triangle_item = new JButton("Triangle");
		a_panel.add(triangle_item);
		
		final JButton rectangle_item = new JButton("Rectangle");
		a_panel.add(rectangle_item);
		
		final JButton  rocket_item = new JButton("Rocket");
		a_panel.add(rocket_item);
		JPanel p = new JPanel();
		
		
		
		ActionListener a = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JButton source = (JButton) e.getSource();
				if(source == triangle_item){
					my_canvas.attachObject(PhyPolygon.getEqTriangle(1));
				} else if (source == circle_item) {
					my_canvas.attachObject(new PhyCircle(.5f));
				} else if (source == rectangle_item) {
					my_canvas.attachObject(PhyPolygon.getSquare(1));
				} else if (source == rocket_item) {
					my_canvas.attachObject(PhyComposite.getRocket(1));
				}
				my_canvas.getCanvas().repaint();

			}
		};
		
		triangle_item.addActionListener(a);
		circle_item.addActionListener(a);
		rectangle_item.addActionListener(a);
		rocket_item.addActionListener(a);
		
		p.add(a_panel);
		p.add(new ViewPanel(my_canvas));
		
		p.setLayout(new GridLayout(0, 1));
		a_panel.setBorder(BorderFactory.createTitledBorder("Add Shape :)"));
		JPanel q = new JPanel();
		q.add(p);
		this.add(q, BorderLayout.EAST);

		
		
	}
	
	
	
	
	
	
}
