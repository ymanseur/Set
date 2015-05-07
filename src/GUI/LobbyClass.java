package GUI;

//importing necessary libraries
import java.awt.*; //imports AWT
import java.awt.event.*; //event library
import java.awt.event.KeyEvent.*; //event when you press a key
import java.awt.Color.*; //imports colors
import java.awt.image.*; //imports classes for creating and modifying images
import java.awt.Robot.*; // for testing
import javax.swing.*; //swing
import javax.swing.event.*; //swing event
import java.io.*; // input/output
import javax.imageio.*; // input/output for image class
import java.util.*;
import java.util.HashMap; //hash table
import javax.swing.ListSelectionModel;// the rest are all specific imports of list action things
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

//to many warnings were showing up, so this helps the program run quicker
@SuppressWarnings("serial")

public class LobbyClass extends JPanel {
	
	//main panel that contains everything within the Lobby with 5 default regions
	private JPanel lobbyPanel = new JPanel(new BorderLayout());
	
	//what it's anticipated to look like:
	// ------------------------------------------------------------------
	// | 			|			Game Image					|			|
	// | 			|			Welcome						|			|
	// | 			|			Errors						|			|
	// | 			|-------------------------------------- |			|
	// | 			|			Errors						|			|
	// | 			|			Create New Game				|			|
	// | active 	|			Game list					| Join Game |
	// | users		|										|			|
	// | 			|										|			|
	// | 			|---------------------------------------|			|
	// | 			|										|			|
	// | 			|				LobbyChat				|			|
	// | 			|										|			|
	// | 			|										|			|
	// ------------------------------------------------------------------
	
	//need the following regions
	private JPanel west = new JPanel();
	private JPanel east = new JPanel();
	private JPanel north = new JPanel();
	private JPanel south = new JPanel();
	private JPanel center = new JPanel();
	
	//need to create error labels
	private JLabel errorLabel = new JLabel();
	
	//apology strings
	private String roomInProgress = "Sorry, there is a game in progress";
	private String fullRoom = "Sorry, game room is full.";
	
	//subcomponents of main panel
	
	//reference to the login class
	private LoginClass loginFrame;
	
	//initialize username
	private String username;
	
	//initialize welcome message
	private JLabel welcome;
	
	//logout option
	private JButton logout;
	
	//Client object
	private ClientMessenger callObject;
	
	//components of lobby chat section
	private JLabel chatTitle; 
	private String chatRoomTitle = "Lobby Chat Input:"; //title of chat section
	private JTextField messageInput; //where users input messages
	private JTextArea chatHistory; //where previous messages are displayed
	private JScrollPane chatScroll; //allows user to scroll through history
	private JButton sendMessage; //button for sending message
	
	//components of the section of the lobby that lists games and users & their models
	private JScrollPane userScroll;
	private JList<String> listOfUsers;
	private JList<String> listOfGames;
	public DefaultListModel<String> activeUsers;
	public DefaultListModel<String> activeGames;
	
	//components that will deal with either an area for joining games, or section of main panel that will have a button
	//private JPopupMenu optionMenu;
	//private JMenuItem joinGameItem;
	private JButton joinGame;
	private boolean joinGameEnabled = false;
	//joinGame.setEnabled(false);
	
	//case where no games are active
	private String noActiveGames = "No active games available.";
	
	//hash table that stores data from the game rooms
	private HashMap<Integer,roomData> listOfGameRooms = new HashMap<Integer, roomData>();
	
	//component that is separate popup menu for creating a game
	private JPopupMenu createRoom;
	
	//components that deal with room name
	private JPanel roomNamePanel; //panel
	private JLabel nameOfRoom; //name of the game user wants to create
	private final JTextField nameOfRoomField = new JTextField(12); //can't keep renaming
	
	//components that deal with number of players
	private JPanel numberOfPlayersPanel; //panel
	private JLabel numberOfPlayers; //maximum number of players the user defines (max 4 min 1)
	private final JTextField numberOfPlayersField = new JTextField(1); //can't keep changing max players
	
	private JButton createGameButton = new JButton("Create");
	private JButton cancelCreateButton = new JButton("Cancel");
	private boolean cancelCreate = false;
	
	//label to display if there is an error, which will appear in a dialog box
	private JLabel gameCreationError = new JLabel("");  
	
