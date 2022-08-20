package structures.basic;

import structures.GameState;
import commands.BasicCommands;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;
import akka.actor.ActorRef;
 
/* Azure_Herald (Card) @ Yunyi, Yusheng
 * special ability:
 * when it's been summoned, its avatar add 2 health
*/

public class Azure_Herald extends Card {
	
	//constructors
	public Azure_Herald() {}
	
	public Azure_Herald (int id, String cardname, int manacost, MiniCard miniCard, BigCard bigCard) {
		super(id, cardname, manacost, miniCard, bigCard);
	}
	
	@Override
	public boolean play(ActorRef out, GameState gameState, int tilex, int tiley) {
		
		//load the unit
		Unit unit = BasicObjectBuilders.loadUnit(StaticConfFiles.u_azure_herald, gameState.selectedCard.getId(), Unit.class);
		unit.setPositionByTile(out,gameState.boardTiles[tiley][tilex]); 
		gameState.playerUnitList.add(unit);

		//set unit start Health
		unit.setStartHealth(gameState.selectedCard.getBigCard().getHealth());
		//set unit health
		unit.setHealth(out,unit.startHealth);
		//set unit start attack
		unit.setStartAttack(gameState.selectedCard.getBigCard().getAttack());
		//set unit attack
		unit.setAttack(out,unit.getStartAttack());
		//set unit name
		unit.setUnitName(this.cardname);
		//System.out.println(unit.getUnitName());
		
		//summon animation
		EffectAnimation ef = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_summon);
		BasicCommands.playEffectAnimation(out, ef, gameState.boardTiles[tiley][tilex]);
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
		
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

