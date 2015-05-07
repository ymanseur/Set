package Communication;

import java.io.DataOutputStream;
import java.net.*;
import java.io.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;

/**
 * @author MSmarsch
 */
public class ThreadMessenger extends Thread {
    final ConcurrentMap<Integer, Socket> sockets;
    final BlockingQueue<Message> outMessages;
    DataOutputStream ostream;
    boolean isActive;

    public ThreadMessenger(boolean isActive, ConcurrentMap<Integer, Socket> sockets, BlockingQueue<Message> outMessages)
    {
        this.isActive = isActive;
        this.sockets = sockets;
        this.outMessages = outMessages;
    }

    void sendMessage(Socket socket, Message msg)
    {
        try
        {
            ostream = new DataOutputStream(socket.getOutputStream());
            ostream.writeBytes(msg.message + "\n");
        }
        catch(IOException e)
        {
            System.err.println("Output stream connection unsuccessful!");
        }
    }

    @Override
    public void run()
    {
        System.out.println("Sending output messages to sockets!");
        while(isActive)
        {
            try
            {
                Message outMessage = outMessages.take();
                if(sockets.containsKey(outMessage.userID))
                {
                    sendMessage(sockets.get(outMessage.userID), outMessage);
                }
            }
            catch(InterruptedException e)
            {
                System.err.println("Message could not be sent.");
            }
        }
    }
}
