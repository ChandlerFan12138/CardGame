package structures.basic.unit.action;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Position;
import structures.basic.Tile;
import structures.basic.TwiceAttackUnit;
import structures.basic.Unit;
import utils.BasicObjectBuilders;

/**
 * 
 * @author YuZeng
 * Find a valid shortest path for moving, then move to the tile and attack, move ability would be disabled after this action
 *
 *
 */

public class MoveAndAttack {
	
	public static void moveAndAttack(ActorRef out,GameState gameState,Unit attacker, Unit defender) {
		int xa = attacker.getPosition().getTilex();
		int ya = attacker.getPosition().getTiley();
		int xd = defender.getPosition().getTilex();
		int yd = defender.getPosition().getTiley();
		
		//Attacker will move to the shortest destination of (xaD,yaD), if it's invalid destination, try to find another valid destination
		//There would be 3 possible valid tile at most for moving. The first one would be the shortest terminal tile.
		int xaD = 0,yaD=0;
		int tempx = 0;
		int tempy = 0;
		if((xd-xa)!=0 && (yd-ya)!=0) {
			tempx = xd-(xd-xa)/Math.abs(xd-xa);
			tempy = yd-(yd-ya)/Math.abs(yd-ya);
			if(!reachable(out,gameState,xa,ya,tempx,tempy)) {
				tempx = xd;
				tempy = yd-(yd-ya)/Math.abs(yd-ya);
				if(!reachable(out,gameState,xa,ya,tempx,tempy)) {
					tempx = xd-(xd-xa)/Math.abs(xd-xa);
					tempy = yd;
				}
			}
		}
		else if(xd-xa == 0) {
			tempx = xd;
			tempy = yd-(yd-ya)/Math.abs(yd-ya);
			if(!reachable(out,gameState,xa,ya,tempx,tempy)) {
				tempx = xd + 1;
				tempy = yd-(yd-ya)/Math.abs(yd-ya);
				if(!reachable(out,gameState,xa,ya,tempx,tempy)) {
					tempx = xd - 1;
					tempy = yd-(yd-ya)/Math.abs(yd-ya);
				}
				
			}
		}
		else if(yd-ya == 0){
			tempx = xd-(xd-xa)/Math.abs(xd-xa);
			tempy = yd ;
			if(!reachable(out,gameState,xa,ya,tempx,tempy)) {
				tempx = xd-(xd-xa)/Math.abs(xd-xa);
				tempy = yd + 1;
				if(!reachable(out,gameState,xa,ya,tempx,tempy)) {
					tempx = xd-(xd-xa)/Math.abs(xd-xa);
					tempy = yd - 1;
				}
			}
		}
		if(reachable(out,gameState,xa,ya,tempx,tempy)) {
			xaD = tempx;
			yaD = tempy;
			
			System.out.println("Attacker desitination position:("+xaD+","+yaD+"), occupied:"+gameState.boardTiles[yaD][xaD].isHasUnit());
			Position destination = new Position(xaD-1,yaD-1, xaD, yaD);
			
			//Move
			if(Math.abs(xaD-xa)==1 && Math.abs(yaD-ya)==1) {
				int tilexDirectionHelper = xaD ;	// x = x of destination
				int tileyDirectionHelper = ya; // y = y of start point 
				
				if(gameState.boardTiles[tileyDirectionHelper][tilexDirectionHelper].isHasUnit()
				&&(gameState.boardTiles[tileyDirectionHelper][tilexDirectionHelper].getUnitId()>19
				&& gameState.boardTiles[tileyDirectionHelper][tilexDirectionHelper].getUnitId()!=77)){
					// y first
					System.out.println("y first");
					BasicCommands.moveUnitToTile(out, attacker, gameState.boardTiles[yaD][xaD], true);
					try {Thread.sleep(3000);} catch (InterruptedException e) {e.printStackTrace();}
				}
				else {
					System.out.println("x first");
					BasicCommands.moveUnitToTile(out, attacker, gameState.boardTiles[yaD][xaD]);
					try {Thread.sleep(3000);} catch (InterruptedException e) {e.printStackTrace();}
				}
			}
			else {
				BasicCommands.moveUnitToTile(out, attacker, gameState.boardTiles[yaD][xaD]);
				try {Thread.sleep(3000);} catch (InterruptedException e) {e.printStackTrace();}
			}

			
			//Update destination tile
			gameState.boardTiles[yaD][xaD].setHasUnit(true);
			gameState.boardTiles[yaD][xaD].setUnitId(attacker.getId());
			//Initialize start tile
			gameState.boardTiles[ya][xa].setHasUnit(false);
			gameState.boardTiles[ya][xa].setUnitId(999);
			//Update unit position
			attacker.setPosition(destination);
			attacker.moved = true;
			
			//Attack
			if (attacker.canAttackTwice()) {
				TwiceAttack.tiwceAttack(out, gameState, (TwiceAttackUnit) attacker, defender);
			}
			else {
				AdjacentAttack.adjacentAttack(out, gameState, attacker, defender);
			}
			
			
		}
		else {
			System.err.println("Invalid desination for move and attack:("+tempx+","+tempy+")");
		}



		
	}
	
	
	private static boolean reachable(ActorRef out,GameState gameState,int tilexs,int tileys,int tilexd,int tileyd) {
		// Test if the destination tile is valid for target unit to move on
		if(tilexd > 8 || tilexd< 0 || tileyd > 4 || tileyd < 0) {	
			return false;// outside boarder
		}
		
		int xDistance = Math.abs(tilexd-tilexs);
		int yDistance = Math.abs(tileyd-tileys);
		int totalDistance = xDistance + yDistance;
		if(totalDistance>2) {
			return false; // out of move range
		}
		else {
			if(xDistance==0 && yDistance !=0) {

				if(gameState.boardTiles[tileyd][tilexd].isHasUnit()) {
					/*   X       D
					 *   ? _  or X _
					 *   U _ _   U _ _ 
					 */
					return false; // is occupied
				}
				else {
					int middleTilex = tilexd;
					int middleTiley = tileyd - (tileyd-tileys)/Math.abs(tileyd-tileys);
					
					if (gameState.boardTiles[middleTiley][middleTilex].isHasUnit()
							&& (gameState.boardTiles[middleTiley][middleTilex].getUnitId()>19 
									&& gameState.boardTiles[middleTiley][middleTilex].getUnitId()!=77)//has an enemy unit
							) {
						return false; //is blocked
					}
				}
			}
			else if(xDistance !=0 && yDistance==0) {
				if(gameState.boardTiles[tileyd][tilexd].isHasUnit()) {
					/*   _       _
					 *   _ _  or _ _
					 *   S ? X   S X D 
					 */
					return false; // is occupied
				}
				else {
					int middleTilex = tilexd - (tilexd - tilexs)/Math.abs(tilexd - tilexs) ;
					int middleTiley = tileyd ;
					
					if (gameState.boardTiles[middleTiley][middleTilex].isHasUnit()
							&& (gameState.boardTiles[middleTiley][middleTilex].getUnitId()>19 
									&& gameState.boardTiles[middleTiley][middleTilex].getUnitId()!=77)//has an enemy unit 
							) {
						return false; //is blocked
					}
				}	
			}
			else if(xDistance==1 && yDistance==1) {
				if(gameState.boardTiles[tileyd][tilexd].isHasUnit()) {
					/* _
					 * ? X
					 * S ? _
					 */
					
					return false; // is occupied
				}
				int middle1Tilex = tilexd - (tilexd - tilexs)/Math.abs(tilexd - tilexs);
				int middle1Tiley = tileyd ;
				
				int middle2Tilex = tilexd;
				int middle2Tiley = tileyd - (tileyd-tileys)/Math.abs(tileyd-tileys);
				
				if(         (gameState.boardTiles[middle1Tiley][middle1Tilex].isHasUnit() 
						&&  (gameState.boardTiles[middle1Tiley][middle1Tilex].getUnitId()>19 
						&&   gameState.boardTiles[middle1Tiley][middle1Tilex].getUnitId()!=77))
						&&
							(gameState.boardTiles[middle2Tiley][middle2Tilex].isHasUnit() 
						&&  (gameState.boardTiles[middle2Tiley][middle2Tilex].getUnitId()>19 
						&&   gameState.boardTiles[middle2Tiley][middle2Tilex].getUnitId()!=77))	
					) {
					/* _
					 * X D
					 * S X _
					 */
					
					return false; // is blocked by enemy in both direction
				}
			}
			else if(totalDistance==0) {
				return false; // destination position equals to start position
			}
		}
		return true ;
	}


	
}
