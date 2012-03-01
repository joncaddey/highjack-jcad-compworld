package main;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;

import phyObj.PhyObject;
import phyObj.Vector2f;

/**
 * 
 * @author Steven Cozart Jonathan Caddey
 *
 */
@SuppressWarnings("serial")
public class PropertiesPanel extends JPanel implements Observer,
		ActionListener, FocusListener {

	private static final DecimalFormat DF = new DecimalFormat("0.00");
	private JTextField my_xField;
	private JTextField my_yField;
	private JTextField my_scaleField;
	private JSlider my_scaleSlider;
	private JTextField my_degreesField;
	private JTextField currentlyModifying;

	private final VirtualCanvas my_canvas;
	private JButton my_delteButton;

	public PropertiesPanel(final VirtualCanvas the_canvas) {
		super();
		my_canvas = the_canvas;
		the_canvas.addObserver(this);
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		addTranslatePanel();
		addScalePanel();
		addRotationPanel();
		addDeletePanel();
		updateSelected();

	}

	private void addTranslatePanel() {
		final JPanel p = new JPanel();
		p.setBorder(BorderFactory.createTitledBorder("Position"));

		my_xField = new JTextField(5);
		my_xField.addFocusListener(this);
		my_xField.addActionListener(this);
		p.add(new JLabel("x:"));
		p.add(my_xField);

		my_yField = new JTextField(5);
		my_yField.addFocusListener(this);
		my_yField.addActionListener(this);
		p.add(new JLabel("y:"));
		p.add(my_yField);
		add(p);
	}

	private void addScalePanel() {
		final JPanel p = new JPanel();
		p.setBorder(BorderFactory.createTitledBorder("Scale"));
		my_scaleField = new JTextField(5);
		my_scaleField.addFocusListener(this);
		my_scaleField.addActionListener(this);
		p.add(my_scaleField);
		p.add(new JLabel("%"));

		my_scaleSlider = new JSlider();
		my_scaleSlider.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent the_e) {
				fixScaleSlider();
			}
		});
		// p.add(my_scaleSlider());
		fixScaleSlider();
		add(p);
	}

	private void fixScaleSlider() {
		// my_scaleSlider.setMinimum(minimum)
	}

	private void addRotationPanel() {
		final JPanel p = new JPanel();
		p.setBorder(BorderFactory.createTitledBorder("Rotation"));
		my_degreesField = new JTextField(5);
		my_degreesField.addFocusListener(this);
		my_degreesField.addActionListener(this);
		p.add(new JLabel("degrees:"));
		p.add(my_degreesField);
		add(p);
	}

	/**
	 * Makes a pannel with a delete button and adds it to the properties panel
	 */
	private void addDeletePanel() {
		final JPanel p = new JPanel();
		p.setBorder(BorderFactory.createTitledBorder("Option"));
		my_delteButton = new JButton();
		my_delteButton.setText("Remove");
		my_delteButton.addFocusListener(this);
		my_delteButton.addActionListener(this);
		p.add(my_delteButton);
		add(p);
	}

	@Override
	public void update(Observable the_canvas, Object obj) {
		if (the_canvas != my_canvas) {
			return;
		}
		updateSelected();

	}

	private void updateSelected() {
		// this is kind of dirty, simply asking for reference to node itself
		PhyObject obj = my_canvas.getSelected();
		if (obj == null) {
			if (my_xField.isEnabled()) {
				my_xField.setEnabled(false);
				my_yField.setEnabled(false);
				my_scaleField.setEnabled(false);
				my_degreesField.setEnabled(false);
				my_delteButton.setEnabled(false);

				my_xField.setText("");
				my_yField.setText("");
				my_scaleField.setText("");
				my_degreesField.setText("");
			}
		} else {
			if (!my_xField.isEnabled()) {
				my_xField.setEnabled(true);
				my_yField.setEnabled(true);
				my_scaleField.setEnabled(true);
				my_degreesField.setEnabled(true);
				my_delteButton.setEnabled(true);
			}
			if (currentlyModifying != my_xField) {
			my_xField.setText(DF.format(obj.getPosition().x));
			}
			if (currentlyModifying != my_yField) {
				my_yField.setText(DF.format(obj.getPosition().y));
			}
			if (currentlyModifying != my_scaleField) {
				my_scaleField.setText(DF.format(obj.getSize() * 100));
			}
			if (currentlyModifying != my_degreesField) {
				my_degreesField.setText(String.valueOf(((int)obj.getRotationDegrees()) % 180));
			}
			if (currentlyModifying == null) {
				final JTextField f;
				if (my_xField.hasFocus()) {
					f = my_xField;
				} else if (my_yField.hasFocus()) {
					f = my_yField;
				} else if (my_scaleField.hasFocus()) {
					f = my_scaleField;
				} else if(my_degreesField.hasFocus()) {
					f = my_degreesField;
				} else f = null;
				if (f != null) {
					currentlyModifying = f;
					f.setCaretPosition(0);
					f.moveCaretPosition(f.getText().length());
				}
			}
		}
		
		
	}


	@Override
	public void focusGained(FocusEvent e) {
		if (e.getSource() instanceof JTextField) {
			JTextField f = (JTextField) e.getSource();
			f.setCaretPosition(0);
			f.moveCaretPosition(f.getText().length());
			currentlyModifying = f;
		}
	}

	@Override
	public void focusLost(FocusEvent arg0) {
		currentlyModifying = null;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof JTextField) {
			JTextField f = (JTextField) e.getSource();
			
			PhyObject obj = my_canvas.getSelected();
			if (obj != null) {
				try {
					if (f == my_xField) {
						obj.setPosition(Float.parseFloat(my_xField
								.getText()), obj.getPosition().y);
						obj.setVelocity(new Vector2f(0, 0));
					} else if (f == my_yField) {
						obj.setPosition(obj.getPosition().x, Float.parseFloat(my_yField
.getText()));
						obj.setVelocity(new Vector2f(0, 0));
					} else if (f == my_scaleField) {
						obj.setSize(Float.parseFloat(my_scaleField.getText()) / 100);
					} else if (f == my_degreesField) {
					obj.setRotationDegrees(Float.parseFloat(my_degreesField
							.getText()));
					obj.setAngularVelocity(0);
					}
				} catch (NumberFormatException the_nfe) {
					// don't caaare
				} catch (IllegalArgumentException the_iae) {
					// still don't
				}
				my_canvas.refresh();
				currentlyModifying = null;
			}
		} else if (e.getSource() == my_delteButton) {
			my_canvas.remove();
		}

	}

}
