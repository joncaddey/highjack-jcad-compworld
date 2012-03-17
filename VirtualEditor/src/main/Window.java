package main;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;


/**
 * 
 * @author Steven Cozart Jonathan Caddey
 *
 */
@SuppressWarnings("serial")
public class Window extends JFrame implements Observer{
	
	
	private final AsteroidsGame my_game;
	private AsteroidsCanvas my_canvas;
	private JTextField my_score;
	private JTextField my_level;

	public Window(String the_title){
		super(the_title);
		my_game = new AsteroidsGame();
		my_game.addObserver(this);
		my_canvas = AsteroidsCanvas.getInstance(my_game);
		my_score = new JTextField(15);
		my_score.setEnabled(false);
		my_level = new JTextField(5);
		my_level.setEnabled(false);
		setup();
	}

	private void setup() {
		//sets the icon to the hulk..... hulk smash
		Image icon = Toolkit.getDefaultToolkit().createImage(
		"src/hulk.jpg");
		this.setIconImage(icon);
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
		JPanel root = new JPanel();
		root.setLayout(new BorderLayout());
		root.add(my_canvas, BorderLayout.CENTER);
		JPanel status = new JPanel();
		//status.setLayout(new BoxLayout(status, BoxLayout.X_AXIS));
		status.add(new JButton("New game"));
		
		JPanel p;
		
		p = new JPanel();
		p.add(new JLabel("Score:"));
		p.add(my_score);
		status.add(p);
		
		p = new JPanel();
		p.add(new JLabel("Level:"));
		p.add(my_level);
		status.add(p);
		
		root.add(status, BorderLayout.NORTH);
		this.add(root);
		
	
		
		this.setMinimumSize(new Dimension(256, 256));
		this.setLocationRelativeTo(null);
		if (Toolkit.getDefaultToolkit().isFrameStateSupported(JFrame.MAXIMIZED_BOTH))
			this.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		this.setVisible(true);
		my_canvas.requestFocus(); // TODO whenever you start
	}

	@Override
	public void update(Observable the_observer, Object the_arg) {
		if (the_arg.getClass().equals(Long.class)) {
			my_score.setText(String.valueOf((Long) the_arg));
		} else if (the_arg instanceof Boolean) {
			JOptionPane.showMessageDialog(this, "Game Over");
		}
		
	}
}
