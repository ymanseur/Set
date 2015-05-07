package GUI;

import Communication.Messenger;

import javax.swing.*;

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
}
