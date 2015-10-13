
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.border.TitledBorder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.awt.image.BufferedImage;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import java.io.* ;
import java.net.URL;
import java.net.*;
import javax.imageio.ImageIO;
import javax.swing.* ;
import java.applet.*;
import java.util.Collections;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;


/** AliensRacko is the main applet code for building the 
* AliensRacko GUI and handling all the major input from
* players in the form of custom action listener classes
*@author Scott Twombly
**/
public class AliensRacko extends JApplet{
	/** main JPanel used in AliensRacko, uses card Layout, holds all other panels */
	private JPanel backBoard;
	/** represents the title screen where players can configure and start the game */
	private JPanel startScreen;
	/** represents the main game screen, where aliensRacko is actually played */
	private JPanel gameScreen;
	/** multiplayer game screen*/
	private JPanel MPGameScreen;
	/** lobby panel for setting up MP game */
	private JPanel lobby;
	/** represents the end game screen */
	private JPanel endScreen;
	/** middle game panel, holds decks */
	public JPanel midGameP;
	/** animated alien in turn tracker */
	public AlienSprite alien;
	/** animated facehugger when you lose */
	public FaceHuggerSprite faceHugger;
	/** a flag to determine if in singleplayer mode*/
	private Boolean singlePlayer;
	
	/** button used on win/end screen*/
	private JButton playAgain;
	/** locally stored "player number", aligns with player array*/
	private int clientID;
	/** socket used for server communication */
	private Socket socket = null;
	/** outgoing printwriter to server */
	private PrintWriter out = null;
	/** incoming buffered reader from server */
	private BufferedReader in = null;
	/** connected laberl for ripley */
	private JLabel ripleyCON;
	/** connected label for hicks */
	private JLabel hicksCON;
	/** connected label for hudson */
	private JLabel hudsonCON;
	/** connected label for sarge */
	private JLabel sargeCON;
	/** start button in multiplayer lobby */
	private JButton startMP;
	/** determines which player's rack blink */
	private int rackToBlink;
	/** thread for the turn listener */
	private Thread turnListenerThread;
	
	/** used to control alien tracker animation */
	private Timer turnTimer;
	/** used to blink cards when other players make their move */
	private Timer cblinker;
	/** used to blink decks when other players make their move */
	private Timer dblinker;
	/** used to blink the main deck when AI uses it */
	private Timer mblinker;
	/** used to slow down AI */
	private Timer pauser;
	/** animator timer for losing screen */
	private Timer faceHuggerTimer;
	
	
	/** used to save the old border before changing it to green */
	private Border oldBorderCard;
	/** used to save the old border before changing it to green */
	private Border oldBorderDeck;
	/** so cblinker knows the position in the rack to blink */
	private int toBeBlinked;
	/** so dblinker knows which deck to blink */
	private Deck deckToBlink;
	
	
	//stuff for the startScreen:
	/** hidden cheat "button" */
	private JPanel hiddenCheat;
	/** this is the panel with the hidden cheats on it */
	private JPanel cheatPanel;
	/** text field used to retrieve goal score */
	private JTextField goalScoreTextField;
	/** text field used to grab pre-placed cards */
	private JTextField prePlaceTF;
	/** panel that holds the goal score, dissapears if single round game*/
	private JPanel goalScorePanel;
	/** radiobutton for multiround game: yes*/
	private JRadioButton myes;
	/** radiobutton for multiround game: no*/
	private JRadioButton mno;
	/** radiobutton for demo game: yes*/
	private JRadioButton dyes;
	/** radiobutton for demo game: no*/
	private JRadioButton dno;
	/** radiobutton for view mode game: yes*/
	private JRadioButton vyes;
	/** radiobutton for view mode game: no*/
	private JRadioButton vno;
	
	/** radiobutton for sort mode : yes*/
	private JRadioButton syes;
	/** radiobutton for sort mode : no */
	private JRadioButton sno;
	/** boolean value for sortMode cheat */
	private boolean sortMode = false;
	/** boolean for demoMode used in setting up the game*/
	private boolean demoMode = false;
	/** boolean for viewMode used in setting up the game*/
	private boolean viewMode = false;
	/** boolean for place mode, used in setting up the game*/
	private boolean placeMode = false;
	/** holds pre place arguments from prePlaceTF */
	private String prePlaceString;
	
	/** the size of the main deck, changes depending on number of players */
	private int DECK_SIZE;
	/** used to determine if the current game ended*/
	private boolean gameOver = false;
	/** number of the game currently being played*/
	private int gameCount = 0;
	/** target number of points to win the game */
	private int goalScore = 75;
	/** the main playing deck object */
	private Deck main;
	/** the discard playing deck object */
	private Deck discard;
	/** main array to hold player objects, where index = playerID */
	private Player[] player;
	/** array to hold rack objects, where index = playerID */
	private Rack[] rack;
	/** final int to hold number of players playing */
	private int PLAYERS;
	/** random seed used in shuffling main deck */
	private long seed;
	 
	/**Listener for discard pile*/
    private DiscardListener dl;
    /**Listener for main pile*/
    private MainListener ml;
    /**Listener for cards in rack*/
    private RackListener[] rl;
	/**Listener for cards in rack*/
    private RackListener[] r2;
	/**Listener for cards in rack*/
    private RackListener[] r3;
	/**Listener for cards in rack*/
    private RackListener[] r4;
	/** Listener for the AI rack */
	private AIRackListener[] AIrl;
	/** listener for AI rack in demo mode */
	private AIRackListener[] AIrl2;
    
	private JPanel losegamePanel;
	/** used in singleplayer to display current player */
	private JLabel[] turnLabels = new JLabel[2];
	/** not used in this version of racko */
	private String lastMove = "";
	/** updating score labels for multiplayer mode */
	private JLabel[] scoreLabel = new JLabel[4];
	
	/** url for the noise that is player at the end of a player's turn */
	private URL alienNoise = getClass().getResource("aliennoise.wav");
	/** url for the noise at the lose game screen (currently not working) */
	private URL gameOverNoise = getClass().getResource("gameover.wav");
	
	
	/** init method inherited from JApplet, calls buildStartScreen and adds
	* startScreen to itself.
	*/
	public void init(){
		
		buildStartScreen();
		add(startScreen);
		
	}
	
	/**
	* this method initializes all the game items, Player objects,
	* Deck objects, Rack objects, and calls the various methods to
	* build the UI
	*/
	private void gameSetup(){
		gameCount++;
		PLAYERS = 2;
		singlePlayer = true;
		Random rand = new Random();
        seed = rand.nextLong();
		
		
		//set up listeners
		ml = new MainListener();
        dl = new DiscardListener();
        rl = new RackListener[10];

		AIrl = new AIRackListener[10];
		AIrl2 = new AIRackListener[10];

		//initialize animation timers and blinkers
		cblinker = new javax.swing.Timer(100, new CardBlinker());
		dblinker = new javax.swing.Timer(100, new DeckBlinker());
		mblinker = new Timer(100, new MainBlinker());
		pauser = new Timer(3000, new Pauser());
		pauser.setRepeats(false);
		
		//pre-place initialization, make array list of cards
		if (placeMode) {
			demoMode = false;
			 
			String delims = "[ ,]+";
			String[] prePlaceTokens = prePlaceString.split(delims);
			int[] prePlace = new int[prePlaceTokens.length];
			int j = 0;
			ArrayList<Card> cardsToPlace = new ArrayList<Card>();
			System.out.print("pre placing: ");
			for (String tokens : prePlaceTokens) {
				prePlace[j] = Integer.parseInt(tokens);
				System.out.print(prePlace[j] + " ");
				cardsToPlace.add(new Card(prePlace[j], DECK_SIZE));
				j++;
			}
			System.out.println(" ");
			//set up deck for preplace mode:
			main = new Deck(DECK_SIZE, cardsToPlace);
			//set up players for preplace mode:
			player = new Player[PLAYERS];
			player[0] = new HumanPlayer(0, main, true, cardsToPlace);
			player[0].setIsTurn(true);
			if (viewMode)
				player[1] = new ComputerPlayer(1, main, 500, true);
			else
				player[1] = new ComputerPlayer(1, main, 500, false);
			player[1].setIsTurn(false);
			//main.print();
		}
		
		if (demoMode) {
			viewMode = false;
			//initialize players
			player = new Player[PLAYERS];
			//set up main deck
			main = new Deck(DECK_SIZE, seed);
			//set up players
			player[0] = new ComputerPlayer(0, main, 500, true);
			player[0].setIsTurn(true);
			player[1] = new ComputerPlayer(1, main, 500, true);
			player[1].setIsTurn(false);
			//initialize racks
			rack = new Rack[PLAYERS];
			rack[0] = new Rack(player[0]);
			for (int i = 0; i < 10; i++) {
				AIrl2[i] = new AIRackListener();
				rack[0].cards[i].addActionListener(AIrl2[i]);
			}
			rack[1] = new Rack(player[1]);
			for (int i = 0; i < 10; i++) {
				AIrl[i] = new AIRackListener();
				rack[1].cards[i].addActionListener(AIrl[i]);
			}
			
		}
		
		if (!placeMode && !demoMode) {
			//initialize players
			player = new Player[PLAYERS];
			//set up main deck
			main = new Deck(DECK_SIZE, seed);
			//set up players
			player[0] = new HumanPlayer(0, main, true);
			player[0].setIsTurn(true);
			if (viewMode)
				player[1] = new ComputerPlayer(1, main, 500, true);
			else
				player[1] = new ComputerPlayer(1, main, 500, false);
			player[1].setIsTurn(false);
		}
	
		if (!demoMode) {
			//initialize racks
			rack = new Rack[PLAYERS];
			rack[0] = new Rack(player[0]);
			for (int i = 0; i < 10; i++) {
				rl[i] = new RackListener();
				rack[0].cards[i].addActionListener(rl[i]);
			}
			rack[1] = new Rack(player[1]);
			for (int i = 0; i < 10; i++) {
				AIrl[i] = new AIRackListener();
				rack[1].cards[i].addActionListener(AIrl[i]);
			}
		}
		
		//set up default deck border
		oldBorderDeck = main.getBorder();
		
		//set up discard
		discard = new Deck('d');
		discard.pile.add(main.pile.pop());
		//System.out.println(discard.pile.peek().value);
		
	}
	
