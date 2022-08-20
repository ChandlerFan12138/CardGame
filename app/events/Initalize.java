package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.*;
import utils.BasicObjectBuilders;
import utils.OrderedCardLoader;
import utils.StaticConfFiles;

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

public class Initalize implements EventProcessor{
	
	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
gameState.gameInitalised = true;
		
		//Initialization @Shiyu Ren
		BasicCommands.addPlayer1Notification(out, "Establishing battle's control, stanby...", 2);
		
		//Draw board
		BasicCommands.addPlayer1Notification(out, "Now Board...", 2);

		//Draw all tiles
		for(int i = 0;i < 5;i++) {
			for(int j = 0; j < 9; j++) {
				Tile tile = BasicObjectBuilders.loadTile(j, i); 
				gameState.boardTiles[i][j] = tile;
				BasicCommands.drawTile(out, gameState.boardTiles[i][j], 0);
			}
		}
		
		//Initialize player data
		//Human
		BasicCommands.addPlayer1Notification(out, "Now Player1...", 2);
		BasicCommands.addPlayer1Notification(out, "Setting P1 HP...", 1);
		Player humanPlayer = new Player(20, 0);
		gameState.Player1= humanPlayer;
		BasicCommands.setPlayer1Health(out, gameState.Player1);

				//Human mana
		for (int m = 0; m<3; m++) {
			BasicCommands.addPlayer1Notification(out, "setPlayer1Mana ("+m+")", 1);
			gameState.Player1.setMana(m);
			BasicCommands.setPlayer1Mana(out, gameState.Player1);
		}
		
