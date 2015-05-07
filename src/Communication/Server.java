package Communication;

/**
 * @author MSmarsch
 */
public class Server {

    public static void main(String[] args)
    {
        int serverPort = 5000;
        ServerMessenger serverMessenger;
        serverMessenger = new ServerMessenger();
        MessageRouter messageRouter;
        messageRouter = new MessageRouter(serverPort, serverMessenger);
        messageRouter.run();
        Message message;
    }
}