	/**
	* main function used to set up a multiplayer game. gets executed after
	* the host player in the lobby starts the game, uses seed passed from
	* server
	*/
	private void MPGameSetup() {
		gameCount++;
		singlePlayer = false;
		
		//deck blinker timers
		cblinker = new javax.swing.Timer(100, new CardBlinker());
		dblinker = new javax.swing.Timer(100, new DeckBlinker());
		
		//setup main deck:
		if (PLAYERS == 4)
			DECK_SIZE = 60;
		if (PLAYERS == 3)
			DECK_SIZE = 50;
		if (PLAYERS == 2)
			DECK_SIZE = 40;
		main = new Deck(DECK_SIZE, seed);
		ml = new MainListener();
		main.addActionListener(ml);
        
		
		//init players and racks
		player = new Player[PLAYERS];
		rack = new Rack[PLAYERS];
		for (int i = 0; i < PLAYERS; i++){
			if (i == clientID) {
				player[i] = new HumanPlayer(i, main, true);
			} else {
				if (viewMode) {
					player[i] = new HumanPlayer(i, main, true);
				}else{
					player[i] = new HumanPlayer(i, main, false);
				}
			}
			player[i].setIsTurn(false);
			rack[i] = new Rack(player[i]);
		}
		player[0].setIsTurn(true);
		
		
		//TODO make this only happen to clientID player
		rl = new RackListener[10];
		for (int i = 0;i<10;i++) {
			rl[i] = new RackListener();
			rack[clientID].cards[i].addActionListener(rl[i]);
		}
		
		//set up default deck border
		oldBorderDeck = main.getBorder();
		
		//discard deck
		discard = new Deck('d');
		discard.pile.add(main.pile.pop());
		dl = new DiscardListener();
		discard.addActionListener(dl);
		
	}
	
	
	/**
	* builds the intro screen, pastes it on the
	* backBoard panel includes cheat menu
	*/
	private void buildStartScreen(){
		
		//used for filler dimensions
		Dimension minSize = new Dimension(5, 10);
		Dimension prefSize = new Dimension(5, 10);
		Dimension maxSize = new Dimension(Short.MAX_VALUE, 10);
		
		
		//main panel that gets added to backboard
		startScreen = new JPanel(null);
		startScreen.setSize(800,600);
		startScreen.setBackground(Color.BLACK);
		
		//background image
		JLabel aliensBG = new JLabel();
		aliensBG.setSize(800, 600);
		ImageIcon aliensIMG = new ImageIcon(getClass().getResource("aliensStart.jpg"));
		aliensBG.setIcon(aliensIMG);
		aliensBG.setText(null);
		
		
		
		//start menu:
		JPanel startMenu = new JPanel();
		startMenu.setBackground(Color.BLACK);
		startMenu.setLocation(5, 300);
		startMenu.setSize(350, 395);
		startMenu.setLayout(new BoxLayout(startMenu, BoxLayout.Y_AXIS));
		
		
		
		
		//multiround and goalscore selection
		JLabel multiRoundLabel = new JLabel("Multiple Rounds:");
		multiRoundLabel.setBackground(Color.BLACK);
		multiRoundLabel.setForeground(Color.GREEN);
		multiRoundLabel.setFont(new Font("consolas",Font.PLAIN,16));
		multiRoundLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		startMenu.add(multiRoundLabel);
		//multiround radiobutton panel:
		JPanel multiButtonPanel = new JPanel(new FlowLayout());
		multiButtonPanel.setMaximumSize(new Dimension(200, 30));
		multiButtonPanel.setBackground(Color.BLACK);
		//yes and no buttons:
		myes = new JRadioButton("Yes", true);
		myes.setFont(new Font("consolas",Font.PLAIN,12));
		myes.setBackground(Color.BLACK);
		myes.setForeground(Color.GREEN);
		myes.addActionListener(new RadioButtonListener());
		mno = new JRadioButton("No");
		mno.setFont(new Font("consolas",Font.PLAIN,12));
		mno.setBackground(Color.BLACK);
		mno.setForeground(Color.GREEN);
		mno.addActionListener(new RadioButtonListener());
		//add to buttons to button group
		ButtonGroup multiGroup = new ButtonGroup();
		multiGroup.add(myes);
		multiGroup.add(mno);
		//add buttons to button panel
		multiButtonPanel.add(myes);
		multiButtonPanel.add(mno);
		startMenu.add(multiButtonPanel);
		//goal score panel, label, and text field
		goalScorePanel = new JPanel(new FlowLayout());
		goalScorePanel.setBackground(Color.BLACK);
		goalScorePanel.setForeground(Color.GREEN);
		goalScorePanel.setFont(new Font("consolas",Font.PLAIN,16));
		goalScorePanel.setMaximumSize(new Dimension(200, 50));
		JLabel goalScoreLabel = new JLabel("Goal Score:");
		goalScoreLabel.setBackground(Color.BLACK);
		goalScoreLabel.setForeground(Color.GREEN);
		goalScoreLabel.setFont(new Font("consolas",Font.PLAIN,16));
		goalScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		//add label to panel
		goalScorePanel.add(goalScoreLabel);
		goalScoreTextField = new JTextField(5);
		goalScoreTextField.setMaximumSize(new Dimension(25, 15));
		goalScoreTextField.setText("250");
		//add textfield to panel
		goalScorePanel.add(goalScoreTextField);
		//add to startMenu
		startMenu.add(goalScorePanel);
		
		
		
		//demo mode option pane
		JPanel demoPanel = new JPanel(new FlowLayout());
		demoPanel.setBackground(Color.BLACK);
		demoPanel.setForeground(Color.GREEN);
		demoPanel.setFont(new Font("consolas",Font.PLAIN,16));
		demoPanel.setMaximumSize(new Dimension(200, 50));
		JLabel demoLabel = new JLabel("Demo Mode:");
		demoLabel.setBackground(Color.BLACK);
		demoLabel.setForeground(Color.GREEN);
		demoLabel.setFont(new Font("consolas",Font.PLAIN,16));
		dyes = new JRadioButton("Yes");
		dyes.setFont(new Font("consolas",Font.PLAIN,12));
		dyes.setBackground(Color.BLACK);
		dyes.setForeground(Color.GREEN);
		dyes.addActionListener(new RadioButtonListener());
		dno = new JRadioButton("No", true);
		dno.setFont(new Font("consolas",Font.PLAIN,12));
		dno.setBackground(Color.BLACK);
		dno.setForeground(Color.GREEN);
		dno.addActionListener(new RadioButtonListener());
		ButtonGroup demoGroup = new ButtonGroup();
		demoGroup.add(dyes);
		demoGroup.add(dno);
		//add demo stuff to demo panel:
		demoPanel.add(demoLabel);
		demoPanel.add(dyes);
		demoPanel.add(dno);
		startMenu.add(demoPanel);
		//start game button
		JButton start = new JButton("Single Player");
		start.addActionListener(new StartListener());
		start.setActionCommand("START");
		start.setAlignmentX(Component.CENTER_ALIGNMENT);
		startMenu.add(start);
		//filler for better look and feel
		startMenu.add(new Box.Filler(minSize, prefSize, maxSize));
		
		//multi-player button
		JButton multiP = new JButton("Multi-Player");
		multiP.addActionListener(new StartListener());
		multiP.setActionCommand("MP");
		multiP.setAlignmentX(Component.CENTER_ALIGNMENT);
		startMenu.add(multiP);
		
		aliensBG.add(startMenu);
		
		
		//hidden cheat button
		hiddenCheat = new JPanel();
		hiddenCheat.setBackground(Color.BLACK);
		hiddenCheat.setSize(15,15);
		hiddenCheat.setLocation(600,235);

		hiddenCheat.addMouseListener(new MouseAdapter()
			{
				public void mouseClicked(MouseEvent ME) {
					System.out.println("You found the cheats!");
					hiddenCheat.setVisible(false);
					cheatPanel.setVisible(true);
				}
			});
		aliensBG.add(hiddenCheat);
		
		//cheat panel:
		cheatPanel = new JPanel();
		cheatPanel.setLayout(new BoxLayout(cheatPanel, BoxLayout.Y_AXIS));
		cheatPanel.setSize(200, 350);
		cheatPanel.setLocation(510, 137);
		cheatPanel.setBackground(Color.DARK_GRAY);
		TitledBorder titleCP = BorderFactory.createTitledBorder(null, "CHEATS", 
															TitledBorder.CENTER, 
															TitledBorder.TOP, 
															new Font("consolas",Font.PLAIN,12), 
															Color.GREEN);
		titleCP.setTitleJustification(TitledBorder.CENTER);
		cheatPanel.setBorder(titleCP);
		cheatPanel.setVisible(false);
		//filler label for better look and feel:
		minSize = new Dimension(5, 50);
		prefSize = new Dimension(5, 50);
		maxSize = new Dimension(Short.MAX_VALUE, 50);
		cheatPanel.add(new Box.Filler(minSize, prefSize, maxSize));
		//label for pre place mode
		JLabel placeCardsLabel = new JLabel("List cards to pre-place in Rack:");
		placeCardsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		placeCardsLabel.setBackground(Color.DARK_GRAY);
		placeCardsLabel.setForeground(Color.BLACK);
		placeCardsLabel.setFont(new Font("consolas",Font.PLAIN,12));
		cheatPanel.add(placeCardsLabel);
		//textfield for pre place mode:
		prePlaceTF = new JTextField(25);
		prePlaceTF.setMaximumSize(new Dimension(350, 20));
		prePlaceTF.setAlignmentX(Component.CENTER_ALIGNMENT);
		cheatPanel.add(prePlaceTF);
		//filler label for better look and feel:
		cheatPanel.add(new Box.Filler(minSize, prefSize, maxSize));
		//Label for Multiplayer cheats
		JLabel mpCheatsL = new JLabel("Multiplayer Compatible:");
		mpCheatsL.setAlignmentX(Component.CENTER_ALIGNMENT);
		mpCheatsL.setForeground(Color.BLACK);
		mpCheatsL.setBackground(Color.DARK_GRAY);
		mpCheatsL.setFont(new Font("consolas",Font.PLAIN,12));
		cheatPanel.add(mpCheatsL);
		//label for view mode:
		JLabel viewLabel = new JLabel("See Opponent's Rack?");
		viewLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		viewLabel.setBackground(Color.DARK_GRAY);
		viewLabel.setForeground(Color.BLACK);
		viewLabel.setFont(new Font("consolas",Font.PLAIN,12));
		cheatPanel.add(viewLabel);
		//radio buttons for yes/no
		vyes = new JRadioButton("Yes");
		vyes.setFont(new Font("consolas",Font.PLAIN,12));
		vyes.setBackground(Color.DARK_GRAY);
		vyes.setForeground(Color.BLACK);
		vyes.addActionListener(new RadioButtonListener());
		vno = new JRadioButton("No", true);
		vno.setFont(new Font("consolas",Font.PLAIN,12));
		vno.setBackground(Color.DARK_GRAY);
		vno.setForeground(Color.BLACK);
		vno.addActionListener(new RadioButtonListener());
		ButtonGroup viewGroup = new ButtonGroup();
		viewGroup.add(vyes);
		viewGroup.add(vno);
		JPanel viewButtonPanel = new JPanel();
		viewButtonPanel.setLayout(new FlowLayout());
		viewButtonPanel.setBackground(Color.DARK_GRAY);
		viewButtonPanel.add(vyes);
		viewButtonPanel.add(vno);
		cheatPanel.add(viewButtonPanel);
		//label for sort cheat
		JLabel sortLabel = new JLabel("Sort rack by pressing 's'");
		sortLabel.setFont(new Font("consolas", Font.PLAIN, 12));
		sortLabel.setBackground(Color.DARK_GRAY);
		sortLabel.setForeground(Color.BLACK);
		sortLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		cheatPanel.add(sortLabel);
		//radio buttons for yes/no
		syes = new JRadioButton("Yes");
		syes.setFont(new Font("consolas",Font.PLAIN,12));
		syes.setBackground(Color.DARK_GRAY);
		syes.setForeground(Color.BLACK);
		syes.addActionListener(new RadioButtonListener());
		sno = new JRadioButton("No", true);
		sno.setFont(new Font("consolas",Font.PLAIN,12));
		sno.setBackground(Color.DARK_GRAY);
		sno.setForeground(Color.BLACK);
		sno.addActionListener(new RadioButtonListener());
		ButtonGroup sortGroup = new ButtonGroup();
		sortGroup.add(syes);
		sortGroup.add(sno);
		JPanel sortButtonPanel = new JPanel();
		sortButtonPanel.setLayout(new FlowLayout());
		sortButtonPanel.setBackground(Color.DARK_GRAY);
		sortButtonPanel.add(syes);
		sortButtonPanel.add(sno);
		cheatPanel.add(sortButtonPanel);
		
		//add cheat panel to aliensBG
		aliensBG.add(cheatPanel);
		
		
		startScreen.add(aliensBG);
		
	}
	
