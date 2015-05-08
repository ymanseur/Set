package GUI;

import Backend.Card;
import javax.swing.*;

import java.net.*;
import java.awt.*; //imports AWT
import java.awt.event.*; //event library
import java.awt.event.KeyEvent.*; //event when you press a key
import java.awt.Color.*; //imports colors
import java.awt.image.*; //imports classes for creating and modifying images
import java.awt.Robot.*; // accepts user input such as mouse and keyboard clicks

import javax.swing.event.*; //swing event

import java.io.*; // input/output

import javax.imageio.*; // input/output for image class

import java.util.*;

import javax.swing.text.*;
import javax.swing.ListSelectionModel;// the rest are all specific imports of list action things
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

@SuppressWarnings("Serial")

public class GameClass extends JPanel{
    //the format will resemble that of the lobbyClass but of course will have different
//objects within each panel
    private JPanel gamePanel = new JPanel(new BorderLayout());
    private JPanel cardPane;
    private JPanel west = new JPanel();
    private JPanel east = new JPanel();
    private JPanel north = new JPanel();
    private JPanel south = new JPanel();
    private JPanel center = new JPanel();
    private JPanel readygamebutton;
    private JPanel submitbutton;

    //components for game room chat
    private JLabel chatTitle;
    private String chatroomtitle = "InGameChatBoard";
    private JTextField messageInput;
    private JTextArea chatHistory;
    private JScrollPane chatScroll;
    private JButton sendMessage;
    private JButton readyButton;

    //List of users
    private JScrollPane userScroll;
    private JList<String> listofUsers;
    public DefaultListModel<String> ingameUsers;

    //Other game components
    private JLabel welcome;
    private JLabel errorLabel = new JLabel();
    private String username;
    private JButton exitCurrentGame;
    private JButton setcardSubmitButton;
    private ClientMessenger callObject;
    boolean playStatus = false; //Consider by default the game to be off | false = game off
    boolean GameEnd; //Set boolean variable to consider when the game is over
    boolean submit_True;
    private LobbyClass lobbyFrame;
    private LoginClass loginFrame;

    ArrayList<String> cardSelection = new ArrayList<String>();
    HashMap<JToggleButton, String> cards = new HashMap<JToggleButton, String>();
    //Want to use Deque so we can select and de-select in any order
    Deque<JToggleButton> selectedCards = new ArrayDeque<>();
    int[] userScores = new int[4];
    String[] userNames = new String[4];
    static Object[][] gameScoreboard = new Object[4][2];
    static JTable gameScoretable;

    public GameClass(LoginClass loginFrame, LobbyClass lobbyFrame)
    {
        this.lobbyFrame = lobbyFrame;
        this.loginFrame = loginFrame;
    }

    //Call ref for game
    public void setGameClient(ClientMessenger callObject, String username)
    {
        this.callObject = callObject;
        this.username = username;
    }

    public void gameKeySet(){
        this.getRootPane().setDefaultButton(setcardSubmitButton);
    }


