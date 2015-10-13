import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.File;
import java.awt.*;
import javax.swing.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.*;
import java.awt.image.AffineTransformOp;


public class FaceHuggerSprite extends JLabel {
	private String file = "facehuggerspritesheet";
	private String file2 = "jumper";
	private String file3 = "youlose";
	
	private Sprite facehuggerspritesheet = new Sprite(file, 80, 20);
	private Sprite jumper = new Sprite(file2, 800, 600);
	private Sprite youlosesheet = new Sprite(file3, 800, 600);
	private BufferedImage[] walking = { facehuggerspritesheet.getSprite(0, 0), 
										facehuggerspritesheet.getSprite(1, 0), 
										facehuggerspritesheet.getSprite(2, 0),
										facehuggerspritesheet.getSprite(3, 0),
										facehuggerspritesheet.getSprite(0, 1),
										facehuggerspritesheet.getSprite(1, 1),
										facehuggerspritesheet.getSprite(2, 1),
										facehuggerspritesheet.getSprite(3, 1)};
	private BufferedImage standing = facehuggerspritesheet.getSprite(0,0);
	private BufferedImage[] jumping = { jumper.getSprite(0, 0),
										jumper.getSprite(1, 0),
										jumper.getSprite(2, 0),
										jumper.getSprite(3, 0),
										jumper.getSprite(0, 1),
										jumper.getSprite(1, 1),
										jumper.getSprite(2, 1),
										jumper.getSprite(3, 1)};
	private BufferedImage loser = youlosesheet.getSprite(0, 0);
	private ImageIcon current = new ImageIcon(standing);
	private int frame;
	public int startx = 100;  //80
	public int starty = 300;  //80
	private int x = 100;
	private int y = 300;
	private int dx;
	private int zoom = 0;
	private int ticks = 0;
	public int currentTurn;
	private int PLAYERS;
	
	public FaceHuggerSprite() {
		super();

		this.dx = 10;
		this.setSize(80,20);
		this.setLocation(startx, starty);    //startx, starty
		this.setIcon(current);
		this.frame = 0;

	}
	
	/** updates the image to make it appear as if the face hugger
	* is walking to the right
	*/
	public void update() {
	
		if(frame == 0){
			current = new ImageIcon(walking[0]);
			this.setIcon(current);
			x+=dx;
			setLocation(x, y);
			frame++;
		} else if(frame == 1){
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
			current = new ImageIcon(walking[4]);
			this.setIcon(current);
			x+=dx;
			setLocation(x, y);
			frame++;
		} else if(frame == 5){
			current = new ImageIcon(walking[5]);
			this.setIcon(current);
			x+=dx;
			setLocation(x, y);
			frame++;
		} else if(frame == 6){
			current = new ImageIcon(walking[6]);
			this.setIcon(current);
			x+=dx;
			setLocation(x, y);
			frame++;
		} else if(frame == 7){
			current = new ImageIcon(walking[7]);
			this.setIcon(current);
			x+=dx;
			setLocation(x, y);
			frame = 1;
		}
	
	}
	
	/** induces the zooming effect at the lose game screen
	* where the facehugger jumps at the player before displaying
	* YOU LOSE
	*/
	public void jump() {
		
		this.setSize(800, 600);
		this.setLocation(0,0);
		if(zoom == 0){
			current = new ImageIcon(jumping[0]);
			this.setIcon(current);
			zoom++;
		} else if(zoom == 1){
			current = new ImageIcon(jumping[1]);
			this.setIcon(current);
			zoom++;
		} else if(zoom == 2){
			current = new ImageIcon(jumping[2]);
			this.setIcon(current);
			zoom++;
		} else if(zoom == 3){
			current = new ImageIcon(jumping[3]);
			this.setIcon(current);
			zoom++;
		} else if(zoom == 4){
			current = new ImageIcon(jumping[4]);
			this.setIcon(current);
			zoom++;
		} else if(zoom == 5){
			current = new ImageIcon(jumping[5]);
			setLocation(x, y);
			zoom++;
		} else if(zoom == 6){
			current = new ImageIcon(jumping[6]);
			this.setIcon(current);
			zoom++;
		} else if(zoom == 7){
			current = new ImageIcon(jumping[7]);
			this.setIcon(current);
			zoom++;
		} else if(zoom == 8){
			current = new ImageIcon(loser);
			this.setIcon(current);
			zoom = 1;
		}
	}
	
	public void reset() {
	
	}
}