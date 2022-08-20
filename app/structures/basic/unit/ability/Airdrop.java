package structures.basic.unit.ability;



import java.util.ArrayList;

import akka.actor.ActorRef;
import akka.parboiled2.Position;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;
import structures.GameState;

public class Airdrop {
	// 相关可以在任意空格上召唤
	// The associated unit can be summoned on any free tile
	public static void airdrop(ActorRef out,GameState gameState, Card card, Tile targetTile) {
		if (validTile(targetTile)) {
			//在目标格子上召唤
//			summon(card,targetTile);
		}
	}
	
	public static ArrayList<Tile> highlightSummon(ActorRef out,GameState gameState, Unit unit){
		ArrayList<Tile> validMovingTiles = new ArrayList<Tile>();
		if (!unit.moved) {
			for(int i=0;i<5;i++) {
				for(int j=0;j<9;j++) {
					if(Airdrop.validTile(gameState.boardTiles[i][j])) {
						//highlight 
						gameState.boardTiles[i][j].setCurrentTileTexture(1);//texture设置为可召唤
						
						BasicCommands.addPlayer1Notification(out, "Highlight Summon", 1);
						BasicCommands.drawTile(out, gameState.boardTiles[i][j], 1);//高亮格子
						try {Thread.sleep(200);} catch (InterruptedException e) {e.printStackTrace();}
						
						validMovingTiles.add(gameState.boardTiles[i][j]);// add valid tile to list
					}
				}
			}
		}
		return validMovingTiles;
	}
	
	private static boolean validTile(Tile targetTile) {
		// check if there is a unit on the target tile
		if(targetTile.getUnitId()<= Unit.MAX_UNIT_ID) {
			return false;
		}
		return true;
	}
	
}
