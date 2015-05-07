package GUI;

import Communication.MessageRouter;

/**
 * @author MSmarsch
 */
public class SetClient {

    public static void main(String[] args)
    {
        int serverPort = 5000;
        String serverIP = "127.0.0.1";
        ClientMessenger clientMessenger = new ClientMessenger(serverIP, serverPort);
        MessageRouter messageRouter = new MessageRouter(clientMessenger);
        messageRouter.run();
    }
}
