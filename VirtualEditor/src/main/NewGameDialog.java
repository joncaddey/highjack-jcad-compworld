package main;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.peer.ComponentPeer;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import network.Peer;

import com.jogamp.newt.event.KeyEvent;


@SuppressWarnings("serial")
public class NewGameDialog extends JDialog implements ActionListener{
	
	public static void main(String args[]) {
//		JFrame a  = new JFrame();
//		a.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		
//		
//		NewGameDialog b = new NewGameDialog(a);
//		
//		
//		a.setVisible(true);
//		System.out.print(b.showDialog());
//		a.add(new JButton("i like this class :D"));
//		a.pack();
		
		
//		JFrame a  = new JFrame();
//		a.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		NewGameDialog b = new NewGameDialog(a);
//		b.getLocalIP();
	}
	
	private Peer my_peer;
	private long my_id;
	private int my_port;
	private String my_ip;
	private JPanel player_panel;
	private JPanel network_panel;
	
	
	private String singleText, createText, joinText, startText;

	private boolean my_single_player;
	
	private boolean my_create_network;

	private JTextField network_textbox;

	private JTextField port_textbox;
	
	private JTextField id_textbox;
	private String my_default_port = "5507";
	

	private boolean my_approved;
	
	public NewGameDialog(final Frame the_owner){
		super(the_owner,"Choose game", true);
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
		setLocationRelativeTo(the_owner);
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
		port_textbox = new JTextField(my_default_port, 5);
		a_JPanel.add(port_textbox);
		
		//make the ID label
		a_JPanel.add(new JLabel("ID:"));

		//make the ID text box
		id_textbox = new JTextField(5);
		a_JPanel.add(id_textbox);
		
		return a_JPanel;
	}
	
	private JPanel makePlayerPanel(){
		JPanel a_panel= new JPanel();
		
		//Group the radio buttons.
	    ButtonGroup group = new ButtonGroup();
		
		//make single player radio button
		singleText = new String("Single Player");
		JRadioButton single = new JRadioButton(singleText);
		single.setMnemonic(KeyEvent.VK_S);
		single.setActionCommand(singleText);
		single.addActionListener(this);
		single.setSelected(true);
		a_panel.add(single);
		group.add(single);
		
		//make a create network radio button
		createText = new String("Create Network");
		JRadioButton create = new JRadioButton(createText);
		create.setMnemonic(KeyEvent.VK_C);
		create.setActionCommand(createText);
		create.addActionListener(this);
		a_panel.add(create);
		group.add(create);
		
		//make a join game radio button
		joinText = new String("Join Network");
		JRadioButton join = new JRadioButton(joinText);
		join.setMnemonic(KeyEvent.VK_J);
		join.setActionCommand(joinText);
		join.addActionListener(this);
		a_panel.add(join);
		group.add(join);
		
		//make newgame button
		startText = new String("Start Game");
		JButton start_game = new JButton(startText);
		start_game.setMnemonic(KeyEvent.VK_G);
		start_game.setActionCommand(startText);
		start_game.addActionListener(this);
		a_panel.add(start_game);
		
		
		return  a_panel;
	}
	
	public void actionPerformed(ActionEvent e) {
		
		String command = e.getActionCommand();
		if(command.equals(joinText) || command.equals(createText)){
			enable(network_panel);
			network_textbox.setEnabled(command.equals(joinText));
			my_create_network = command.equals(createText);
			my_single_player = false;
		}else if(command.equals(singleText)) {
			my_single_player = true;
			unenable(network_panel);
		}else if (command.equals(startText)){
			my_approved = validateFields();
			if (!my_approved) return;
			if (my_single_player) {
				my_approved = true;
			} else {
				my_peer = new Peer();
				if (my_create_network) {
					if (id_textbox.getText().length() == 0) {
						my_peer.createNetwork();
					} else {
						my_approved = my_peer.createNetwork(my_id);
						if (!my_approved) return;
					}
				} else {
					try {
						if (id_textbox.getText().length() == 0) {
							my_approved = my_peer.connectToNetwork(my_ip, my_port);
						} else {
							my_approved = my_peer.connectToNetwork(my_ip, my_port, my_id);
						}
					} catch (Exception the_e) {
						JOptionPane.showMessageDialog(null, the_e.toString());
						my_approved = false;
					}
					if (!my_approved) {
						return;
					}
				}
			}
			my_approved = true;
			setVisible(false);
		}
	}
	
	private boolean validateFields() {
		boolean good = true;
		if (!id_textbox.getText().equals("")) {
			try {
				my_id = Long.parseLong(id_textbox.getText());
			} catch (NumberFormatException e) {
				id_textbox.setText("");
				good = false;
			}
		}
		
		try {
			my_port = Integer.parseInt(port_textbox.getText());
		} catch (NumberFormatException e) {
			port_textbox.setText(my_default_port);
			good = false;
		}

		my_ip = network_textbox.getText();
		return good;
	}
	
	public boolean getSinglePlayer(){
		return my_single_player;
	}
	
	public String getIP(){
		return my_ip;
	}
	
	public int getPort(){
		return my_port;
	}
	
	public long getID() {
		return my_id;
	}
	
	public Peer getNetworkingPeer() {
		if (my_single_player) {
			return null;
		} else {
			return my_peer;
		}
	}


	private void unenable(JPanel the_panel){
		for(Component a_compComponent : the_panel.getComponents()){
			a_compComponent.setEnabled(false);
			pack();
		}
	}
	
	private void enable(JPanel the_panel){
		for(Component a_compComponent : the_panel.getComponents()){
			a_compComponent.setEnabled(true);
			pack();
		}
	}
	
	/**
	 * @author Steven Cozart and Solairis(www.solairis.com)
	 * @return Your default ip or null if none found
	 */
	private String getLocalIP(){
		String myHost = null;
		try {

			InetAddress addr = InetAddress.getLocalHost();
			

			String myIP = addr.getHostAddress();

			// Bonus. Get your hostname.
			myHost = addr.getHostName();

			System.out.println(myIP + " " + myHost);

		} catch (UnknownHostException e) {
			System.out.println("Unknown Host: "+e);
		}
		return myHost;
	}

	public boolean showDialog() {
		my_approved = false;
		setVisible(true);
		return my_approved;
	}
	
}
