package Communication;

import java.net.*;
import java.util.Scanner;
import java.io.IOException;
import java.io.PrintWriter;


/**
 * @author MSmarsch
 */
public class ServerThread implements Runnable {
    public int sockNum;
    public Socket SOCKET;
    public String message;
    public ServerThread(int sockNum, Socket SOCKET)
    {
        this.sockNum = sockNum;
        this.SOCKET = SOCKET;

    }
}
