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
	int gameState; //-1 inactive, 0 active, 1 finished
	
	public Game(String name, int numPlayers){
		players = new ArrayList<>();
		this.name = name;
		this.numPlayers = numPlayers;
		this.gameState = -1;
	}
	
	public String initializeGame(){
		board = new Board();
		board.refillBoard();
		scoring = new Scoring(players);
		gameState = 0;
		return board.toString();
	}
	
	public void resetRoom(){
		gameState = -1;
		scoring.resetScores();
	}
	
	//returns actual protocol G~FLAG~BOARD~SCORES
	public String serverBoardMessage(String flag, Board b, Scoring s){
		String message = "G~";
		message += flag;
		message += "~";
		message += b.toString();
		message += "~";
		message += s.toString();
		return message;		
	}
	
	public String playerEnterSet(int id, String c1, String c2, String c3){
		Set set = new Set(stringToCard(c1), stringToCard(c2), stringToCard(c3));
		if(board.checkAndRemoveSet(set)){
			scoring.correctSet(id);
			if(board.refillBoard())
				return serverBoardMessage("Y",board,scoring);
			else{ //end the game
				gameState = 1;
				return serverBoardMessage("F",board,scoring);
			}
			
		}else{
			scoring.incorrectSet(id);
			return serverBoardMessage("N",board,scoring);
		}
	}
	
	// convert string to corresponding Card object
	// parameter will have the form x_x_x_x
	public Card stringToCard(String sc){
		int color = Character.getNumericValue(sc.charAt(0));
		int pattern = Character.getNumericValue(sc.charAt(2));
		int shape = Character.getNumericValue(sc.charAt(4));
		int count = Character.getNumericValue(sc.charAt(6));
		Card c = new Card(color, pattern, shape, count);
		return c;
	}
	
	public String updateGame(){
		return serverBoardMessage("U",board,scoring);
	}
	
	public List<Integer> getPlayerIds(){
		List<Integer> playerIds = new ArrayList<>();
		for(int i = 0; i < players.size(); i++)
			playerIds.add(players.get(i).getId());
		return playerIds;
	}

	public void addPlayer(int id, String username){
		players.add(new Player(username, id, 0));
	}
	
	public void removePlayer(int id){
		for(int i = 0; i < players.size(); i++){
			if(players.get(i).getId() == id)
				players.remove(i);
		}
	}
	
	
}
