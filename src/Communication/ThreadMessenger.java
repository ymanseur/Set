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
    final ConcurrentMap<Integer, Socket> SOCKETS;
    final BlockingQueue<Message> outMessages;
    DataOutputStream ostream;
    boolean isActive;

    public ThreadMessenger(boolean isActive, ConcurrentMap<Integer, Socket> SOCKETS, BlockingQueue<Message> outMessages)
    {
        this.isActive = isActive;
        this.SOCKETS = SOCKETS;
        this.outMessages = outMessages;
    }

    void sendMessage(Socket SOCKET, Message msg)
    {
        try
        {
            ostream = new DataOutputStream(SOCKET.getOutputStream());
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
                if(SOCKETS.containsKey(outMessage.userID))
                {
                    sendMessage(SOCKETS.get(outMessage.userID), outMessage);
                }
            }
            catch(InterruptedException e)
            {
                System.err.println("Message could not be sent.");
            }
        }
    }
}
