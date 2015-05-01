package backend;
/**
 * @author MSmarsch
 * @author Yacine
 */
public class Card {
	public final static int RED = 0;
	public final static int GREEN = 1;
	public final static int PURPLE = 2;
	
	public final static int SOLID = 0;
	public final static int STRIPED = 1;
	public final static int OUTLINED = 2;
	
	public final static int DIAMOND = 0;
	public final static int SQUIGGLE = 1;
	public final static int OVAL = 2;
	
	public final static int ONE = 0;
	public final static int TWO = 1;
	public final static int THREE = 2;
	
    public final int color;
    public final int pattern;
    public final int shape;
    public final int count;
    
    public Card(int color, int pattern, int shape, int count){
        this.color = color;
        this.pattern = pattern;
        this.shape = shape;
        this.count = count;
    }
    
    public int getColor() {
    	return this.color;
    }
    
    public String getColorAsString() {
    	switch (color) {
	    	case RED:		return "Red";
	    	case GREEN:		return "Green";
	    	case PURPLE:	return "Purple";
	    	default:		return null;
    	}
    }
    
    public int getPattern(){
    	return this.pattern;
    }
    
    public String getPatternAsString() {
    	switch (pattern) {
	    	case SOLID:		return "Solid";
	    	case STRIPED:	return "Striped";
	    	case OUTLINED:	return "Outlined";
	    	default:		return null;
    	}
    }
    
    public int getShape(){
    	return this.shape;
    }
    
    public String getShapeAsString() {
    	switch (shape) {
	    	case DIAMOND:	return "Diamond";
	    	case SQUIGGLE:	return "Squiggle";
	    	case OVAL:		return "Oval";
	    	default:		return null;
    	}
    }
    
    public int getCount(){
    	return this.count;
    }
    
    public String getCountAsString() {
    	switch (count) {
	    	case ONE:	return "One";
	    	case TWO:	return "Two";
	    	case THREE:	return "Three";
	    	default:	return null;
    	}
    }
    
    @Override
    public boolean equals(Object tmp){
    	Card temp = (Card) tmp;
    	return (color==temp.getColor() 
    			&& pattern==temp.getPattern() 
    			&& shape==temp.getShape() 
    			&& count==temp.getCount());	
    }
    
    public String toString(){
    	return(this.getColorAsString() + " " + this.getCountAsString() + " " + this.getPatternAsString() + " " + this.getShapeAsString() + "\n");
    }
    
}