	/** builds the multiplayer lobby, uses connect() function to get
	* seed info and clientID from server then starts the waitForOthers 
	* thread
	*/
	private void buildLobby() {
		seed = connect();
		
		//lobby background
		System.out.print("Building lobby screen...");
		lobby = new JPanel();
		lobby.setSize(800, 600);
		lobby.setBackground(Color.BLACK);
		lobby.setForeground(Color.GREEN);
		lobby.setLayout(new BorderLayout());
		//player panel
		JPanel playerPanel = new JPanel();
		playerPanel.setLayout(new FlowLayout());
		playerPanel.setBackground(Color.BLACK);
		//player 1 (ripley)
		JPanel ripleyPanel = new JPanel();
		ripleyPanel.setBackground(Color.BLACK);
		ripleyPanel.setLayout(new BoxLayout(ripleyPanel, BoxLayout.Y_AXIS));
		ripleyPanel.setSize(200,200);
		ImageIcon ripleyID = new ImageIcon(getClass().getResource("ripley.png"));
		JLabel ripleyIDLabel = new JLabel(ripleyID);
		ripleyIDLabel.setLocation(50, 100);
		ripleyPanel.add(ripleyIDLabel);
		ripleyCON = new JLabel("Connected");
		ripleyCON.setBackground(Color.BLACK);
		ripleyCON.setForeground(Color.GREEN);
		ripleyCON.setFont(new Font("consolas", Font.BOLD, 12));
		ripleyPanel.add(ripleyCON);
		playerPanel.add(ripleyPanel);
		//player 2 (hicks)
		JPanel hicksPanel = new JPanel();
		hicksPanel.setBackground(Color.BLACK);
		hicksPanel.setLayout(new BoxLayout(hicksPanel, BoxLayout.Y_AXIS));
		hicksPanel.setSize(200,200);
		ImageIcon hicksID = new ImageIcon(getClass().getResource("hicks.png"));
		JLabel hicksIDLabel = new JLabel(hicksID);
		hicksIDLabel.setLocation(50,100);
		hicksPanel.add(hicksIDLabel);
		hicksCON = new JLabel("Not Connected");
		hicksCON.setBackground(Color.BLACK);
		hicksCON.setForeground(Color.RED);
		hicksCON.setFont(new Font("consolas", Font.BOLD, 12));
		hicksPanel.add(hicksCON);
		playerPanel.add(hicksPanel);
		//player 3 (hudson)
		JPanel hudsonPanel = new JPanel();
		hudsonPanel.setBackground(Color.BLACK);
		hudsonPanel.setLayout(new BoxLayout(hudsonPanel, BoxLayout.Y_AXIS));
		hudsonPanel.setSize(200,200);
		ImageIcon hudsonID = new ImageIcon(getClass().getResource("hudson.png"));
		JLabel hudsonIDLabel = new JLabel(hudsonID);
		hudsonIDLabel.setLocation(50,100);
		hudsonPanel.add(hudsonIDLabel);
		hudsonCON = new JLabel("Not Connected");
		hudsonCON.setBackground(Color.BLACK);
		hudsonCON.setForeground(Color.RED);
		hudsonCON.setFont(new Font("consolas", Font.BOLD, 12));
		hudsonPanel.add(hudsonCON);
		playerPanel.add(hudsonPanel);
		//player 4 (sarge)
		JPanel sargePanel = new JPanel();
		sargePanel.setBackground(Color.BLACK);
		sargePanel.setLayout(new BoxLayout(sargePanel, BoxLayout.Y_AXIS));
		sargePanel.setSize(200,200);
		ImageIcon sargeID = new ImageIcon(getClass().getResource("sarge.png"));
		JLabel sargeIDLabel = new JLabel(sargeID);
		sargeIDLabel.setLocation(50,100);
		sargePanel.add(sargeIDLabel);
		sargeCON = new JLabel("Not Connected");
		sargeCON.setBackground(Color.BLACK);
		sargeCON.setForeground(Color.RED);
		sargeCON.setFont(new Font("consolas", Font.BOLD, 12));
		sargePanel.add(sargeCON);
		playerPanel.add(sargePanel);
		lobby.add(playerPanel, BorderLayout.NORTH);
		
		//"you are:" screen
		JPanel youAre = new JPanel();
		youAre.setLayout(new FlowLayout());
		youAre.setBackground(Color.BLACK);
		youAre.setForeground(Color.GREEN);
		youAre.setSize(300, 200);
		JLabel youAreLabel = new JLabel();
		youAreLabel.setBackground(Color.BLACK);
		youAreLabel.setForeground(Color.GREEN);
		youAreLabel.setFont(new Font("consolas", Font.BOLD, 20));
		ImageIcon youAreIcon = new ImageIcon();
		if (clientID == 0) {
			youAreLabel.setText("You are: RIPLEY   ");
			youAreIcon = new ImageIcon(getClass().getResource("ripley.png"));
		} else if (clientID == 1) {
			youAreLabel.setText("You are: HICKS    ");
			youAreIcon = new ImageIcon(getClass().getResource("hicks.png"));
			hicksCON.setForeground(Color.GREEN);
			hicksCON.setText("Connected");
			PLAYERS = 2;
		} else if (clientID == 2) {
			youAreLabel.setText("You are: HUDSON   ");
			youAreIcon = new ImageIcon(getClass().getResource("hudson.png"));
			hudsonCON.setForeground(Color.GREEN);
			hudsonCON.setText("Connected");
			hicksCON.setForeground(Color.GREEN);
			hicksCON.setText("Connected");
			PLAYERS = 3;
		} else if (clientID == 3) {
			youAreLabel.setText("You are: SARGE    ");
			youAreIcon = new ImageIcon(getClass().getResource("sarge.png"));
			sargeCON.setForeground(Color.GREEN);
			sargeCON.setText("Connected");
			hudsonCON.setForeground(Color.GREEN);
			hudsonCON.setText("Connected");
			hicksCON.setForeground(Color.GREEN);
			hicksCON.setText("Connected");
			PLAYERS = 4;
		}
		JLabel youArePic = new JLabel(youAreIcon);
		youAre.add(youArePic);
		youAre.add(youAreLabel);
		lobby.add(youAre, BorderLayout.CENTER);
		
		//start button
		startMP = new JButton("Start");
		startMP.addActionListener(new StartListener());
		startMP.setActionCommand("STARTMP");
		startMP.setSize(100, 25);
		startMP.setLocation(350, 550);
		JPanel startButtonPanel = new JPanel();
		startButtonPanel.setBackground(Color.BLACK);
		startButtonPanel.add(startMP);
		lobby.add(startButtonPanel, BorderLayout.SOUTH);
		startMP.setEnabled(false);
		repaint();
		System.out.println("done with lobby");

		
	}
	
