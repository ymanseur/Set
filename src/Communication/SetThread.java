package Communication;

import java.net.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * @author MSmarsch
 */
public class SetThread extends Thread{
    boolean isActive;
    final int id;
    final BlockingQueue<Message> inMessages;
    final ConcurrentMap<Integer, Socket> sockets;
    Messenger messenger;
    final private BufferedReader inStream;

    public SetThread(int id, boolean isActive, BlockingQueue<Message> inMessages, BufferedReader inStream, ConcurrentMap<Integer, Socket> sockets, Messenger messenger)
    {
        super("Connection: " + id);
        this.id = id;
        this.isActive = isActive;
        this.inMessages = inMessages;
        this.inStream = inStream;
        this.sockets = sockets;
        this.messenger = messenger;
    }

    @Override
    public void run()
    {
        String inMessage;
        System.out.println("Successfully connected to User: " + id);
        while(isActive)
        {
            try
            {
                inMessage = inStream.readLine();
                if(inMessage != null)
                    try
                    {
                        inMessages.put(new Message(id, inMessage));
                    }
                    catch(InterruptedException e)
                    {
                        System.err.println(e.getMessage());
                    }
                else
                {
                    System.out.println("User " + id + " has been disconnected.");
                    sockets.remove(id);
                    messenger.disconnect(id);
                    break;
                }
            }
            catch(IOException e)
            {
                System.err.println(e.getMessage());
            }
        }
    }
}
