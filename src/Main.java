import java.util.Scanner;
import Backend.*;

public class Main {
	/**
	public static void play(){
			Deck deck = new Deck();
			deck.shuffle();
			
			while(!deck.isEmpty())
				System.out.println(deck.dealCard().toString());
		}
	
	public static void main(String[] args){
		Scanner scan = new Scanner(System.in);
		Player p1;
		String username;
		String choice;
		boolean quit = false;
		
		System.out.println("Welcome to the game of Set!");
		System.out.print("Please enter your username: ");
		username = scan.nextLine();

		p1 = new Player(username,1,0);
		
		do{
			System.out.println("Would you like to start a new game?");
			System.out.print("Enter y to start, n to exit: ");
			choice = scan.next();
			if(choice.equals("y")){
				play();
			}
			else if(choice.equals("n"))
				quit = true;
			else
				System.out.println("Not a valid answer. \n");
		} while (!quit);
		
		System.out.println("\nThank you for playing!");
	}
	*/
}
