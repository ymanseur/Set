package Communication;


import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;

/**
 * @author MSmarsch
 */
public class MessageRouter {
    final ConcurrentMap<Integer, Socket> SOCKETS;
    final BlockingQueue<Message> inMessages;
    final BlockingQueue<Message> outMessages;
    final Messenger messenger;
    ThreadMessenger threadMessenger;
    ThreadEstablisher threadEstablisher;
    boolean isActive;

    public MessageRouter(int serverPort, Messenger messenger)
    {
        this.isActive = messenger.isActive;
        this.messenger = messenger;
        SOCKETS = messenger.SOCKETS;
        inMessages = messenger.inMessages;
        outMessages = messenger.outMessages;
        threadMessenger = new ThreadMessenger(isActive, SOCKETS, outMessages);
        threadEstablisher = new ThreadEstablisher(serverPort, isActive, SOCKETS, inMessages, messenger);
    }

    public MessageRouter(Messenger messenger)
    {
        this.isActive = messenger.isActive;
        this.messenger = messenger;
        SOCKETS = messenger.SOCKETS;
        inMessages = messenger.inMessages;
        outMessages = messenger.outMessages;
        threadMessenger = new ThreadMessenger(isActive, SOCKETS, outMessages);
        threadEstablisher = null;
    }

    public void run()
    {
        if(threadEstablisher != null)
        {
            threadEstablisher.start();
        }
        threadMessenger.start();
        messenger.connect();
        Message msg;
        System.out.println("Routing messages...");
        while(isActive)
        {
            msg = null;
            try
            {
                msg = inMessages.take();
                messenger.processMessage(msg);
            }
            catch(InterruptedException e)
            {
                System.err.println(e.getMessage());
            }
        }
    }
}
