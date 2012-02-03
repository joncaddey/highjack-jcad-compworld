import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.media.opengl.GLProfile;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;


public class Main {

	VirtualCanvas my_canvas;
	public Main() {
		my_canvas = new VirtualCanvas(); 
	}
	
	public void start() {
		
		JFrame appFrame;
		appFrame = new JFrame("Virtual Editor");
		appFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		appFrame.setMinimumSize(new Dimension(256, 256));
		JPanel properties = new PropertiesPanel(my_canvas);
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(my_canvas.getCanvas(), BorderLayout.CENTER);
		panel.add(properties, BorderLayout.SOUTH);
		//JSplitPane panel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, my_canvas.getCanvas(), properties);
		appFrame.add(panel);
		appFrame.pack();
		//if (Toolkit.getDefaultToolkit().isFrameStateSupported(JFrame.MAXIMIZED_BOTH))
			//appFrame.setExtendedState(appFrame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		appFrame.setLocationRelativeTo(null);
		appFrame.setVisible(true);
	}
	public static void main(String args[]) {
		
		GLProfile.initSingleton();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				(new Main()).start();
			}
		});
	}
}
