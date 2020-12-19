package gameClient;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import javax.imageio.ImageIO;
import javax.swing.*;
import api.game_service;


/**
 * represent the frame of the Pokemon game
 * @author liadn7
 * @author avielc11
 * 
 */
public class guiFrame extends JFrame implements ActionListener{
	private MenuItem start;
	private MenuItem saveResult;
	private MenuItem log;
	private MenuItem exit;

	private JButton btnNewButton;	//Button start the game

	private boolean flag;
	private int lvl;
	private JTextField txtPressLevel;
	private String score;
	private game_service game;

	/**
	 * constructor for the frame.
	 * @param x - Which frame is it.
	 * @param game -  the server of the game.
	 */
	public guiFrame(int x, game_service game) {
		if(x==0) {
			initframe();
			initpanel();
			this.setVisible(true);
			this.game=game;
			addMenu();
			this.flag = true;
			setTitle("Pokemon game (created by liadn7 and avielc11)");
			while(flag) {
				btnNewButton.addActionListener(this);
			}
		}
		else {
			initframe();
			this.game=game;
			addMenu();
			this.flag = true;			
			this.setVisible(true);

		}

	}

	/**
	 * Determines the default configuration of the frame.
	 */
	public void initframe() {
		int frameWidth = 1000;
		int frameHeight = 600;
		ImageIcon image = new ImageIcon("./img/icon.png"); //create an ImageIcon
		this.setIconImage(image.getImage()); //change icon of frame
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((int) screenSize.getWidth() - frameWidth, 0, frameWidth, frameHeight);
		this.score="null";
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/**
	 * Determines the default configuration of the panel.
	 */
	private void initpanel() {
		this.setLayout(null);
		class ImagePanel extends JComponent {
			private static final long serialVersionUID = 1L;
			private Image image;
			public ImagePanel(Image image) {
				this.image = image;
			}
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(image, 0, 0, this);
			}
		}
		File file = new File("./img/screen.jpg");
		BufferedImage myImage;
		try {
			myImage = ImageIO.read(file);
			this.setContentPane(new ImagePanel(myImage));			
		} catch (IOException e) {
			e.printStackTrace();
		}

		txtPressLevel = new JTextField();
		txtPressLevel.setText("0");
		txtPressLevel.setBounds(66, 79, 82, 71);
		txtPressLevel.setFont(new Font("Tahoma", Font.PLAIN, 30));
		txtPressLevel.setHorizontalAlignment(SwingConstants.CENTER);
		this.add(txtPressLevel);
		txtPressLevel.setColumns(10);

		JLabel lblNewLabel = new JLabel("choose a level");
		lblNewLabel.setForeground(Color.WHITE);
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblNewLabel.setBounds(10, 11, 228, 57);
		this.add(lblNewLabel);

		btnNewButton = new JButton("");
		btnNewButton.setIcon(new ImageIcon("./img/start.png"));
		btnNewButton.setBounds(370, 253, 228, 227);
		this.add(btnNewButton);
	}	

	/**
	 * Add all the menu-bar and the item of the menu.
	 * 
	 */
	private void addMenu() {
		MenuBar menuB = new MenuBar();
		Menu menu = new Menu("options");
		menuB.add(menu);
		this.setMenuBar(menuB);

		this.start = new MenuItem("Start the game");		
		this.saveResult = new MenuItem("Save result");
		this.log = new MenuItem("Upload result to the server");
		this.exit = new MenuItem("Exit");

		menu.add(this.start);
		menu.add(this.saveResult);
		menu.add(this.log);
		menu.add(this.exit);

		this.exit.addActionListener(this);
		this.saveResult.addActionListener(this);
		this.log.addActionListener(this);
		this.start.addActionListener(this);
	}


	/**
	 * Performs an action when a click occurs.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == saveResult) save();
		else if(e.getSource() == btnNewButton)	stringCheck();
		else if(e.getSource() == exit) System.exit(0);
		else if(e.getSource() == log) logIn();
		else if(e.getSource() == start) btnNewButton.doClick();
	}


	/**
	 * Checks if the level is between 0 to 23 --> [0,23]
	 * and give a messages if wrong else start the game.
	 */
	private void stringCheck() {
		String s = this.txtPressLevel.getText();
		if (s.length()>2)
			System.out.println("you give a wrong level");
		else if(s.length()==0) 
			System.out.println("you dont give a level");
		else if(s.length()==1) {
			char c = s.charAt(0);
			if(c>='0' && c<= '9') {
				this.lvl = Integer.parseInt(""+c);
				this.flag = false;
			}	
			else
				System.out.println("you give a wrong level");
		}
		else {
			char c0 = s.charAt(0);
			char c1 = s.charAt(1);		
			if((c0=='1' && c1>='0' && c1<= '9')||((c0== '2' && c1>='0' && c1<= '3' ))) {
				this.lvl = Integer.parseInt(s.substring(0,2));
				this.flag = false;
			}	
			else
				System.out.println("you give a wrong level");
		}
	}

	/**
	 * Uploads the result to the server.
	 */
	private void logIn() {
		if(this.score.equals("null")) System.out.println("The game is not over yet!!!");
		else {
			this.game.login(205485618);
			this.game.login(206192999);
			System.out.println("Upload complete!");
		}
	}

	/**
	 * Save the score on the PC (in the project folder)
	 * and the name of the file is "score.txt"
	 */
	private void save() {
		try {
			PrintWriter pw=new PrintWriter(new File("score.txt"));
			pw.write(this.score);
			pw.close();
		}
		catch(FileNotFoundException e){
			e.printStackTrace();
		}
	}

	/**
	 * Puts the result at the end of the game
	 * @param s - a string represent the score after the game finish
	 */
	public void addScore(String s) {this.score=s;}

	/**
	 * @return The level was chosen.
	 */
	public int getlvl(){return this.lvl;}
}



