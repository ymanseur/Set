package backend;
import java.util.*;

/**
 * 
 * @author Yacine
 *
 */

public class Board{
	Deck deck;
	List<Card> active; //current cards on the table

	public Board(){
		deck = new Deck();
		active = new ArrayList<>();
		deck.shuffle();
		dealCards(12);
	}

	// Deals specified number of cards to the board
	// Returns false if deck doesn't have enough cards left
	public boolean dealCards(int num){
		if(deck.cardsRemaining >= num){
			for(int i=0; i<num; i++)
				active.add(deck.dealCard());
			return true;
		}
		return false;
	}

	// Check all possible card combinations in active for sets
	public boolean checkBoardForSets(){
		for(int i=0; i<active.size(); i++){
			for(int j=1; j<active.size(); j++){
				for(int k=2; k<active.size(); k++){
					Set s = new Set(active.get(i), active.get(j), active.get(k));
					if(s.checkValidSet())
						return true;
				}
			}
		}
		return false;
	}
	
	// Checks if set is valid. if Set is valid, it updates active
	public boolean checkAndRemoveSet(Set s){
		if(s.checkValidSet()){
			for(int i=0; i < 3; i++){
				Card c = s.getCard(i);
				int index = findCardIndex(c);
				//Exception handler
				if(index == -1)
					return false;
				active.remove(index);
			}
		return true;
		}
		return false;
	}
	
	public boolean refillBoard(){
		while(!checkBoardForSets())
			if(!dealCards(3))
				return false;
		if(active.size() < 12)
			dealCards(12-active.size());
		return true;		
	}
	
	// returns index of card in active
	public int findCardIndex(Card c){
		for(int i=0; i<active.size(); i++)
			if(active.get(i).equals(c))
				return i;
		return -1;
	}
	
	@Override public String toString(){
		String boardString = "";
		for(int i = 0; i < active.size(); i++){
			Card temp = active.get(i);
			boardString += temp.toString();
			boardString += " ";
		}
		boardString = boardString.substring(0, boardString.length()-1);
		return boardString;
	}
}