	//components to be used in a separate dialog window for error handling
	final JComponent[] newGameComponents = new JComponent[]{
			gameCreationError,
			new JLabel("Enter your name of game:"),
			nameOfRoomField,
			new JLabel("Enter your \"x\" number of players (1<= x <=4)"),
			numberOfPlayersField,
			createGameButton,
			cancelCreateButton
	};
	
	//LobbyClass method
	public LobbyClass(LoginClass loginFrame){
		this.loginFrame = loginFrame;
	}
	
	//Allows users to enter lobby with their usernames
	//adds user's username to the server's list of active users
	public void enterNewUser (String username, ClientMessenger callObject){
		this.username = username;
		this.callObject = callObject;
		//sets welcome label text
		welcome.setText(username + "has entered the lobby");
		enterKeySet();
	}
	
	//UI-defined button "sendMessage" will be the defined event that will occur when
	//the user presses the "Enter" key, whether or not the button has keyboard focus
	public void enterKeySet(){
		this.getRootPane().setDefaultButton(sendMessage);
	}
	
	//Finally actually creating and displaying the GUI
	
	public final void lobbyGUICreation(){
		//calls to methods for each region that define how each region will look
		makeWest();
		makeNorth();
		makeSouth();
		makeCenter();
		makeEast();
		
		//setting each region to a section of the border layout
		lobbyPanel.add(west, BorderLayout.WEST);
		lobbyPanel.add(east, BorderLayout.EAST);
		lobbyPanel.add(north, BorderLayout.NORTH);
		lobbyPanel.add(south, BorderLayout.SOUTH);
		lobbyPanel.add(center, BorderLayout.CENTER);
		
		// join game popup menu
//	    joinMenu = new JPopupMenu();
//	    menuJoin = new JMenuItem("Join Game");
//	    joinMenu.add(menuJoin);
//	    menuJoin.addActionListener(new JoinGameListener());
//	    add(panel);
		
		//displays the create game menu popup
		createRoom = new JPopupMenu();
		//vertical layout
		createRoom.setLayout(new BoxLayout(createRoom, BoxLayout.Y_AXIS));
		
		//properties of each label
		//room
		nameOfRoom = new JLabel("Enter Room Name: ");
		roomNamePanel = new JPanel();
		roomNamePanel.setLayout(new BoxLayout(roomNamePanel, BoxLayout.X_AXIS));
		//players
		numberOfPlayers = new JLabel("Enter Number of Players (4 max):");
		numberOfPlayersPanel = new JPanel();
		numberOfPlayersPanel.setLayout(new BoxLayout(roomNamePanel, BoxLayout.X_AXIS));
	}
		public void makeWest(){
			//user list style and display
			activeUsers = new DefaultListModel<String>();
			listOfUsers = new JList<String>(activeUsers);
			listOfUsers.setPreferredSize(new Dimension(50,100));
			
			//scroll
			userScroll = new JScrollPane(listOfUsers);
			userScroll.setAlignmentX(LEFT_ALIGNMENT);
			
			//title of list of users region
			JPanel userListTitlePanel = new JPanel();
			JLabel userListTitle = new JLabel("Current Users:");
			userListTitlePanel.add(userListTitle);
			userListTitlePanel.add(userScroll);
			userListTitlePanel.setLayout(new BoxLayout(userListTitlePanel, BoxLayout.Y_AXIS));
			
			west.setLayout(new BoxLayout(west, BoxLayout.Y_AXIS));
			west.add(userListTitlePanel);
			west.add(Box.createRigidArea(new Dimension(40,0)));
		}
		
