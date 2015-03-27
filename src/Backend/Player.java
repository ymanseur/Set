package Backend;

/**
 * @author MSmarsch
 */
public class Player {
    public int id;
    public String username;
    public int score;

    public Player(int id, String username, int score){
        this.id = id;
        this.username = username;
        this.score = score;
    }

    public void setScore(int value){
        score = value;
    }

    public int getScore(){
        return score;
    }

    public void incrementScore(){
        score++;
    }

    public void decrementScore(){
        score--;
    }
}
