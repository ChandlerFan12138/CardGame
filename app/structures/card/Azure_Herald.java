package structures.card;

import structures.GameState;
import commands.BasicCommands;

import java.util.HashMap;

import com.fasterxml.jackson.databind.JsonNode;
import structures.GameState;
import structures.basic.*;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;
import akka.actor.ActorRef;

public class Azure_Herald extends UnitCard {
	public Azure_Herald() {
		this.setId(5);
		this.setCardname("c_azure_herald");
		this.setManacost(2); //这块有点问题
	}
	
	public boolean play(ActorRef out, GameState gameState, int tilex, int tiley) {
		HashMap<String, String> unitMap = new HashMap<>();
		//player1's units
		unitMap.put("Comodo Charger",StaticConfFiles.u_comodo_charger);
		unitMap.put("Azure Herald", StaticConfFiles.u_azure_herald);
		unitMap.put("Azurite Lion", StaticConfFiles.u_azurite_lion);
		unitMap.put("Fire Spitter", StaticConfFiles.u_fire_spitter);
		unitMap.put("Hailstone Golem", StaticConfFiles.u_hailstone_golem);
		unitMap.put("Ironcliff Guardian", StaticConfFiles.u_ironcliff_guardian);
		unitMap.put("Pureblade Enforcer", StaticConfFiles.u_pureblade_enforcer);
		unitMap.put("Silverguard Knight", StaticConfFiles.u_silverguard_knight);
		//AIplayer's units
		unitMap.put("Blaze Hound", StaticConfFiles.u_blaze_hound);
		unitMap.put("Bloodshard Golem", StaticConfFiles.u_bloodshard_golem);
		unitMap.put("Hailstone GolemR", StaticConfFiles.u_hailstone_golemR);
		unitMap.put("Planar Scout", StaticConfFiles.u_planar_scout);
		unitMap.put("Pyromancer", StaticConfFiles.u_pyromancer);
		unitMap.put("Rock Pulveriser", StaticConfFiles.u_rock_pulveriser);
		unitMap.put("Serpenti", StaticConfFiles.u_serpenti);
		unitMap.put("Windshrike", StaticConfFiles.u_windshrike);
		
		//load the unit
		Unit unit = BasicObjectBuilders.loadUnit(unitMap.get(gameState.selectedCard.getCardname()), gameState.selectedCard.getId(), Unit.class);
		unit.setPositionByTile(out,gameState.boardTiles[tiley][tilex]); 
		gameState.playerUnitList.add(unit);
		BasicCommands.addPlayer1Notification(out, ""+gameState.playerUnitList.size()+"unitId"+gameState.boardTiles[tiley][tilex].getUnitId(), 2);
		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
		//set unit Health
		unit.setHealth(out,gameState.selectedCard.getBigCard().getHealth());
		
		//set unit attack
		unit.setAttack(out, gameState.selectedCard.getBigCard().getAttack());
		
		//summon animation
		EffectAnimation ef = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_summon);
		BasicCommands.playEffectAnimation(out, ef, gameState.boardTiles[tiley][tilex]);
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
		
		for(Unit unit1:gameState.playerUnitList) {
			if(unit1.getId() == 77) {
				if(unit1.getHealth()>=17)  unit1.setHealth(out, 20);
				else unit1.setHealth(out, unit1.getHealth()+3);
			}
			//BasicCommands.setUnitHealth(out, unit1, unit1.getHealth());
		}
		
		return true;
		
	}
}
