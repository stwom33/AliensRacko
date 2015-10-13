

import java.awt.Dimension;
import javax.swing.JLayeredPane;

/**
 *  GUI representation of a player's rack. Used in the GUI version of RACKO
 * @author Scott
 */
public class Rack extends JLayeredPane {

    /**
     * Array of card objects
     */
    public Card[] cards;

    /**
     * the player's ID
     */
    public int playerID;

    /**
     * player associated with the rack
     */
    public Player player;

    /**
     * constructor 
     * @param player a rack will be generated for this player
     */
    public Rack(Player player) {
        this.playerID = player.id;
        this.player = player;
        this.setPreferredSize(new Dimension(200, 300));
        this.setBounds(0, 0, 200, 300);
        //System.out.println(this.player.showHand());
        cards = new Card[10];
        int placement = 9;
        
        for (int i = 0; i < 10; i++) {
            
            cards[i] = player.rack.get(i);
			cards[i].setPressedIcon(cards[i].getSelectedIcon());
			//System.out.println(cards[i].value);
            cards[i].setActionCommand("pos" + i);
            cards[i].setBounds((i * 4), 200 - (i * 20), 150, 100);   //225
            
            if (player.showCards) {
                cards[i].show = true;
            } else {
                cards[i].show = false;
            }
       
            cards[i].position = i;
            this.add(cards[i], new Integer(placement));     
            placement--;
        }
		
    }
    
    /**
     * this method remakes the rack. It removes all the cards from the rack and 
     * replaces them with new updated cards.
     */
    public void remakeRack() {
        //System.out.println("remaking rack");
        this.removeAll();
        int placement = 9;
        for (int i = 0; i < 10; i++) {
            
            cards[i] = player.rack.get(i);
            //cards[i].setActionCommand("pos" + i);
            cards[i].setBounds((i * 4), 200 - (i * 20), 150, 100);
            if (this.player.showCards) {
                cards[i].show = true;
            } else {
                cards[i].show = false;
            }
            cards[i].position = i;
            //cards[i].addActionListener(new RackListener());
            this.add(cards[i], new Integer(placement));
            
            placement--;
        }
    }

}