    //The Game GUI
    public final void gameGUICreation()
    {
        makeWest();
        makeNorth();
        makeSouth();
        makeCenter();
        makeEast();

        gamePanel.add(west, BorderLayout.WEST);
        gamePanel.add(east, BorderLayout.EAST);
        gamePanel.add(north, BorderLayout.NORTH);
        gamePanel.add(south, BorderLayout.SOUTH);
        gamePanel.add(center, BorderLayout.CENTER);
        add(gamePanel);
    }
    //GUI components
    //Scoreboard with list of users
    public void makeWest()
    {
        String[] titles = {"Players", "Scores"};
        for(int i=0;i<4;i++)
        {
            for(int j=0;j<2;j++)
            {
                gameScoreboard[i][j] = "";
            }
        }
        gameScoretable = new JTable(gameScoreboard,titles);
        west.setLayout(new BoxLayout(west,BoxLayout.Y_AXIS));
        west.add(gameScoretable.getTableHeader(), BorderLayout.CENTER);
        west.add(gameScoretable, BorderLayout.CENTER);
    }
    //Title Page
    public void makeNorth()
    {
        BufferedImage titleImage;
        Boolean imageImport = true;
        JLabel titleLabel;
        try{
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
        welcome.setAlignmentX(CENTER_ALIGNMENT);
        welcome.setText("Welcome!");

        exitCurrentGame = new JButton("Exit");
        exitCurrentGame.setAlignmentX(RIGHT_ALIGNMENT);

        exitCurrentGame.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent event)
            {
                callObject.messageServer("E");
            }
        });
        JPanel headerPanel = new JPanel();
        headerPanel.add(welcome);
        headerPanel.add(Box.createRigidArea(new Dimension(400,0)));
        headerPanel.add(exitCurrentGame);
        north.setLayout(new BoxLayout(north,BoxLayout.Y_AXIS));
        north.add(titleLabel);
        north.add(headerPanel);
        north.add(errorLabel);
        headerPanel.setVisible(true);
    }
    //Submit options in to the right
    public void makeEast()
    {
        setcardSubmitButton = new JButton("Submit!");
        setcardSubmitButton.setAlignmentX(CENTER_ALIGNMENT);
        setcardSubmitButton.addActionListener(new cardsubmit());
        readyButton = new JButton("Ready");
        readyButton.setAlignmentX(CENTER_ALIGNMENT);
        readyButton.addActionListener(new readystart());

        submitbutton = new JPanel();
        readygamebutton = new JPanel();
        submitbutton.add(setcardSubmitButton);
        submitbutton.add(Box.createRigidArea(new Dimension(40,0)));
        submitbutton.setVisible(true);
        readygamebutton.add(readyButton);
        readygamebutton.add(Box.createRigidArea(new Dimension(40,0)));
        readygamebutton.setVisible(true);

        east.setLayout(new BoxLayout(east, BoxLayout.Y_AXIS));
        east.add(submitbutton);
        east.add(readygamebutton);
        east.add(Box.createRigidArea(new Dimension(40,0)));
    }
    //Chat box in the south end
    public void makeSouth()
    {
        south.setLayout(new BoxLayout(south,BoxLayout.X_AXIS));
        chatTitle = new JLabel(chatroomtitle);
        chatTitle.setAlignmentX(CENTER_ALIGNMENT);
        chatHistory = new JTextArea("",40,10);
        chatScroll = new JScrollPane(chatHistory);
        chatHistory.setLineWrap(true);
        chatHistory.setEditable(false);
        messageInput = new JTextField(20);
        sendMessage = new JButton("Send");
        sendMessage.addActionListener(new SendButtonListener());
        sendMessage.setVisible(false);
        south.add(chatTitle);
        south.add(chatScroll);
        south.add(messageInput);
        south.add(sendMessage);

    }
    //Game itself in the center
    public void makeCenter()
    {
        cardPane = new JPanel(new FlowLayout());
        cardPane.setBackground(new Color(0,128,0));
        cardPane.setPreferredSize(new Dimension(400,400));
        cardPane.removeAll();
        center.setLayout(new BoxLayout(center, BoxLayout.X_AXIS));
        center.add(cardPane, BorderLayout.NORTH);
        center.add(Box.createRigidArea(new Dimension(40,0)));
    }

    private class readystart implements ActionListener
    {
        public void actionPerformed(ActionEvent event)
        {
            errorLabel.setVisible(false);
            if(playStatus)
            {
                readyButton.setEnabled(submit_True);

            }
            else
            {
                readygamebutton.setVisible(false);
                callObject.messageServer("G");
            }
        }
    }

    //submit a set of cards selected
    private class cardsubmit implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            errorLabel.setVisible(false);
            if (playStatus) {
                submitbutton.setEnabled(submit_True);
                if (cardSelection.size() != 3 || (messageInput.isFocusOwner())) {
                    System.out.println("Invalid submission");
                } else {
                    String setSub = "S~";
                    for (int i = 0; i < 3; i++) {
                        if (i != 2) {
                            setSub = setSub + cardSelection.get(i) + "~";
                        } else {
                            setSub = setSub + cardSelection.get(i);
                        }
                    }
                    callObject.messageServer(setSub);
                    cardSelection.clear();
                }
            }
        }
    }

    //Exit game button action
    public class ExitGame implements ActionListener
    {
        public void actionPerformed(ActionEvent ex)
        {
            errorLabel.setVisible(false);
            callObject.messageServer("E");
        }
    }

    //game class selector
    public class gameCardSelector implements ItemListener
    {
        public void itemStateChanged(ItemEvent i)
        {
            errorLabel.setVisible(false);
            JToggleButton selectedCard = (JToggleButton) i.getSource();
            if(i.getStateChange()==ItemEvent.SELECTED)
            {
                cardSelection.add(cards.get(selectedCard));
                selectedCards.offer(selectedCard);
                if(selectedCards.size()==4)
                {
                    JToggleButton removedCard = selectedCards.removeFirst();
                    removedCard.doClick();
                }
                System.out.println("Selected card " + cards.get(selectedCard));
            }
            else
            {
                cardSelection.remove(cards.get(selectedCard));
                selectedCards.remove(selectedCard);
                System.out.println("Un-selected card " + cards.get(selectedCard));
            }
        }
    }

    //send button listener for the chat box
    private class SendButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent event){
            errorLabel.setVisible(false);
            if(messageInput.isFocusOwner()){
                String userMessage = messageInput.getText();
                if(!userMessage.equals("")){
                    messageInput.setText("");
                    callObject.messageServer("T~" + userMessage);
                }
            }
            else {};
        }
    }

    //Reset function when game is over
    public void resetEndGame()
    {
        GameEnd = false;
    }

    //Resets the game components when users leave
    //Clears the cards from the field and resets everything
    public void gamereset()
    {
        cards.clear();
        cardPane.removeAll();
        playStatus = false;
        chatHistory.setText("");
        messageInput.setText("");
        setcardSubmitButton.setText("Ready to Play!");
        readygamebutton.setVisible(true);
        //reset the game's scoreboard
        for (int i = 0+1; i < 4; i++)
            for (int j=0; j < 2 ; j++)
            {
                gameScoreboard[i][j] = "";
            }
    }

    public void displayPanelboard(String[] serverString)
    {
        for (int i=1; i< serverString.length; i++)
        {
            System.out.println(serverString[i]);
        }
        switch(serverString[1].charAt(0))
        {
            case 'U':
                System.out.println("Score");
                break;
            case 'R':
                System.out.println("Reset game");
                break;
            case 'F':
                System.out.println("Finished");
                GameEnd = true;
                cards.clear();
                cardPane.removeAll();
                messageInput.setText("");
                playStatus = false;
                setcardSubmitButton.setText("Submit");
                setcardSubmitButton.setEnabled(submit_True);

                for (int i=0;i<4;i++)
                {
                    for (int j=0;j<1;j++)
                    {
                        gameScoreboard[i][j] = "";
                    }
                }
                break;
            default:
                System.out.println("Board received:");
                System.out.println(serverString[2]);
                System.out.println(serverString[0]);

                if(serverString[1].charAt(0) == 'S' || serverString[1].charAt(0) == 'B')
                {
                }
                else
                {
                    System.out.println("clearing");
                    cardSelection.clear();
                    selectedCards.clear();
                    cards.clear();
                    cardPane.removeAll();
                }
                if(serverString[1].charAt(0) == 'S')
                {
                    cardSelection.clear();
                    selectedCards.clear();
                }
                if(!(serverString[1].charAt(0) == 'N'))
                {
                    String cardString = serverString[2];
                    String cardsToShow[] = cardString.split(" ");
                    for(int i=0; i<cardsToShow.length;i++)
                    {
                        System.out.println("Received " + cardsToShow.length + " cards:");
                        JToggleButton setCard = new JToggleButton(new ImageIcon("src/cardimages/" + cardsToShow[i] + ".gif"));
                        setCard.setPreferredSize(new Dimension(100,100));
                        setCard.setBackground(new Color(192,192,192));
                        setCard.addItemListener(new gameCardSelector());
                        cards.put(setCard, cardsToShow[i]);
                        cardPane.add(setCard);
                    }
                    playStatus = true;
                }
                if((serverString[1].charAt(0)=='Y'||(serverString[1].charAt(0)=='N')||serverString[1].charAt(0) == 'S'))
                {
                    System.out.println("Updating");
                    String scoreStr = serverString[3];
                    String[] scores = scoreStr.split(" ");
                    for (int i=0;i<4;i++)
                    {
                        for (int j = 0; j<2; j++)
                        {
                            gameScoreboard[i][j] = "";
                        }
                    }
                    for (int i = 0; i<scores.length;i++)
                    {
                        String[] sp = scores[i].split("_");
                        String user = sp[0];
                        int score = Integer.parseInt(sp[1]);
                        System.out.println(sp[0] + " " + sp[1]);
                        if(score != userScores[i])
                        {
                            if(username.equals(user))
                            {
                                //got a set
                                if(serverString[1].charAt(0) == 'Y')
                                {
                                    System.out.println("One point for " + username);
                                    callObject.messageServer("M~" + username + " got 1 point1!");
                                }
                                //wrong set
                                else if(serverString[1].charAt(0) == 'N')
                                {
                                    System.out.println("Taking 1 point from " + username);
                                    callObject.messageServer("M~" + username + " got 1 point...TAKEN AWAY!");
                                    cardSelection.clear();
                                    selectedCards.clear();
                                    for(JToggleButton key : cards.keySet())
                                    {
                                        key.setSelected(false);
                                    }
                                    //do nothing if nothing is submitted
                                }
                                else{}
                            }
                        }
                        userScores[i] = score;
                        userNames[i] = user;
                        gameScoreboard[i][0] = user;
                        gameScoreboard[i][1] = score;
                        System.out.println(userNames[i] + " " + userScores[i]);
                    }
                    west.updateUI();
                }
                break;
        }
        cardPane.updateUI();
        System.out.println("Cards shown");
    }

    //update Score
    public void userScoreUpdate(String serverMessage)
    {
    }

    //updates and displays messages sent to server from chat
    public void updateChat(String username, String message){
        if(username.equals("")){
            chatHistory.append(message + "\n");
        }
        else{
            chatHistory.append(username + ": " + message + "\n");
        }
        JScrollBar vertical = chatScroll.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
    }
}