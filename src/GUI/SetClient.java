package GUI;

import Communication.MessageRouter;

/**
 * @author MSmarsch
 */
public class SetClient {

    public static void main(String[] args)
    {
        int serverPort = 5701;
        String serverIP = "199.98.20.122";
        ClientMessenger clientMessenger = new ClientMessenger(serverIP, serverPort);
        MessageRouter messageRouter = new MessageRouter(clientMessenger);
        messageRouter.run();
    }
}
