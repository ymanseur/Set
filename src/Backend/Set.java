package Backend;
/**
 * 
 * @author Yacine
 *
 */

public class Set {
	Card[] cards;
	
	Set(Card c1, Card c2, Card c3){
		cards[0] = c1;
		cards[1] = c2;
		cards[2] = c3;
	}
	
	public Card getCard(int index){
		return cards[index];
	}
	
	public boolean checkValidSet(){
		if(colorsEqualOrOpposite()
				&& patternsEqualOrOpposite()
				&& shapesEqualOrOpposite()
				&& countsEqualOrOpposite())
			return true;
		else
			return false;
	}
	
	public boolean colorsEqualOrOpposite(){
		boolean b = (cards[0].getColor() == cards[1].getColor()
				&& cards[1].getColor() == cards[2].getColor()
				&& cards[0].getColor() == cards[2].getColor())
				||(cards[0].getColor() != cards[1].getColor()
				&& cards[1].getColor() != cards[2].getColor()
				&& cards[0].getColor() != cards[2].getColor());
		return b;
	}
	
	public boolean patternsEqualOrOpposite(){
		boolean b = (cards[0].getPattern() == cards[1].getPattern()
				&& cards[1].getPattern() == cards[2].getPattern()
				&& cards[0].getPattern() == cards[2].getPattern())
				||(cards[0].getPattern() != cards[1].getPattern()
				&& cards[1].getPattern() != cards[2].getPattern()
				&& cards[0].getPattern() != cards[2].getPattern());
		return b;
	}
	
	public boolean shapesEqualOrOpposite(){
		boolean b = (cards[0].getShape() == cards[1].getShape()
				&& cards[1].getShape() == cards[2].getShape()
				&& cards[0].getShape() == cards[2].getShape())
				||(cards[0].getShape() != cards[1].getShape()
				&& cards[1].getShape() != cards[2].getShape()
				&& cards[0].getShape() != cards[2].getShape());
		return b;
	}
	
	public boolean countsEqualOrOpposite(){
		boolean b = (cards[0].getCount() == cards[1].getCount()
				&& cards[1].getCount() == cards[2].getCount()
				&& cards[0].getCount() == cards[2].getCount())
				||(cards[0].getCount() != cards[1].getCount()
				&& cards[1].getCount() != cards[2].getCount()
				&& cards[0].getCount() != cards[2].getCount());
		return b;
	}

}
