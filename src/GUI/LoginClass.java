package GUI;

import java.awt.BorderLayout;
import java.awt.CardLayout; //imports AWT
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter; //event library
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
//import java.awt.Robot.*; // for testing
import javax.swing.JFrame; //swing
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
//event when you press a key
//imports colors
//imports classes for creating and modifying images
//swing event
// input/output


//main class for the other GUIs that will contain the main frame extended from JFrame
//and implements ActionListener
public class LoginClass extends JFrame implements ActionListener {
	
	//what it's anticipated to look like:
	// ------------------------------------------
	// | 										|
	// |			Game Image					|
	// |			Set String					|
	// | -------------------------------------- |
	// | 			Username					|
	// | 			Password					|
	// |			^fields						|
	// |										|
	// |										|
	// | ---------------------------------------|
	// |										|
	// |				Errors					|
	// | 										|
	// | 										|
	// ------------------------------------------
	
	
	//initialize swing components and variables
	
	//create mainPanel pane for CardLayout that will contain all panels (login, lobby, game)
	private JPanel mainFrame;
	
	//create CardLayout and define windows
	private CardLayout frameLayout;
	//don't need login frame because that will be first frame
	private LobbyClass lobbyFrame;
	private GameClass gameFrame;
	
	//main panel that contains everything within the Lobby with 5 default regions
	private JPanel loginPanel = new JPanel(new BorderLayout());
	
	
	//string corresponding to title of each pane
	private final static String loginTitle = "Login Window";
	private final static String lobbyTitle = "Lobby Window";
	private final static String gameTitle = "Game Window";
	
	//need the following regions
	//private JPanel west = new JPanel();
	//private JPanel east = new JPanel();
	private JPanel north = new JPanel();
	private JPanel south = new JPanel();
	private JPanel center = new JPanel();
	
	//global variable for username
	public String playerUsername;
	
	//Client Object
	private ClientMessenger callObject;
	
	//components of the different panels in login frame textfield(s), label(s), and button(s)
	
	//welcome text
	private JLabel gameWelcome;
	
	//username
	private JLabel usernameLabel;
	private JTextField usernameField;
	private JPanel usernamePanel;
	
	//password
	private JLabel passwordLabel;
	private JPasswordField passwordField;
	private JPanel passwordPanel;
	
	//need to create error labels
	private JLabel errorLabel = new JLabel();
	
	//enter lobby
	private JButton enterLobbyButton;
	private JButton registerButton;
	
	//boolean to check if succesfully logged in
	public boolean isLoggedIn;
	
	//LoginClass method
	public LoginClass(ClientMessenger callObject){
		this.callObject = callObject;
		
		//actually create the mainPanel that will have all the other panels
		//and the panels themselves
		mainFrame = new JPanel(new CardLayout());
		lobbyFrame = new LobbyClass(this);
		gameFrame = new GameClass(this, lobbyFrame);
		frameLayout = (CardLayout)(mainFrame.getLayout());
		//player protocol to grabPanels
		callObject.setPanels(this, lobbyFrame, gameFrame);
		
		//add the three different windows to da mainPanel
		mainFrame.add(loginPanel, loginTitle);
		mainFrame.add(lobbyFrame, lobbyTitle);
		mainFrame.add(gameFrame, gameTitle);
		
		//since at login, only want to show login frame currently
		frameLayout.show(mainFrame, loginTitle);
		
		//intialize login boolean
		isLoggedIn = false;
		
		//call loginGUICreation()
		totalGUICreation();
	}
	
