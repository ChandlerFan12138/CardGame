package structures.basic;
import structures.GameState;
import structures.basic.unit.action.AdjacentAttack;
import utils.BasicObjectBuilders;
import commands.BasicCommands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import akka.actor.ActorRef;

/* WindShrike @Yusheng, Yunyi, YuZeng
 * this class reprents the card WindShrike
 * special ability:
 * when it dead, its player draw a card
 */

public class WindShrike extends Unit{
	
	//constructor
	public WindShrike() {
		super();
		this.flying = true;
	}
	
	//its special ability
	// it can move anywhere on the board
	public void fly(ActorRef out, GameState gameState) {//method: The ability of fly, the unit will choose to move to the adjacent tiles of player's unit if it can
		Unit unit = gameState.playerUnitList.get(0); // to extract the player's avatar
		ArrayList<Tile> temp_Tile = new ArrayList<Tile>();// List to store targeted tiles
		if(unit.getPosition().getTiley()+1>=0 &&unit.getPosition().getTiley()+1<=4 && unit.getPosition().getTilex()-1>=0 && unit.getPosition().getTilex()-1<=8) {
			temp_Tile.add(gameState.boardTiles[unit.getPosition().getTiley()+1][unit.getPosition().getTilex()-1]);
		}
		if(unit.getPosition().getTiley()+1>=0 &&unit.getPosition().getTiley()+1<=4 && unit.getPosition().getTilex()>=0 && unit.getPosition().getTilex()<=8) {
			temp_Tile.add(gameState.boardTiles[unit.getPosition().getTiley()+1][unit.getPosition().getTilex()]);
		}
		if(unit.getPosition().getTiley()+1>=0 &&unit.getPosition().getTiley()+1<=4 && unit.getPosition().getTilex()+1>=0 && unit.getPosition().getTilex()+1<=8) {
			temp_Tile.add(gameState.boardTiles[unit.getPosition().getTiley()+1][unit.getPosition().getTilex()+1]);
		}
		if(unit.getPosition().getTiley()>=0 &&unit.getPosition().getTiley()<=4 && unit.getPosition().getTilex()+1>=0 && unit.getPosition().getTilex()+1<=8) {
			temp_Tile.add(gameState.boardTiles[unit.getPosition().getTiley()][unit.getPosition().getTilex()+1]);
		}
		if(unit.getPosition().getTiley()>=0 &&unit.getPosition().getTiley()<=4 && unit.getPosition().getTilex()-1>=0 && unit.getPosition().getTilex()-1<=8) {
			temp_Tile.add(gameState.boardTiles[unit.getPosition().getTiley()][unit.getPosition().getTilex()-1]);
		}
		if(unit.getPosition().getTiley()-1>=0 &&unit.getPosition().getTiley()-1<=4 && unit.getPosition().getTilex()+1>=0 && unit.getPosition().getTilex()+1<=8) {
			temp_Tile.add(gameState.boardTiles[unit.getPosition().getTiley()-1][unit.getPosition().getTilex()+1]);
		}
		if(unit.getPosition().getTiley()-1>=0 &&unit.getPosition().getTiley()-1<=4 && unit.getPosition().getTilex()>=0 && unit.getPosition().getTilex()<=8) {
			temp_Tile.add(gameState.boardTiles[unit.getPosition().getTiley()-1][unit.getPosition().getTilex()]);
		}
		if(unit.getPosition().getTiley()-1>=0 &&unit.getPosition().getTiley()-1<=4 && unit.getPosition().getTilex()-1>=0 && unit.getPosition().getTilex()-1<=8) {
			temp_Tile.add(gameState.boardTiles[unit.getPosition().getTiley()-1][unit.getPosition().getTilex()-1]);
		}
		for(Iterator<Tile> iterator = temp_Tile.iterator(); iterator.hasNext();) {//remove the tiles which already has units
			Tile tile = iterator.next();
			if(tile.isHasUnit()){
				iterator.remove();
			}
		}
		if (temp_Tile.size()>0) { //If the adjacnet tiles are empty, then fly
			//@YuZeng
			int Pre_Unitx = this.getPosition().getTilex();
			int Pre_Unity = this.getPosition().getTiley();
			Tile temp = BasicObjectBuilders.loadTile(Pre_Unitx, Pre_Unity);
			Tile tile = temp_Tile.get(new Random().nextInt(temp_Tile.size()));
			int num = Math.abs((this.getPosition().getTilex()-tile.getTilex()))+Math.abs((this.getPosition().getTiley()-tile.getTiley()));
			BasicCommands.moveUnitToTile(out, this, tile);
			this.setPositionByTile(out, tile,true);
			try {Thread.sleep(num*1000);} catch (InterruptedException e) {e.printStackTrace();}
			//initialize the tile
			gameState.boardTiles[Pre_Unity][Pre_Unitx] = temp;
			
			//if it will dead by counter attack when it attack a human player's avatar,
			//it will not attack the unit
			if((this.getHealth()<=gameState.playerUnitList.get(0).getAttack())
					&&  unit.getAttack()< gameState.playerUnitList.get(0).getHealth()) {
				//Debuff: Cowards, avoid death
				BasicCommands.addPlayer1Notification(out,"Unit "+unit.getId()+" does nothing because it would die if Unit "+gameState.playerUnitList.get(0).getId()+" counterstrikes on it.", 2);
				try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
			}
			else {
				//focus on aviator
				AdjacentAttack.adjacentAttack(out, gameState, this, gameState.playerUnitList.get(0));
			}

			
		}
	}
	
	public void setPositionByTile(ActorRef out, Tile tile,boolean a) {
		position = new Position(tile.getXpos(),tile.getYpos(),tile.getTilex(),tile.getTiley());
		tile.setUnitId(this.id);
		tile.setHasUnit(true);
	}
	
	@Override
	//when it dead, its player draw a card
	public void dead(ActorRef out, GameState gameState, int tilex, int tiley) {

		 //set 0 health
		 BasicCommands.setUnitHealth(out, this, 0);
		 try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
		 setHealth(0);
		 //check it belongs which player and delete unit from unit list
		 if(gameState.AIUnitList.contains(this)) {
		 	gameState.AIUnitList.remove(this);
		 }else if(gameState.playerUnitList.contains(this)){
		 	gameState.playerUnitList.remove(this);
		 }
		 //unit dead animation
		 BasicCommands.playUnitAnimation(out, this, UnitAnimationType.death);
		 try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
		 //delete the card
		 BasicCommands.deleteUnit(out, this);
		 gameState.boardTiles[tiley][tilex].setUnitId(999);
		 gameState.boardTiles[tiley][tilex].hasUnit=false;
		
		 //draw a card
		if(gameState.AIDeck.size()!=0) {
			if (gameState.AIHandCard.size() >= 6) {
				gameState.AIDeck.remove(0);
			}else{
				//ai draw a card
				gameState.AIHandCard.add(gameState.AIDeck.get(0));
				gameState.AIDeck.remove(0);
				System.out.println("WindShrike dead and draw card. ai player deck size:"+gameState.AIDeck.size());
			}
		}
	}
}
