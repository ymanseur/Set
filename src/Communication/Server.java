package Communication;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author MSmarsch
 */
public class Server {
    public static ConcurrentMap<Integer, Socket> sockets = new ConcurrentHashMap<>();
    private static int sockNum = 0;
    public static void main(String[] args) throws IOException
    {
        try{
            final int PORT = 3333;
            ServerSocket SERVER = new ServerSocket(PORT);
            System.out.println("Waiting for clients...");
            while(true)
            {
                Socket SOCKET = SERVER.accept();
                System.out.println("Client connecting from: " + SOCKET.getLocalAddress().getHostName());
                sockNum++;
                sockets.put(sockNum, SOCKET);
                ServerThread serverThread = new ServerThread(sockNum, SOCKET);
            }
        }
        catch(IOException e)
        {
            System.err.println("IOException caught.");
        }
    }
}