			//Initialize AI data
		BasicCommands.addPlayer1Notification(out, "Now AIPlayer...", 2);
		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();} 
		BasicCommands.addPlayer1Notification(out, "Setting AI HP...", 1);
		AI aiPlayer = new AI(20, 0);
		gameState.AIPlayer=aiPlayer;
		BasicCommands.setPlayer2Health(out,  gameState.AIPlayer);
				//AI mana
		for (int m = 0; m<3; m++) {
			BasicCommands.addPlayer1Notification(out, "setAIMana ("+m+")", 1);
			gameState.AIPlayer.setMana(m);
			BasicCommands.setPlayer2Mana(out, gameState.AIPlayer);
		}
		
		
		
		//Deploy avatars
			//P1 AVA
		BasicCommands.addPlayer1Notification(out, "drawUnit P1 AVA NOW...", 2);
		Unit unit0 = BasicObjectBuilders.loadUnit(StaticConfFiles.humanAvatar, 77, Unit.class);
		unit0.setHealth(20);
		unit0.setStartHealth(20);
		unit0.setAttack(2);
		unit0.setStartAttack(2);
		unit0.setUnitName("Human Avatar");
		Tile tP1AvaIni = BasicObjectBuilders.loadTile(1, 2);
		unit0.setPositionByTile(tP1AvaIni); 
		gameState.playerUnitList.add(unit0); 
		gameState.playerUnitList.get(0).moved = false;
		gameState.playerUnitList.get(0).attacked = false;
		gameState.playerUnitList.get(0).setPositionByTile(gameState.boardTiles[2][1]);
		gameState.boardTiles[2][1].setHasUnit(true);
		gameState.boardTiles[2][1].setUnitId(77);
		BasicCommands.drawUnit(out, gameState.playerUnitList.get(0), tP1AvaIni);

				//P1 AVA Animation:summon
		BasicCommands.addPlayer1Notification(out, "playEffectAnimation P1 AVA",2);

		BasicCommands.addPlayer1Notification(out, StaticConfFiles.f1_summon, 0);
		EffectAnimation ef = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_summon);
		BasicCommands.playEffectAnimation(out, ef, tP1AvaIni);
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
				// setUnitAttack
		BasicCommands.addPlayer1Notification(out, "setUnitAttack P1 AVA", 2);
		BasicCommands.setUnitAttack(out,gameState.playerUnitList.get(0), 2);
		try {Thread.sleep(0);} catch (InterruptedException e) {e.printStackTrace();}
				// setUnitHealth
		BasicCommands.addPlayer1Notification(out, "setUnitHealth P1 AVA", 2);
		BasicCommands.setUnitHealth(out, gameState.playerUnitList.get(0), 20);
		try {Thread.sleep(0);} catch (InterruptedException e) {e.printStackTrace();}
		
		
			//AI AVA
		BasicCommands.addPlayer1Notification(out, "drawUnit AI AVA NOW...", 2);
		Unit unit1 = BasicObjectBuilders.loadUnit(StaticConfFiles.aiAvatar, 88, Unit.class);
		unit1.setHealth(20);
		unit1.setStartHealth(20);
		unit1.setAttack(2);
		unit1.setStartAttack(2);
		unit1.setUnitName("AI Avatar");
		Tile AIAvaIni = BasicObjectBuilders.loadTile(7, 2);
		unit1.setPositionByTile(AIAvaIni); 
		gameState.AIUnitList.add(unit1);
		gameState.AIUnitList.get(0).moved = false;
		gameState.AIUnitList.get(0).attacked = false;
		
		gameState.AIUnitList.get(0).setPositionByTile(gameState.boardTiles[2][7]);
		gameState.boardTiles[2][7].setHasUnit(true);
		gameState.boardTiles[2][7].setUnitId(88);
		BasicCommands.drawUnit(out,gameState.AIUnitList.get(0),AIAvaIni);

				//AI AVA Animation:summon
		BasicCommands.addPlayer1Notification(out, "playEffectAnimation AI AVA",2);
		BasicCommands.addPlayer1Notification(out, StaticConfFiles.f1_summon, 0);
		EffectAnimation ef1 = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_summon);
		BasicCommands.playEffectAnimation(out, ef1, AIAvaIni);
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
				// setUnitAttack
	    BasicCommands.addPlayer1Notification(out, "setUnitAttack AI AVA", 2);
		BasicCommands.setUnitAttack(out, gameState.AIUnitList.get(0), 2);
		try {Thread.sleep(0);} catch (InterruptedException e) {e.printStackTrace();}
				// setUnitHealth
		BasicCommands.addPlayer1Notification(out, "setUnitHealth AI AVA", 2);
		BasicCommands.setUnitHealth(out,gameState.AIUnitList.get(0),20);
		try {Thread.sleep(0);} catch (InterruptedException e) {e.printStackTrace();}
				
		//Load Human player's deck
		gameState.player1Deck=OrderedCardLoader.getPlayer1Cards();
		//Load Human player's deck
		gameState.AIDeck=OrderedCardLoader.getPlayer2Cards();

		
		//Human draw card
		gameState.playerHandCard.add(gameState.player1Deck.get(0));
		gameState.playerHandCard.add(gameState.player1Deck.get(1));
		gameState.playerHandCard.add(gameState.player1Deck.get(2));
		gameState.player1Deck.remove(0);
		gameState.player1Deck.remove(0);
		gameState.player1Deck.remove(0);

			//Draw cards
		BasicCommands.drawCard(out,gameState.playerHandCard.get(0), 1, 0);
		BasicCommands.drawCard(out,gameState.playerHandCard.get(1), 2, 0);
		BasicCommands.drawCard(out,gameState.playerHandCard.get(2), 3, 0);

		
		
			//AI draw card
		gameState.AIHandCard.add(gameState.AIDeck.get(0));
		gameState.AIHandCard.add(gameState.AIDeck.get(1));
		gameState.AIHandCard.add(gameState.AIDeck.get(2));
		gameState.AIDeck.remove(0);
		gameState.AIDeck.remove(0);
		gameState.AIDeck.remove(0);	
		
		if(gameState.isPlayerTurn) {
			BasicCommands.addPlayer1Notification(out, "Battle's control online",2);
			try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
		}

	}

}


