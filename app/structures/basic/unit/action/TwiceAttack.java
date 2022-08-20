package structures.basic.unit.action;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.TwiceAttackUnit;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;

public class TwiceAttack {

/**
 * @author YuZeng
 * @param out
 * @param gameState
 * @param attacker who can attack twice
 * @param defender
 * In the first strike, there is a flag to record wither attacker has finished it's first attack. 
 * After the second strike, the unit could neither attack nor move in this round.
 */
	public static void tiwceAttack(ActorRef out,GameState gameState, TwiceAttackUnit attacker, Unit defender) {
		
		if (!attacker.isFirstAttacked()) {
			System.out.println("First attack [1/2]");
			//first attack action
			int tempHealthD = defender.getHealth() - attacker.getAttack();
			
			BasicCommands.addPlayer1Notification(out,"Unit "+attacker.getUnitName()+"'s First Stike On Unit "+defender.getUnitName()+" [1/2]", 2);
			BasicCommands.playUnitAnimation(out, attacker, UnitAnimationType.attack);
			try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
			
			if(tempHealthD <=0) {
				//call defender death
				defender.setHealth(out, gameState, 0);
				defender.dead(out, gameState, defender.getPosition().getTilex(), defender.getPosition().getTiley());
			}
			else {
				defender.setHealth(out, gameState, tempHealthD);
				CounterAttack.counterAttack(out,gameState, attacker,defender);
			}
			
			attacker.setFirstAttaced(true);
		}
		else {
			//second attack action
			System.out.println("Second attack [2/2]");
			int tempHealthD = defender.getHealth() - attacker.getAttack();
			
			BasicCommands.addPlayer1Notification(out,"Unit "+attacker.getUnitName()+"'s Second Strike On Unit "+defender.getUnitName()+" [2/2]", 2);
			BasicCommands.playUnitAnimation(out, attacker, UnitAnimationType.attack);
			try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
			
			if(tempHealthD <=0) {
				//call defender death
				defender.setHealth(out, gameState, 0);
				defender.dead(out, gameState, defender.getPosition().getTilex(), defender.getPosition().getTiley());
			}
			else {
				defender.setHealth(out, gameState, tempHealthD);
				CounterAttack.counterAttack(out,gameState, attacker,defender);
			}
			// attack and move is finished
			attacker.attacked = true;
			attacker.moved = true;
		}
		
		
	}
}