		public void makeNorth(){
			//lobby image
			BufferedImage titleImage;
			Boolean imageImport = true;
			JLabel titleLabel;
			try{
				//hard code since it is expected to come from GitHub
				titleImage = ImageIO.read(new File("src/main/pictures/title_image.png"));
				
			}catch (IOException e){
				imageImport = false;
				titleImage = null;
			}
			if(!imageImport){
				titleLabel = new JLabel("Title Image Not Found");
			}else{
				titleLabel = new JLabel(new ImageIcon(titleImage));
				titleLabel.setAlignmentX(CENTER_ALIGNMENT);
			}
			welcome = new JLabel();
			welcome.setAlignmentX(LEFT_ALIGNMENT);
			
			logout = new JButton("logout");
			logout.setAlignmentX(RIGHT_ALIGNMENT);
			//event listener for logout button being pressed which calls the logout method
			logout.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent event){
					loginFrame.logout();
				}
			});
			//create header panel and add everything into it
			JPanel headerPanel = new JPanel();
			headerPanel.add(welcome);
			headerPanel.add(Box.createRigidArea(new Dimension(400,0)));
			headerPanel.add(logout);
			//headerPanel.setVisible(true);
			
			//now it adds all the components to the north panel
			north.setLayout(new BoxLayout(north, BoxLayout.Y_AXIS));
			north.add(titleLabel);
			north.add(headerPanel);
			north.add(errorLabel);
			headerPanel.setVisible(true);
		}
		
		public void makeSouth(){
			south.setLayout(new BoxLayout(south, BoxLayout.X_AXIS));
			
			//title section of chat
			chatTitle = new JLabel(chatRoomTitle);
			chatTitle.setAlignmentX(CENTER_ALIGNMENT);
			
			//chat history section
			chatHistory = new JTextArea("",40,10);
			chatScroll = new JScrollPane(chatHistory);
			
			//don't want users editing log and want there to be line wrap
			chatHistory.setLineWrap(true);
			chatHistory.setEditable(false);
			
			//where user an input messages and no button necessary to enter listener
			messageInput = new JTextField(20);
			
			//the send message button that technically can't be seen
			sendMessage = new JButton("Send");
			sendMessage.addActionListener(new SendButtonListener());
			sendMessage.setVisible(false);
			
			//add everything to the south panel
			south.add(chatTitle);
			south.add(chatScroll);
			south.add(messageInput);
			south.add(sendMessage);
		}
		
		public void makeCenter(){
			//button for creating a new game
			JButton newGameButton = new JButton("New Game");
			newGameButton.addActionListener(new CreateGameListener());
			
			//game list style and display
			activeGames = new DefaultListModel<String>();
			//activeGames.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			listOfGames = new JList<String>(activeGames);
			
			//scroll
			JScrollPane listOfGamesScroll = new JScrollPane(listOfGames);
			listOfGamesScroll.setAlignmentX(CENTER_ALIGNMENT);
			
			//listener for mouse joining games
			//listOfGames.addMouseListener(new JoinListener());
			listOfGames.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			listOfGames.addListSelectionListener(new ListItemSelected());
			listOfGames.setVisibleRowCount(15);
			listOfGames.setPreferredSize(new Dimension(250,300));
			
			//changes error text to orange in case of display
			errorLabel.setForeground(new Color(0xff8000));
			errorLabel.setVisible(false);
			
			//create a panel to put every component in which will go into the center panel
			JPanel centerCompPanel = new JPanel();
			centerCompPanel.setLayout(new BoxLayout(centerCompPanel, BoxLayout.Y_AXIS));
			centerCompPanel.add(errorLabel);
			centerCompPanel.add(newGameButton);
			centerCompPanel.add(Box.createRigidArea(new Dimension(0,10)));
			centerCompPanel.add(new JLabel("Active Rooms"));
			centerCompPanel.add(Box.createRigidArea(new Dimension(0,5)));
			centerCompPanel.add(listOfGamesScroll);
			
			//add everything to the center panel
			center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
			center.add(centerCompPanel);
			center.add(Box.createRigidArea(new Dimension(40,0)));
			
			//event listener for cancel create button
			cancelCreateButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent evt){
					cancelCreate = true;
				}
			});
			//sets the error color to orange if there is a game creation error
			gameCreationError.setForeground(new Color(0xff8000));
		}
		
		public void makeEast(){
			//displays join game button and adds listener
			joinGame = new JButton("Join");
			joinGame.setEnabled(joinGameEnabled);
			joinGame.setAlignmentX(CENTER_ALIGNMENT);
			joinGame.addActionListener(new JoinGameAction());
			
			//adds button to panel and sets it visible
			JPanel joinGameButton = new JPanel();
			joinGameButton.add(joinGame);
			joinGameButton.add(Box.createRigidArea(new Dimension(40,0)));
			joinGameButton.setVisible(true);
			
			east.setLayout(new BoxLayout(east, BoxLayout.Y_AXIS));
			east.add(joinGameButton);
			east.add(Box.createRigidArea(new Dimension(40,0)));
			
		}
		
		//listener methods:
		//list selection listener
		public class ListItemSelected implements ListSelectionListener{
			public void valueChanged(ListSelectionEvent event){
				if(event.getValueIsAdjusting() == false){
					if(listOfGames.getSelectedIndex() == -1){
						//no game has been selected therefore disable join game button
						joinGameEnabled = false;
					}else{
						//allow join game button to be enabled
						joinGameEnabled = true;
					}
				}
			}
		}
		
		//join game listener
		public class JoinGameAction implements ActionListener{
			public void actionPerformed(ActionEvent event){
				errorLabel.setVisible(false);
				//roomID
				int roomID;
				//get info that describes a game
				String activeGameInfo = listOfGames.getSelectedValue();
				//need to parse the activeGameInfo to get roomID
				String [] gameInfoSplit = activeGameInfo.split("");
				//since our roomInfo string is split up as: "roomID: " we need to parse the roomID
				//even more and take out the colon
				roomID = Integer.parseInt(gameInfoSplit[0].substring(0, gameInfoSplit[0].length()-1));
				
				//send info to server that user wants to join game with specified roomID
				callObject.messageServer("J~"+roomID);
				}
		}
		
		//create new game listener
		public class CreateGameListener implements ActionListener {
			public void actionPerformed(ActionEvent event){
				errorLabel.setVisible(false);
				int numberOfPlayers;
				boolean retry = false;
				cancelCreate = false;
				gameCreationError.setText("");
				do{
					JOptionPane.showMessageDialog(loginFrame, newGameComponents, "Test Dialog", JOptionPane.PLAIN_MESSAGE);
					gameCreationError.setText("");
					//checking proper input for number of players
					try{
						numberOfPlayers = Integer.parseInt(numberOfPlayersField.getText());
						if (numberOfPlayers > 4 || numberOfPlayers < 1){
							retry = true;
						}
						if (retry) {
							gameCreationError.setText("Please have only 1-4 players per room.");
						}	
					}
					catch(NumberFormatException e){
						gameCreationError.setText("You need to enter a valid number from 1-4");
						retry = true;
					}
					//checking proper input for game room name
					if(nameOfRoomField.getText().contains("~")){
						gameCreationError.setText("You can't use the tilde character.");
						retry = true;
					}
					if(nameOfRoomField.getText().equals("")){
						gameCreationError.setText("You can't have a blank name for a room.");
						retry = true;
					}
					//debugging
					//System.out.println("cancel create = " + cancelCreate);
					if(cancelCreate){
						numberOfPlayersField.setText("");
						nameOfRoomField.setText("");
						break;
					}
				} while(retry);
				//if there are no errors and the player doesn't want to cancel game creation, then send server info to create a new game
				if(!cancelCreate){
					callObject.messageServer("N~"+nameOfRoomField.getText()+"~"+numberOfPlayersField.getText());
					loginFrame.enterGame();
				}
				//clear text fields
				numberOfPlayersField.setText("");
				nameOfRoomField.setText("");
			}
		}
		//list item has been clicked