	/**This method builds the final win/lose game screens
	* and pastes them onto the backboard Panel
	*/
	private void buildEndScreen(boolean win) {
		
		
		
		//winning the game
		JPanel wingamePanel = new JPanel();
		wingamePanel.setLayout(new FlowLayout());
		wingamePanel.setSize(800, 600);
		wingamePanel.setSize(800, 600);
		wingamePanel.setBackground(Color.BLACK);
		JLabel wingameLabel = new JLabel();
		wingameLabel.setSize(800, 310);
		ImageIcon wingameIMG = new ImageIcon(getClass().getResource("youwinbackground.png"));
		wingameLabel.setIcon(wingameIMG);
		wingameLabel.setText(null);
		
		//guns
		JLabel gunsLabel = new JLabel();
		gunsLabel.setSize(400, 210);
		gunsLabel.setLocation(200, 400);
		ImageIcon guns = new ImageIcon(getClass().getResource("youwinanimated.gif"));
		gunsLabel.setIcon(guns);
		gunsLabel.setText(null);
		gunsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		//play again button
		JButton wplayAgain = new JButton("Play Again");
		wplayAgain.setAlignmentX(Component.CENTER_ALIGNMENT);
		wplayAgain.addActionListener(new NewGameListener());
		//add everything to win game panel
		wingamePanel.add(wingameLabel);
		wingamePanel.add(gunsLabel);
		wingamePanel.add(wplayAgain);
		
		//losing the game
		losegamePanel = new JPanel();
		losegamePanel.setLayout(null);
		losegamePanel.setSize(800, 600);
		losegamePanel.setBackground(Color.BLACK);
		//facehugger
		faceHugger = new FaceHuggerSprite();
		faceHugger.addMouseListener(new MouseAdapter()
			{
				public void mouseClicked(MouseEvent ME) {
					if (singlePlayer){
						player[0].runningScore = 0;
						player[1].runningScore = 0;
						newGame();
					} else {					
						newNetworkGame();
					}
				}
			});
		faceHugger.setLocation(100, 300);
		faceHuggerTimer = new Timer(50, new FaceHuggerListener());
		//play again button
		JButton lplayAgain = new JButton("Play Again");
		lplayAgain.setSize(100, 35);
		lplayAgain.setLocation(350, 710);
		lplayAgain.addActionListener(new NewGameListener());
		//add everything to win game panel
		losegamePanel.add(faceHugger);
		losegamePanel.add(lplayAgain);
		
		
		//setting up endgame panel
		endScreen = new JPanel(null);
		endScreen.setSize(800, 600);
		if (!win) {
			endScreen.add(losegamePanel);
			backBoard.add(endScreen, "End");
			faceHuggerTimer.start();
		} else {
			endScreen.add(wingamePanel);
			backBoard.add(endScreen, "End");
		}
		
	}
	
	
	/**
	* rather large method used to construct the initial
	* game screen, building the various panels and adding
	* them to the gameScreen JPanel, finallu pasting the 
	* panel to the backBoard
	*/
	private void buildGameScreen(){
		gameScreen = new JPanel(new BorderLayout());
		gameScreen.setBackground(Color.BLACK);
		gameScreen.setSize(800,600);
		
		
		//build the title panel:
		JLabel title = new JLabel();
		title.setSize(800,135);
		ImageIcon titleIMG = new ImageIcon(getClass().getResource("title.jpg"));
		title.setIcon(titleIMG);
		title.setText(null);
		//add it to the gameScreen
		gameScreen.add(title, BorderLayout.NORTH);
		
		
		//add player's racks to gameScreen:
		//player 1:
		JPanel p1Panel = new JPanel();
		p1Panel.setLayout(new BoxLayout(p1Panel, BoxLayout.Y_AXIS));
		p1Panel.setBackground(Color.BLACK);
		rack[0].setAlignmentX(Component.CENTER_ALIGNMENT);
		turnLabels[0] = new JLabel("Your Turn!");
		turnLabels[0].setBackground(Color.BLACK);
		turnLabels[0].setForeground(Color.GREEN);
		turnLabels[0].setFont(new Font("consolas", Font.BOLD, 12));
		turnLabels[0].setAlignmentX(Component.CENTER_ALIGNMENT);
		p1Panel.add(turnLabels[0]);
		p1Panel.add(rack[0]);
		gameScreen.add(p1Panel, BorderLayout.WEST);
		//player 2:
		JPanel p2Panel = new JPanel();
		p2Panel.setLayout(new BoxLayout(p2Panel, BoxLayout.Y_AXIS));
		p2Panel.setBackground(Color.BLACK);
		rack[1].setAlignmentX(Component.CENTER_ALIGNMENT);
		turnLabels[1] = new JLabel("Your Turn!");
		turnLabels[1].setBackground(Color.BLACK);
		turnLabels[1].setForeground(Color.GREEN);
		turnLabels[1].setFont(new Font("consolas", Font.BOLD, 12));
		turnLabels[1].setAlignmentX(Component.CENTER_ALIGNMENT);
		p2Panel.add(turnLabels[1]);
		p2Panel.add(rack[1]);
		gameScreen.add(p2Panel, BorderLayout.EAST);
		turnLabels[1].setText(" ");
		
		//build the middle panel (decks and stuff):
		midGameP = new JPanel();
		midGameP.setLayout(new BoxLayout(midGameP, BoxLayout.Y_AXIS));
		midGameP.setBackground(Color.BLACK);
		Border greenline = BorderFactory.createLineBorder(Color.GREEN);
		//filler label for better look and feel:
		Dimension minSize = new Dimension(5, 50);
		Dimension prefSize = new Dimension(5, 50);
		Dimension maxSize = new Dimension(Short.MAX_VALUE, 50);
		midGameP.add(new Box.Filler(minSize, prefSize, maxSize));
		
		//main deck setup
		JPanel mainMP = new JPanel();
		mainMP.setLayout(new BoxLayout(mainMP, BoxLayout.Y_AXIS));
		mainMP.setBackground(Color.BLACK);
		mainMP.setForeground(Color.GREEN);
		mainMP.setLayout(new FlowLayout());
		mainMP.setMaximumSize(new Dimension(170,140));
        mainMP.setMinimumSize(new Dimension(170,140));
		TitledBorder titleMB = BorderFactory.createTitledBorder(null, "Main Deck", 
															TitledBorder.CENTER, 
															TitledBorder.TOP, 
															new Font("consolas",Font.PLAIN,12), 
															Color.GREEN);
		titleMB.setTitleJustification(TitledBorder.CENTER);
		mainMP.setBorder(titleMB);
		//add action listneer and add main deck
		main.show = false;
		main.addActionListener(ml);
		mainMP.add(main);
		
		//discard panel setup
		JPanel discDP = new JPanel();
		discDP.setLayout(new BoxLayout(discDP, BoxLayout.Y_AXIS));
		discDP.setBackground(Color.BLACK);
		discDP.setForeground(Color.GREEN);
		discDP.setLayout(new FlowLayout());
		discDP.setMaximumSize(new Dimension(170,140));
        discDP.setMinimumSize(new Dimension(170,140));
		TitledBorder titleDB = BorderFactory.createTitledBorder(null, "Discard Deck", 
															TitledBorder.CENTER, 
															TitledBorder.TOP, 
															new Font("consolas",Font.PLAIN,12), 
															Color.GREEN);
		titleDB.setTitleJustification(TitledBorder.CENTER);
		discDP.setBorder(titleDB);
		//discard button 
		discard.show = true;
		discard.addActionListener(dl);
		discDP.add(discard);
		
		//add decks to middle panel
		midGameP.add(mainMP);
		midGameP.add(discDP);
		
		
		//add middle panel to gameScreen
		gameScreen.add(midGameP, BorderLayout.CENTER);
		
		//make bottom panel for gameScreen
		JPanel botGameP = new JPanel(new GridLayout(1, 4, 5, 5));
		botGameP.setBackground(Color.BLACK);
		//player 1 score display
		JPanel p1scorePanel = new JPanel();
        p1scorePanel.setBorder((BorderFactory.createTitledBorder(null, "Player 1 Score", 
															TitledBorder.CENTER, 
															TitledBorder.TOP, 
															new Font("consolas",Font.PLAIN,12), 
															Color.GREEN)));
        p1scorePanel.setBackground(Color.BLACK);
        p1scorePanel.setForeground(Color.GREEN);
        JLabel p1scoreLabel = new JLabel(String.valueOf(player[0].runningScore));
        p1scoreLabel.setBackground(Color.BLACK);
        p1scoreLabel.setForeground(Color.GREEN);
        p1scorePanel.add(p1scoreLabel);
		
		//player 2 score display
		JPanel p2scorePanel = new JPanel();
        p2scorePanel.setBorder((BorderFactory.createTitledBorder(null, "Player 2 Score", 
															TitledBorder.CENTER, 
															TitledBorder.TOP, 
															new Font("consolas",Font.PLAIN,12), 
															Color.GREEN)));
        p2scorePanel.setBackground(Color.BLACK);
        p2scorePanel.setForeground(Color.GREEN);
        JLabel p2scoreLabel = new JLabel(String.valueOf(player[1].runningScore));
        p2scoreLabel.setBackground(Color.BLACK);
        p2scoreLabel.setForeground(Color.GREEN);
        p2scorePanel.add(p2scoreLabel);
		
		//goal score display
		JPanel goalScoreP = new JPanel();
        goalScoreP.setBorder((BorderFactory.createTitledBorder(null, "Goal Score", 
															TitledBorder.CENTER, 
															TitledBorder.TOP, 
															new Font("consolas",Font.PLAIN,12), 
															Color.GREEN)));
        goalScoreP.setBackground(Color.BLACK);
        goalScoreP.setForeground(Color.GREEN);
        JLabel goalScoreL = new JLabel(String.valueOf(goalScore));
        goalScoreL.setBackground(Color.BLACK);
        goalScoreL.setForeground(Color.GREEN);
        goalScoreP.add(goalScoreL);
		
		//game count display
		JPanel gameCountP = new JPanel();
        gameCountP.setBorder((BorderFactory.createTitledBorder(null, "Game Number", 
															TitledBorder.CENTER, 
															TitledBorder.TOP, 
															new Font("consolas",Font.PLAIN,12), 
															Color.GREEN)));
        gameCountP.setBackground(Color.BLACK);
        gameCountP.setForeground(Color.GREEN);
        JLabel gameCountL = new JLabel(String.valueOf(gameCount));
        gameCountL.setBackground(Color.BLACK);
        gameCountL.setForeground(Color.GREEN);
        gameCountP.add(gameCountL);
		
		
		botGameP.add(p1scorePanel);
		botGameP.add(goalScoreP);
		botGameP.add(gameCountP);
		botGameP.add(p2scorePanel);
		
		//add bottom panel to game screen
		gameScreen.add(botGameP, BorderLayout.SOUTH);
		
		
		int condition = gameScreen.WHEN_IN_FOCUSED_WINDOW;
		InputMap inputMap = gameScreen.getInputMap(condition);
		ActionMap actionMap = gameScreen.getActionMap();
		
		KeyStroke sStroke = KeyStroke.getKeyStroke(KeyEvent.VK_S, 0);
		inputMap.put(sStroke, sStroke.toString());
		actionMap.put(sStroke.toString(), new SAction());
		
		KeyStroke hStroke = KeyStroke.getKeyStroke(KeyEvent.VK_H, 0);
		inputMap.put(hStroke, hStroke.toString());
		actionMap.put(hStroke.toString(), new HAction());
	}
	
	/**
	* builds a multiplayer game screen (3 or 4 players)
	*/
	private void buildMPGameScreen() {
		System.out.print("building MP game screen...");
		MPGameScreen = new JPanel();
		MPGameScreen.setSize(800,600);
		MPGameScreen.setBackground(Color.BLACK);
		//MPGameScreen.setLayout(new BoxLayout(MPGameScreen, BoxLayout.Y_AXIS));
		MPGameScreen.setLayout(null);
		
		//turn tracker panel
		JPanel turnTracker = new JPanel(null);
		turnTracker.setSize(800,100);
		turnTracker.setBorder((BorderFactory.createTitledBorder(null, "Turn Tracker", 
															TitledBorder.CENTER, 
															TitledBorder.TOP, 
															new Font("consolas",Font.PLAIN,12), 
															Color.GREEN)));
		turnTracker.setBackground(Color.BLACK);
		turnTracker.setForeground(Color.GREEN);
		turnTracker.setLocation(0,0);
		//aliensprite
		alien = new AlienSprite(PLAYERS);
		//alien.setLocation(10, 10);
		turnTimer = new Timer(50, new TurnAnimationListener());
		turnTracker.add(alien);
		MPGameScreen.add(turnTracker);
		
		
		
		
		//Rack panel
		JPanel rackPanel = new JPanel();
		rackPanel.setSize(800, 300);
		rackPanel.setLayout(new BoxLayout(rackPanel, BoxLayout.X_AXIS));
		//rackPanel.setLayout(new GridLayout(1, PLAYERS));
		rackPanel.setBackground(Color.BLACK);
		rackPanel.setForeground(Color.GREEN);
		for (int i=0; i <PLAYERS;i++) {
			//System.out.println("adding rack[" + i + "]");
			rackPanel.add(rack[i]);
		}
		rackPanel.setLocation(0,200);
		MPGameScreen.add(rackPanel);
		
		//id Cards
		JPanel IDPanel = new JPanel();
		IDPanel.setLayout(new BoxLayout(IDPanel, BoxLayout.X_AXIS));
		IDPanel.setSize(800, 100);
		IDPanel.setBackground(Color.BLUE);
		IDPanel.setForeground(Color.GREEN);
		IDPanel.setLocation(0,100);
		//player 1 ID;
		JPanel p1IDPanel = new JPanel();
		p1IDPanel.setSize(200,100);
		p1IDPanel.setBackground(Color.BLACK);
		p1IDPanel.setLayout(null);
		ImageIcon p1IDImgIcon = new ImageIcon(getClass().getResource("ripley.png"));
		JLabel p1IDImg = new JLabel();
		p1IDImg.setIcon(p1IDImgIcon);
		p1IDImg.setSize(100,100);
		ContextMenu p1Context = new ContextMenu(0);
		p1IDImg.setComponentPopupMenu(p1Context);
		p1IDPanel.add(p1IDImg);
		scoreLabel[0] = new JLabel(player[0].lastMove);
        scoreLabel[0].setBackground(Color.BLACK);
        scoreLabel[0].setForeground(Color.GREEN);
		scoreLabel[0].setFont(new Font("consolas", Font.PLAIN, 10));
		scoreLabel[0].setBorder((BorderFactory.createTitledBorder(null, "Last Move", 
															TitledBorder.CENTER, 
															TitledBorder.TOP, 
															new Font("consolas",Font.PLAIN,10), 
															Color.GREEN)));
		scoreLabel[0].setSize(100,100);
		scoreLabel[0].setLocation(100,0);
		p1IDPanel.add(scoreLabel[0]);
		IDPanel.add(p1IDPanel);
		//player 2 ID
		JPanel p2IDPanel = new JPanel();
		p2IDPanel.setSize(200,100);
		p2IDPanel.setBackground(Color.BLACK);
		p2IDPanel.setLayout(null);
		ImageIcon p2IDImgIcon = new ImageIcon(getClass().getResource("hicks.png"));
		JLabel p2IDImg = new JLabel();
		p2IDImg.setIcon(p2IDImgIcon);
		p2IDImg.setSize(100,100);
		ContextMenu p2Context = new ContextMenu(1);
		p2IDImg.setComponentPopupMenu(p2Context);
		p2IDPanel.add(p2IDImg);
		scoreLabel[1] = new JLabel(player[1].lastMove);
        scoreLabel[1].setBackground(Color.BLACK);
        scoreLabel[1].setForeground(Color.GREEN);
		scoreLabel[1].setFont(new Font("consolas", Font.PLAIN, 10));
		scoreLabel[1].setBorder((BorderFactory.createTitledBorder(null, "Last Move", 
															TitledBorder.CENTER, 
															TitledBorder.TOP, 
															new Font("consolas",Font.PLAIN,10), 
															Color.GREEN)));
		scoreLabel[1] .setSize(100,100);
		scoreLabel[1] .setLocation(100,0);
		p2IDPanel.add(scoreLabel[1]);
		IDPanel.add(p2IDPanel);
		//player 3 ID
		if (PLAYERS > 2) {
			JPanel p3IDPanel = new JPanel();
			p3IDPanel.setSize(200,100);
			p3IDPanel.setBackground(Color.BLACK);
			p3IDPanel.setLayout(null);
			ImageIcon p3IDImgIcon = new ImageIcon(getClass().getResource("hudson.png"));
			JLabel p3IDImg = new JLabel();
			p3IDImg.setIcon(p3IDImgIcon);
			p3IDImg.setSize(100,100);
			ContextMenu p3Context = new ContextMenu(2);
			p3IDImg.setComponentPopupMenu(p3Context);
			p3IDPanel.add(p3IDImg);
			scoreLabel[2] = new JLabel(player[2].lastMove);
			scoreLabel[2].setBackground(Color.BLACK);
			scoreLabel[2].setForeground(Color.GREEN);
			scoreLabel[2].setFont(new Font("consolas", Font.PLAIN, 10));
			scoreLabel[2].setBorder((BorderFactory.createTitledBorder(null, "Last Move", 
																TitledBorder.CENTER, 
																TitledBorder.TOP, 
																new Font("consolas",Font.PLAIN,10), 
																Color.GREEN)));
			scoreLabel[2].setSize(100,100);
			scoreLabel[2].setLocation(100,0);
			p3IDPanel.add(scoreLabel[2]);
			IDPanel.add(p3IDPanel);
		}
		//player 4 ID
		if (PLAYERS > 3) {
			JPanel p4IDPanel = new JPanel();
			p4IDPanel.setSize(200,100);
			p4IDPanel.setBackground(Color.BLACK);
			p4IDPanel.setLayout(null);
			ImageIcon p4IDImgIcon = new ImageIcon(getClass().getResource("sarge.png"));
			JLabel p4IDImg = new JLabel();
			p4IDImg.setIcon(p4IDImgIcon);
			p4IDImg.setSize(100,100);
			ContextMenu p4Context = new ContextMenu(3);
			p4IDImg.setComponentPopupMenu(p4Context);
			p4IDPanel.add(p4IDImg);
			scoreLabel[3] = new JLabel(player[3].lastMove);
			scoreLabel[3].setBackground(Color.BLACK);
			scoreLabel[3].setForeground(Color.GREEN);
			scoreLabel[3].setFont(new Font("consolas", Font.PLAIN, 10));
			scoreLabel[3].setBorder((BorderFactory.createTitledBorder(null, "Last Move", 
																TitledBorder.CENTER, 
																TitledBorder.TOP, 
																new Font("consolas",Font.PLAIN,10), 
																Color.GREEN)));
			scoreLabel[3].setSize(100,100);
			scoreLabel[3].setLocation(100,0);
			p4IDPanel.add(scoreLabel[3]);
			IDPanel.add(p4IDPanel);
		}
		
		MPGameScreen.add(IDPanel);
		
		
		//decks Panel
		JPanel decksPanel = new JPanel();
		decksPanel.setSize(800, 100);
		//decksPanel.setLayout(new BoxLayout(decksPanel, BoxLayout.X_AXIS));
		decksPanel.setLayout(new FlowLayout());
		decksPanel.setBackground(Color.BLACK);
		decksPanel.setForeground(Color.GREEN);
		decksPanel.add(new JLabel("Main Deck"));
		main.show = false;
		decksPanel.add(main);
		//filler
		JLabel filler = new JLabel("       ");
		filler.setBackground(Color.BLACK);
		filler.setSize(50, 100);
		decksPanel.add(filler);
		discard.show = true;
		decksPanel.add(discard);
		decksPanel.add(new JLabel("Discard Deck"));
		decksPanel.setLocation(0,500);
		MPGameScreen.add(decksPanel);
		
		int condition = MPGameScreen.WHEN_IN_FOCUSED_WINDOW;
		InputMap inputMap = MPGameScreen.getInputMap(condition);
		ActionMap actionMap = MPGameScreen.getActionMap();

		KeyStroke sStroke = KeyStroke.getKeyStroke(KeyEvent.VK_S, 0);
		inputMap.put(sStroke, sStroke.toString());
		actionMap.put(sStroke.toString(), new SAction());
		
		KeyStroke hStroke = KeyStroke.getKeyStroke(KeyEvent.VK_H, 0);
		inputMap.put(hStroke, hStroke.toString());
		actionMap.put(hStroke.toString(), new HAction());
	}
	
