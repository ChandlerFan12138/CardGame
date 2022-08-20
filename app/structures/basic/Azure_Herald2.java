package structures.basic;

import structures.GameState;
import commands.BasicCommands;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;
import akka.actor.ActorRef;

/*
 * special ability:
 * when it's been summoned, its avatar add 2 health
*/
public class Azure_Herald2 extends Card {
	public Azure_Herald2() {
	}
	public Azure_Herald2 (int id, String cardname, int manacost, MiniCard miniCard, BigCard bigCard) {
		super(id, cardname, manacost, miniCard, bigCard);
	}
	
	public boolean play(ActorRef out, GameState gameState, int tilex, int tiley) {
		//load the unit
		Unit unit = BasicObjectBuilders.loadUnit(StaticConfFiles.u_azure_herald, gameState.selectedCard.getId(), Unit.class);
		unit.setPositionByTile(out,gameState.boardTiles[tiley][tilex]); 
		gameState.playerUnitList.add(unit);
		//BasicCommands.addPlayer1Notification(out, ""+gameState.playerUnitList.size()+"unitId"+gameState.boardTiles[tiley][tilex].getUnitId(), 2);
		//try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
		//set unit Health
		unit.setHealth(out,gameState.selectedCard.getBigCard().getHealth());
		//set unit attack
		unit.setAttack(out, gameState.selectedCard.getBigCard().getAttack());
		//summon animation
		EffectAnimation ef = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_summon);
		BasicCommands.playEffectAnimation(out, ef, gameState.boardTiles[tiley][tilex]);
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
		//add 2 health to its player
		for(Unit unit1:gameState.playerUnitList) {
			if(unit1.getId() == 77) {
				if(unit1.getHealth()>=17) {
					unit1.setHealth(out, gameState, 20);
				}else{
					unit1.setHealth(out, gameState, unit1.getHealth()+3);
				}
			}
		}
		return true;
		
	}
}

