import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class PropertiesPanel extends JPanel{

	JTextField xField;
	JTextField yField;
	JTextField scaleField;
	JTextField radiansField;
	JTextField degreesField;
	
	public PropertiesPanel() {
		super();
		//setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		addTranslatePanel();
		addScalePanel();
		addRotationPanel();
	}
	
	private void addTranslatePanel() {
		final JPanel p = new JPanel();
		p.setBorder(BorderFactory.createTitledBorder("Position"));
		
		xField = new JTextField(5);
		p.add(new JLabel("x:"));
		p.add(xField);
		
		yField = new JTextField(5);
		p.add(new JLabel("y:"));
		p.add(yField);
		add(p);
	}
	
	private void addScalePanel() {
		final JPanel p = new JPanel();
		p.setBorder(BorderFactory.createTitledBorder("Scale"));
		scaleField = new JTextField(5);
		p.add(new JLabel("scale:"));
		p.add(scaleField);
		add(p);
	}
	
	private void addRotationPanel() {
		final JPanel p = new JPanel();
		p.setBorder(BorderFactory.createTitledBorder("Rotation"));
		degreesField = new JTextField(5);
		p.add(new JLabel("degrees:"));
		p.add(degreesField);
		
		radiansField = new JTextField(5);
		p.add(new JLabel("radians:"));
		p.add(radiansField);
		add(p);
	}
	
	
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JFrame f = new JFrame();
		f.add(new PropertiesPanel());
		f.pack();
		f.setVisible(true);
	}

}
