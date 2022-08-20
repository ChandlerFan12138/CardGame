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

public class Blaze_Hound extends UnitCard  {
	public Blaze_Hound() {
		this.setId(23);
		this.setCardname("c_blaze_hound");
		this.setManacost(3); //这块有点问题，攻击生命好像没定义？
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
		
		//召唤时双方抽一张卡
		Card tempCard = gameState.player1Deck.get(0);
		gameState.playerHandCard.add(tempCard);
		gameState.player1Deck.remove(0);
		
		BasicCommands.drawCard(out,tempCard ,gameState.playerHandCard.size() , 0);
		
		
		Card tempCard1 = gameState.AIDeck.get(0);
		gameState.AIHandCard.add(tempCard1);
		gameState.AIDeck.remove(0);
		return true;
		
	}

	
}
