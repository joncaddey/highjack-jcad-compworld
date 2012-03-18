package main;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractButton;
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
public class Window extends JFrame implements Observer, ActionListener{
	
	
	private final AsteroidsGame my_game;
	private final AsteroidsCanvas my_canvas;
	private final JTextField my_score;
	private final JTextField my_level;
	private final JButton my_new_game_button;
	private final NewGameDialog my_new_game_dialog;
	private final JTextField my_bombs;
	
	
	public Window(String the_title){
		super(the_title);
		
		//stevens min code
		addComponentListener(new java.awt.event.ComponentAdapter() {
			  public void componentResized(ComponentEvent event) {
				 int width = getWidth();
				 int hight = getHeight();
				 if(width < 600 || hight < 500){
					setSize(600, 500);
				 }else if(width > (hight *2)){
					 setSize(hight, hight);
				 }else if(hight > (width *2)){
					 setSize(width, width);
				 }
		
			  }
			});
		
		
		my_game = new AsteroidsGame(this);
		my_game.addObserver(this);
		my_canvas = AsteroidsCanvas.getInstance(my_game);
		my_score = new JTextField(15);
		my_score.setEnabled(false);
		my_level = new JTextField(5);
		my_level.setEnabled(false);
		
		my_bombs = new JTextField(2);
		my_bombs.setText("0");
		my_bombs.setEnabled(false);
		
		my_new_game_dialog = new NewGameDialog(this);
		my_new_game_button = new JButton("New Game...");
		setup();
		JOptionPane.showMessageDialog(null, "Use arrow keys to move, press space to fire,\nand press down arrow for a rechargeable shield.\nUse B to fire a bomb that will destroy everything.\nStart a game with New Game.", "Welcome to Asteroids!", JOptionPane.INFORMATION_MESSAGE);
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
		my_new_game_button.addActionListener(this);
		status.add(my_new_game_button);
		
		JPanel p;
		
		p = new JPanel();
		p.add(new JLabel("Score:"));
		p.add(my_score);
		status.add(p);
		
		p = new JPanel();
		p.add(new JLabel("Level:"));
		p.add(my_level);
		status.add(p);
		
		p = new JPanel();
		p.add(new JLabel("Bombs:"));
		p.add(my_bombs);
		status.add(p);
		
		root.add(status, BorderLayout.NORTH);
		this.add(root);
		
	
		
		this.setMinimumSize(new Dimension(256, 256));
		this.setLocationRelativeTo(null);
		if (Toolkit.getDefaultToolkit().isFrameStateSupported(JFrame.MAXIMIZED_BOTH))
			this.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		this.setVisible(true);
	}

	@Override
	public void update(Observable the_observer, Object the_arg) {
		if (the_arg.getClass().equals(Long.class)) {
			my_score.setText(String.valueOf((Long) the_arg));
		} else if (the_arg.getClass().equals(Integer.class)) {
			my_level.setText(String.valueOf((Integer) the_arg));
		} else if (the_arg instanceof Boolean) {
			JOptionPane.showMessageDialog(this, "Game Over");
			my_new_game_button.requestFocus();
		}else if(the_arg.getClass().equals(String.class)){
			my_bombs.setText((String) the_arg);
		}
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == my_new_game_button) {
			if (my_new_game_dialog.showDialog()) {
				my_game.setPeer(my_new_game_dialog.getNetworkingPeer());
				my_game.startGame();
				my_canvas.requestFocus();
			}
		}
		
	}
}
