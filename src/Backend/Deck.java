package Backend;

/**
 * 
 * @author Yacine
 *
 */
public class Deck {
	public int cardsRemaining;
	public Card[] deck;
	
	public Deck(){
		this.cardsRemaining = 81;
		deck = new Card[this.cardsRemaining];
		int cardCount = 0; // Number of cards created
		
		for(int color = 0; color <= 2; color++){
			for(int pattern = 0; pattern <= 2; pattern++){
				for(int shape = 0; shape <= 2; shape++){
					for(int count = 0; count <= 2; count++){
						deck[cardCount] = new Card(color,pattern,shape,count);
						cardCount++;
					}
				}
			}
		}
	}

	public void shuffle(){
		for(int i = deck.length-1; i > 0; i--){
			int rand = (int)(Math.random()*(i+1));
			Card temp = deck[i];
			deck[i] = deck[rand];
			deck[rand] = temp;
		}
	}
	
	public int cardsLeft(){
		return this.cardsRemaining;
	}
	
	public Card dealCard(){
		this.cardsRemaining--;
		return deck[this.cardsRemaining];
	}
	
	public boolean isEmpty(){
		if(cardsRemaining == 0)
			return true;
		else
			return false;
	}
}
