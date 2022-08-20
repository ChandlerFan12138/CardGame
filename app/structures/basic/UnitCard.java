package structures.basic;

import java.util.HashMap;
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;


/*Unit Card @Yunyi
 * 
 * this class represents unit cards
 *
*/


public class UnitCard extends Card{
	
	//constructors
	public UnitCard(){}
	
	public UnitCard (int id, String cardname, int manacost, MiniCard miniCard, BigCard bigCard) {
		super(id, cardname, manacost, miniCard, bigCard);
	}
	
	//the method of playing
	//when player play a unit card, a unit will be summoned on a tile on the board
	public boolean play(ActorRef out, GameState gameState, int tilex, int tiley) {
		//the map stores card name and the related attribute in StaticConfFiles class
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
		unitMap.put("WindShrike", StaticConfFiles.u_windshrike);
		
		//the map stores card names and the classes of related units
		HashMap<String, Class<? extends Unit>> unitClassMap = new HashMap<>();
		//classes of human player's units
		unitClassMap.put("Comodo Charger",Unit.class);
		unitClassMap.put("Azure Herald", Unit.class);
		unitClassMap.put("Azurite Lion", TwiceAttackUnit.class);
		unitClassMap.put("Fire Spitter", RangeAttackUnit.class);
		unitClassMap.put("Hailstone Golem", Unit.class);
		unitClassMap.put("Ironcliff Guardian", ProvokeUnit.class);
		unitClassMap.put("Pureblade Enforcer", Unit.class);
		unitClassMap.put("Silverguard Knight", ProvokeUnit.class);
		//store classes of AIplayer's units
		unitClassMap.put("Blaze Hound", Unit.class);
		unitClassMap.put("Bloodshard Golem", Unit.class);
		unitClassMap.put("Hailstone GolemR", Unit.class);
		unitClassMap.put("Planar Scout", Unit.class);
		unitClassMap.put("Pyromancer", RangeAttackUnit.class);
		unitClassMap.put("Rock Pulveriser", ProvokeUnit.class);
		unitClassMap.put("Serpenti", TwiceAttackUnit.class);
		unitClassMap.put("WindShrike", WindShrike.class);
		
		//load the unit
		//the ID of a unit is same as the ID of the related card
		Unit unit = BasicObjectBuilders.loadUnit(unitMap.get(gameState.selectedCard.getCardname()), gameState.selectedCard.id, unitClassMap.get(gameState.selectedCard.getCardname()));
		//set the position on the board
		unit.setPositionByTile(out,gameState.boardTiles[tiley][tilex]);
//		System.out.println(unit.id);
//		System.out.println("rangeAttack:"+unit.isRangeAttack());
//		System.out.println("provoke:"+unit.canProvoke);
		
		if(unit.getId()>=0&&unit.getId()<=19) {
			//when a unit is summoned,
			//add it in human player's unit list if a unit belongs to human player
			
			gameState.playerUnitList.add(unit);
			//System.out.println(gameState.playerUnitList.size());
			if(unit.getId()== 3||unit.getId()==10) {
				//when silverguard knight is summoned
				gameState.hasSilverguardKnight = true;
				//System.out.println("has silverguardKnight"+gameState.hasSilverguardKnight);
			}
		}else if(unit.getId()>=20 && unit.getId()<=39) {
			//when a unit belongs to ai player
			gameState.AIUnitList.add(unit);
			//System.out.println(gameState.AIUnitList.size());
		}
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
		
		return true;
		
	}
	
}
