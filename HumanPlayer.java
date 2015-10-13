
//package racko2;

//import Input.InputReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * File: HumanPlayer.java
 * Description: Class used to store fields and methods associated with Human
 * Players in RACKO, inherits from Player.java
 * Date Created: 2/17/2014
 * Last Modified: 2/24/2014
 * @author Scott Twombly
 */
public class HumanPlayer extends Player{
    
    
    /**
     * constructor simply calls the super constructor
     * @param playerNum player ID to be assigned
     * @param main main deck
     * @param showCards wether or not to display cards face up
     */
    public HumanPlayer(int playerNum, Deck main, boolean showCards){
        super(playerNum, main, showCards);
    }
    
   
    /**
     * alternate constructor for when in oMode, calling the super's alternate constructor
     * @param playerNum player ID to be assigned
     * @param main main deck
     * @param show
     * @param cardsToPlace arrayList representing cards to place in oMode
     */
    public HumanPlayer(int playerNum, Deck main, Boolean show, ArrayList<Card> cardsToPlace){
        super(playerNum, main, show, cardsToPlace);
    }
    
    
    /**
     * uses Spiegel InputReader package to get player input, dValue is current
     * discard pile value, not used when a Human Player
     * @param dValue current discard value
     * @return char choice, inout from user
     * @throws IOException 
     */
    public char getInput(int dValue) throws IOException{
        System.out.print("Draw from MAIN DECK(m) or DISCARD(d)?\n>");
        char choice = 'a';
        return choice;
    }
    
    
   
    /**
     * uses Spiegel InputReader package to get players input for swap decision
     * assigns temp card argument to current place holder variable
     * @param temp current card in hand
     * @return char choice from user
     * @throws IOException 
     */
    public char getSwapInput(Card temp) throws IOException {
        current = temp;
        System.out.print(" Which position would you like to place " + temp.getValue() + ": ");
        char choice = 'a';
        return choice;
    }
    
    

    /**
     * uses Spiegel InputReader package to get players input for keeping or 
     * discarding card drawn from main pile
     * @param temp
     * @return
     * @throws IOException 
     */
    public char getKeepOrNot(Card temp)throws IOException {
        System.out.print(" Keep(k) or Discard(d)?: ");
        char choice = 'a';
        return choice;
    }
    
	
	
	
    /**
     *
     * @throws InterruptedException
     */
    public void timeWait() throws InterruptedException{
        
    }
	
	/**
	 * Not used in human players
	 */
	public void resetRackFlags() {
	 //does nothing
	 }
}