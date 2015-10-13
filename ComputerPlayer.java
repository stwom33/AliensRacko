
//package racko2;

import java.io.IOException;

/**
 *
 * Class used to store fields and methods associated with Computer Players in
 * RACKO, inherits from Player.java Date Created: 2/17/2014 Last Modified:
 * 2/24/2014 Author: Scott Twombly
 *
 * @author Scott
 */
public class ComputerPlayer extends Player {

    /**
     * current value of discard pile, used in AI decisions
     */
    public int dValue;
    /**
     * value used in the wait methods so AI turns don't happen instantly
     */
    public final int TIME;

    /**
     * flag represents if AI needs a new card in slot 0
     */
    boolean sA = false;
    /**
     * flag represents if AI needs a new card in slot 1
     */
    boolean sB = false;
    /**
     * flag represents if AI needs a new card in slot 2
     */
    boolean sC = false;
    /**
     * flag represents if AI needs a new card in slot 3
     */
    boolean sD = false;
    /**
     * flag represents if AI needs a new card in slot 4
     */
    boolean sE = false;
    /**
     * flag represents if AI needs a new card in slot 5
     */
    boolean sF = false;
    /**
     * flag represents if AI needs a new card in slot 6
     */
    boolean sG = false;
    /**
     * flag represents if AI needs a new card in slot 7
     */
    boolean sH = false;
    /**
     * flag represents if AI needs a new card in slot 8
     */
    boolean sI = false;
    /**
     * flag represents if AI needs a new card in slot 9
     */
    boolean sJ = false;

    /**
     * constructor simply calls super constructor for rack creation and then
     * sets TIME variable, (demoMode has shorter TIME so turns go faster)
     *
     * @param playerNum the ID to be assigned to the player
     * @param main the main deck
     * @param Time time value for simulating AI "thought"
     * @param showCards wether or not to show the cards
     */
    public ComputerPlayer(int playerNum, Deck main, int Time, boolean showCards) {
        super(playerNum, main, showCards);
        TIME = Time;
		isAI = true;
        //GUI_fillRack();
    }

    /**
     * prints a "..." to simulate AI thought
     * @throws InterruptedException 
     */
    private void timeEllipse() throws InterruptedException {
        Thread.sleep(TIME);
        System.out.print(".");
        Thread.sleep(TIME);
        System.out.print(".");
        Thread.sleep(TIME);
        System.out.print(".\n");
    }