//		private class JoinListener extends MouseAdapter {
//			public void mouseClicked(MouseEvent event){
//				try{
//					
//				}
//			}
//		}
		
		private class SendButtonListener implements ActionListener {
			public void actionPerformed(ActionEvent event){
				errorLabel.setVisible(false);
				if(messageInput.isFocusOwner()){
					String userMessage = messageInput.getText();
					if(!userMessage.equals("")){
						messageInput.setText("");
						callObject.messageServer("C~"+userMessage);
					}
				}
				else {}; //chillout bro
			}
		}
		
		private class roomData{
			//intializes variables
			String nameOfRoom;
			String roomInfo;
			int activePlayers;
			int maxPlayers;
			boolean ongoingGame;
			boolean capped;
			
			public roomData(String nameOfRoom, int activePlayers, int maxPlayers, boolean ongoing){
				this.nameOfRoom = nameOfRoom;
				this.activePlayers = activePlayers;
				this.maxPlayers = maxPlayers;
				this.ongoingGame = ongoing;
				
				if(maxPlayers == 1){
					this.capped = true;
				}else{
					this.capped = false;
				}
				
			}
			
			//updating hash table
			public void updateActiveGames(int roomID){
				try{
					int index = activeGames.indexOf(this.getHashString());
					this.setHashString(roomID);
					String tempString = this.getHashString();
					activeGames.set(index, tempString);
				}
				catch(NullPointerException ep){
					System.err.println("Error updating the active games list");
				}
			}
			
			//makes the hash data string based on status of room
			public void setHashString(int roomID){
				String statusOfGame;
				if(ongoingGame){
					statusOfGame = "Playing";
				}else{
					statusOfGame = "Open";
				}
				if(statusOfGame.equals("Open") && capped){
					statusOfGame = "Full";
				}
				this.roomInfo = roomID + ": " + nameOfRoom + " " + activePlayers + "/" + maxPlayers + " " + statusOfGame;
			}
			
			//returns info string
			public String getHashString(){
				return roomInfo;
			}
			
			//returns whether or not room is full
			public boolean testCapped(){
				if(activePlayers == maxPlayers){
					capped = true;
				}else{
					capped = false;
				}
				return capped;
			}
			
			//changes
			public void gameStart(){
				ongoingGame = true;
			}
			
			public void inactiveGame(){
				ongoingGame = false;
			}
			
			public void setActive(){
				ongoingGame = true;
			}
			
			public void addPlayer(){
				if(!capped){
					activePlayers += 1;
				}
			}
			public void removePlayer(){
				if(activePlayers > 0){
					activePlayers -= 1;
				}
			}
			
		}
		
		//Creating a new game room data
		public void createNewGame(int roomID, String nameOfRoom, int activePlayers, int maxPlayers, boolean ongoingGame){
			roomData newGame = new roomData(nameOfRoom, activePlayers, maxPlayers, ongoingGame);
			listOfGameRooms.put(roomID, newGame);
			newGame.setHashString(roomID);
			String gameInfo = newGame.getHashString();
			if(!activeGames.contains(gameInfo)){
				activeGames.addElement(gameInfo);
			}
		}
		
		//Removing an awesome game room
		public void removeGame(int roomID){
			try{
				roomData removedRoom = listOfGameRooms.get(roomID);
				String roomInfo = removedRoom.getHashString();
				activeGames.removeElement(roomInfo);
				listOfGameRooms.remove(roomID);
			}
			catch(NullPointerException ep){
				;
			}
		}
		
		//set room as being inactive
		public void setIncactiveGame(int roomID){
			roomData gameRoom = listOfGameRooms.get(roomID);
				gameRoom.inactiveGame();
				gameRoom.updateActiveGames(roomID);
		}
		
		//set room as being active
		public void setActive(int roomID){
			roomData gameRoom = listOfGameRooms.get(roomID);
			gameRoom.setActive();
			gameRoom.updateActiveGames(roomID);
		}
		
		//add players
		public void addPlayers(int roomID){
			roomData gameRoom = listOfGameRooms.get(roomID);
			gameRoom.addPlayer();
			gameRoom.testCapped();
			gameRoom.updateActiveGames(roomID);
		}
		
		//decrease players
		public void decreasePlayers(int roomID){
			roomData gameRoom = listOfGameRooms.get(roomID);
			try{
				gameRoom.removePlayer();
				gameRoom.testCapped();
				gameRoom.updateActiveGames(roomID);
			}
			catch(NullPointerException ep){
				System.err.println("Couldn't Decrease Players");
			}
		}
		
		//updates and displays messages sent to server from chat
		public void updateChat(String username, String message){
			if(username.equals("")){
				chatHistory.append(message + "\n");
			}else{
				chatHistory.append(username + ": " + message + "\n");
			}
			JScrollBar vertical = chatScroll.getVerticalScrollBar();
			vertical.setValue(vertical.getMaximum());
		}
		
		//updates active player list based on if player is added or removed
		public void updateActiveUsers(String mode, String username){
			//add user
			if(mode.equals("A")){
				if(!activeUsers.contains(username)){
					activeUsers.addElement(username);
				}
				//remove user
			}else if(mode.equals("R")){
				activeUsers.removeElement(username);
			}else{
				//update was unnecessary
				System.err.println("Update was not necessary.");
			}
		}
		
		//clear everything
		public void clearLobby(){
			chatHistory.setText("");
			activeUsers.clear();
			activeGames.clear();
			listOfGameRooms.clear();
		}
		
		//adjust errorText based on attempts to join game through server
		public void attemptToJoin(char mode){
			if(mode == 'J'){
				loginFrame.enterGame();
				errorLabel.setVisible(false);
			}else if(mode == 'I'){
				errorLabel.setText(roomInProgress);
				errorLabel.setVisible(true);
			}else if(mode == 'F'){
				errorLabel.setText(fullRoom);
				errorLabel.setVisible(true);
			}else{
				//no other mode possible
				System.err.println("There is no other possible mode");
			}
		}
	}
