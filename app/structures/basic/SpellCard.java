package structures.basic;

import java.util.HashMap;
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

/*SpellCard @Yunyi
 * this class represents spell card
*/
public class SpellCard extends Card{

	//setters and getters
	public SpellCard() {
		
	}
	public SpellCard(int id, String cardname, int manacost, MiniCard miniCard, BigCard bigCard) {
		super(id, cardname, manacost, miniCard, bigCard);
	}
	
	//when playing a spell card, it needs to be used on a unit on the board
	public boolean play(ActorRef out, GameState gameState, int tilex, int tiley) {
		//judge if the card has been used successfully
		boolean isPlayed = false;
		
		//human player's spell card
		if(gameState.boardTiles[tiley][tilex].isHasUnit() == true) {
			if(gameState.selectedCard.getId()==4||gameState.selectedCard.getId()==14) {
			//check whether it is a truestrike card by its id
			//truestrike:-2 health to enemy unit, mana cost 1
				int index = searchWhichAIUnit(gameState, tiley, tilex);
				Unit unit = gameState.AIUnitList.get(index);

				//play the animation
				this.playAnimation(out, gameState.selectedCard.getId(), gameState.boardTiles[tiley][tilex]);
				try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
				unit.setHealthAndDeath(out, gameState, tilex, tiley, unit.getHealth()-2);
				//System.out.println(this.bigCard.attack);
				//System.out.println(unit.health);
				isPlayed = true;
				
				//notification
				BasicCommands.addPlayer1Notification(out, "Human player used "+this.getCardname()+" on "+unit.getUnitName()+" successfully.", 2);
				try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
				
			}else if(gameState.selectedCard.getId()==8||gameState.selectedCard.getId()==18) {
				//check whether it is a sundrop elixir card by its id
				//sundrop elixir:add +5 health to a unit(cannot over its starting health valuer),1
				for(Unit unit : gameState.playerUnitList) {
					if(unit.getId() == gameState.boardTiles[tiley][tilex].getUnitId()) {
						//play the animation
						this.playAnimation(out, gameState.selectedCard.getId(), gameState.boardTiles[tiley][tilex]);
						
						if(unit.getHealth()+5 < unit.getStartHealth()) {
						//when unit's health+5 less than its starting health valuer
							unit.setHealth(out,unit.getHealth()+5);
						}else {
							unit.setHealth(out,unit.getStartHealth());
						}
						isPlayed = true;
						//notification
						BasicCommands.addPlayer1Notification(out, "Human player used "+this.getCardname()+" on"+unit.getUnitName()+" successfully.", 2);
						try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
						break;
					}
					
				}
			//enemy's spell card
			}else if(gameState.selectedCard.getId()==22||gameState.selectedCard.getId()==32) {
				//check whether it is staff of ykir card by its id
				//staff of ykir:add +2 attack to your avatar, 2
				Unit unit = gameState.AIUnitList.get(0);
				for(Unit unit1:gameState.AIUnitList) {
					if(unit1.getId()==88) {
						unit = unit1;
					}
				}
				//System.out.println(gameState.selectedCard.getCardname()+"has been used");
				if(unit.getId() == 88) {
					this.playAnimation(out, gameState.selectedCard.getId(), gameState.boardTiles[tiley][tilex]);
					unit.setAttack(out,unit.getAttack()+2);
					isPlayed = true;
					//System.out.println(gameState.selectedCard.getCardname()+"has been used");
					
					//notification
					BasicCommands.addPlayer1Notification(out, "AI player used "+this.getCardname()+" on AIplayer's avatar successfully.", 2);
					try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
					
					//pureblade enforcer ability's triggered
					Unit.purebladeEnforcerAbility(out, gameState);
				}
				
			}else if(gameState.selectedCard.getId()==27||gameState.selectedCard.getId()==37) {
				//check whether it is a entropic decay card by its id
				//entropic decay:reduce a non-avator unit to 0 health,5
				if(gameState.boardTiles[tiley][tilex].getUnitId() != 77 && gameState.boardTiles[tiley][tilex].isHasUnit() == true ) {
					int index = searchWhichOurUnit(gameState, tiley,tilex);
					Unit unit = gameState.playerUnitList.get(index);
					this.playAnimation(out, gameState.selectedCard.getId(), gameState.boardTiles[tiley][tilex]);
					unit.dead(out, gameState, tilex, tiley);
					isPlayed = true;
					
					//notification
					BasicCommands.addPlayer1Notification(out, "AI player used "+this.getCardname()+" on "+ unit.getUnitName()+" successfully.", 2);
					try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
					
					//pureblade enforcer ability's triggered
					Unit.purebladeEnforcerAbility(out, gameState);
				}
				
			}
			
		}
		return isPlayed;
	}
	//the method of playing the animation when a card is used
	public void playAnimation(ActorRef out, int unitId, Tile tile) {
		//the map stores the card's id and its playing animation
		HashMap<String, String> animationMap = new HashMap<>();
		animationMap.put("14", StaticConfFiles.f1_inmolation);
		animationMap.put("4", StaticConfFiles.f1_inmolation);
		animationMap.put("18", StaticConfFiles.f1_buff);
		animationMap.put("8", StaticConfFiles.f1_buff);
		animationMap.put("22", StaticConfFiles.f1_buff);
		animationMap.put("32", StaticConfFiles.f1_buff);
		animationMap.put("27", StaticConfFiles.f1_martyrdom);
		animationMap.put("37", StaticConfFiles.f1_martyrdom);
		
		//load the animation
		EffectAnimation ef = BasicObjectBuilders.loadEffect(animationMap.get(unitId+""));
		BasicCommands.playEffectAnimation(out, ef, tile);
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
	}
	
	//search which my unit on the tile by tile
	//return the index of human player's unit list
	public int searchWhichOurUnit (GameState gameState,int tiley, int tilex) {
		int ourUnitId = gameState.boardTiles[tiley][tilex].getUnitId();
		for (Unit u : gameState.playerUnitList) {
			if(u.getId() == ourUnitId) {
				return gameState.playerUnitList.indexOf(u);
			}
			else {
				continue;
			}
		}
		return 999;
	}
	
	//search which enemy unit on the tile
	//return the index of ai player's unit list
	public int searchWhichAIUnit (GameState gameState,int tiley, int tilex) {
		int ourUnitId = gameState.boardTiles[tiley][tilex].getUnitId();
		for (Unit u : gameState.AIUnitList) {
			if(u.getId() == ourUnitId) {
				return gameState.AIUnitList.indexOf(u);
			}
			else {
				continue;
			}
		}
		return 999;
	}
	
}
