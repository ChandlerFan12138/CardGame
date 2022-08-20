package structures.basic.unit.ability;

import java.util.ArrayList;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;

public class Flying {
	
	public static void flying(ActorRef out, Unit unit, Tile departureTile, Tile destinationTile) {
		//if unit havsn't moved and the tile is valid
		if (Flying.validTile(destinationTile) && !unit.moved) {
			
			departureTile.setUnitId(999);
			departureTile.setHasUnit(false);
			
			// playUnitAnimation [Move]
			BasicCommands.addPlayer1Notification(out, "Unit "+unit.getId()+" moves to tile ("+destinationTile.getXpos()+","+destinationTile.getYpos()+")", 2);
			BasicCommands.playUnitAnimation(out, unit, UnitAnimationType.move);
			try {Thread.sleep(3000);} catch (InterruptedException e) {e.printStackTrace();}
			
			unit.setPositionByTile(destinationTile);
			unit.moved = true;
			destinationTile.setUnitId(unit.getId());
		}
	}
	
	private static ArrayList<Tile> highlightMove(ActorRef out,GameState gameState, Unit unit) {
		//高亮可移动到的格子并返回这些格子
		
		ArrayList<Tile> validMovingTiles = new ArrayList<Tile>();
		if (!unit.moved) {
			for(int i=0;i<5;i++) {
				for(int j=0;j<9;j++) {
					if(Flying.validTile(gameState.boardTiles[i][j])) {
						//highlight 
						gameState.boardTiles[i][j].setCurrentTileTexture(1);//texture设置为可移动
						
						BasicCommands.addPlayer1Notification(out, "Highlight Move", 1);
						BasicCommands.drawTile(out, gameState.boardTiles[i][j], 1);//高亮格子
//						try {Thread.sleep(200);} catch (InterruptedException e) {e.printStackTrace();}
						
						validMovingTiles.add(gameState.boardTiles[i][j]);// add valid tile to list
					}
				}
			}
		}
		return validMovingTiles;
	}
	
	private static boolean validTile(Tile destinationTile) {
		// check if there is a unit on the destination tile
		if(destinationTile.isHasUnit()) {
			//if there is a unit on the tile, return false
			return false;
		}
		return true;
	}
}
