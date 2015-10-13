//package racko2;

import java.awt.* ;
import java.awt.image.* ;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import java.io.* ;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.* ;
import java.util.*;

/**
 * File: Card.java
 * Description: Class used to store fields and methods associated with Cards 
 * used in RACKO.java
 * Date Created: 2/4/2014
 * Last Modified: 2/24/2014
 * @author Scott Twombly
 */
public class Card extends JButton
{
    /** value of the card */
    public int value;
    /** is the front of the card showing? */
    public boolean show;
    /** highlighted version used in simulating AI turns */
    public boolean compFront = false;
    /** highlighted version used in simulating AI turns */
    public boolean compBack = false;
    /** used to determine if this card came from the main pile*/
    public boolean cameFromMain = false;
    /** the front of the card*/
    public ImageIcon front;
    /** the back of the card*/
    public ImageIcon back;
    /** the highlighted front of the card used for AI turns*/
    public ImageIcon front2;
    /** the highlighted back of the card used for AI turns*/
    public ImageIcon back2;
    /** the cards position in the rack, if not in a rack then = -1*/
    public int position = -1;
    /** the size of the deck these cards will be placed in */
	public final int DECK_SIZE;
    
    
     
    /**
     * default constructor
     */
    public Card(int DECK_SIZE)
    {
        this.setMaximumSize(new Dimension(150,100));
        this.setMinimumSize(new Dimension(150,100));
        this.setPreferredSize(new Dimension(150,100));
		this.setSize(150,100);
		this.setPressedIcon(this.getSelectedIcon());
		this.DECK_SIZE = DECK_SIZE;
    }
    
    // constructor with initial value passed as argument
    /**
     * constructor with initial value passed as argument.
     * @param value card's value
     */
    public Card(int value, int DECK_SIZE)
    {
        show = true;
        cameFromMain = false;
        this.value = value;
		this.DECK_SIZE = DECK_SIZE;
		this.setMaximumSize(new Dimension(150,100));
        this.setMinimumSize(new Dimension(150,100));
		this.setPreferredSize(new Dimension(150,100));
        
        BufferedImage image = new BufferedImage(150,100,TYPE_INT_RGB); //need a BufferedImage to handle this
        int xValue;
		if (DECK_SIZE == 40)
			xValue = (value*3);    //this computes the 1st x coordinate of the text that will be inserted onto the card
        else if (DECK_SIZE == 50)
			xValue = (value*2);
		else       //  DECK_SIZE == 60  -DECK for 4 player game
			xValue = (value*2);    //this computes the 1st x coordinate of the text that will be inserted onto the card
        
		
        //Instead of assigning text to a card, I assign an image instead
        try 
        {
            URL url = getClass().getResource("cardfront.jpg");
			//System.out.println(url);
            File file = new File(url.getPath());
            image = ImageIO.read(file); //image now contains the background pic I created
        } 
        catch (IOException e) 
        {
            // do nothing
        }

        //to insert text onto an image, the Graphics API is needed
        Graphics g = image.getGraphics();               //retrieves the image
        Font f = new Font("consolas", Font.BOLD, 12); //custom font that g will be using
        g.setFont(f);                                   //sets the font
        g.setColor(Color.GREEN);                        //sets the color of the font
        g.drawString(Integer.toString(value), xValue, 12); //draws data member, Value, onto the card 
        g.drawImage(image, 0, 0, null);                 //reconstructs image based off of what g contains
        front = new ImageIcon(image);
															//card object has a data member called front
        g.dispose();                                    //we don't need the graphics anymore
		
 
        back = new ImageIcon(getClass().getResource("cardback.jpg"));
		
       
    }
    
    /**
     * Overrides paintCompnent method, depending on if the card is showing or not
     * @param g graphics component.
     */
    @Override
    public void paintComponent(Graphics g) {
        if (show)
            g.drawImage(front.getImage(), TOP, TOP, this);
        else
            g.drawImage(back.getImage(), TOP, TOP, this);
    }   
    
   
    /**
     * basic setter 
     * @param value the value of the card
     */
    public void setValue(int value){
        this.value = value;
        
    }
    
    
    /**
     * basic getter
     * @return the value of the card
     */
    public int getValue(){
        return value;
    }

}