	/**
	* This method builds the main GUI JPanel named backBoard.
	* backBoard has a card layout and holds the introScreen, 
	* gameScreen, and endScreen
	*/
	private void buildBackBoard() {
		backBoard = new JPanel(new CardLayout());
		backBoard.setSize(800,600);
		backBoard.add(startScreen, "Start");
		if (singlePlayer) {
			System.out.print("adding SP to backboard...");
			backBoard.add(gameScreen, "Game");
			System.out.println("done");
		}
		else {
			System.out.print("adding MP to backboard...");
			backBoard.add(lobby, "Lobby");
			backBoard.add(MPGameScreen, "MPGame");
			System.out.println("done");
		}
	}
	
	/** initiates a new game from the end of a network game
	*/
	private void newNetworkGame() {
		remove(backBoard);
		for (int i=0;i<PLAYERS;i++) {
			player[i].runningScore = 0;
			player[i].setIsTurn(false);
			player[i].isWinner = false;
			player[i].current = null;
			player[i].emptyRack();
		}
		gameOver = false;
		buildStartScreen();
		add(startScreen);
		validate();
		repaint();
	}
			
	
	/**
	* Initiates a new game from singpleplayer mode, used mainly in 
	* running score mode, resets various flags and counters, sets up
	* next game
	*/
	private void newGame() {
		gameCount++;
		gameOver = false;
		Random rand = new Random();
        seed = rand.nextLong();
		
		
		//clear player's racks and current cards (if any)
		player[0].current = null;
		player[0].emptyRack();
		player[0].isWinner = false;
		player[0].setIsTurn(true);
		if (demoMode)
			player[0].resetRackFlags();

		
		player[1].current = null;
		player[1].emptyRack();
		player[1].isWinner = false;
		player[1].setIsTurn(false);
		player[1].resetRackFlags();
		
		//new main deck;
		main = new Deck(40, seed);
		System.out.println("main deck has: " + main.pile.size() + " cards");
		
		//fill player's racks:
		player[0].fillRack(main);
		player[1].fillRack(main);
		System.out.println("done filling racks");
		System.out.println("main deck has: " + main.pile.size() + " cards");
		
		//initialize racks
		rack = new Rack[PLAYERS];
		rack[0] = new Rack(player[0]);
		for (int i = 0; i < 10; i++) {
            rl[i] = new RackListener();
            rack[0].cards[i].addActionListener(rl[i]);
        }
		rack[1] = new Rack(player[1]);
		for (int i = 0; i < 10; i++) {
            AIrl[i] = new AIRackListener();
            rack[1].cards[i].addActionListener(AIrl[i]);
        }
		//set up discard pile
		discard = new Deck('d');
		discard.pile.add(main.pile.pop());
		
		
		backBoard.remove(gameScreen);
		buildGameScreen();
		backBoard.add(gameScreen, "Game");
		CardLayout cardLayout = (CardLayout) backBoard.getLayout();
		cardLayout.show(backBoard, "Game");
		
		validate();
		repaint();
	}
	
	/**
	* This method will swap out your currently held card
	* with one in the rack
	* @param currentPlayer the currentPlayer doing the swap
	* @param pos the position of the card in the rack
	*/
	private void swap(Player currentPlayer, int pos) {
		//System.out.println("hello from swap");
		int id = currentPlayer.id;
		Card toBeDiscarded;
		toBeDiscarded = player[id].rack.get(pos);
		if (clientID == id)
			player[id].lastMove += (" and placed in slot " + pos);

		//do the swap
		player[id].swaps++;
		player[id].rack.remove(pos);
		player[id].rack.add(pos, player[id].current);
		player[id].rack.get(pos).addActionListener(rl[pos]);
		toBeDiscarded.removeActionListener(rl[pos]);
		
		
		//pops from the main or discard, depending on if your card was 
		//drawn from that deck and repaints for good measure
		if(player[id].current.value == main.pile.peek().value) {
			main.pile.pop();
			checkMain();
			discard.pile.add(toBeDiscarded);
			main.show = false;
			main.repaint();
		} else if (player[id].current.value == discard.pile.peek().value) {
			discard.pile.pop();
			discard.pile.add(toBeDiscarded);
			System.out.println("DISCARD has : " + discard.pile.size() + " cards");	
		}
		//reset your "current" card to null
		player[id].current = null;
		
		//remake the rack and decks and reset action listeners
		if(singlePlayer){
			if(!demoMode){
				rack[id].remakeRack();
				for (int i = 0; i < 10; i++) {
					rack[0].cards[i].removeActionListener(rl[i]);
					rl[i] = new RackListener();
					rack[0].cards[i].addActionListener(rl[i]);
				}
				for (int i = 0; i < 10; i++) {
					rack[1].cards[i].removeActionListener(AIrl[i]);
					AIrl[i] = new AIRackListener();
					rack[1].cards[i].addActionListener(AIrl[i]);
				}
			}
			if (demoMode) {
				rack[id].remakeRack();
				for (int i = 0; i < 10; i++) {
					rack[0].cards[i].removeActionListener(AIrl2[i]);
					AIrl2[i] = new AIRackListener();
					rack[0].cards[i].addActionListener(AIrl2[i]);
				}
				for (int i = 0; i < 10; i++) {
					rack[1].cards[i].removeActionListener(AIrl[i]);
					AIrl[i] = new AIRackListener();
					rack[1].cards[i].addActionListener(AIrl[i]);
				}
			}
		}else{
			//System.out.println("Remaking rack");
			rack[id].remakeRack();
		}
		discard.repaint();
		player[id].tookAction = true;
		testWin(currentPlayer);
		if (currentPlayer.isAI)
			pauser.start();
		else
			switchTurns(id);

	}
	
	
	
	/** performs a swap fro the player over the network, performs
	* necessary game maintainence and moves the game forward
	* @param pos the position in the rack to swap
	*/
	private void networkSwap(int pos) {
		int id = getCurrentPlayer();
		if(id != clientID) {
			Card toBeDiscarded;
			toBeDiscarded = player[id].rack.get(pos);
			

			//do the swap
			player[id].swaps++;
			player[id].rack.remove(pos);
			player[id].rack.add(pos, player[id].current);
			rack[id].remakeRack();
			player[id].lastMove += (" and placed in slot " + pos);
			//pops from the main or discard, depending on if your card was 
			//drawn from that deck and repaints for good measure
			if(player[id].current.value == main.pile.peek().value) {
				main.pile.pop();
				checkMain();
				discard.pile.add(toBeDiscarded);
				main.show = false;
				main.repaint();
			} else if (player[id].current.value == discard.pile.peek().value) {
				discard.pile.pop();
				discard.pile.add(toBeDiscarded);
				System.out.println("DISCARD has : " + discard.pile.size() + " cards");	
			}
			
			toBeBlinked = pos;
			oldBorderCard = rack[id].cards[pos].getBorder();
			cblinker.start();
			
			player[id].current = null;
			testWin(player[id]);
			switchTurns(id);
		}
	}
	
