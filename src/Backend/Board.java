package Backend;

/**
 * @author MSmarsch
 */
public class Board {
    List<Card> inDeck;
    List<Card> onBoard;

    public Board() {
        inDeck = new ArrayList<Card>();
        onBoard = new ArrayList<Card>();
        initializeDeck(inDeck)
    }

    public void initializeDeck(List<Card> inDeck){
        for(int i = 0; i < 81; i++)
        {
            inDeck.add(new Card(i));
        }

    }
}
