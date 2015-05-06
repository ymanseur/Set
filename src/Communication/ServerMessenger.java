package Communication;

import Backend.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Set;
import java.util.*;
import java.io.BufferedReader;

/**
 * @author MSmarsch
 */
public class ServerMessenger extends Messenger {
    final Map<Integer, Client> users;
    final Map<Integer, Game> games;
    int numRooms;
    Sql db;

    public ServerMessenger() {
        super();
        active = true;
        users = new HashMap<>();
        games = new HashMap<>();
        numRooms = 0;
        db = new Sql();
    }

    @Override
    public void sendMessage(int userID, String message) {
        System.out.println("Attempting to send message: " + message);
        if (userID != -1) //send to specific user
        {
            try {
                outMessages.put(new Message(userID, message));
            } catch (InterruptedException e) {
                System.err.println("'" + message + "' could not be sent.");
            }
        } else //message is sent to all users
        {
            Set<Integer> ids = SOCKETS.keySet();
            for (Integer id : ids) {
                try {
                    outMessages.put(new Message(id, message));
                } catch (InterruptedException e) {
                    System.err.println("'" + message + "' could not be sent.");
                }
            }
        }
    }

    @Override
    public void processMessage(Message message) {
        String msg = message.message;
        int userID = message.userID;
        System.out.println("Message Received: " + msg);
        String[] parsedMessage = msg.split("~");
        char instr = parsedMessage[0].charAt(0);
        switch (instr) {
            default:
                System.out.println("Unknown Message Identifier: " + msg);
                break;
            case 'R':
                System.out.println("Processing Registration Request...");
                Register(userID, parsedMessage);
                System.out.println("Registration Complete!");
                break;
            case 'L':
                System.out.println("Processing Login Request...");
                Login(userID, parsedMessage);
                System.out.println("Login Complete!");
                break;
            case 'D':
                System.out.println("Processing Disconnection Request...");
                Disconnect(userID, parsedMessage);
                System.out.println("Disconnection Complete!");
                break;
            case 'N':
                System.out.println("Processing Create Game Request...");
                NewGame(userID, parsedMessage);
                System.out.println("Create Game Complete!");
                break;
            case 'J':
                System.out.println("Processing Join Game Request...");
                Join(userID, parsedMessage);
                System.out.println("Join Game Complete!");
                break;
            case 'G':
                System.out.println("Processing Ready Request...");
                Ready(userID, parsedMessage);
                System.out.println("Ready Complete!");
                break;
            case 'S':
                System.out.println("Processing Set Request...");
                SetRequest(userID, parsedMessage);
                System.out.println("Set Request Complete!");
                break;
            case 'E':
                System.out.println("Processing Exit Request...");
                Exit(userID, parsedMessage);
                System.out.println("Exit Complete!");
                break;
            case 'C':
                System.out.println("Processing Lobby Chat Request... ");
                LobbyChat(userID, parsedMessage);
                System.out.println("Lobby Chat Request Complete!");
                break;
            case 'T':
                System.out.println("Processing Game Chat Request...");
                GameChat(userID, parsedMessage);
                System.out.println("Game Chat Request Complete!");
                break;
            case 'M':
                System.out.println("Processing Game Message Request...");
                GameMessage(userID, parsedMessage);
                System.out.println("Game Message Request Complete!");
                break;
        }
    }

    void Register(int userID, String[] parsedMessage) {
        String username = parsedMessage[1];
        String password = parsedMessage[2];
        if (parsedMessage.length != 3) {
            System.err.println("Invalid message.");
            sendMessage(userID, "X~<html><p><center>Invalid Username or Password!<br>" + "Cannot contain '~'<br></center></p></html>");
            return;
        }

        if (username.contains(" ") || username.contains("_")) {
            System.err.println("Invalid Registration Attempt.");
            sendMessage(userID, "X~<html><p><center>Invalid Username!<br>" + "Cannot contain ' ' or '_'<br></center></p></html>");
            return;
        }

        if (db.addUser(username, password)) {
            Client user = db.getUser(username);
            System.out.println("'" + username + "' has been added to the database.");
            users.put(userID, user);
            sendMessage(-1, "P~A~" + username);
            updateInfo(userID);
        } else {
            sendMessage(userID, "X~Username taken!");
            System.out.println("Username taken!");
        }

    }

    void Login(int userID, String[] parsedMessage) {
        if (parsedMessage.length != 3) {
            System.err.println("Invalid message.");
            sendMessage(userID, "X~<html><p><center>Invalid Username or Password!<br>" + "Cannot contain '~'<br></center></p></html>");
            return;
        }
        String username = parsedMessage[1];
        String password = parsedMessage[2];
        Client account = db.getUser(username);
        if (account.userID != -1)
        {
            for (Client online : users.values()) {
                if (username.equals(online.username)) {
                    sendMessage(userID, "X~<html><p><center>User already logged in!</center></p></html>");
                    return;
                }
            }
            if (password.equals(account.password))
            {
                users.put(userID, account);
                sendMessage(-1, "P~A~" + username);
                updateInfo(userID);
            }
            else
            {
                sendMessage(userID, "X~Invalid Password");
            }
        }
        else
        {
            sendMessage(userID, "X~Invalid Username");
        }
    }