	/**
	* discards for the player over the network, performs the necessary
	* game management steps
	*/
	private void networkDiscard() {
		int id = getCurrentPlayer();
		player[id].lastMove += "and discarded.";
		discard.pile.add(player[id].current);
		player[id].current = null;
		
		main.pile.pop();
		checkMain();
		main.show = false;
		main.repaint();
		
		deckToBlink = discard;
		
		dblinker.start();
		testWin(player[id]);
		switchTurns(id);
	}
	
	
	/**
      * method responsible for the managing the whoseTurn field, grabbing the AI
      * input when necessary, and updating the GUI
      */
    private void switchTurns(int currentID){
		
		if(gameOver)
			return;
		play(alienNoise);
		//this stuff only happens in single player mode
		if (singlePlayer){
			int next = currentID + 1;
			if (next > (PLAYERS-1)) {
				next = 0;
			}
			player[currentID].setIsTurn(false);
			player[next].setIsTurn(true);
			player[next].tookAction = false;
			if (singlePlayer) {
				turnLabels[currentID].setText(" ");
				turnLabels[next].setText("Your Turn!");
				repaint();
			} else {
				turnTimer.start();
			}
			if (player[next].isAI) {
				System.out.println("about to call getAI");
				try{
					getAI(player[next]);
				} catch (Exception ex) {
					System.out.println(ex.getMessage());
				}
			}
		//multiplayer logic	
		} else {
			scoreLabel[currentID].setText("<html>"+player[currentID].lastMove+"</html>");
			scoreLabel[currentID].revalidate();
			scoreLabel[currentID].repaint();
			player[currentID].lastMove = "";
			int next = currentID + 1;
			if (next >= PLAYERS) {
				next = 0;
			}
			//System.out.println("CURRENT: " +currentID+" NEXT: "+next);
			player[currentID].setIsTurn(false);
			player[next].setIsTurn(true);
			turnTimer.start();
		}
	}
	

	
	
	/**
     * This method goes through a large if conditional to see if player one or
     * player two has a completely in order rack, if either of them do it calls
     * the win() from Player.java and then calls the local method youWin() or 
     * youLose() depending
	 * @param currentPlayer the player that just went, is checking if won or not
     */
	private void testWin(Player currentPlayer) {
		if (singlePlayer) {
			if (currentPlayer.rack.get(0).getValue() < currentPlayer.rack.get(1).getValue()
					&& currentPlayer.rack.get(1).getValue() < currentPlayer.rack.get(2).getValue()
					&& currentPlayer.rack.get(2).getValue() < currentPlayer.rack.get(3).getValue()
					&& currentPlayer.rack.get(3).getValue() < currentPlayer.rack.get(4).getValue()
					&& currentPlayer.rack.get(4).getValue() < currentPlayer.rack.get(5).getValue()
					&& currentPlayer.rack.get(5).getValue() < currentPlayer.rack.get(6).getValue()
					&& currentPlayer.rack.get(6).getValue() < currentPlayer.rack.get(7).getValue()
					&& currentPlayer.rack.get(7).getValue() < currentPlayer.rack.get(8).getValue()
					&& currentPlayer.rack.get(8).getValue() < currentPlayer.rack.get(9).getValue()) {
				currentPlayer.wins();
				gameOver = true;
				
			}
			if (currentPlayer.isWinner) {
				JOptionPane.showMessageDialog(null, "Player " + (currentPlayer.id+1) + " has scored RACKO!");
				currentPlayer.runningScore += 75;
				if (currentPlayer.id == 0) {
					player[1].runningScore += findScore(player[1]);
					
				} else {
					player[0].runningScore += findScore(player[0]);
				}
				if (testBigWin()) {
					if (player[0].runningScore > player[1].runningScore) {
						buildEndScreen(true);
						CardLayout cardLayout = (CardLayout) backBoard.getLayout();
						cardLayout.show(backBoard, "End");
					} else {
						buildEndScreen(false);
						CardLayout cardLayout = (CardLayout) backBoard.getLayout();
						cardLayout.show(backBoard, "End");
						play(gameOverNoise);
						AudioClip gameOverClip = getAudioClip(gameOverNoise);
						gameOverClip.play();
					}
				} else {
					newGame();

				}
			}
			System.out.println("Player 1 runningScore: " + player[0].runningScore);
			System.out.println("Player 2 runningScore: " + player[1].runningScore);
		
		//multiplayer logic:
		} else {
			if (currentPlayer.rack.get(0).getValue() < currentPlayer.rack.get(1).getValue()
					&& currentPlayer.rack.get(1).getValue() < currentPlayer.rack.get(2).getValue()
					&& currentPlayer.rack.get(2).getValue() < currentPlayer.rack.get(3).getValue()
					&& currentPlayer.rack.get(3).getValue() < currentPlayer.rack.get(4).getValue()
					&& currentPlayer.rack.get(4).getValue() < currentPlayer.rack.get(5).getValue()
					&& currentPlayer.rack.get(5).getValue() < currentPlayer.rack.get(6).getValue()
					&& currentPlayer.rack.get(6).getValue() < currentPlayer.rack.get(7).getValue()
					&& currentPlayer.rack.get(7).getValue() < currentPlayer.rack.get(8).getValue()
					&& currentPlayer.rack.get(8).getValue() < currentPlayer.rack.get(9).getValue()) {
				currentPlayer.wins();
				gameOver = true;
			
			
				if (currentPlayer.id == clientID) {
					buildEndScreen(true);
					CardLayout cardLayout = (CardLayout) backBoard.getLayout();
					cardLayout.show(backBoard, "End");
					repaint();
				} else {
					if (currentPlayer.id == 0){
						JOptionPane.showMessageDialog(null, "Ripley has scored RACKO!");
					}
					if (currentPlayer.id == 1)
						JOptionPane.showMessageDialog(null, "Hicks has scored RACKO!");
					if (currentPlayer.id == 2)
						JOptionPane.showMessageDialog(null, "Hudson has scored RACKO!");
					if (currentPlayer.id == 3)
						JOptionPane.showMessageDialog(null, "Sarge has scored RACKO!");
					buildEndScreen(false);
					CardLayout cardLayout = (CardLayout) backBoard.getLayout();
					cardLayout.show(backBoard, "End");
					repaint();
				}
			}
		}
	}
	
	
	
	/** 
	 * This method tests the players' runningScore against the goal score and 
	 * determines the real winner of RACKO, building the appropriate end game
	 *  screen
	 * @return boolean to see if someone met the goal score 
	 */
	private boolean testBigWin() {
		for (Player currentP : player ) {
			if (currentP.runningScore >= goalScore) {
				System.out.println("Player " + (currentP.id+1) + " is over goalScore");
				return true;
			}
		}
		return false;
	}
	
	/**
     * Calculates the players score based on how many in-order cards there are 
     * in that player's rack.
     * @param playerNum
     * @return the player's score
     */
    private int findScore(Player otherPlayer) {
        int score = 5;
        boolean stop = false;
        for (int i = 0; i<9; i++){
            if (otherPlayer.rack.get(i).getValue() < otherPlayer.rack.get(i + 1).getValue()) 
                score += 5;
            else 
                break;
        }
		return score;
	}
	
	
	
	 /**
     * this method calls the ComputerPlayer methods getInput and getKeepOrNot,
     * translating them into GUI actions, and performing swaps and other method
     * calls when necessary. 
	 * @param Player current player
     * @throws IOException
     * @throws InterruptedException 
     */
    private void getAI(Player currentPlayer) throws IOException, InterruptedException {
        //System.out.println("AI TURN");
	
        gameScreen.revalidate();
        char discardOrMain, keepOrNot;
		
        discardOrMain = currentPlayer.getInput(discard.pile.peek().value);
        switch(discardOrMain){
            case 'M':
            case 'm':
                //System.out.print("Drawing From MAIN: ");    
				main.doClick(30);
				mblinker.start();
                lastMove += "AI Drew From Main, ";
				getKeepOrNot(currentPlayer);
                break;
            case 'D':
            case 'd':
                //System.out.print("Drawing From DISCARD: ");
				deckToBlink = discard;
				
				dblinker.start();
				discard.doClick(30);
				AISwap(currentPlayer);
                break;            
            default:
                break;
        }
		currentPlayer.current = null;
		System.out.println("Bottom of getAI for player[" + currentPlayer.id + "]");
    }
	
	/** private AI function, AI decides to keep the card it drew from main or not
	* @param currentPlayer the current player
	* @throws IOException
	* @throws InterruptedException
	*/
	private void getKeepOrNot(Player currentPlayer) throws IOException, InterruptedException {
        char keepOrNot;
		keepOrNot = currentPlayer.getKeepOrNot(currentPlayer.current);
        switch(keepOrNot){
            case 'K':
                AISwap(currentPlayer);
                break;
            case 'D':
                lastMove += "and discarded";
				deckToBlink = discard;
				
				dblinker.start();
				discard.doClick(30);
                currentPlayer.current = null;
                break;
            default:
                break;
        }    
        
    }
	
	
	/**
     * AISwap grabs the appropriate position of the rack in which the computer
     * wants to swap his currently held card with, ends with a switchTurns() call
     * @throws IOException
     * @throws InterruptedException 
     */
    private void AISwap(Player currentPlayer) throws IOException, InterruptedException {
        
        char choice;
		choice = currentPlayer.getSwapInput(currentPlayer.current);
		int id = currentPlayer.id;
		//System.out.println("hello from AISwap: " + player[id].current.value);
        switch(choice){
            case 'A':
				System.out.println(" and swapped with his first card");
				rack[id].cards[0].doClick(30);
                break;
            case 'B':
				System.out.println(" and swapped with his second card");
				rack[id].cards[1].doClick(30);
                break;
            case 'C': 
				System.out.println(" and swapped with his third card");
				rack[id].cards[2].doClick(30);
                break;
            case 'D': 
				System.out.println(" and swapped with his fourth card");
                rack[id].cards[3].doClick(30);
                break;
            case 'E': 
				System.out.println(" and swapped with his fifth card");
				rack[id].cards[4].doClick(30);
                break;
            case 'F': 
				System.out.println(" and swapped with his sixth card");
                rack[id].cards[5].doClick(30);
                break;
            case 'G':
				System.out.println(" and swapped with his seventh card");
                rack[id].cards[6].doClick(30);
                break;
            case 'H': 
				System.out.println(" and swapped with his eigth card");
				rack[id].cards[7].doClick(30);
                break;
            case 'I': 
				System.out.println(" and swapped with his ninth card");
                rack[id].cards[8].doClick(30);
                break;
            case 'J': 
				System.out.println(" and swapped with his tenth card");
				rack[id].cards[9].doClick(30);
                break;
            default:
                break;
        } 
		
    }
	
	
    /**
     * simple method used to see if the main deck is empty and calls remake main
     * if it is.
     */
    private void checkMain() {
		//System.out.println("MAIN has: " + main.pile.size() + " cards");
		//System.out.println("DISCARD HAS: " + discard.pile.size() + " cards");
        if (main.pile.empty()) {
            remakeMain();
        }
    }

    /**
     * remakes the main deck by popping off the discard stack until the discard
     * stack is empty, in effect "flipping over" the discard stack to make the 
     * new main stack
     */
    private void remakeMain() {
        System.out.println("Remaking Main Deck");
        while (!discard.pile.empty()) {
            main.pile.push(discard.pile.pop());
        }
        discard.pile.push(main.pile.pop());
    }
	
	
	
	/**
	* custom actionlistener class to listen to the 
	* start button and multiplayer button on introScreen
	*@implements ActionListener
	*/
	private class StartListener implements ActionListener {
		public void actionPerformed(ActionEvent e){
		
			if (e.getActionCommand().equals("START")) {
				if (goalScorePanel.isVisible())
					goalScore = Integer.parseInt(goalScoreTextField.getText());
				if (cheatPanel.isVisible()){
					System.out.println("Grabbing Pre place cards");
					placeMode = true;
					prePlaceString = prePlaceTF.getText();
					if (prePlaceString.equals(""))
						placeMode = false;
				}
				singlePlayer = true;
				DECK_SIZE = 40;
				gameSetup();
				buildGameScreen();
				buildBackBoard();
				remove(startScreen);
				add(backBoard);
				CardLayout cardLayout = (CardLayout) backBoard.getLayout();
				cardLayout.show(backBoard, "Game");
				if (demoMode) {
					try{
					System.out.println("STARTING DEMO MODE");
					getAI(player[0]);
					} catch (Exception ex) {
						System.out.println(ex.getMessage());
					}
				}		

			}
			if (e.getActionCommand().equals("MP")) {
				//System.out.println("Multiplayer was clicked");		
				buildLobby();
				remove(startScreen);
				add(lobby);
				revalidate();
				repaint();
				//CardLayout cardLayout = (CardLayout) backBoard.getLayout();
				//cardLayout.show(backBoard, "Lobby");
				//repaint();
				WaitForOthers waitForOthers = new WaitForOthers();
				Thread t = new Thread(waitForOthers);
				t.start();
			}
			if (e.getActionCommand().equals("STARTMP")) {
				//System.out.println("Starting Multiplayer");
				starter();
				buildMPGameScreen();
				buildBackBoard();
				remove(lobby);
				add(backBoard);
				CardLayout cardLayout = (CardLayout) backBoard.getLayout();
				cardLayout.show(backBoard, "MPGame");
			}
		}
	}
	
