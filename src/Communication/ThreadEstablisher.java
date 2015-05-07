package Communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;

/**
 * @author MSmarsch
 */
public class ThreadEstablisher extends Thread {
    final ConcurrentMap<Integer, Socket> SOCKETS;
    final BlockingQueue<Message> inMessages;
    Messenger messenger;
    boolean isActive;
    int serverPort;

    public ThreadEstablisher(int serverPort, boolean isActive, ConcurrentMap<Integer, Socket> SOCKETS, BlockingQueue<Message> inMessages, Messenger messenger)
    {
        super("ThreadEstablisher");
        this.serverPort = serverPort;
        this.isActive = isActive;
        this.SOCKETS = SOCKETS;
        this.inMessages = inMessages;
        this.messenger = messenger;
    }

    @Override
    public void run()
    {
        ServerSocket serverSocket = null;
        try
        {
            serverSocket = new ServerSocket(serverPort);
        }
        catch(IOException e)
        {
            System.err.println("Failure on port " + serverPort);
        }
        int numConnections = 0;
        while(isActive)
        {
            try
            {
                Socket clientSocket = serverSocket.accept();
                if(clientSocket == null) {
                    System.err.println("Thread could not be established!");
                    continue;
                }
                BufferedReader inStream = null;
                try
                {
                    inStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                }
                catch(IOException e)
                {
                    System.err.println(e.getMessage());
                }
                SOCKETS.put(numConnections, clientSocket);
                if(!messenger.processEstablishMessage(numConnections, inStream, clientSocket))
                {
                    continue;
                }
                SetThread thread = new SetThread(numConnections, isActive, inMessages, inStream, SOCKETS, messenger);
                ++numConnections;
                thread.start();
            }
            catch(IOException e)
            {
                System.err.println("Connection Failure!");
            }
        }
    }
}