	//will always contain the same appearance/values therefore final might be of good use
	public  void totalGUICreation(){
		
		//create Lobby and Game GUIs as well as Login GUI
		lobbyFrame.lobbyGUICreation();
		gameFrame.gameGUICreation();
		
		//call methods that make each region
		makeNorth();
		makeCenter();
		makeSouth();
		
		loginPanel.add(north, BorderLayout.NORTH);
		loginPanel.add(center, BorderLayout.CENTER);
		loginPanel.add(south, BorderLayout.SOUTH);
		
		//add the mainFrame & set initial size (loginsize)
		add(mainFrame);
		setSize(500,200);
		setLocationRelativeTo(null);
		
		//define close operations & event
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent event){
				//do closeFrames method
				closeFrames();
			}
		});
		
		//setEnterKey to be default to loginButton when in this window
		this.getRootPane().setDefaultButton(enterLobbyButton);	
	}
	
	//make north part
	public void makeNorth(){
		//lobby image
		BufferedImage loginImage;
		Boolean imageImport = true;
		JLabel loginTitleLabel;
		try{
			//hard code since it is expected to come from GitHub
			loginImage = ImageIO.read(new File("src/main/pictures/title_image.png"));
			
		}catch (IOException e){
			imageImport = false;
			loginImage = null;
		}
		if(!imageImport){
			loginTitleLabel = new JLabel("Title Image Not Found");
		}else{
			loginTitleLabel = new JLabel(new ImageIcon(loginImage));
			loginTitleLabel.setAlignmentX(CENTER_ALIGNMENT);
		}
		
		gameWelcome = new JLabel("Welcome to Set Bruh!");
		JPanel gameWelcomePanel = new JPanel();
		gameWelcomePanel.add(gameWelcome);
		gameWelcomePanel.setAlignmentX(CENTER_ALIGNMENT);
		
		north.setLayout(new BoxLayout(north, BoxLayout.Y_AXIS));
		north.add(Box.createRigidArea(new Dimension(0,5)));
		north.add(loginTitleLabel);
		north.add(gameWelcomePanel);
	}
	
	//make center part
	public void makeCenter(){
		
		//username components
		usernameLabel = new JLabel("Username:");
		usernameField = new JTextField(10);
		usernamePanel = new JPanel();
		usernamePanel.setLayout(new BoxLayout(usernamePanel, BoxLayout.X_AXIS));
		usernamePanel.add(usernameLabel);
		usernamePanel.add(usernameField);
		usernamePanel.setAlignmentX(CENTER_ALIGNMENT);
		
		//password components
		passwordLabel = new JLabel("Password:");
		passwordField = new JPasswordField(10);
		passwordPanel = new JPanel();
		passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.X_AXIS));
		passwordPanel.add(passwordLabel);
		passwordPanel.add(passwordField);
		passwordPanel.setAlignmentX(CENTER_ALIGNMENT);
		
		//buttons
		JPanel buttonPanel = new JPanel();
		enterLobbyButton = new JButton("Login");
		enterLobbyButton.addActionListener(this);
        enterLobbyButton.setActionCommand("Login");
        enterLobbyButton.setAlignmentX(CENTER_ALIGNMENT);
		registerButton = new JButton("Register");
		registerButton.addActionListener(this);
        registerButton.setActionCommand("Register");
		registerButton.setAlignmentX(CENTER_ALIGNMENT);
		buttonPanel.add(enterLobbyButton);
		buttonPanel.add(registerButton);
		
		
		
		//username/password/login/register total panel
		JPanel centerComponentPanel = new JPanel();
		centerComponentPanel.setLayout(new BoxLayout(centerComponentPanel, BoxLayout.Y_AXIS));
		centerComponentPanel.add(usernamePanel);
		centerComponentPanel.add(passwordPanel);
		centerComponentPanel.add(buttonPanel);
		centerComponentPanel.setAlignmentX(CENTER_ALIGNMENT);
		
		
		center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
		center.add(Box.createRigidArea(new Dimension(0,5)));
		center.add(centerComponentPanel);
	}
	
	public void makeSouth(){
		errorLabel.setForeground(new Color(0xff8000));
		south.setLayout(new BoxLayout(south, BoxLayout.Y_AXIS));
		
		south.add(Box.createRigidArea(new Dimension(0,10)));
		south.add(errorLabel);
		south.setVisible(false);
	}

	public void actionPerformed(ActionEvent event){
		//send message to server with username & password and
		// then clear the contents of the two fields
		playerUsername = usernameField.getText();
		char[] playerPassword = passwordField.getPassword();
        if(event.getActionCommand().equals("Login")) {
            callObject.messageServer("L~" + playerUsername + "~" + new String(playerPassword));
        }

		//send message to server with username and password registration
		//then clear the contents of the two fields
		else if(event.getActionCommand().equals("Register"))
        {
            callObject.messageServer("R~" + playerUsername + "~" + new String(playerPassword));
        }
        usernameField.setText("");
		passwordField.setText("");
    }
	
	//method for setting what error text is and letting it be visible
	public void displayError(String errorString){
		errorLabel.setText(errorString);
		south.setVisible(true);
	}
	
	//third level -> second level
	public void login(String username){
		isLoggedIn = true;
		playerUsername = username;

        frameLayout = (CardLayout)(mainFrame.getLayout());
		//enters the new user aka saying welcome and setting up the default button
        setSize(1000,1000);
		lobbyFrame.enterNewUser(username, callObject);
		frameLayout.show(mainFrame, lobbyTitle);
		south.setVisible(false);
        setTitle("Lobby?");
	}
	
	//second level -> first level
	public void enterGame() {
	    gameFrame.gameKeySet();
	    setSize(1000,500); // figure out appropriate size
	    //game_Panel.joinGame();
	    gameFrame.setGameClient(callObject, playerUsername);
	    gameFrame.resetEndGame();
	    frameLayout.show(mainFrame, gameTitle);
	    pack();
	  }
	
	//first level -> second level
	//method for exiting game (aka going from Game to Lobby)
	public void exitGame(){
		gameFrame.gamereset();
		lobbyFrame.enterKeySet();
		setSize(1000,500);
		frameLayout.show(mainFrame, lobbyTitle);
	}
	
	//second level -> third level
	//method for logging out (aka going from Lobby to Login)
	//logout changes dimension from Lobby to Login and sets everything back to Login style
	public void logout(){
		isLoggedIn = false;
		//disconnect from server
		callObject.messageServer("D");
		
		//clear lobby frame
		lobbyFrame.clearLobby();
		
		//reset window size and what gets shown in card layout
		setSize(500,200);
		frameLayout.show(mainFrame, loginTitle);
		
		//have to specify what new default button is for enter key
		this.getRootPane().setDefaultButton(enterLobbyButton);
		
	}
	
	//third level -> leaving permanently
	//closing out of entire game
	private void closeFrames(){
		dispose();
		System.exit(0);
	}
}