    void Disconnect(int userID, String[] parsedMessage)
    {
        if (parsedMessage.length != 1) {
            System.err.println("Invalid message.");
            return;
        }
        Client disconnect = users.get(userID);

        if(disconnect != null)
        {
            sendMessage(-1, "P~R~" + disconnect.username);
            if(disconnect.roomID >= 0)
            {
                Game curr = games.get(disconnect.roomID);
                if(curr != null)
                {
                    curr.removePlayer(userID);
                    if(curr.isInactive())
                    {
                        sendMessage(-1, "U~Y~" + disconnect.roomID);
                    }
                    if(curr.getNumPlayers() > 0)
                    {
                        messageGame(curr, "T~" + disconnect.username + " disconnected");
                        messageGame(curr, curr.getPlayerNames());

                        if(curr.isActive())
                        {
                            if(!db.updateUser(disconnect))
                            {
                                System.err.println("User could not be found.");
                            }
                            if(curr.getNumPlayers() == 1)
                            {
                                curr.isCompleted();
                                endGame(curr);
                            }
                        }
                        else
                        {
                            messageGame(curr, "T~" + disconnect.username + " disconnected! Press ready to start game.");
                            curr.resetReady();
                            messageGame(curr, "G~R");
                        }
                    }
                    else
                    {
                        if(curr.isInactive())
                        {
                            curr.removePlayer(userID);
                            sendMessage(-1, "U~R~" + disconnect.roomID);
                        }
                        games.remove(disconnect.roomID);
                    }
                }
                else
                {
                    System.err.println("Shouldn't Be Here Ever.");
                }
            }
            users.remove(userID);
        }
    }

    void NewGame(int userID, String[] parsedMessage)
    {
        if (parsedMessage.length != 3)
        {
            sendMessage(userID, "X~Invalid New Game Request");
            System.err.println("New Game Error.");
            return;
        }
        Client creator = users.get(userID);
        if(creator.roomID >= 0)
        {
            sendMessage(userID, "A");
            return;
        }
        creator.roomID = numRooms;
        Game newGame = new Game(parsedMessage[1], Integer.parseInt(parsedMessage[2]));
        newGame.addPlayer(userID, creator.username);
        sendMessage(userID, newGame.getPlayerNames());
        sendMessage(-1, "U~A~" + numRooms + "~" + parsedMessage[1] + "~" + newGame.numPlayers() + "~" + newGame.getMaxNumPlayers() + "~Inactive");
        sendMessage(-1, "C~" + creator.username + " created a new game! Name: " + parsedMessage[1] + " ID: " + numRooms);
        ++numRooms;
    }

    void Join(int userID, String[] parsedMessage)
    {
        if (parsedMessage.length != 2) {
            System.err.println("Join Game Error!");
            return;
        }
        Client join = users.get(userID);
        if(join.roomID >=0)
        {
            sendMessage(userID, "A");
            return;
        }
        join.roomID = Integer.parseInt(parsedMessage[1]);
        Game joinedRoom = games.get(join.roomID);
        if(joinedRoom == null)
        {
            System.err.println("No Game Room Found.");
            join.roomID = -1;
        }
        else
        {
            if(joinedRoom.getNumPlayers() < joinedRoom.getMaxPlayers())
            {
                if(joinedRoom.isActive())
                {
                    join.roomID = -1;
                    sendMessage(userID, "J~I");
                }
                else
                {
                    joinedRoom.addPlayer(userID, join.username);
                    sendMessage(userID, "J~J");
                    messageGame(joinedRoom, joinedRoom.getPlayerNames());
                    sendMessage(-1, "C~" + join.username + " joined " + joinedRoom.getRoomName() + " with ID " + parsedMessage[1]);
                    sendMessage(-1, "U~X~" + join.roomID);
                    messageGame(joinedRoom, "T~" + join.username + " joined.");
                }
            }
            else
            {
                join.roomID = -1;
                sendMessage(userID, "J~F");
            }
        }
    }

    void Ready(int userID, String[] parsedMessage)
    {
        if(parsedMessage.length != 1)
        {
            System.err.println("Ready Request Error");
        }
        Client ready = users.get(userID);
        Game currGame = games.get(ready.roomID);
        if(currGame != null)
        {
            currGame.incNumReady();
            messageGame(currGame, "T~" + ready.username + "is ready to play!");
            if(currGame.getNumPlayers() == currGame.getNumReady())
            {
                currGame.startPlaying();
                messageGame(currGame, "T~Beginning Game...");
                messageGame(currGame, currGame.initializeGame());
                currGame.setRemoved();
                sendMessage(-1, "U~R~" + ready.roomID);
            }
        }
        else
        {
            System.err.println("You shouldn't be here!");
        }
    }