	/** small function for starting the game from the lobby, sets
	* up turn listening threads, starts them and starts the 
	* multiplayer game setup
	*/
	void starter() {
		if (clientID == 0)
			out.println("s");
		MPGameSetup();
		turnListenerThread = new Thread(new TurnListener());
		turnListenerThread.start();
	}
	
	/**
     * Custom action listener for the main deck of cards. Determines if you can
     * legally draw from the deck (can only do so once during your turn) 
     * @interface ActionListener
     */
    private class MainListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            //Single Player logic:
			if (singlePlayer) {
				Deck clickedOnDeck = (Deck)e.getSource();
				Card clickedOn = clickedOnDeck.pile.peek();
				//System.out.println("clicked on main pile : " + clickedOn.value );
				int id = 0;
				if(player[0].isTurn)
					id = 0;
				if(player[1].isTurn)
					id = 1;
				
				if (player[id].isTurn && player[id].tookAction == true){
					System.out.println("you already made a move, please hit end turn");
				} else if (player[id].current == null && player[id].isTurn) {
					if(demoMode){
						main.show = true;
					} else if(id == 0) {
						main.show = true;
					}
					player[id].current = main.pile.peek();
					player[id].current.cameFromMain = true;
					
				} else if (player[id].current != null && player[id].isTurn) {
					System.out.print("You already drew! Make your move and/or click END TURN");
				}
				
			//multiplayer logic:
			} else {
				Deck clickedOnDeck = (Deck)e.getSource();
				Card clickedOn = clickedOnDeck.pile.peek();
				int currentID = getCurrentPlayer();
				if (currentID == clientID && player[currentID].current == null) {
					player[currentID].lastMove = "Drew from main ";
					player[currentID].drawnFromMain++;
					main.show = true;
					player[currentID].current = main.pile.peek();
					player[currentID].current.cameFromMain = true;
					out.println("m");
				} else {
					System.out.println("It's not your turn or you already drew a card");
					System.out.println("Your ID: " + clientID);
					System.out.println("Current player: " + getCurrentPlayer());
				}
			}
        }
		
    }
	
    /**
     * Custom action listener for the discard pile, determines if a click is for 
     * picking up off the discard pile or for discarding a card that was previously
     * drawn from the main pile
     * @interface ActionListener
     */
    private class DiscardListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            //SinglePLayer logic
			if (singlePlayer){
				int id = 0;	
				if(player[0].isTurn)
					id = 0;
				if(player[1].isTurn)
					id = 1;
				
				//System.out.println("player " + id + " clicked on discard pile");
				Deck clickedOnDeck = (Deck)e.getSource();
				Card clickedOn = clickedOnDeck.pile.peek();
				
				if (player[id].current == null && player[id].isTurn && player[id].tookAction == true) {
					System.out.println("you already took an action this turn, please hit end turn");
					
				} else if (player[id].current == null && player[id].isTurn) {
					player[id].current = discard.pile.peek();
					//System.out.println("player " + id + " drew from discard: " 
										//+ player[id].current.value);
					
					
				} else if (player[id].current != null && !player[id].current.cameFromMain) {
					System.out.println("you cant discard a card you just picked up from discard");
					
				} else if (player[id].current != null && player[id].isTurn) {
					discard.pile.add(player[id].current);
					player[id].current = null;
					player[id].lastMove += "and discarded";
					main.pile.pop();
					checkMain();
					main.show = false;
					main.repaint();
					discard.repaint();
					player[id].tookAction = true;
					testWin(player[id]);
					if (!player[id].isAI)
						switchTurns(id);
					else
						pauser.start();
				}
				
			//Multiplayer logic:
			} else {
				Deck clickedOnDeck = (Deck)e.getSource();
				Card clickedOn = clickedOnDeck.pile.peek();
				int currentID = getCurrentPlayer();
				if (currentID == clientID && player[currentID].current == null) {
					//player is drawing from discard
					player[currentID].lastMove = ("Drew from discard["+clickedOn.value+"] ");
					player[currentID].drawnFromDiscard++;
					player[currentID].current = discard.pile.peek();
					out.println("d");
				} else if (player[currentID].current != null 
							&& !player[currentID].current.cameFromMain) {
					//players clicked on discard twice in a row
					System.out.println("you cant discard a card you just picked up from discard");
				} else if(currentID == clientID && player[currentID].current != null) {
					//player is discarding card he/she drew from main
					player[currentID].lastMove += ("and discarded.");
					discard.pile.add(player[currentID].current);
					player[currentID].current = null;
					main.pile.pop();
					checkMain();
					main.show = false;
					main.repaint();
					discard.repaint();
					out.println("d");
					testWin(player[currentID]);
					switchTurns(currentID);
				}
			}

        }

    }

    /**
     * Custom action listener class for Rack buttons, determines if a swap
     * can be performed (only when the current player drew a card from main or 
     * discard)
     * @interface ActionListener
     */
    private class RackListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
			//Single Player Logic:
			if (singlePlayer){
				Card clickedOn = (Card)e.getSource();
				//clickedOn.setBorder(new LineBorder(Color.RED, 3));
				int id = 0;
				if(player[0].isTurn)
					id = 0;
				if(player[1].isTurn)
					id = 1;	
					
				System.out.println("player " + id + 
									" clicked on rack[" + 
									clickedOn.position + "] : " + 
									clickedOn.value);

				if (player[id].current != null && player[id].isTurn){
					swap(player[id], clickedOn.position);
				}
			}
			//Multiplayer Logic:
			else {
				Card clickedOn = (Card)e.getSource();
				System.out.println("You clicked on your rack");
				if (getCurrentPlayer() == clientID && player[clientID].current != null) {
					out.println("" + clickedOn.position);
					swap(player[clientID], clickedOn.position);
				} else {
					System.out.println("Either its not your turn or you need to draw a card first");
				}	
			}				
		}
    }	
	
	/**
	 * AI rack listener class for AI clicks during single player
	 * @interface ActionListener
	 */
	private class AIRackListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Card clickedOn = (Card)e.getSource();
            System.out.println("clicked on rack[" + 
								clickedOn.position + "] : " + 
								clickedOn.value);
			int id = 0;
			if(player[0].isTurn)
				id = 0;
			if(player[1].isTurn)
				id = 1;
			
			if (player[id].isTurn && player[id].current != null) {
				toBeBlinked = clickedOn.position;
				oldBorderCard = clickedOn.getBorder();
				cblinker.start();
				
				swap(player[id], clickedOn.position);
			} else if(player[id].isTurn && player[id].current == null) {
				System.out.println("dummy click");
			} else {
				System.out.println("this is not your rack!");
			}
		}
	}
				
	/** 
	* action listener attached to the new game buttons displayer
	* at the end of the game
	*@implements ActionListener
	*/
	private class NewGameListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (singlePlayer) {
				player[0].runningScore = 0;
				player[1].runningScore = 0;
				newGame();
				
			//multiplayer logic:
			} else {
				newNetworkGame();
			}

		}
	}
	
	
	/**
     * Custom action listener for the multiround radio buttons. 
     * @interface ActionListener
     */
	private class RadioButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == mno) {
				goalScorePanel.setVisible(false);
			}
			if (e.getSource() == myes) {
				goalScorePanel.setVisible(true);
			}
			if (e.getSource() == dyes) {
				System.out.println("Demo mode activated");
				demoMode = true;
			}
			if (e.getSource() == dno) {
				System.out.println("Demo mode de-activated");
				demoMode = false;
			}
			if(e.getSource() == vyes) {
				System.out.println("view mode activated");
				viewMode = true;
			}
			if(e.getSource() == vno) {
				System.out.println("view mode de-activated");
				viewMode = false;
			}
			if(e.getSource() == syes) {
				System.out.println("sort mode activated");
				sortMode = true;
			}
			if(e.getSource() == sno) {
				System.out.println("sort mode de-activated");
				sortMode = false;
			}
		}
	}
	
	
	/** Timer that keeps track of the alien "turn tracker" at the
	* top of the network game screen. uses "ticks" to maintain
	* animation status
	*@implements ActionListener
	*/
	private class TurnAnimationListener implements ActionListener {
		int ticks = 0;
		public void actionPerformed(ActionEvent e) {
			//System.out.println("ticks: " + ticks);
			//Alien animations for 4 players
			if (PLAYERS == 4) {
				if (alien.currentTurn < 4) {
					if (ticks < 40) {
						alien.update();
						repaint();
						ticks++;
					} else {
						ticks = 0;
						turnTimer.stop();
						alien.currentTurn++;
						//System.out.println("Current turn: " + alien.currentTurn);
						return;
					}
				} else {
					if (ticks < 40) {
						alien.jump();
						ticks++;
					} else {
						ticks = 0;
						turnTimer.stop();
						alien.reset();
						//System.out.println("Done jumping");
						return;
					}
				}
			}
			//Alien animations for 3 players
			if (PLAYERS == 3) {
				if (alien.currentTurn < 3) {
					if (ticks < 27) {
						alien.update();
						repaint();
						ticks++;
					}else {
						ticks = 0;
						turnTimer.stop();
						alien.currentTurn++;
					}
				} else {
					if (ticks < 40) {
						alien.jump();
						ticks++;
					} else {
						ticks = 0;
						turnTimer.stop();
						alien.reset();
					}
				}
			}
			//Alien animations for 2 players
			if (PLAYERS == 2) {
				if (alien.currentTurn == 1) {
					if (ticks < 40) {
						alien.update();
						repaint();
						ticks++;
					} else {
						ticks = 0;
						turnTimer.stop();
						alien.currentTurn++;
					}
				} else {
					if (ticks < 30) {
						alien.jump();
						ticks++;
					} else {
						ticks = 0;
						turnTimer.stop();
						alien.reset();
					}
				}
			}
		}
	}
	
	/** custom animation listener for the facehugger during the
	* lose game screen
	*@implements ActionListener
	*/
	private class FaceHuggerListener implements ActionListener {
		int ticks = 0;
		public void actionPerformed(ActionEvent e) {
			if (ticks < 25) {
				faceHugger.update();
				repaint();
				ticks++;
			} else {
				if (ticks < 25+9){
					faceHugger.jump();
					repaint();
					ticks++;
				} else {
					ticks = 0;
					faceHuggerTimer.stop();
				}
			}
		}
	}
	
	/** used to make decks blink when clicked on by oppnents
	* in network mode or singleplayer
	*@implements ActionListener
	*/
	private class DeckBlinker implements ActionListener {
		int ticks =0;
		public void actionPerformed(ActionEvent e) {
			//System.out.println("Hello from deck blinker");
			if(ticks < 20) {
				deckToBlink.setBorder(BorderFactory.createLineBorder(Color.GREEN, 4));
				ticks++;
			} else {
				deckToBlink.setBorder(oldBorderDeck);
				main.setBorder(oldBorderDeck);
				ticks = 0;
				dblinker.stop();
			}
		}
	}
	
	private class MainBlinker implements ActionListener {
		int ticks = 0;
		public void actionPerformed(ActionEvent e) {
			//System.out.println("Hello from deck blinker");
			if(ticks < 20) {
				main.setBorder(BorderFactory.createLineBorder(Color.GREEN, 4));
				ticks++;
			} else {
				main.setBorder(oldBorderDeck);
				ticks = 0;
				mblinker.stop();
			}
		}
	}
	
	/** used to make opponents cards blink when swapped
	* in network mode or singleplayer
	*@implements ActionListener
	*/
	private class CardBlinker implements ActionListener {
		int ticks = 0;
		public void actionPerformed(ActionEvent e) {
			if (singlePlayer){
				//System.out.println("Hello from CardBlinker: " + toBeBlinked);
				int id = getCurrentPlayer();
				if(ticks < 20) {
					rack[id].cards[toBeBlinked].setBorder(BorderFactory.createLineBorder(Color.GREEN, 4));
					ticks++;
				} else {
					rack[id].cards[toBeBlinked].setBorder(oldBorderCard);
					ticks = 0;
					cblinker.stop();
				}
				
			//multiplayer logic
			} else {
				if(ticks < 20) {
					rack[rackToBlink].cards[toBeBlinked].setBorder(BorderFactory.createLineBorder(Color.GREEN, 4));
					ticks++;
				} else {
					rack[rackToBlink].cards[toBeBlinked].setBorder(oldBorderCard);
					ticks = 0;
					cblinker.stop();
				}
			}
		}
	}
	
	/**used to slow down the AI in single player mode
	*@implements ActionListener
	*/
	private class Pauser implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			System.out.println("AI done");
			switchTurns(getCurrentPlayer());
		}
	}
	

	
	/**
	* This private method finds and returns the currentPlayer ID
	*/
	private int getCurrentPlayer() {
		if (player[0].isTurn) {	
			//System.out.println("getCurrent returning 0");
			return 0;
		} 
		if (player[1].isTurn) {
			//System.out.println("getCurrent returning 1");
			return 1;
		}
		if(PLAYERS > 2) {
			if(player[2].isTurn) {
				//System.out.println("getCurrent returning 2");
				return 2;
			}
		}
		if(PLAYERS > 3) {
			if(player[3].isTurn) {
				//System.out.println("getCurrent returning 3");
				return 3;
			}
		}
		//System.out.println("getCurrent returning -1");
		return -1;
	}
	
	/**
	* connects to acad server and returns a client ID to host applet
	*/
	private long connect() {
		System.out.println("Attempting server connect");
		try {
			socket = new Socket("acad.kutztown.edu", 15026);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}catch(UnknownHostException e) {
			System.err.println("unkown host: acad.kutztown.edu");
		}catch(IOException ex) {
			System.err.println(ex.getMessage());
		}
		System.out.println("Connected to server");
		try {
			String cmd = in.readLine();
			//System.out.println(cmd);
			clientID = Integer.parseInt(cmd);
			String seedString = in.readLine();
			seed = Long.parseLong(seedString);
			//System.out.println("Seed: " + seed);
		} catch (IOException ioe) {
			System.err.println(ioe.getMessage());
		}
		return seed;
	}
	
	/** waits for other players to connect to multiplayer game
	*@implements Runnable
	*/
	private class WaitForOthers implements Runnable {
		public void run() {
			while(true){
				//System.out.println("hello from inside runner");
				repaint();
				try {
					String cmd = in.readLine();
					//System.out.println(cmd);
					char action = cmd.charAt(0);
					if (action == 'i') {
						hicksCON.setForeground(Color.GREEN);
						hicksCON.setText("Connected");
						PLAYERS = 2;
						startMP.setEnabled(true);
						repaint();
					}
					if (action == 'u') {
						hudsonCON.setForeground(Color.GREEN);
						hudsonCON.setText("Connected");
						PLAYERS = 3;
					}
					if (action == 's') {
						sargeCON.setForeground(Color.GREEN);
						sargeCON.setText("Connected");
						PLAYERS = 4;
					}
					if (action == 't'){
						//starting game
						System.out.println("Starting game for player["+clientID+"]");
						if (clientID != 0){
							startMP.setEnabled(true);
							startMP.doClick(1);
						}
						break;
					}
				} catch (Exception ex) {
					System.out.println(ex.getMessage());
				}				
			}
			System.out.println("While loop over for " + clientID);
		}
	}
	
	/** private class for when the s key is pressed on the
	* user's keyboard
	*@extends AbstractAction
	*/
	private class SAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("s key pressed");
			if (sortMode && clientID == getCurrentPlayer()){
				System.out.println("sorting rack");
					int id = getCurrentPlayer();
					sort(id);
					if (!singlePlayer)
						out.println('x');
					switchTurns(id);
			}
		}
   }
   
   /** private class for when the h key is pressed on the
	* user's keyboard
	*@extends AbstractAction
	*/
   private class HAction extends AbstractAction {
	   @Override
	   public void actionPerformed(ActionEvent e) {
			System.out.println("hint key pressed for: " + clientID);
			if (clientID == getCurrentPlayer()) {
				getHint(clientID);
			}
	   }
   }
   
   /*
   * provides a hint when it's your turn
   */
   private void getHint(int id) {
		int dvalue = discard.pile.peek().value;
		int cardsPerSlot;
		if (PLAYERS == 2) {
			cardsPerSlot = 4;
		} else if (PLAYERS == 3) {
			cardsPerSlot = 5;
		} else {
			cardsPerSlot = 6;
		}
		System.out.println("Cards per slot = "+ cardsPerSlot);
		int slot = ((dvalue-1) / cardsPerSlot);
		System.out.println("slot =" + slot);
		boolean slotTaken;
		if (rack[id].cards[slot].value <= (cardsPerSlot * (slot+1)) 
			&& rack[id].cards[slot].value >= (cardsPerSlot * slot)) {
			System.out.println("nah brah slots good");
			deckToBlink = main;
			dblinker.start();
		} else {
			System.out.println("slot needs fillin bruh bruh");
			deckToBlink = discard;
			dblinker.start();
			toBeBlinked = slot;
			rackToBlink = id;
			oldBorderCard = rack[id].cards[slot].getBorder();
			cblinker.start();
			
		}
		
   }
   

	
	/** sort method used in the sort cheat mode 
	*/
	public void sort(int id){
		System.out.println("sorting rack");
		ArrayList<Integer> toBeSorted = new ArrayList<Integer>();
		for (int i=0;i<10;i++){
			toBeSorted.add(new Integer(player[id].rack.get(i).value));
			System.out.println(toBeSorted.get(i));
		}
		Collections.sort(toBeSorted);
		player[id].rack.clear();
		for (int j=0;j<10;j++){
			player[id].rack.add(new Card(toBeSorted.get(j), DECK_SIZE));
		}
		player[id].lastMove = "CHEATED";
		rack[id].remakeRack();
		for (int k=0;k<10;k++){
			rack[id].cards[k].addActionListener(rl[k]);
		}
	}
	
	/** listens for other player's turns coming from the server
	*@implements Runnable
	*/
	private class TurnListener implements Runnable {
		public void run() {
			System.out.println("Starting turn listener for " + clientID);
			while (true) {
				revalidate();
				repaint();
				try {
					//get deck info
					System.out.println(clientID + " waiting for turn info (which deck)");
					String whichDeck = in.readLine();
					System.out.println(clientID + " got " + whichDeck);  
					char action = whichDeck.charAt(0);
					int currentID = getCurrentPlayer();
					rackToBlink = currentID;
					if (action == 'm') {
						player[currentID].lastMove = "Drew from main ";
						player[currentID].drawnFromMain++;
						System.out.println(getCurrentPlayer() + "clicked on main");
						player[currentID].current = main.pile.peek();
						deckToBlink = main;
						dblinker.start();						
					} else if (action == 'd') {
						player[currentID].lastMove = ("Drew from discard["+discard.pile.peek().value+"] ");
						player[currentID].drawnFromDiscard++;
						System.out.println(getCurrentPlayer() + "clicked discard");
						player[currentID].current = discard.pile.peek();
						deckToBlink = discard;
						dblinker.start();
					} else if (action == 'x') {
						player[currentID].lastMove = "CHEATED";
						sort(currentID);
						switchTurns(currentID);
						continue;
					}
					//get next move
					System.out.println(clientID + " waiting for turn info (swap?)");
					String nextMove = in.readLine();
					System.out.println(clientID + " got " + nextMove);
					action = nextMove.charAt(0);

					if (action == '0') {
						System.out.println(getCurrentPlayer() + " clicked rack 0");
						networkSwap(0);

					} else if (action == '1') {
						System.out.println(getCurrentPlayer() + " clicked rack 1");
						networkSwap(1);

					} else if (action == '2') {
						System.out.println(getCurrentPlayer() + " clicked rack 2");
						networkSwap(2);

					} else if (action == '3') {
						System.out.println(getCurrentPlayer() + " clicked rack 3");
						networkSwap(3);
						
					} else if (action == '4') {
						System.out.println(getCurrentPlayer() + " clicked rack 4");
						networkSwap(4);

					} else if (action == '5') {
						System.out.println(getCurrentPlayer() + " clicked rack 5");
						networkSwap(5);

					} else if (action == '6') {
						System.out.println(getCurrentPlayer() + " clicked rack 6");
						networkSwap(6);

					} else if (action == '7') {
						System.out.println(getCurrentPlayer() + " clicked rack 7");
						networkSwap(7);

					} else if (action == '8') {
						System.out.println(getCurrentPlayer() + " clicked rack 8");
						networkSwap(8);
						
					} else if (action == '9') {
						System.out.println(getCurrentPlayer() + " clicked rack 9");
						networkSwap(9);
					} else if (action == 'd') {
						System.out.println(getCurrentPlayer() + " discarded");
						networkDiscard();
					} else if (action == 'x') {
						player[currentID].lastMove = "CHEATED";
						sort(currentID);
					}
				} catch(IOException ioe) {
					System.out.println(ioe.getMessage());
					return;
					
				}
			}
		}
	}
	
	/** subclass of JPopupMenu that is used in multiplayer mode to
	* keep track of stats during gameplay
	*@extends JPopupMenu
	*/
	private class ContextMenu extends JPopupMenu {
		int drawnFromMain;
		int drawnFromDiscard;
		int swaps;
		int currentScore;
		int id;
		JMenuItem dfm;
		JMenuItem dfd;
		JMenuItem swapped;
		JMenuItem cScore;
		public ContextMenu(int id) {
			this.id = id;
			this.drawnFromMain = player[id].drawnFromMain;
			this.drawnFromDiscard = player[id].drawnFromDiscard;
			this.swaps = player[id].swaps;
			this.currentScore = player[id].getCurrentScore();
			dfm = new JMenuItem("Drawn from main: " + drawnFromMain);
			dfd = new JMenuItem("Drawn from discard: " + drawnFromDiscard);
			swapped = new JMenuItem("Swapped: " + swaps);
			cScore = new JMenuItem("Current Score: " + currentScore);
			this.add(dfm);
			this.add(dfd);
			this.add(swapped);
			this.add(cScore);
		}
		
		@Override
		public void paintComponent(Graphics g) {
			drawnFromMain = player[id].drawnFromMain;
			drawnFromDiscard = player[id].drawnFromDiscard;
			swaps = player[id].swaps;
			currentScore = player[id].getCurrentScore();
			dfm.setText("Drawn from main: " + drawnFromMain);
			dfd.setText("Drawn from discard: " + drawnFromDiscard);
			swapped.setText("Swapped: " + swaps);
			cScore.setText("Current Score: " + currentScore);
		}   
		
	}
}