package Backend;

import java.util.*;

/**
 * 
 * @author Yacine
 *
 */


public class Game {
	final List<Player> players;
	String name;
	final int numPlayers;
	Board board;
	Scoring scoring;
	
	public Game(String name, int numPlayers){
		players = new ArrayList<>();
		this.name = name;
		this.numPlayers = numPlayers;
	}
	
	public String initializeGame(){
		board = new Board();
		board.refillBoard();
		scoring = new Scoring(players);
		// decide on way to simply send the board between client/server
		// probably as a string
		return "";
	}
	

}