    /**
     * generic wait method to keep AI decisions on screen so player is aware of them
     * @throws InterruptedException if thread is interrupted
     */
    public void timeWait() throws InterruptedException {
        Thread.sleep(TIME);
    }

    
    /**
     * AI version of getInput (main or discard). uses discard value (dValue) to
     * determine if the current discarded card can be used. Also sets the various
     * boolean flags if the spots in the rack need "filling"
     * @param dValue values of the card currently on top of discard pile
     * @return char choice (AI choice)
     * @throws IOException
     * @throws InterruptedException 
     */
    public char getInput(int dValue) throws IOException, InterruptedException {
        //System.out.println("getting AI choice");
        System.out.print("AI Deciding to draw from main or discard : ");
        //timeEllipse();
        char choice;

        //sets the value to true if the card value in the spot is between its indicated values
        if (this.rack.get(0).getValue() >= 1 && this.rack.get(0).getValue() <= 4) {
            sA = true;
        }
        if (this.rack.get(1).getValue() >= 5 && this.rack.get(1).getValue() <= 8) {
            sB = true;
        }
        if (this.rack.get(2).getValue() >= 9 && this.rack.get(2).getValue() <= 12) {
            sC = true;
        }
        if (this.rack.get(3).getValue() >= 13 && this.rack.get(3).getValue() <= 16) {
            sD = true;
        }
        if (this.rack.get(4).getValue() >= 17 && this.rack.get(4).getValue() <= 20) {
            sE = true;
        }
        if (this.rack.get(5).getValue() >= 21 && this.rack.get(5).getValue() <= 24) {
            sF = true;
        }
        if (this.rack.get(6).getValue() >= 25 && this.rack.get(6).getValue() <= 28) {
            sG = true;
        }
        if (this.rack.get(7).getValue() >= 29 && this.rack.get(7).getValue() <= 32) {
            sH = true;
        }
        if (this.rack.get(8).getValue() >= 33 && this.rack.get(8).getValue() <= 36) {
            sI = true;
        }
        if (this.rack.get(9).getValue() >= 37 && this.rack.get(9).getValue() <= 40) {
            sJ = true;
        }

        //checks to see if the card on the discard pile can be used
        this.dValue = dValue;
        if (dValue >= 1 && dValue <= 4 && !sA) {
            choice = 'd';
        } else if (dValue >= 5 && dValue <= 8 && !sB) {
            choice = 'd';
        } else if (dValue >= 9 && dValue <= 12 && !sC) {
            choice = 'd';
        } else if (dValue >= 13 && dValue <= 16 && !sD) {
            choice = 'd';
        } else if (dValue >= 17 && dValue <= 20 && !sE) {
            choice = 'd';
        } else if (dValue >= 21 && dValue <= 24 && !sF) {
            choice = 'd';
        } else if (dValue >= 25 && dValue <= 28 && !sG) {
            choice = 'd';
        } else if (dValue >= 29 && dValue <= 32 && !sH) {
            choice = 'd';
        } else if (dValue >= 33 && dValue <= 36 && !sI) {
            choice = 'd';
        } else if (dValue >= 37 && dValue <= 40 && !sJ) {
            choice = 'd';
        } else {
            choice = 'm';
        }
        if (choice == 'm') {
            System.out.println("AI Decided to draw from main deck.");
            //timeWait();
        } else {
            System.out.println("AI Decided to draw from discard deck.");
            //timeWait();
        }
        return choice;
    }

    
    /**
     * AI version of getSwapInput(), finds the spot on the rack the temp Card 
     * (card in hand) needs to go by looking at its value and the boolean flags.
     * @param temp current card passed to it, used in decision making
     * @return char choice (ai choice)
     * @throws IOException
     * @throws InterruptedException 
     */
    public char getSwapInput(Card temp) throws IOException, InterruptedException {
        char choice = ' ';
        System.out.print("AI Deciding card placement " + temp.getValue());
        //timeEllipse();
        if (temp.getValue() >= 1 && temp.getValue() <= 4) {
            choice = 'A';
        }
        if (temp.getValue() >= 5 && temp.getValue() <= 8) {
            choice = 'B';
        }
        if (temp.getValue() >= 9 && temp.getValue() <= 12) {
            choice = 'C';
        }
        if (temp.getValue() >= 13 && temp.getValue() <= 16) {
            choice = 'D';
        }
        if (temp.getValue() >= 17 && temp.getValue() <= 20) {
            choice = 'E';
        }
        if (temp.getValue() >= 21 && temp.getValue() <= 24) {
            choice = 'F';
        }
        if (temp.getValue() >= 25 && temp.getValue() <= 28) {
            choice = 'G';
        }
        if (temp.getValue() >= 29 && temp.getValue() <= 32) {
            choice = 'H';
        }
        if (temp.getValue() >= 33 && temp.getValue() <= 36) {
            choice = 'I';
        }
        if (temp.getValue() >= 37 && temp.getValue() <= 40) {
            choice = 'J';
        }
        System.out.println("AI is placing the card at: " + choice);

        // calls timewait 4 times, seemed like a good amount of time for the 
        // human player to "catch up"
       // timeWait();
      //  timeWait();
        //timeWait();
       // timeWait();
        return choice;
    }

   
    /**
     * AI version of getKeepOrNot(), determines if the card drawn from the main
     * pile can be used, discards it if not.
     * @param temp card in hand
     * @return  cahr chocie (AI choice)
     * @throws IOException
     * @throws InterruptedException 
     */
    public char getKeepOrNot(Card temp) throws IOException, InterruptedException {
        current = temp;
        int cardValue = temp.getValue();
        char choice;
        System.out.print("AI Deciding to Keep or Not: ");
       // timeEllipse();

        if (cardValue >= 1 && cardValue <= 4 && !sA) {
            choice = 'K';
        } else if (cardValue >= 5 && cardValue <= 8 && !sB) {
            choice = 'K';
        } else if (cardValue >= 9 && cardValue <= 12 && !sC) {
            choice = 'K';
        } else if (cardValue >= 13 && cardValue <= 16 && !sD) {
            choice = 'K';
        } else if (cardValue >= 17 && cardValue <= 20 && !sE) {
            choice = 'K';
        } else if (cardValue >= 21 && cardValue <= 24 && !sF) {
            choice = 'K';
        } else if (cardValue >= 25 && cardValue <= 28 && !sG) {
            choice = 'K';
        } else if (cardValue >= 29 && cardValue <= 32 && !sH) {
            choice = 'K';
        } else if (cardValue >= 33 && cardValue <= 36 && !sI) {
            choice = 'K';
        } else if (cardValue >= 37 && cardValue <= 40 && !sJ) {
            choice = 'K';
        } else {
            choice = 'D';
        }
        if (choice == 'D') {
            System.out.println("AI chose to discard.");
            //timeWait();
        } else {
            System.out.println("AI chose to keep.");
            //timeWait();
        }

        return choice;
    }
	
	/** resets the boolean flags representing the various AI slots.
	* is called during a new game setup
	*/
	public void resetRackFlags() {
		sA = sB = sC = sD = sE = sF = sG = sH = sI = sJ = false;
	}

}
