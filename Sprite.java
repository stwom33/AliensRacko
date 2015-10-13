
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.net.URL;


/** 
* base class for loading spritesheets used in animations in AliensRacko
*@author Scott Twombly
*/
public class Sprite {

	private BufferedImage spriteSheet;
	private int TILE_SIZEx;
	private int TILE_SIZEy;
	private String filename;
	//private int selection;

	public Sprite(String file, int tileX, int tileY){
		this.TILE_SIZEx = tileX;
		this.TILE_SIZEy = tileY;
		this.filename = file;
		spriteSheet = loadSprite(filename);

	}
	
	/**
	* loads the spritesheet into a BufferedImage
	*@param file filename of spritesheet
	*/
    public BufferedImage loadSprite(String file) {

        BufferedImage sprite = null;

		
        try {
            sprite = ImageIO.read(getClass().getResource(filename + ".png")); 
        } catch (IOException e) {
			System.err.println("cant get spritesheet");
            //e.printStackTrace();
        }

        return sprite;
    }

	
	/**
	* used in various other sprite classes to get the subImages from a sprite sheet
	*@param xGrid the x value for the "grid of pictures" on the sprite sheet
	*@param yGrid the y value for the "grid of pictures" on the sprite sheet
	*/
    public BufferedImage getSprite(int xGrid, int yGrid) {
		return spriteSheet.getSubimage(xGrid * TILE_SIZEx, yGrid * TILE_SIZEy, TILE_SIZEx, TILE_SIZEy);

	}

}