    void SetRequest(int userID, String[] parsedMessage)
    {
        if(parsedMessage.length != 4)
        {
            System.err.println("Set Request Error.");
            return;
        }
        Client submit = users.get(userID);
        Game currGame = games.get(submit.roomID);
        if(currGame.isBlockSets())
        {
            return;
        }
        String msg = currGame.CheckSetAndUpdate(userID, parsedMessage[1], parsedMessage[2], parsedMessage[3]);
        if(msg != null)
        {
            messageGame(currGame, msg);
        }
        if(currGame.isOver())
        {
            endGame(currGame);
        }
    }

    void Exit(int userID, String[] parsedMessage)
    {
        if(parsedMessage.length != 1)
        {
            System.err.println("Exit Request Error.");
            return;
        }
        Client leaving = users.get(userID);
        if(leaving.roomID < 0)
        {
            System.err.println("Exit Game Fault.");
            return;
        }
        Game oldGame = games.get(leaving.roomID);
        sendMessage(userID, "E");
        if(oldGame == null)
        {
            System.err.println("Exit Game Fault.");
            return;
        }
        else
        {
            oldGame.removePlayer(userID);
            if(oldGame.isEmpty())
            {
                if(oldGame.isInactive())
                {
                    oldGame.setRemoved();
                    sendMessage(-1, "U~R~" + leaving.roomID);
                }
                games.remove(leaving.roomID);
            }
            else
            {
                messageGame(oldGame, "T~" + leaving.username + " left the game.");
                messageGame(oldGame, oldGame.getPlayerNames();
                if(oldGame.isInactive())
                {
                    sendMessage(-1, "U~Y~" + leaving.roomID);
                }
                if(oldGame.isActive())
                {
                    if(!db.Update(leaving))
                    {
                        System.err.println("User could not be located.");
                    }

                    if(oldGame.getNumPlayers() == 1)
                    {
                        oldGame.setCompleted();
                        endGame(oldGame);
                    }
                }
            }
        }
    leaving.roomID = -1;
    }

    void LobbyChat(int userID, String[] parsedMessage)
    {
        if (parsedMessage.length != 2) {
            System.err.println("Chat Request Error.");
            return;
        }
        Client sender = users.get(userID);
        Game currGame = games.get(sender.roomID);
        messageGame(currGame, "T~" + sender.username + "~" + parsedMessage[1]);
    }

    void GameChat(int userID, String[] parsedMessage)
    {
        if (parsedMessage.length != 2) {
            System.err.println("Game Chat Request Error.");
            return;
        }
        Client sender = users.get(userID);
        Game currGame = games.get(sender.roomID);
        messageGame(currGame, "T~" + sender.username + "~" + parsedMessage[1]);
    }

    void GameMessage(int userID, String[] parsedMessage)
    {
        if(parsedMessage.length != 2)
        {
            System.err.println("Game Message Request Error.");
        }
        Client sender = users.get(userID);
        Game currGame = games.get(sender.roomID);
        messageGame(currGame, "T~" + parsedMessage[1]);
    }

    void messageGame(Game currGame, String message)
    {
        List<Integer> ids = currGame.getPlayerIds();
        for(Integer id : ids)
        {
            sendMessage(id, message);
        }
    }

    void endGame(Game currGame)
    {
        String victors = "";
        System.out.println("Ending the Current Game.");
        if(!currGame.isOver())
        {
            System.err.println("Shouldn't Be Here!");
        }
        List<Integer> winners = new ArrayList<>();
        currGame.getWinner(winners);
        for(int i = 0; i < winners.size(); i++)
        {
            if(i > 0)
            {
                victors += " and ";
            }
            victors += users.get(winners.get(i));
        }
        victors += " won!";
        messageGame(currGame, "T~" + victors);
        messageGame(currGame, "T~Game Over. Please return to lobby.");
        System.out.println("Game Over.");
        currGame.blockSets();
    }

    void updateInfo(int userID)
    {
        Game game = null;
        String state;
        Collection<Client> clients = users.values();
        String username = users.get(userID).username;
        for(Client c : clients)
        {
            if(username != c.username)
            {
                sendMessage(userID, "P~A~" + c.username);
            }
        }
        Set<Integer> gameIDs = games.keySet();
        for(Integer gameID : gameIDs)
        {
            game = games.get(gameID);
            if(!game.isRemoved())
            {
                if(game.isActive())
                {
                    state = "Active";
                }
                else
                {
                    state = "Inactive";
                }
                sendMessage(userID, "U~A~" + gameID + "~" + game.getName() + "~" + game.numPlayers() + "~" + game.getMaxNumPlayers() + "~" + state);
            }
        }
    }
}