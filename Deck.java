
//package racko2;


import java.util.*;
import java.awt.* ;
import java.awt.image.* ;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import java.io.* ;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.* ;

/**
 * File: Deck.java
 * Description: Class used to store fields and methods associated with Decks 
 * used in RACKO.java
 * Date Created: 2/4/2014
 * Last Modified: 3/25/2014
 * @author Scott Twombly
 */
public class Deck extends JButton
{
    /** used to initialize starting deck*/
    private final int DECK_SIZE; 
    /** stack used to simulate the actual "piles" of cards*/
    public Stack<Card> pile; 

    /**
     * if the top of the deck is showing face up or not
     */
    public boolean show;
	private long seed;
	public ImageIcon front;
	public ImageIcon back;
	public BufferedImage image;
    /**
     * constructor with a deck size argument, initiates the pile Stack
     * calls the generateCards() method
     * @param size size of the deck
     */
    public Deck(int size, long seed)
    {
        this.pile = new Stack<Card>();
        DECK_SIZE = size;
		this.seed = seed;
        generateCards();
		this.show = pile.peek().show;
		this.setMaximumSize(new Dimension(150,100));
        this.setMinimumSize(new Dimension(150,100));
        this.setPreferredSize(new Dimension(150,100));
		this.setSize(150,100);

    }
    
    
    
    /**
     * Deck constructor for alternate main deck during view mode, passed an 
     * additional ArrayList of cards to keep track of the cards it needs to place
     * in the player's rack right away.
     * @param size size of the deck
     * @param cardsToPlace array used in oMode
     */
    public Deck(int size, ArrayList<Card> cardsToPlace)
    {
        this.pile = new Stack<Card>();
        DECK_SIZE = size;

        generateCardsOMode(cardsToPlace);
		this.show = pile.peek().show;
		this.setMaximumSize(new Dimension(150,100));
        this.setMinimumSize(new Dimension(150,100));
        this.setPreferredSize(new Dimension(150,100));
		this.setSize(150,100);
    }
    
    
    /**
     * constructor for discard decks
     * @param d used to signal that this is a discard deck
     */
    public Deck(char d){
        DECK_SIZE = 1;  //doesnt matter as we wont be generating new cards
        this.pile = new Stack<Card>();
		this.setMaximumSize(new Dimension(150,100));
        this.setMinimumSize(new Dimension(150,100));
        this.setPreferredSize(new Dimension(150,100));
		this.setSize(150,100);
        
    }
    

    /**
     * Alternate card generation when in oMode, additional ArrayList<Card>
     * argument to keep track of cards needed to placed in player's rack
     * right away. the boolean foundValue is used as a flag to determine if one
     * of the cards being created should instead be placed directly into
     * player's rack
     * @param cardsToPlace 
     */
    private void generateCardsOMode(ArrayList<Card> cardsToPlace){
        ArrayList<Card> cards = new ArrayList<>();
        boolean foundValue; 
        for (int i=1;i<=DECK_SIZE;i++){
            foundValue = checkForValue(i, cardsToPlace);
            if (foundValue){
               // System.out.println("Not placing: " + i);
            } else {
              //  System.out.println("Placing: " + i);
                cards.add(new Card(i, DECK_SIZE));
            }
        }    
        shuffleOMode(cards, cardsToPlace);
        fillStackOMode(cards, cardsToPlace);
    }
    
    

    /**
     * private method used in generateCardsOMode() method, used to check if
     * one of the cards being created is in the cardsToPlace ArrayList
     * @param i value to check
     * @param cardsToPlace
     * @return 
     */
    private boolean checkForValue(int i, ArrayList<Card> cardsToPlace){
        for (Card it: cardsToPlace){
            if (it.value == i)
                return true;
        }
        return false;
    }
    
    

    /**
     * generates cards, first into an ArrayList<Card> so it can use my shuffle
     * method. (shuffling a stack just seemed wrong), then calls fillStack() method
     * to put the shuffled cards into a "pile".
     */
    private void generateCards(){
        ArrayList<Card> cards = new ArrayList<>();
        // create cards, 1 to DECK_SIZE (40 in this case)
        for (int i=0;i<DECK_SIZE;i++){
            cards.add(i, new Card(i+1, DECK_SIZE));
        }
        
        shuffle(cards);
        fillStack(cards);
    }
    
    

    /**
     * alternate shuffle() method for when in oMode
     * @param cards
     * @param cardsToPlace 
     */
    private void shuffleOMode(ArrayList cards, ArrayList cardsToPlace){
        Random rand = new Random();  //rand represent index's to be swapped
        Card temp;
        int cardsPlaced = cardsToPlace.size();
        
        // 500 seems like a good number of times to swap to simulate a shuffled deck
        for (int t=0;t<500;t++){
            // generates 2 random positions to be swapped, instead of positions
            // 0 - (DECK_SIZE-1), it uses positions 0 - (DECK_SIZE - cardsPlaced)
            int pos1 = rand.nextInt((DECK_SIZE - (cardsPlaced + 1)) + 1);  
            int pos2 = rand.nextInt((DECK_SIZE - (cardsPlaced + 1)) + 1);
            
            //perform the swap
            temp = (Card) cards.get(pos1);
            cards.set(pos1, cards.get(pos2));
            cards.set(pos2, temp);         
        }
    }
    

    /**
     * takes the ArrayList of cards and pushes them one by one into a Stack, 
     * using a throw away Card temp variable 
     * @param cards 
     */
    private void fillStack(ArrayList cards){
        Card temp;
        for(int i=0;i<DECK_SIZE;i++){
            //System.out.println("attempting to push card " + i + " onto stack");
            temp = (Card) cards.get(i);
            pile.push(temp);
        }
        
        
    }
    
 
   
   
    /**
     * alternate fillStack() method used for when in oMode
     * @param cards
     * @param cardsToPlace 
     */
    private void fillStackOMode(ArrayList cards, ArrayList cardsToPlace){
        Card temp;
        int cardsPlaced = cardsToPlace.size();
        for(int i=0;i<(DECK_SIZE-cardsPlaced);i++){
            //System.out.println("attempting to push card " + i + " onto stack");
            temp = (Card) cards.get(i);
            pile.push(temp);
        }
    }
    

    /**
     * shuffle() takes the ArrayList of generated cards from generateCards() and
     * swaps 2 random positions 500 times to simulate a shuffle
     * @param cards 
     */
    private void shuffle(ArrayList cards){
        Random rand = new Random(seed);  //rand represent index's to be swapped
        Card temp;
        
        //500 seems like a good number of times to swap to simulate a shuffled deck
        for (int t=0;t<500;t++){
            //generate 2 random positions to be swapped
            int pos1 = rand.nextInt((DECK_SIZE - 1) + 1);  
            int pos2 = rand.nextInt((DECK_SIZE - 1) + 1);
            
            //perform the swap
            temp = (Card) cards.get(pos1);
            cards.set(pos1, cards.get(pos2));
            cards.set(pos2, temp);          
        }
    }
   

    /**
     * simply displays the top card of the deck
     */
    public void displayTopCard(){
        Card temp = (Card) pile.peek();
        System.out.print("Top Card: ");
        System.out.println(temp.getValue());
    }
	
	    /**
     * Overrides paintCompnent method, depending on if the card is showing or not
     * @param g graphics component.
     */
    @Override
    public void paintComponent(Graphics g) {
        if (show)
            g.drawImage(pile.peek().front.getImage(), TOP, TOP, this);
        else
            g.drawImage(pile.peek().back.getImage(), TOP, TOP, this);
    }   
    
}