package structures.basic.unit.action;

import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.TwiceAttackUnit;
import structures.basic.Unit;

/**
 * 
 * @author YuZeng
 * According to the distance between attacker and defender, try to use related attack method.
 */

public class Attack {
	// when defender's tile is red and clicked on it. The defender is a valid target.
	public static void attack(ActorRef out,GameState gameState,Unit attacker, Unit defender){		
		if(!attacker.attacked ) {
			
			if(inAdjacentRange(attacker,defender)) {
				// defender in adjacent range
				if(attacker.canAttackTwice()) {
					// twice attack ability
					TwiceAttack.tiwceAttack(out, gameState,(TwiceAttackUnit) attacker, defender);
				}
				else {
					AdjacentAttack.adjacentAttack(out, gameState, attacker, defender);
				}
			}
			else if(inMoveAndAttackRange(attacker,defender) && !attacker.isRangeAttack()) {
				// attacker can't range attack
				MoveAndAttack.moveAndAttack(out, gameState, attacker, defender);
			}
			else if(!inAdjacentRange(attacker,defender) && attacker.isRangeAttack()) {
				//can range attack
				RangedAttack.rangedAttack(out, gameState, attacker, defender);
			}	
		}
	}
	
	//helper methods
	static boolean inAdjacentRange(Unit attacker, Unit defender) {
		boolean inAdacentRange;
		
		int xa = attacker.getPosition().getTilex();
		int ya = attacker.getPosition().getTiley();
		int xd = defender.getPosition().getTilex();
		int yd = defender.getPosition().getTiley();
		
		int distanceX = Math.abs(xd-xa);
		int distanceY = Math.abs(yd-ya);
		int distance = distanceX + distanceY;
		
		
		if(distance  >0 
		&& distanceX <=1 
		&& distanceY <= 1) {
			inAdacentRange = true;
		}
		else {
			inAdacentRange = false;
		}
		return inAdacentRange;
	}
	
	static boolean inMoveAndAttackRange(Unit attacker, Unit defender) {
		boolean inMoveAndAttackRange;
		
		int xa = attacker.getPosition().getTilex();
		int ya = attacker.getPosition().getTiley();
		int xd = defender.getPosition().getTilex();
		int yd = defender.getPosition().getTiley();

		int distanceX = Math.abs(xd-xa);
		int distanceY = Math.abs(yd-ya);
		int distance = distanceX + distanceY;
		
		if(distance >0 
		&& !inAdjacentRange(attacker,defender) 
		&& distanceX<=4
		&& distanceY<=4
		&& distance <=4) {
			inMoveAndAttackRange = true;
		}
		else {
			inMoveAndAttackRange = false;
		}
		return inMoveAndAttackRange;
	}
	
}
