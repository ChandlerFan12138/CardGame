package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;

/**
 * IT project 2022 Card Game
 * Team name: Tom Jerry and David Team
 * Member:
 * Yusheng Fan 2660781f
 * Zeyu Miao 2605917m  
 * Shiyu Ren 2518312r
 * Yunyi Wang 2599297W
 * 
 */

public class OtherClicked implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		gameState.isCardClicked = false;
		gameState.isTileClicked = false;
		//delete highlight on selected card
		BasicCommands.drawCard(out, gameState.selectedCard, gameState.playerHandCard.indexOf(gameState.selectedCard)+1, 0);
		this.deHighlighting(out, gameState);
	}
	public void deHighlighting(ActorRef out, GameState gameState) {
		for (int i =0 ;i<gameState.playerHandCard.size();i++) {
			BasicCommands.drawCard(out, gameState.playerHandCard.get(i), i+1, 0); //Firstly, set all cards mode 0
			try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
		}
		for(int i = 0;i < 5;i++) {
			for(int j = 0; j < 9; j++) {
				if(gameState.boardTiles[i][j].getCurrentTileTexture()!=0) {
					gameState.boardTiles[i][j].setCurrentTileTexture(0);
					BasicCommands.drawTile(out, gameState.boardTiles[i][j], 0);
					try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
				}
			}
		}
	}

}


