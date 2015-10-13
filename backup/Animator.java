import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Animator implements ActionListener {
	
	private int startPOS;
	private int stepDistance;
	private Timer timer = new Timer(100, this);
	private int PLAYERS;
	private AlienSprite alien;
	
	public Animator(AlienSprite alien, int PLAYERS) {
		this.alien = alien;
		this.PLAYERS = PLAYERS;
		
		//TODO calculate step distance and starting position based on PLAYERS
		
	}
	
	
	public void actionPerformed(ActionEvent e) {
		System.out.println("hello from actionperformed");
		alien.update();
		alien.repaint();
	}
	
	
	public void run() {
		System.out.println("hello from doRun");
		for (int i = 0; i<50; i++) {
			alien.update();
			try{
			Thread.sleep(10);
			} catch (InterruptedException ex) {
				System.err.println(ex.getMessage());
			}
		}
	}
	
	public void nextTurn() {
		
	}
	
	public void jumpBack() {
		
	}

}