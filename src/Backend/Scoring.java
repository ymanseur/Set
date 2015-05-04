package Backend;
import java.util.*;

public class Scoring {
	public List<Player> players;
	
	public Scoring(List<Player> players){
		this.players = players;
		
		// initialize scores of all players to zero
		resetScores();
	}
	
	public void correctSet(int id){
		for(int i=0; i<players.size(); i++){
			Player p = players.get(i);
			if(p.getId() == id)
				p.addScore();
		}
	}
	
	public void incorrectSet(int id){
		for(int i=0; i<players.size(); i++){
			Player p = players.get(i);
			if(p.getId() == id)
				p.subtractScore();
		}
	}
	
	
	public void resetScores(){
		for (int i = 0; i < this.players.size(); i++)
			this.players.get(i).resetScore();
	}

	public String toString(){
		String text = "";
		for(int i=0; i<players.size(); i++)
			text += players.get(i).toString();
		return text;
	}
}
