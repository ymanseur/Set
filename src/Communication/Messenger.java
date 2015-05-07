package Communication;

import java.io.BufferedReader;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.BlockingQueue;

/**
 * @author MSmarsch
 */
public abstract class Messenger {
    public boolean active;
    public ConcurrentMap<Integer, Socket> SOCKETS;
    public BlockingQueue<Message> inMessages;
    public BlockingQueue<Message> outMessages;

    public Messenger()
    {
        active = true;
        SOCKETS = new ConcurrentHashMap<>();
        inMessages = new LinkedBlockingQueue<>();
        outMessages = new LinkedBlockingQueue<>();
    }

    public void connect()
    {
        throw new UnsupportedOperationException("Lost in Abstract Messenger Class");
    }

    public void disconnect(int userID)
    {
        throw new UnsupportedOperationException("Lost in Abstract Messenger Class");
    }
    public void sendMessage(int userID, String message)
    {
        try {
            outMessages.put(new Message(userID, message));
        }
        catch(InterruptedException e)
        {
            System.err.println("'" + message +"' could not be sent to User: " + userID);
        }
    }

    public boolean processEstablishMessage(int numConnections, BufferedReader inStream, Socket clientSocket)
    {
        throw new UnsupportedOperationException("Lost in Abstract Messenger Class");
    }

    public void processMessage(Message message)
    {
        throw new UnsupportedOperationException("Lost in Abstract Messenger Class");
    }

    public void acceptMessage()
    {
        throw new UnsupportedOperationException("Lost in Abstract Messenger Class");
    }
}
