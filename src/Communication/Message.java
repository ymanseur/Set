package Communication;

/**
 * @author MSmarsch
 */
public class Message {
    public int userID;
    public String message;
    public Message(int userID, String message)
    {
        this.userID = userID;
        this.message = message;
    }
}
