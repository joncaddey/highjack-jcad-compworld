package main;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.jogamp.newt.event.KeyEvent;


@SuppressWarnings("serial")
public class NetworkGui extends JDialog implements ActionListener{
	
	public static void main(String args[]) {
		JFrame a  = new JFrame();
		a.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		NetworkGui b = new NetworkGui(a);
		
		
		a.setVisible(true);
		System.out.print(b.showDialog());
		a.add(new JButton("i like this class :D"));
		a.pack();
	}
	
	JPanel player_panel;
	
	JPanel network_panel;

	private boolean my_single_player;

	private JTextField network_textbox;

	private JTextField port_textbox;

	private boolean my_aproved;
	
	public NetworkGui(final Frame the_owner){
		super(the_owner,"Chose game", true);
		setResizable(false);
		
		JPanel main_panel = new JPanel();
		main_panel.setLayout(new BorderLayout());
		main_panel.setName("Network Panel");
		
		my_single_player = true;
		
		//make network panel
		network_panel = makeNetworkPanel();
		unenable(network_panel);
		main_panel.add(network_panel, BorderLayout.SOUTH);	
		
		//make player panel
		player_panel = makePlayerPanel();
		main_panel.add(player_panel, BorderLayout.NORTH);
		
		//add the main panel to the window
		add(main_panel);
		pack();
	}
	
	
	
	
	private JPanel makeNetworkPanel (){
		//make the panel
		JPanel a_JPanel= new JPanel();
		a_JPanel.setBorder(BorderFactory.createTitledBorder("Network"));

		//make the network label
		JLabel network_label = new JLabel();
		network_label.setText("IP addres");
		a_JPanel.add(network_label);

		//make the network text box
		network_textbox = new JTextField(15);
		a_JPanel.add(network_textbox);
		
		//make the port label
		JLabel port_label = new JLabel();
		port_label.setText("Port #");
		a_JPanel.add(port_label);

		//make the port text box
		port_textbox = new JTextField(5);
		a_JPanel.add(port_textbox);
			
		return a_JPanel;
	}
	
	private JPanel makePlayerPanel(){
		JPanel a_panel= new JPanel();
		
		//Group the radio buttons.
	    ButtonGroup group = new ButtonGroup();
		
		//make single player radio button
		String singleText = new String("Single Player");
		JRadioButton single = new JRadioButton(singleText);
		single.setMnemonic(KeyEvent.VK_S);
		single.setActionCommand(singleText);
		single.addActionListener(this);
		single.setSelected(true);
		a_panel.add(single);
		group.add(single);
		
		//make a multiplayer radio button
		String multiText = new String("Multi player");
		JRadioButton multi = new JRadioButton(multiText);
		multi.setMnemonic(KeyEvent.VK_M);
		multi.setActionCommand(multiText);
		multi.addActionListener(this);
		a_panel.add(multi);
		group.add(multi);
		
		//make newgame button
		String startText = new String("Start Game");
		JButton start_game = new JButton(startText);
		start_game.setMnemonic(KeyEvent.VK_G);
		start_game.setActionCommand(startText);
		start_game.addActionListener(this);
		a_panel.add(start_game);
		
		
		return  a_panel;
	}
	
	public void actionPerformed(ActionEvent e) {
		String comand = e.getActionCommand();
		if(comand.equals("Multi player")){
			enable(network_panel);
			my_single_player = false;
		}else if(comand.equals("Single Player")) {
			my_single_player = true;
			unenable(network_panel);
		}else{
			setVisible(false);
			my_aproved = true;
		}
	}
	
	public boolean getSinglePlayer(){
		return my_single_player;
	}
	
	public String getIP(){
		return network_textbox.getText();
	}
	
	public String getPort(){
		return port_textbox.getText();
	}


	private void unenable(JPanel the_panel){
		for(Component a_compComponent : the_panel.getComponents()){
			a_compComponent.setEnabled(false);
		}
	}
	
	private void enable(JPanel the_panel){
		for(Component a_compComponent : the_panel.getComponents()){
			a_compComponent.setEnabled(true);
		}
	}

	public boolean showDialog() {
		my_aproved = false;
		setVisible(true);
		return my_aproved;
	}
}
