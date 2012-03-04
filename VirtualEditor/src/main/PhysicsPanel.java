package main;

import java.awt.GridLayout;
import java.awt.JobAttributes;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * 
 * @author Steven Cozart Jonathan Caddey
 * 
 */
@SuppressWarnings("serial")
public class PhysicsPanel extends JPanel implements ActionListener, FocusListener {
	private VirtualCanvas my_canvas;
	private final JTextField gravityField;
	private final JTextField speedField;
	private final JCheckBox collisionBox;
	private final JButton removeAllButton;
	private final JTextField powerField;
	private final JButton launchButton;
	
	private int my_power;

	public PhysicsPanel(VirtualCanvas the_canvas) {
		my_canvas = the_canvas;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createTitledBorder("Physics :)"));
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(0, 2));

		p.add(new JLabel("Gravity:"));
		gravityField = new JTextField(3);
		gravityField.setToolTipText("Input must be a integer (negative or postive); press enter to activate");
		gravityField.setText(String.valueOf((int) my_canvas.getGravity()));
		gravityField.addActionListener(this);
		p.add(gravityField);

		p.add(new JLabel("Time:"));
		speedField = new JTextField(3);
		speedField.setToolTipText("Input must be a postive integer; press enter to activate");
		speedField.setText(String.valueOf(my_canvas.getSpeedScale()));
		speedField.addActionListener(this);
		p.add(speedField);

		collisionBox = new JCheckBox("Collisions", true);
		collisionBox.addActionListener(this);

		add(p);

		add(collisionBox);
		removeAllButton = new JButton("Remove All");
		removeAllButton.addActionListener(this);
		add(removeAllButton);

		p = new JPanel();
		p.setBorder(BorderFactory.createTitledBorder("Launcher"));
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		JPanel q = new JPanel();
		q.setLayout(new GridLayout(1, 2));
		q.add(new JLabel("Power:"));
		
		powerField = new JTextField();
		powerField.setText(String.valueOf(my_power));
		powerField.addActionListener(this);
		q.add(powerField);
		p.add(q);
		
		launchButton = new JButton("Launch");
		launchButton.addActionListener(this);
		p.add(launchButton);
		add(p);
	}

	
	@Override
	public void focusGained(FocusEvent arg0) {
	}

	@Override
	public void focusLost(FocusEvent arg0) {
		//parseTextFields();

	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source instanceof JTextField) {
			JTextField f = (JTextField) e.getSource();

			if (f == gravityField) {
				try {
					my_canvas.setGravity(Integer.parseInt(gravityField.getText()));
				} catch (Exception ee) {
				}
				gravityField.setText(String.valueOf((int) my_canvas.getGravity()));
			} else if (f == speedField) {
				try {
					my_canvas.setSpeedScale(Float.parseFloat(speedField.getText()));
				} catch (Exception ee) {
				}
				speedField.setText(String.valueOf(my_canvas.getSpeedScale()));
			} else if (f == powerField) {
				try {
					my_power = Integer.parseInt(powerField.getText());
					my_canvas.launch((float)my_power);
				} catch(NumberFormatException ee) {
					JOptionPane.showMessageDialog(null,  "The power must be a integer", "Number Format",JOptionPane.INFORMATION_MESSAGE  );
				}
				powerField.setText(String.valueOf(my_power));
				
			
			}

			f.setCaretPosition(0);
			f.moveCaretPosition(f.getText().length());
		} else {
			if (source == removeAllButton) {
				if (JOptionPane.showConfirmDialog(null, "Remove all objects?",
						"Remove All", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
					my_canvas.removeAll();
				}
			} else if (source == collisionBox) {
				my_canvas.setCollisions(collisionBox.isSelected());
			} else if (source == launchButton) {
				try {
					my_power = Integer.parseInt(powerField.getText());
					my_canvas.launch((float)my_power);
				} catch(NumberFormatException ee) {
					JOptionPane.showMessageDialog(null,  "The power must be a integer", "Number Format",JOptionPane.INFORMATION_MESSAGE  );
				}
				powerField.setText(String.valueOf(my_power));
				my_canvas.launch((float)my_power);
			}
		}

	}

}
