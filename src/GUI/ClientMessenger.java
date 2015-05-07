package GUI;

import Communication.Messenger;
import Communication.SetThread;
import Communication.Message;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * @author MSmarsch
 */

public class ClientMessenger extends Messenger {
    String serverIP;
    int serverPort;
    final int serverID;
    LobbyClass lobby;
    LoginClass login;
    GameClass game;


    public ClientMessenger(String serverIP, int serverPort)
    {
        super();
        serverID = 0;
        this.serverIP = serverIP;
        this.serverPort = serverPort;
    }

    public void messageServer(String message)
    {
        System.out.println("Sending to server: " + message);
        sendMessage(serverID, message);
    }

    @Override
    public void connect()
    {
        System.out.println("Connecting to server...");
        Socket serverSocket;
        try
        {
            serverSocket = new Socket(serverIP, serverPort);
            BufferedReader serverStream = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
            SOCKETS.put(serverID, serverSocket);
            SetThread setThread = new SetThread(serverID, isActive, inMessages, serverStream, SOCKETS, this);
            setThread.start();
        }
        catch(IOException e)
        {
            System.err.println("Could not connect to server.");
            System.exit(1);
        }
        showLoginScreen();
    }

    @Override
    public void disconnect(int userID)
    {
        System.err.println("System offline!");
        isActive = false;
    }

    public void setPanels(LoginClass login, LobbyClass lobby, GameClass game)
    {
        this.login = login;
        this.lobby = lobby;
        this.game = game;
    }

    public void showLoginScreen()
    {
        final ClientMessenger clientMessenger = this;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LoginClass login = new LoginClass(clientMessenger);
                login.setVisible(true);
            }
        });
    }

    public void processMessage(Message message)
    {
        System.out.println("Message Received: " + message.message);
        String[] parsedMessage = message.message.split("~");
        switch(parsedMessage[0].charAt(0))
        {
            case 'X':
                String errorMessage = parsedMessage[1];
                login.displayError(errorMessage);
                break;
            case 'G':
                game.displayPanelboard(parsedMessage);
                break;
            case 'E':
                login.exitGame();
                break;
            case 'J':
                lobby.attemptToJoin(parsedMessage[1].charAt(0));
                break;
            case 'C':
                if(parsedMessage.length == 3)
                {
                    String username = parsedMessage[1];
                    String chatString = parsedMessage[2];
                    lobby.updateChat(username, chatString);
                }
                else
                {
                    String chatString = parsedMessage[1];
                    lobby.updateChat("", chatString);
                }
                break;
            case 'T':
                //TODO Kimchi has to look at how to implement this
                break;
            case 'P':
                String flag = parsedMessage[1];
                String sender = parsedMessage[2];
                switch(flag)
                {
                    case "A":
                        if (login.playerUsername != null) {
                            if (!login.isLoggedIn && login.playerUsername.equals(sender)) {
                                login.login(sender);
                            }
                            lobby.updateActiveUsers("A", sender);
                        }
                        break;
                    case "R":
                        if (login.isLoggedIn && login.playerUsername.equals(sender)) {
                            login.logout();
                        }
                        lobby.updateActiveUsers("R", sender);
                        break;
                    default:
                        System.err.println("Player Action Error");
                }
                break;
            case 'U':
                int roomId = Integer.parseInt(parsedMessage[2]);
                int numPlayers;
                int maxNumPlayers;
                String roomName;
                String state;
                boolean status;
                switch(parsedMessage[1].charAt(0))
                {
                    case 'A':
                        roomName = parsedMessage[3];
                        numPlayers = Integer.parseInt(parsedMessage[4]);
                        maxNumPlayers = Integer.parseInt(parsedMessage[5]);
                        state = parsedMessage[6];
                        status = state.equals("Playing");
                        lobby.createNewGame(roomId, roomName, numPlayers, maxNumPlayers, status);
                        break;
                    case 'R':
                        lobby.removeGame(roomId);
                        break;
                    case 'I':
                        lobby.setIncactiveGame(roomId);
                        break;
                    case 'X':
                        lobby.addPlayers(roomId);
                        break;
                    case 'Y':
                        lobby.decreasePlayers(roomId);
                        break;
                    default:
                        System.err.println("Game Request Error.");
                }
        }
    }
}
