package structures.card;

import structures.GameState;
import commands.BasicCommands;
import com.fasterxml.jackson.databind.JsonNode;
import structures.GameState;
import structures.basic.*;
import akka.actor.ActorRef;

public class Pureblade_Enforcer extends UnitCard implements Speciality{
	public Pureblade_Enforcer() {
		this.setId(13);
		this.setCardname("c_pureblade_enforcer");
		this.setManacost(2); //这块有点问题，攻击生命好像没定义？
	}

	@Override
	public void ability(ActorRef out, GameState gameState, JsonNode message) {
		if(gameState.isPlayerTurn) {//是不是玩家回合？
			for(Unit unit:gameState.playerUnitList) {
				if (unit.getId()==13) {
					unit.setAttack(out, unit.getAttack()+1);
					unit.setHealth(out, unit.getHealth()+1);
					BasicCommands.setUnitAttack(out, unit, unit.getAttack());
					BasicCommands.setUnitHealth(out, unit, unit.getHealth());
				}
				
			}
		}
		else {
			for(Unit unit:gameState.AIUnitList) {
				if (unit.getId()==13) {
					unit.setAttack(out, unit.getAttack()+1);
					unit.setHealth(out, unit.getHealth()+1);
					BasicCommands.setUnitAttack(out, unit, unit.getAttack());
					BasicCommands.setUnitHealth(out, unit, unit.getHealth());
				}
				
			}
		}
		
	}
}
