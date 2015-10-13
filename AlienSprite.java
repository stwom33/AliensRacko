import java.awt.image.BufferedImage;
import java.awt.*;
import javax.swing.*;

/** Aliens animated sprite used in the turnTracker
*@author Scott Twombly
*/
public class AlienSprite extends JLabel {
	private String file = "alienspritesheet";
	private Sprite alienSpriteSheet = new Sprite(file, 65, 54);
	private BufferedImage[] walking = { alienSpriteSheet.getSprite(0, 0), 
										alienSpriteSheet.getSprite(3, 0), 
										alienSpriteSheet.getSprite(2, 0),
										alienSpriteSheet.getSprite(1, 0)};
	private BufferedImage standing = alienSpriteSheet.getSprite(0, 0);
	private BufferedImage jumping = alienSpriteSheet.getSprite(4, 0);
	private ImageIcon current = new ImageIcon(standing);
	private int frame;
	public int startx = 80;  //80
	public int starty = 40;  //80
	private int x = 80;
	private int y = 40;
	private int dx;
	private int ticks = 0;
	public int currentTurn;
	private int PLAYERS;
	
	public AlienSprite(int PLAYERS) {
		super();

		this.PLAYERS = PLAYERS;
		if (PLAYERS == 4) 
			dx = 5;
		if (PLAYERS == 3 || PLAYERS == 2)
			dx = 10;
		this.setSize(65,54);
		this.setLocation(startx, starty);    //startx, starty
		this.setIcon(current);
		this.frame = 1;
		currentTurn = 1;
	}
	
	/** 
	* method called from the applets animation timer, used move the sprite
	* and change the subsprite to the next image
	*/
	public void update() {
	
		if(frame == 1){
			current = new ImageIcon(walking[1]);
			this.setIcon(current);
			x+=dx;
			setLocation(x, y);
			frame++;
		} else if(frame == 2){
			current = new ImageIcon(walking[2]);
			this.setIcon(current);
			x+=dx;
			setLocation(x, y);
			frame++;
		} else if(frame == 3){
			current = new ImageIcon(walking[3]);
			this.setIcon(current);
			x+=dx;
			setLocation(x, y);
			frame++;
		} else if(frame == 4){
			current = new ImageIcon(walking[0]);
			this.setIcon(current);
			x+=dx;
			setLocation(x, y);
			frame = 1;
		}
	
	}
	
	/** 
	* changes the image to the last image on the sprite sheet, where the alien
	* is jumping back to player 1's position
	*/
	public void jump() {
		current = new ImageIcon(jumping);
		this.setIcon(current);
		int halfway = 0;
		//Point currentPOS = this.getLocation();
		if (PLAYERS == 2)
			halfway = 300;
		else
			halfway = 420;
		
		
		if (x >= halfway) {
			//System.out.println("going up: x =" + x);
			x-=15;
			y-=1;
			setLocation(x,y);
		} 
		if (x < halfway) {
			//System.out.println("going down: x =" + x);
			if (x > 80)
				x-=15;
			if (y < 40)
				y+=1;
			setLocation(x,y);
		}
		if (x == 80) {  //alien is back at starting position
			//System.out.println("alien landed");
			//current = new ImageIcon(walking[0]);
			//this.setIcon(current);
		}
	}
	
	/** 
	* resets the sprite to the start position
	*/
	public void reset() {
		current = new ImageIcon(walking[0]);
		this.setIcon(current);
		currentTurn = 1;
		this.setLocation(startx, starty);
		repaint();
	}
}