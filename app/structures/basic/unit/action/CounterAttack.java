package structures.basic.unit.action;
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.*;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

/**CounterAttack @YuZeng, @Zeyu
 * this class represents counterattack , the defender would strike on attacker if it survived.
 */

public class CounterAttack {
	public static void counterAttack(ActorRef out,GameState gameState,Unit attacker, Unit defender) {
		if(defender.getHealth() !=0) {
			//defender is still alive
			//System.out.println("Counterstriking!!!");
			
			int temphealth = attacker.getHealth() - defender.getAttack();
			if(temphealth<=0) {
				temphealth=0;
			}
			
			//defender is ranged attack unit 
			if(defender.isRangeAttack()) {
				//play projectile counter strike animation 
				
				Tile startTile = gameState.boardTiles[defender.getPosition().getTiley()][defender.getPosition().getTilex()];	
				Tile targetTile= gameState.boardTiles[attacker.getPosition().getTiley()][attacker.getPosition().getTilex()];
				
				BasicCommands.addPlayer1Notification(out,"Unit "+defender.getUnitName()+" Counter strike (RangeAttack) On "+"Unit "+attacker.getUnitName(), 1);
				
				EffectAnimation projectile = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_projectiles);
				
				BasicCommands.playUnitAnimation(out, defender, UnitAnimationType.attack);
				try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
				BasicCommands.playProjectileAnimation(out, projectile, 0, startTile, targetTile);
				try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}

				//update status
				attacker.setHealth(out,gameState, temphealth);
				
				//attacker is dead
				if(temphealth <= 0) {
					attacker.dead(out, gameState, attacker.getPosition().getTilex(), attacker.getPosition().getTiley());
				}
			}

			//defender is adjacent attack unit
			else {	
					//play counter strike animation 
					BasicCommands.addPlayer1Notification(out,"Unit "+ defender.getUnitName()+" Counterstrick On "+"Unit"+attacker.getUnitName(), 2);//
					BasicCommands.playUnitAnimation(out, defender, UnitAnimationType.attack);
					try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
						
					//update status
					attacker.setHealth(out,gameState ,temphealth);
					if(temphealth==0) {
						attacker.dead(out, gameState, attacker.getPosition().getTilex(), attacker.getPosition().getTiley());
					}

			}

		}
	}
}
