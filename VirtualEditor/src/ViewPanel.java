import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


@SuppressWarnings("serial")
public class ViewPanel extends JPanel implements ActionListener, FocusListener{
	private VirtualCanvas my_canvas;
	private final JTextField my_xField;
	private final JTextField my_yField;
	private final JTextField my_zoomField;
	
	public ViewPanel(VirtualCanvas the_canvas) {
		my_canvas = the_canvas;
		my_xField = new JTextField(5);
		my_yField = new JTextField(5);
		my_zoomField = new JTextField(5);
		my_xField.addActionListener(this);
		my_yField.addActionListener(this);
		my_zoomField.addActionListener(this);
		setBorder(BorderFactory.createTitledBorder("Change View"));
		setLayout(new GridLayout(0, 2));
		add(new JLabel("x:", FlowLayout.TRAILING));
		add(my_xField);
		add(new JLabel("y:", FlowLayout.TRAILING));
		add(my_yField);
		add(new JLabel("zoom:", FlowLayout.TRAILING));
		add(my_zoomField);
		reflectReality();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		parseTextFields();
		JTextField f = (JTextField) e.getSource();
		f.setCaretPosition(0);
		f.moveCaretPosition(f.getText().length());
	}
	
	private void parseTextFields() {
		try {
			my_canvas.getRoot().translateX = -Float.parseFloat(my_xField.getText());
			my_canvas.getRoot().translateY = -Float.parseFloat(my_yField.getText());
			my_canvas.getRoot().scale = Float.parseFloat(my_zoomField.getText());
		} catch (NumberFormatException the_nfe) {
			// don't caaare
		}
		reflectReality();
		my_canvas.refresh();
	}
	
	private void reflectReality() {
		my_xField.setText(String.valueOf(-my_canvas.getRoot().translateX));
		my_yField.setText(String.valueOf(-my_canvas.getRoot().translateY));
		my_zoomField.setText(String.valueOf(my_canvas.getRoot().scale));
	}
	@Override
	public void focusGained(FocusEvent arg0) {
		
	}
	@Override
	public void focusLost(FocusEvent e) {
		parseTextFields();
		
	}
}
