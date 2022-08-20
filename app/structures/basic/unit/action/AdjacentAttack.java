package structures.basic.unit.action;
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.*;

/**
 * 
 * @author YuZeng
 * the default situation of attack, will call counterstrike
 *
 */
public class AdjacentAttack {
	
	public static void adjacentAttack(ActorRef out,GameState gameState,Unit attacker, Unit defender) {
		//play adjacent attack animation
		BasicCommands.addPlayer1Notification(out,"Unit "+attacker.getUnitName()+" Strike On Unit "+defender.getUnitName(), 2);
		BasicCommands.playUnitAnimation(out, attacker, UnitAnimationType.attack);
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}

		if(defender.getHealth() > attacker.getAttack()) {
			//update status
			int temphealth = defender.getHealth()-attacker.getAttack();
			defender.setHealth(out,gameState, temphealth); 		

			// call counter strike 
			CounterAttack.counterAttack(out,gameState, attacker,defender);
		}
		else {
			defender.setHealth(out,gameState,0);
			defender.dead(out, gameState, defender.getPosition().getTilex(), defender.getPosition().getTiley());
			
		}
		
		attacker.attacked = true;
		attacker.moved = true;
		System.out.println(attacker.getId()+" has attacked on "+defender.getId());
			
	}
	
	//helper method
	public static boolean inAdjacentRange(Unit attacker, Unit defender) {
		Position pa = attacker.getPosition();
		Position pd = defender.getPosition();
		
		int ax = pa.getXpos();
		int ay = pa.getYpos();
		int dx = pd.getXpos();
		int dy = pd.getYpos();
		
		if (Math.abs(ax-dx)<=1 && Math.abs(ay-dy)<=1) {
			//the defender is in adjacent position
			return true;
		}
		return false;
	}

}
