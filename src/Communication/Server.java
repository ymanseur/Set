package Communication;

/**
 * @author MSmarsch
 */
public class Server {

    public static void main(String[] args)
    {
        int serverPort = 5801;
        ServerMessenger serverMessenger;
        serverMessenger = new ServerMessenger();
        MessageRouter messageRouter;
        messageRouter = new MessageRouter(serverPort, serverMessenger);
        messageRouter.run();
    }
}
