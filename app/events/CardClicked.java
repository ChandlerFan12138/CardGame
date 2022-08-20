package events;


import java.util.ArrayList;
import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.*;
import utils.BasicObjectBuilders;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case a card.
 * The event returns the position in the player's hand the card resides within.
 * 
 * { 
 *   messageType = “cardClicked”
 *   position = <hand index position [1-6]>
 * }
 *  * Member:
 * Yusheng Fan 2660781f
 * Yunyi Wang 2599297W
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class CardClicked implements EventProcessor{
	
	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		
		int handPosition = message.get("position").asInt(); 
		
		gameState.isCardClicked = true;
		
		this.Highlighting(out, gameState, handPosition); //call the method of highlighting
		
		gameState.selectedCard = gameState.playerHandCard.get(handPosition-1); 
		BasicCommands.drawCard(out, gameState.selectedCard, handPosition, 1);

		gameState.isTileClicked = false;
		
	}
	//method: used to highlight all tiles which satisfy the requirements
	public void Highlighting(ActorRef out, GameState gameState, int handPosition) {
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
		ArrayList<Integer> Spell_Fly_Id = new ArrayList<>(); //used to store the id of Spell AND the unit has fly ability
		Spell_Fly_Id.add(14); Spell_Fly_Id.add(18); Spell_Fly_Id.add(32); Spell_Fly_Id.add(37);Spell_Fly_Id.add(4);Spell_Fly_Id.add(8);Spell_Fly_Id.add(22);Spell_Fly_Id.add(27); 
		Spell_Fly_Id.add(6);Spell_Fly_Id.add(16); 
		if(gameState.isPlayerTurn) {
			BasicCommands.drawCard(out, gameState.playerHandCard.get(handPosition-1), handPosition, 1);	//set the clicked card to mode 1
			try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
			if((gameState.playerHandCard.get(handPosition-1)).getManacost() <= gameState.Player1.getMana()&&
			!Spell_Fly_Id.contains(gameState.playerHandCard.get(handPosition-1).getId())) { //justify if player's mana is sufficient and if it is a UnitCard
				ArrayList<Tile> tempTile = new ArrayList<Tile>();// set a list to store all will-be highlighting tiles
				for(Unit unit:gameState.playerUnitList) {//store all tiles around the position of units in PlayerUnitList
					tempTile.add(BasicObjectBuilders.loadTile(unit.getPosition().getTilex()+1, unit.getPosition().getTiley()+1));
					tempTile.add(BasicObjectBuilders.loadTile(unit.getPosition().getTilex()+1, unit.getPosition().getTiley()));
					tempTile.add(BasicObjectBuilders.loadTile(unit.getPosition().getTilex()+1, unit.getPosition().getTiley()-1));
					tempTile.add(BasicObjectBuilders.loadTile(unit.getPosition().getTilex(), unit.getPosition().getTiley()+1));
					tempTile.add(BasicObjectBuilders.loadTile(unit.getPosition().getTilex(), unit.getPosition().getTiley()-1));
					tempTile.add(BasicObjectBuilders.loadTile(unit.getPosition().getTilex()-1, unit.getPosition().getTiley()+1));
					tempTile.add(BasicObjectBuilders.loadTile(unit.getPosition().getTilex()-1, unit.getPosition().getTiley()));
					tempTile.add(BasicObjectBuilders.loadTile(unit.getPosition().getTilex()-1, unit.getPosition().getTiley()-1));
				}
				for(Iterator<Tile> iterator = tempTile.iterator(); iterator.hasNext();) {//remove the tile out of board
					Tile tile = iterator.next();
					if(tile.getTilex()>8||tile.getTilex()<0||tile.getTiley()<0||tile.getTiley()>4) {
						iterator.remove();
					}else {
						continue;
					}
				}
				for(Unit unit:gameState.playerUnitList) {// remove the tile which already has a player unit
					for(Iterator<Tile> iterator = tempTile.iterator(); iterator.hasNext();) {
						Tile tile = iterator.next();
						
						if(tile.getTilex()==unit.getPosition().getTilex()&&tile.getTiley()==unit.getPosition().getTiley()) {
							iterator.remove();
						}
					}
				}
				for(Unit unit:gameState.AIUnitList) {//remove the tile which already has a AI unit
					for(Iterator<Tile> iterator = tempTile.iterator(); iterator.hasNext();) {
						Tile tile = iterator.next();
						
						if(tile.getTilex()==unit.getPosition().getTilex()&&tile.getTiley()==unit.getPosition().getTiley()) {
							iterator.remove();
						}
					}
				}
				for(Tile tile:tempTile) {//Highlighting and set the attribute in Boardtiles[][](currentTexture)
					BasicCommands.drawTile(out, tile, 1);
					try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
					gameState.boardTiles[tile.getTiley()][tile.getTilex()].setCurrentTileTexture(1);
				}
			}else if((gameState.playerHandCard.get(handPosition-1)).getManacost() <= gameState.Player1.getMana()&&
			Spell_Fly_Id.contains(gameState.playerHandCard.get(handPosition-1).getId())) { //justify if the mana is sufficient and it is a spell card or units which can be summoned anywhere
				Card spellCard = gameState.playerHandCard.get(handPosition-1); 
				if(spellCard.getId() == 8||spellCard.getId() ==18||spellCard.getId() ==27||spellCard.getId() ==37) {//Highlight the tiles of spell card
					for(Unit unit:gameState.playerUnitList) {
						Tile a = BasicObjectBuilders.loadTile(unit.getPosition().getTilex(), unit.getPosition().getTiley());
						BasicCommands.drawTile(out, a, 2);//Highlighting
						try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
						gameState.boardTiles[a.getTiley()][a.getTilex()].setCurrentTileTexture(2);//set the tile to texture mode2
					}
				}else if (spellCard.getId() == 4||spellCard.getId() ==14||spellCard.getId() ==22||spellCard.getId() ==32) {//Highlight the tiles of spell card
					for(Unit unit:gameState.AIUnitList) {
						Tile b = BasicObjectBuilders.loadTile(unit.getPosition().getTilex(), unit.getPosition().getTiley());
						BasicCommands.drawTile(out, b, 2);//if its object is AIrunit
						try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
						gameState.boardTiles[b.getTiley()][b.getTilex()].setCurrentTileTexture(2);//set the tile to texture mode2
					}
				}
				else {
					gameState.Player1.Fly_Tile_Justify(out, gameState); //call the method of highlight the tiles without units
				}
			}
		}
	}
}

