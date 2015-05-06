package Backend;
/**
 * 
 * @author Yacine
 *
 */
public class Player {
	String username;
	public int id;
	public int score;
	
	// Constructor
	public Player (String username, int id, int score){
		this.username = username;
		this.id = id;
		this.score = score;
	}

    public String getUsername()
    {
        return this.username;
    }
	public int getId(){
		return this.id;
	}
	public void addScore(){
		score++;
	}
	
	public void subtractScore(){
		score--;
	}
	
	public void resetScore(){
		score = 0;
	}
	
	@Override public String toString(){
		return (id + "_" + score + "_");
	}
	
}
