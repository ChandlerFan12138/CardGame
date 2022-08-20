package structures.basic;

import akka.actor.ActorRef;
import commands.BasicCommands;

/*RangeAttackUnit @Yunyi
 * this class represnets units can attack all enemy units on the board
 */
public class RangeAttackUnit extends Unit{

	public RangeAttackUnit() {
		super();
		this.rangeAttack = true;
	}
	
	/*
	 * overload the setPositionByTile 
	 * set its tile's parameter and 
	 * add the method of drawing the unit
	 */
	public void setPositionByTile(ActorRef out, Tile tile) {
		position = new Position(tile.getXpos(),tile.getYpos(),tile.getTilex(),tile.getTiley());
		tile.setUnitId(this.id);
		tile.setHasUnit(true);
		//draw the unit on the tile
		BasicCommands.drawUnit(out, this, tile);
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
	}
}
