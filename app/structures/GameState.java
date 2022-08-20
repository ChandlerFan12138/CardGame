package structures;

import java.util.ArrayList;
import java.util.List;


import structures.basic.*;


/**
 * This class can be used to hold information about the on-going game.
 * Its created with the GameActor.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class GameState {

	
	public boolean gameInitalised = false;
	public int turn = 1;
	public boolean isCardClicked = false;
	public boolean isTileClicked = false;
	public boolean isEndTurnClicked = false;
	public boolean isGameOver = false;
	// whether the turn is human player's turn
	public boolean isPlayerTurn = true;
	
//	//if player can play card on the clicked tile
//	public boolean isTileValid = true;
	
	
	//test
	public boolean EndTurnClicked = false;
	public boolean OtherClicked = false;
	public boolean HearBeat = false;
	
	
	
	public Card selectedCard;
	
	//the array of tile
	public Tile[][] boardTiles = new Tile[5][9];
	
	//players
	public Player Player1;
	public Player AIPlayer;
	
	public List<Card> player1Deck = new ArrayList<Card>(20);
	public List<Card> AIDeck = new ArrayList<Card>(20);
	//cards in hand
	public List<Card>playerHandCard = new ArrayList<Card>();
	public List<Card> AIHandCard = new ArrayList<Card>();
	//units on the board
	public List<Unit> playerUnitList = new ArrayList<Unit>();
	public List<Unit> AIUnitList = new ArrayList<Unit>();
	
	//if silverguard Knight has been summoned
	public boolean hasSilverguardKnight = false;
	
}
