package structures.basic.unit.action;
import akka.actor.ActorRef;
import commands.BasicCommands;
import events.TileClicked;
import structures.GameState;
import structures.basic.Position;
import structures.basic.Tile;
import structures.basic.Unit;

public class Move{
	public static void move(ActorRef out, Unit mover, Tile startPoint, Tile endPoint) {
//	
//		BasicCommands.addPlayer1Notification(out, "playUnitAnimation [Move]", 3);//
//		BasicCommands.moveUnitToTile(out, mover, endPoint);
//		try {Thread.sleep(3000);} catch (InterruptedException e) {e.printStackTrace();}
//		
//		mover.setPositionByTile(endPoint);
//		
//	
//		startPoint.setUnitId(999);
//		
//		
//		endPoint.setUnitId(mover.getId());
	}
}
