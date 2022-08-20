package structures.basic.unit.action;
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.*;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

/**RangedAttack @YuZeng, @Zeyu
 * this class represents rangedattack, will decide what animation would be called based on the distance between attacker and defender
 */

public class RangedAttack {
	public static void rangedAttack(ActorRef out,GameState gameState,Unit attacker, Unit defender) {
		//defender is not in a adjacent tile
		if(validRangedAttack(attacker)) {
			//attack 
			int tempHealthD = defender.getHealth() - attacker.getAttack();
			
			// play projectile attack animation
			Tile startTile = gameState.boardTiles[attacker.getPosition().getTiley()][attacker.getPosition().getTilex()];	
			Tile targetTile= gameState.boardTiles[defender.getPosition().getTiley()][defender.getPosition().getTilex()];
			
			BasicCommands.addPlayer1Notification(out, "Unit "+attacker.getUnitName()+" strikes (projectile) on Unit "+defender.getUnitName(), 2);
			
			EffectAnimation projectile = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_projectiles);
			
			BasicCommands.playUnitAnimation(out, attacker, UnitAnimationType.attack);
			try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
			
			BasicCommands.playProjectileAnimation(out, projectile, 0, startTile, targetTile);
			try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
			// update status
			if(tempHealthD<=0) {
				defender.setHealth(out, gameState, 0);
				defender.dead(out, gameState, defender.getPosition().getTilex(), defender.getPosition().getTiley());	
			}
			else {
				defender.setHealth(out, gameState, tempHealthD);
				if(defender.isRangeAttack()) {
					CounterAttack.counterAttack(out,gameState, attacker,defender);
				}
			}	
		}
		attacker.attacked = true;
		attacker.moved = true;
	}
	
	
	//helper methods
	static boolean validRangedAttack(Unit attacker) {
		boolean valid = false;
		valid = (attacker.isRangeAttack()) && !attacker.attacked ;
		
		return valid;
	}
		
}
