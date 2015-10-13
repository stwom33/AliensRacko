//package racko2;


import java.io.IOException;
import java.util.*;

/**
 * File: Player.java
 * Description: abstract Class used to store fields and methods associated with
 * Players in RACKO, both HumanPlayer and ComputerPlayer inherit from this
 * Date Created: 2/5/2014
 * Last Modified: 2/24/2014
 * @author Scott Twombly
 */
public abstract class Player
{
    /** player's ID*/
    public final int id;
	/** player's name*/
	public String name;
    /** is it that player's turn?*/
    public boolean isTurn = false; 
	/** a flag to determine if the player took an action on his/her turn */
	public boolean tookAction = false;
    /** flag for winning*/
    public boolean isWinner;
	/** flag to see if player is AI */
	public boolean isAI = false;
    /** that player's rack*/
    public ArrayList<Card> rack;
    /** have you drawn from main?*/
    public boolean hasDrawn = false;
    /** should your cards be visibile?*/
    public boolean showCards = false;
    /** current "card in hand" */
    public Card current = null;
	/** running score of player */
	public int runningScore = 0;
	/** string that stores last move */
	public String lastMove;
	/** how many times this player has drawn from main */
	public int drawnFromMain = 0;
	/** how many times this player has drawn from discad */
	public int drawnFromDiscard = 0;
	/** how many swaps this player has performed */
	public int swaps = 0;
	/** the score if the game where to end abruptly */
	public int currentScore = 0;
	
    
    
   
    /**
     * constructor with playerNum and the main Deck Object passed as arguments.
     * initializes player id and rack, passes main deck to fillRack() method
     * 
     * @param playerNum player ID
     * @param main main deck
     * @param showCards are cards visible?
     */
    public Player(int playerNum, Deck main, boolean showCards)
    {
        this.id = playerNum;
        this.showCards = showCards;
        rack = new ArrayList<Card>();
        current = null;
        fillRack(main);
        this.lastMove = "";
    }
    
   
    /**
     * alternate constructor for when in oMode, has an additional argument for
     * cardsToPlace directly into rack, calling an alternate fillRack() method
     * @param playerNum player ID
     * @param main main deck
     * @param show
     * @param cardsToPlace array used in oMode
     */
    public Player(int playerNum, Deck main, Boolean show, ArrayList<Card> cardsToPlace)
    {
        this.id = playerNum;
        this.showCards = true;
        rack = new ArrayList<Card>();
        fillRackOMode(main, cardsToPlace);       
    }
   
    
    
   
    /**
     * alternate fillRack() method to use when in oMode, places cards from
     * cardsToPlace into the rack, before filling the remaining slots from the
     * shuffled main deck
     * 
     * @param main main deck
     * @param cardsToPlace array used in oMode
     */
    private void fillRackOMode(Deck main, ArrayList<Card> cardsToPlace){
        int cardsPlaced = cardsToPlace.size();

        while (!cardsToPlace.isEmpty()){
            rack.add(cardsToPlace.remove(0));
        }
        
        for (int i=cardsPlaced;i<10;i++){
            rack.add(main.pile.pop());   
        }  
 
    }
            
  
    /**
     * method called from constructor that populates player's rack from the main
     * (shuffled) deck
     * @param main main deck
     */
    public void fillRack(Deck main){
        //System.out.println("SIZE OF DECK: " + main.pile.size());
        for (int i=0;i<10;i++){
            rack.add(main.pile.pop());   
        }
		System.out.println(showHand());
    }
    
    /**
     *
     */
    public void emptyRack() {
		while (!rack.isEmpty()) {
			rack.remove(0);
			System.out.println("Removed from rack");
		}
	}
    
	/**
	* This method sets the player's isTurn flag on or off
     * @param t
	*/
	public void setIsTurn(boolean t){
		this.isTurn = t;
	}
	
    
    /**
     * show's the player's hand, used mainly in testing
     * @return string containing the players hand
     */
    public String showHand(){
        String hand = new String();
        for (int i=0;i<10;i++){
            hand += (rack.get(i).getValue() + " ");
        }
        return hand;
    }
    
    /**
     * sets isWinner flag to true
     */
    public void wins(){
        isWinner = true;
    }
	
	/** 
	* calculates current score, used in context menus in Multiplayer mode
	*/
	public int getCurrentScore(){
		int score = 5;
		for (int i = 0; i<9; i++) {
			if (rack.get(i).value < rack.get(i+1).value) {
				score+=5;
			} else {
				break;
			}
		}
		return score;
	}

    // abstract methods to be implemented in subclasses (Human/ComputerPlayer)

    /**
     *	This method gets the AI input for the first wave of decision making
     * @param dValue
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
        public abstract char getInput(int dValue) throws IOException, InterruptedException;

    /**
     * This method gets AI decision from ComputerPlayer about keeping a card
	 * it has drawn from main
     * @param temp
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public abstract char getKeepOrNot(Card temp) throws IOException, InterruptedException;

    /**
     *	This method polls the AI to get which position in the rack it would like
	 * to swap with
     * @param temp
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public abstract char getSwapInput(Card temp) throws IOException, InterruptedException;
    
     /** Method resets the AI rack flags used in AI decision making during swap polls
	 */
	 public abstract void resetRackFlags();

}