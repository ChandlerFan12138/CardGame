package structures.basic;


import java.util.ArrayList;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;

/**
 * A basic representation of of the Player. A player
 * has health and mana.
 * 
 * @author Dr. Richard McCreadie
 * 
 * @author Yunyi, Yusheng
 *
 */
public class Player{

	int health;
	int mana;
	
	//constructors
	public Player() {
		super();
		this.health = 20;
		this.mana = 0;
	}
	public Player(int health, int mana) {
		super();
		this.health = health;
		this.mana = mana;
	}
	
	//getters and setters
	public int getHealth() {
		return health;
	}
	public void setHealth(int health) {
		this.health = health;
	}

	/*
	 * set human player's health 
	 * draw human player's health
	*/
	public void setPlayerHealth(ActorRef out, int health) {
		this.health = health;
		BasicCommands.setPlayer1Health(out, this);
	}
	/*
	 * set AIplayer's health 
	 * draw AIplayer's health
	*/
	public void setAIPlayerHealth(ActorRef out, int health) {
		this.health = health;
		BasicCommands.setPlayer2Health(out, this);
	}
	public int getMana() {
		return mana;
	}
	public void setMana(int mana) {
		this.mana = mana;
	}
	
	/*
	 * this method set and draw mana of human player
	 */
	public void setPlayer1Mana(ActorRef out, int mana) {
		//when mana is less than and equals to 9
		if(mana <= 9) {
			for (int m = 0; m<= mana; m++) {
				this.setMana(m);
				BasicCommands.setPlayer1Mana(out, this);
				try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
			}
		}else {
			for (int m = 0; m<= 9; m++) {
				this.setMana(m);
				BasicCommands.setPlayer1Mana(out, this);
				try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
			}
		}
	}
	/*
	 * this method set and draw mana of AIplayer
	 */
	public void setPlayer2Mana(ActorRef out, int mana) {
		if(mana <= 9) {
			for (int m = 0; m<=mana; m++) {
				this.setMana(m);
				BasicCommands.setPlayer2Mana(out, this);
				try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
			}
		}else {
			for (int m = 0; m<=9; m++) {
				this.setMana(m);
				BasicCommands.setPlayer2Mana(out, this);
				try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
			}
		}	
	}
	
	/*
	 * this method delete the used card in hand
	 */
	public void deleteCardInHand(ActorRef out, GameState gameState) {
		//when click a card then click a valid tile
		//delete the card in the hand and reorder the hand cards
		//the position in the playerHandCard (0-5)
		int position = gameState.playerHandCard.indexOf(gameState.selectedCard);
		gameState.playerHandCard.remove(gameState.selectedCard);
		//BasicCommands.addPlayer1Notification(out, ""+gameState.playerHandCard.get(position), 3);
		BasicCommands.deleteCard(out, position+1);
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
		//re-order of card
		if(gameState.playerHandCard.size() > 0) {
			for(int i = position; i< gameState.playerHandCard.size(); i++) {
				//BasicCommands.addPlayer1Notification(out, ""+gameState.playerHandCard.get(position).getCardname(), 3);
				BasicCommands.drawCard(out, gameState.playerHandCard.get(i), i+1, 0);
				try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
			}
		}
		BasicCommands.deleteCard(out, gameState.playerHandCard.size()+1);
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
	}
		
	/*
	 * play a card
	 * call play method of card.class
	 * if the card is a unit card, a unit will be summone
	 * if the card is a spell card, use its ability on a unit
	 * cost corresponding mana and call deleteCardInHand method if the card has been used successfully
	 */
	public void playCard(ActorRef out, GameState gameState, int tilex, int tiley) {
		
		//play a card
		if(gameState.selectedCard.play(out, gameState, tilex, tiley)) {
			//delete the used card in the hand
			deleteCardInHand(out, gameState);
			
			//cost mana 
			//System.out.println(gameState.selectedCard.getManacost());
			this.setMana(mana-gameState.selectedCard.getManacost());
			BasicCommands.setPlayer1Mana(out, this);
			try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
			//delete the selectedCard in the gameState
			gameState.selectedCard = null;
		}else {
			BasicCommands.addPlayer1Notification(out, "Incorrect Selected Unit", 2);
		}
	}
	
	/*
	 * this method find and highlight tiles where units with summoning anywhere can
	 * be summoned
	 */
	public ArrayList<Tile> Fly_Tile_Justify(ActorRef out, GameState gameState) {
		ArrayList<Tile> temp_Tile = new ArrayList<Tile>();
		for(int i = 0; i<5; i++) {
			for(int j = 0; j< 9; j++) {
				if(gameState.boardTiles[i][j].isHasUnit() == false) {
					gameState.boardTiles[i][j].setCurrentTileTexture(1);
					temp_Tile.add(gameState.boardTiles[i][j]);
				}
			}
		}
		if(gameState.isPlayerTurn) {
			for(Tile tile : temp_Tile) {
				BasicCommands.drawTile(out, tile, 1);
				try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
			}
		}
		return temp_Tile;
	}
}