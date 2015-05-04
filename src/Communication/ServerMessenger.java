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
        if (messagePieces.length != 1) {
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
                    curr.remove(userID);
                    if(curr.isInactive())
                    {
                        sendMessage(-1, "U~Y~" + disconnect.roomID);
                    }
                    if(curr.numPlayers() > 0)
                    {
                        messageGame(curr, "T~" + disconnect.username + " disconnected");
                        
                    }
                }
            }
        }
    }
}