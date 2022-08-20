package structures.basic;

import structures.GameState;
import commands.BasicCommands;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;
import akka.actor.ActorRef;

/*Blaze Hound @Yusheng, Yunyi, Zeyu
 * special ability:
 * 	when it's been summoned, each player draw a new card from their deck respectively
*/
public class Blaze_Hound extends Card  {
	
	//constructors
	public Blaze_Hound() {
		
	}
	public Blaze_Hound(int id, String cardname, int manacost, MiniCard miniCard, BigCard bigCard) {
		super(id, cardname, manacost, miniCard, bigCard);
	}
	public boolean play(ActorRef out, GameState gameState, int tilex, int tiley) {
		//load the unit
		Unit unit = BasicObjectBuilders.loadUnit(StaticConfFiles.u_blaze_hound, gameState.selectedCard.getId(), Unit.class);
		unit.setPositionByTile(out,gameState.boardTiles[tiley][tilex]); 
		gameState.AIUnitList.add(unit);
		try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
		//set unit Health
		unit.setHealth(out,gameState.selectedCard.getBigCard().getHealth());
		try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
		//set unit attack
		unit.setAttack(out, gameState.selectedCard.getBigCard().getAttack());
		try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
		
		//summon animation
		EffectAnimation ef = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_summon);
		BasicCommands.playEffectAnimation(out, ef, gameState.boardTiles[tiley][tilex]);
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
		
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
		
		//each player draw a card when it's been summoned
		drawCard(out, gameState);
		return true;
	}
	
	//the method of drawing a card
	public void drawCard(ActorRef out, GameState gameState) {
		//when deck has cards
		if(gameState.player1Deck.size()!=0) {
			//when the number of player's hand card are over then 6
			if (gameState.playerHandCard.size() >= 6) {
				gameState.player1Deck.remove(0);
			}else{
				//add it in player hand
				gameState.playerHandCard.add(gameState.player1Deck.get(0));
				//draw the card in player's hand card
				BasicCommands.drawCard(out,gameState.player1Deck.get(0), gameState.playerHandCard.size(), 0);
				gameState.player1Deck.remove(0);
				System.out.println("blaze_hound draw card. playear deck size:"+gameState.player1Deck.size());
			}
		}
		if(gameState.AIDeck.size()!=0) {
			if (gameState.AIHandCard.size() >= 6) {
				gameState.AIDeck.remove(0);
			}else{
				//ai draw a card
				gameState.AIHandCard.add(gameState.AIDeck.get(0));
				gameState.AIDeck.remove(0);
				System.out.println("blaze_hound draw card. ai player deck size:"+gameState.AIDeck.size());
			}
		}
	}
}
