package Communication;

/**
 * @author MSmarsch
 */
public class Client {
    public int userID;
    public String username;
    public String password;
    public int roomID;
    public Client()
    {
        this.userID = -1;
        this.username = "Anonymous";
        this.password = "";
        this.roomID = -1;
    }
    public Client(int userID, String username, String password, int roomID) {
        this.userID = userID;
        this.username = username;
        this.password = password;
        this.roomID = roomID;
    }
}
