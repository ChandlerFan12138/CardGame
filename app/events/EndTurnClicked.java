package events;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;
import structures.basic.TwiceAttackUnit;
import structures.basic.Unit;
import structures.basic.WindShrike;
import structures.basic.unit.action.AdjacentAttack;
import structures.basic.unit.action.RangedAttack;
import structures.basic.unit.action.TwiceAttack;
import utils.BasicObjectBuilders;

/**
 * IT project 2022 Card Game
 * Team name: Tom Jerry and David Team
 * Member:
 * Yusheng Fan 2660781f
 * Zeyu Miao 2605917m  
 * Shiyu Ren 2518312r
 * Yunyi Wang 2599297W
 * Yu Zeng 2543042z
 */

public class EndTurnClicked implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		
		//EndTurn Start
		if (gameState.isPlayerTurn) {
			
			for(Card card : gameState.AIHandCard) {
				System.out.println("Start:"+card.getCardname()+card.getManacost());
			}
			
			
			//clear humans mana
			gameState.Player1.setMana(0);
			BasicCommands.setPlayer1Mana(out, gameState.Player1);
			
			//switch control right
			gameState.isPlayerTurn = false;
			BasicCommands.addPlayer1Notification(out, "AI's Turn now", 2);
			try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();} 
			
			//set AI mana

			if(gameState.turn<=8) {
			int m = gameState.turn;
			gameState.AIPlayer.setMana(m+1);
			BasicCommands.setPlayer2Mana(out, gameState.AIPlayer);
			gameState.turn += 1; 
			
			}
			else {
				gameState.AIPlayer.setMana(9);
				BasicCommands.setPlayer2Mana(out, gameState.AIPlayer);
				gameState.turn += 1; 
			}
			
			//cancel all hand cards and tiles highlight
			cancelAllHandCardHighlight(gameState, out);
			cancellAllTileTexture(gameState,out);
			
			
			
			
			//AI begins to use cards(Summon units or use spell card)
			if(!gameState.isGameOver)
			{
				AISummon(out,gameState);
			}
			//AI begins to move or attack after using cards
			if(!gameState.isGameOver)
			{
				AI_Action(out,gameState);
			}
						
			//overdraw @Zeyu
			this.overDraw(out, gameState);
				//Human draw card
			if(gameState.player1Deck.size()!=0) {
				if(gameState.playerHandCard.size()<6) {
					gameState.playerHandCard.add(gameState.player1Deck.get(0));
					gameState.player1Deck.remove(0);
					for(Card c : gameState.playerHandCard) {
					BasicCommands.drawCard(out,c , gameState.playerHandCard.indexOf(c)+1, 0);}
				}
			}
			
				//AI draw card
			if(gameState.AIDeck.size()!=0) {
				if(gameState.AIHandCard.size()<6) {
					gameState.AIHandCard.add(gameState.AIDeck.get(0));
					gameState.AIDeck.remove(0);
		
				}
			}
			
			//output
//			for(Card card : gameState.AIHandCard) {
//				System.out.println("end:"+card.getCardname()+card.getManacost());
//			}
			
			
			//reset attacked and moved @Shiyu, YuZeng
			for(Unit pu : gameState.playerUnitList) {
				pu.attacked = false;
				pu.moved = false;
				if(pu.canAttackTwice()) {
					pu.setFirstAttaced(false);
				}
			}
			for(Unit aiu : gameState.AIUnitList) {
				aiu.attacked = false;
				aiu.moved = false;
				if(aiu.canAttackTwice()) {
					aiu.setFirstAttaced(false);
				}
			}
			
			//clear AI mana @Shiyu
			gameState.AIPlayer.setMana(0);
			BasicCommands.setPlayer2Mana(out, gameState.AIPlayer);
			
			//give the control right back to humans player @Shiyu
			if(!gameState.isGameOver) {
				gameState.isPlayerTurn = true;
			}
			if(gameState.turn<=8) {
				int m = gameState.turn;
				gameState.Player1.setMana(m+1);
				BasicCommands.setPlayer1Mana(out, gameState.Player1);
				}
				else {
					gameState.Player1.setMana(9);
					BasicCommands.setPlayer1Mana(out, gameState.Player1);
				}
			
			try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();} 
			
		}
		else {
			;//do nothing
		}
		if(!gameState.isGameOver) {
			BasicCommands.addPlayer1Notification(out, "It's player's turn now.", 2);
			try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();} 
		}
		
		
		
	}

	//AI movement @Shiyu
	public void AItile2Tile(ActorRef out, GameState gameState,int preUnitx, int preUnity, int tilex, int tiley) {
		Unit u = gameState.AIUnitList.get(searchWhichAIUnit(gameState,preUnity,preUnitx));
		int id = u.getId();
		BasicCommands.moveUnitToTile(out, u, gameState.boardTiles[tiley][tilex]);
		gameState.boardTiles[tiley][tilex].setUnitId(id) ;
		gameState.boardTiles[tiley][tilex].setHasUnit(true);
		Tile tmp = BasicObjectBuilders.loadTile(preUnitx, preUnity);
		gameState.boardTiles[preUnity][preUnitx] = tmp;
		u.setPositionByTile(gameState.boardTiles[tiley][tilex]);
	}
	
	
	//search which AI unit on the tile @Shiyu
	public int searchWhichAIUnit (GameState gameState,int preUnity, int preUnitx) {
		int ourUnitId = gameState.boardTiles[preUnity][preUnitx].getUnitId();
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
	
	//@Shiyu
	public void cancelAllHandCardHighlight(GameState gameState,ActorRef out) {
		for (int i =0 ;i<gameState.playerHandCard.size();i++) {
			BasicCommands.drawCard(out, gameState.playerHandCard.get(i), i+1, 0);
		}
	}
	
	//cancel tiles highlight @Shiyu
	public void cancellAllTileTexture(GameState gameState,ActorRef out)  {
		for(int i = 0;i < 5;i++) {
			for(int j = 0; j < 9; j++) {	
				if(gameState.boardTiles[i][j].getCurrentTileTexture()==1
						||gameState.boardTiles[i][j].getCurrentTileTexture()==2) {
					gameState.boardTiles[i][j].setCurrentTileTexture(0);
					BasicCommands.drawTile(out, gameState.boardTiles[i][j], 0);
				}
			}
		}
	}
	
	// judge this unit is an AIUnit or not @Shiyu
	public boolean isAIUnit(GameState gameState, int tiley, int tilex) {
		if(gameState.boardTiles[tiley][tilex].getUnitId()==88
				||(gameState.boardTiles[tiley][tilex].getUnitId()>=20)&&gameState.boardTiles[tiley][tilex].getUnitId()<=39) {
			return true;
		}
		return false;
	}
	
	// Method of AI using cards: Using cards by order if the mana is sufficient @Yusheng
	public void AISummon(ActorRef out, GameState  gameState) {
		boolean flag = true;
		while(flag) {
			int index = -1;
			if(gameState.AIHandCard.size()!=0) {
				for(Card card: gameState.AIHandCard) { // loop all cards
					if(card.getManacost()<=gameState.AIPlayer.getMana()) { // decide if the mana is sufficient
						gameState.selectedCard = card;
						this.aiPlayCard(out, gameState, gameState.selectedCard);
						index = gameState.AIHandCard.indexOf(card);
						break;
					}
					if(gameState.AIHandCard.indexOf(card)==gameState.AIHandCard.size()-1&&card.getManacost()>gameState.AIPlayer.getMana()) {// mana insufficient, loop continue
						flag = false;
					}
				}
			}else {
				flag = false;
			}
			if(index != -1) { // delete card after being used
				gameState.AIHandCard.remove(index);
			}
			for(Card card:gameState.AIHandCard) { 
				System.out.println(card.getId());
			}
			
		}
		
	}
	
	// Method of using cards, called in method(AISummon). 
	public void aiPlayCard(ActorRef out, GameState gameState, Card card) {
		if(card.getId() != 22 && card.getId() != 32 && card.getId() != 27 && card.getId() != 37) { // decide if it is a Unit card
			ArrayList<Tile> tempTile = new ArrayList<Tile>();//Set a list to store tiles
			for(Unit unit:gameState.AIUnitList) {//store all tiles around AI units
				tempTile.add(BasicObjectBuilders.loadTile(unit.getPosition().getTilex()+1, unit.getPosition().getTiley()+1));
				tempTile.add(BasicObjectBuilders.loadTile(unit.getPosition().getTilex()+1, unit.getPosition().getTiley()));
				tempTile.add(BasicObjectBuilders.loadTile(unit.getPosition().getTilex()+1, unit.getPosition().getTiley()-1));
				tempTile.add(BasicObjectBuilders.loadTile(unit.getPosition().getTilex(), unit.getPosition().getTiley()+1));
				tempTile.add(BasicObjectBuilders.loadTile(unit.getPosition().getTilex(), unit.getPosition().getTiley()-1));
				tempTile.add(BasicObjectBuilders.loadTile(unit.getPosition().getTilex()-1, unit.getPosition().getTiley()+1));
				tempTile.add(BasicObjectBuilders.loadTile(unit.getPosition().getTilex()-1, unit.getPosition().getTiley()));
				tempTile.add(BasicObjectBuilders.loadTile(unit.getPosition().getTilex()-1, unit.getPosition().getTiley()-1));
			}
			for(Iterator<Tile> iterator1 = tempTile.iterator(); iterator1.hasNext();) {//remove the tile out of board
				Tile tile = iterator1.next();
				if(tile.getTilex()>8||tile.getTilex()<0||tile.getTiley()<0||tile.getTiley()>4) {
					iterator1.remove();
				}else {
					continue;
				}
			}
			for(Unit unit:gameState.playerUnitList) {// remove the tile which already has a player unit
				for(Iterator<Tile> iterator1 = tempTile.iterator(); iterator1.hasNext();) {
					Tile tile = iterator1.next();
					
					if(tile.getTilex()==unit.getPosition().getTilex()&&tile.getTiley()==unit.getPosition().getTiley()) {
						iterator1.remove();
					}
				}
			}
			for(Unit unit:gameState.AIUnitList) {//remove the tile which already has a AI unit
				for(Iterator<Tile> iterator1 = tempTile.iterator(); iterator1.hasNext();) {
					Tile tile = iterator1.next();
					
					if(tile.getTilex()==unit.getPosition().getTilex()&&tile.getTiley()==unit.getPosition().getTiley()) {
						iterator1.remove();
					}
				}
			}
			Tile summon_tile = tempTile.get(new java.util.Random().nextInt(tempTile.size()));
			Tile summon_tile1 = gameState.boardTiles[summon_tile.getTiley()][summon_tile.getTilex()];
			gameState.AIPlayer.playCard(out, gameState, summon_tile1.getTilex(), summon_tile1.getTiley()); //Play cards
			
			
		}
		else if(card.getId()==27||card.getId()==37) { //Spell card entropic_decay played, randomly choose one player unit to play
			if(gameState.playerUnitList.size()>1) {
				int index = 0;
				while(index == 0) {
					index = new Random().nextInt(gameState.playerUnitList.size());//[0,size)
				}
				System.out.println("index:"+index);
				Unit tempUnit = gameState.playerUnitList.get(index);
				gameState.AIPlayer.playCard(out, gameState, tempUnit.getPosition().getTilex(), tempUnit.getPosition().getTiley());
			}
		}
		else if (card.getId() == 22 || card.getId()==32) {//Spell card staff_of_ykir played, the target is AIplayer avatar
				Unit tempUnit = null;
				for(Unit unit:gameState.AIUnitList) {
					if (unit.getId() == 88) {
						tempUnit = unit;
					}
				}
				gameState.AIPlayer.playCard(out, gameState, tempUnit.getPosition().getTilex(), tempUnit.getPosition().getTiley());
		}
	}

	public void overDraw(ActorRef out, GameState gameState) {
		//@Zeyu
		//when the player's handcard is 6 or gerater, the top card in playerdeck is removed
		if (gameState.playerHandCard.size() >= 6) {
			gameState.player1Deck.remove(0);
		}
		//when the AI's handcard is 6 or gerater, the top card in AIdeck is removed
		if (gameState.AIHandCard.size() >= 6) {
			gameState.AIDeck.remove(0);
		}
	}
	
	//Method: AI actions(Move and Attack)
	public void AI_Action(ActorRef out, GameState gameState) {

		for(Unit unit: gameState.AIUnitList) {//loop all units in AIUnitList
			if(!unit.attacked && !unit.moved && !gameState.isGameOver) { //decide if the Unit has moved or attack
				ArrayList<Tile> temp_Tile = new ArrayList<Tile>(); // set a list to store targeted Tiles of attack
				if(unit.getPosition().getTiley()+1>=0 &&unit.getPosition().getTiley()+1<=4 && unit.getPosition().getTilex()-1>=0 && unit.getPosition().getTilex()-1<=8) {
					temp_Tile.add(gameState.boardTiles[unit.getPosition().getTiley()+1][unit.getPosition().getTilex()-1]);
				}
				if(unit.getPosition().getTiley()+1>=0 &&unit.getPosition().getTiley()+1<=4 && unit.getPosition().getTilex()>=0 && unit.getPosition().getTilex()<=8) {
					temp_Tile.add(gameState.boardTiles[unit.getPosition().getTiley()+1][unit.getPosition().getTilex()]);
				}
				if(unit.getPosition().getTiley()+1>=0 &&unit.getPosition().getTiley()+1<=4 && unit.getPosition().getTilex()+1>=0 && unit.getPosition().getTilex()+1<=8) {
					temp_Tile.add(gameState.boardTiles[unit.getPosition().getTiley()+1][unit.getPosition().getTilex()+1]);
				}
				if(unit.getPosition().getTiley()>=0 &&unit.getPosition().getTiley()<=4 && unit.getPosition().getTilex()+1>=0 && unit.getPosition().getTilex()+1<=8) {
					temp_Tile.add(gameState.boardTiles[unit.getPosition().getTiley()][unit.getPosition().getTilex()+1]);
				}
				if(unit.getPosition().getTiley()>=0 &&unit.getPosition().getTiley()<=4 && unit.getPosition().getTilex()-1>=0 && unit.getPosition().getTilex()-1<=8) {
					temp_Tile.add(gameState.boardTiles[unit.getPosition().getTiley()][unit.getPosition().getTilex()-1]);
				}
				if(unit.getPosition().getTiley()-1>=0 &&unit.getPosition().getTiley()-1<=4 && unit.getPosition().getTilex()+1>=0 && unit.getPosition().getTilex()+1<=8) {
					temp_Tile.add(gameState.boardTiles[unit.getPosition().getTiley()-1][unit.getPosition().getTilex()+1]);
				}
				if(unit.getPosition().getTiley()-1>=0 &&unit.getPosition().getTiley()-1<=4 && unit.getPosition().getTilex()>=0 && unit.getPosition().getTilex()<=8) {
					temp_Tile.add(gameState.boardTiles[unit.getPosition().getTiley()-1][unit.getPosition().getTilex()]);
				}
				if(unit.getPosition().getTiley()-1>=0 &&unit.getPosition().getTiley()-1<=4 && unit.getPosition().getTilex()-1>=0 && unit.getPosition().getTilex()-1<=8) {
					temp_Tile.add(gameState.boardTiles[unit.getPosition().getTiley()-1][unit.getPosition().getTilex()-1]);
				}
				
				for(Iterator<Tile> iterator = temp_Tile.iterator(); iterator.hasNext();) {//remove the tile which already has units
					Tile tile = iterator.next();
					if((tile.getUnitId()>19||tile.getUnitId()==88)&&tile.getUnitId()!=77){
						iterator.remove();
					}
					else if(tile.isHasUnit()==false) {
						iterator.remove();
					}
				}
				
				ArrayList<Tile> provokerTiles = new ArrayList<Tile>();
				ArrayList<Tile> attackTargetTiles = new ArrayList<Tile>();
				
				if(temp_Tile.size()!=0) {
					for(Tile targetTile:temp_Tile) {
						if (gameState.playerUnitList.get(searchWhichOurUnit(gameState,targetTile.getTiley(),targetTile.getTilex())).getCanProvoke()) {
							provokerTiles.add(targetTile);
						}
				}
				
				if(provokerTiles.size()!=0) {
					unit.moved = true;
					for(Tile tile:provokerTiles) {
						attackTargetTiles.add(tile);
					}
				}
				else {
					if(temp_Tile.size()!=0){
						for(Tile tile:temp_Tile) {
							attackTargetTiles.add(tile);
						}
					}
				}
					
					
					
					
				}
				
				
				//Action 
				
				if(unit.isRangeAttack()) {
					//Ability: Range attack
					if(provokerTiles.size()!=0) {
						Unit targetPlayerUnit = new Unit();
						targetPlayerUnit.setHealth(1000);
						for(Tile tile1:provokerTiles) {
							for(Unit unit2:gameState.playerUnitList) {
								if(tile1.getUnitId()==unit2.getId()) {
									if(targetPlayerUnit.getHealth()>unit2.getHealth()) {
										targetPlayerUnit=unit2;
									}
								}
							}
							BasicCommands.addPlayer1Notification(out,"Unit "+unit.getUnitName()+" has found a target Unit "+targetPlayerUnit.getUnitName(), 2);
							try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
						}
						
						if((unit.getHealth()<=targetPlayerUnit.getAttack())
								&&  unit.getAttack()< targetPlayerUnit.getHealth()) {
							//Debuff: Cowards, afraid of death
							BasicCommands.addPlayer1Notification(out,"Unit "+unit.getUnitName()+" does nothing because it would die if Unit "+targetPlayerUnit.getUnitName()+" counterstrikes on it.", 2);
							try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
						}
						else 
						{
//							RangedAttack.rangedAttack(out, gameState, unit, targetPlayerUnit);
							// Attack
							AdjacentAttack.adjacentAttack(out, gameState, unit,targetPlayerUnit);
						}
					}
					else {
						RangedAttack.rangedAttack(out, gameState, unit, gameState.playerUnitList.get(0));
//						if(gameState.isGameOver) {
//							//Game over
//						}
					}

				}
				else if(unit.isFlying()){
					//Ability: Flying
					if (provokerTiles.size()!=0) {
						BasicCommands.addPlayer1Notification(out,"Unit "+unit.getUnitName()+" is provoked, it's too tired to do anything!", 2);
						try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
					}
					else {
						if(!gameState.isGameOver) { // Trigger the ability of fly of units 
							WindShrike unit1 = (WindShrike)unit;
							unit1.fly(out, gameState);
							unit.moved = true; 
						}
					}
				}
				else {
					if(attackTargetTiles.size()>0){//valid attack target
						Unit targetPlayerUnit = new Unit();
						targetPlayerUnit.setHealth(1000);
						for(Tile tile1:attackTargetTiles) {
							for(Unit unit2:gameState.playerUnitList) {
								if(tile1.getUnitId()==unit2.getId()) {
									if(targetPlayerUnit.getHealth()>unit2.getHealth()) {
										targetPlayerUnit=unit2;
									}
								}
							}
							BasicCommands.addPlayer1Notification(out,"Unit "+unit.getUnitName()+" has found a target Unit "+targetPlayerUnit.getUnitName(), 2);
							try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
						}
						
						if((unit.getHealth()<=targetPlayerUnit.getAttack())
								&&  unit.getAttack()< targetPlayerUnit.getHealth()) {
							//Debuff: Cowards, afraid of death
							BasicCommands.addPlayer1Notification(out,"Unit "+unit.getUnitName()+" does nothing because it would die if Unit "+targetPlayerUnit.getUnitName()+" counterstrikes on it.", 2);
							try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
						}
						else {
							// Attack or twice attack
							AIUnitAttack(out, gameState, unit, targetPlayerUnit);
						}
					}
					else {
						// Move
						if(unit.getPosition().getTilex()-1>=0 && !gameState.boardTiles[unit.getPosition().getTiley()][unit.getPosition().getTilex()-1].isHasUnit()) {
							AItile2Tile(out,gameState,unit.getPosition().getTilex(),unit.getPosition().getTiley(),unit.getPosition().getTilex()-1,unit.getPosition().getTiley());
							try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
							unit.moved = true;
						}
					}
				}
				
				
				

				
			}
			
			
			
			
//			else {
//				//unit has moved or attacked,and does nothing
//				;
//			}

		}	
	}

	//search our unit ID @Shiyu
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
	
	//@Shiyu
	public boolean AIUnitisProvoked(GameState gameState,Unit unit,ActorRef out) {
		
		int tiley = unit.getPosition().getTiley();
		int tilex = unit.getPosition().getTilex();

		
		if(tiley-1>=0 && tilex-1>=0) {
			if (gameState.boardTiles[tiley-1][tilex-1].getUnitId()<=19 || gameState.boardTiles[tiley-1][tilex-1].getUnitId()==77)
			{
				if(gameState.playerUnitList.get(searchWhichOurUnit(gameState, tiley-1, tilex-1)).getCanProvoke()) {
					return true;
				}
			}
		}
		

		if(tilex-1>=0) {
				if (gameState.boardTiles[tiley][tilex-1].getUnitId()<=19 || gameState.boardTiles[tiley][tilex-1].getUnitId()==77)
				{
					if(gameState.playerUnitList.get(searchWhichOurUnit(gameState, tiley, tilex-1)).getCanProvoke()) {
						return   true;
					}
				}
		}
		

		if(tiley+1<=4 && tilex-1>=0) {
			if (gameState.boardTiles[tiley+1][tilex-1].getUnitId()<=19 || gameState.boardTiles[tiley+1][tilex-1].getUnitId()==77)
			{
				if(gameState.playerUnitList.get(searchWhichOurUnit(gameState, tiley+1, tilex-1)).getCanProvoke()) {
					return   true;
				}
			}
		}
		

		if(tiley+1<=4) {
			if (gameState.boardTiles[tiley+1][tilex].getUnitId()<=19 || gameState.boardTiles[tiley+1][tilex].getUnitId()==77)
			{
				if(gameState.playerUnitList.get(searchWhichOurUnit(gameState, tiley+1, tilex)).getCanProvoke()) {
					return   true;
				}
			}
		}
		

		if(tiley+1<=4 && tilex+1<=8) {
			if (gameState.boardTiles[tiley+1][tilex+1].getUnitId()<=19 || gameState.boardTiles[tiley+1][tilex+1].getUnitId()==77)
			{
				if(gameState.playerUnitList.get(searchWhichOurUnit(gameState, tiley+1, tilex+1)).getCanProvoke()) {
					return   true;
				}
			}
		}
		

		if(tilex+1<=8) {
			if (gameState.boardTiles[tiley][tilex+1].getUnitId()<=19 || gameState.boardTiles[tiley][tilex+1].getUnitId()==77)
			{
				if(gameState.playerUnitList.get(searchWhichOurUnit(gameState, tiley, tilex+1)).getCanProvoke()) {
					return   true;
				}
			}
		}
		

		if(tiley-1>=0 && tilex+1<=8) {
			if (gameState.boardTiles[tiley-1][tilex+1].getUnitId()<=19 || gameState.boardTiles[tiley-1][tilex+1].getUnitId()==77)
			{
				if(gameState.playerUnitList.get(searchWhichOurUnit(gameState, tiley-1, tilex+1)).getCanProvoke()) {
					return   true;
				}
			}
		}
		

		if(tiley-1>=0) {
			if (gameState.boardTiles[tiley-1][tilex].getUnitId()<=19 || gameState.boardTiles[tiley-1][tilex].getUnitId()==77)
			{
				if(gameState.playerUnitList.get(searchWhichOurUnit(gameState, tiley-1, tilex)).getCanProvoke()) {
					return  true;
				}
			}
		}
		
		return false;
	}
	
	//@YuZeng
	private void AIUnitAttack(ActorRef out, GameState gameState,Unit unit, Unit targetPlayerUnit) {
		//Attack Logic, range attack not included 
//		if(unit.isRangeAttack()) {
//			RangedAttack.rangedAttack(out, gameState, unit, gameState.playerUnitList.get(0));
//			if(gameState.isGameOver) {
//				//Game over
//				return;
//			}
//		}
//		else
			{
			if(unit.canAttackTwice()) {
				//special unit
				int initSize = gameState.playerUnitList.size();
				// TwiceAttack [1/2]
				TwiceAttack.tiwceAttack(out, gameState, (TwiceAttackUnit) unit, targetPlayerUnit);
				if(!gameState.isGameOver) {
					if(gameState.playerUnitList.size()<initSize) {
						// targetUnit is dead in it's first striking, stop action
					}
					else {
						if((unit.getHealth()<=targetPlayerUnit.getAttack())
								&&  unit.getAttack()< targetPlayerUnit.getHealth()) {
							//Debuff: Cowards
							BasicCommands.addPlayer1Notification(out,"Unit "+unit.getUnitName()+" doesn't attack because it would die if Unit "+targetPlayerUnit.getUnitName()+" counterstrikes on it.", 2);
							try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
							return;
						}
						else {
							// TwiceAttack [2/2]
							TwiceAttack.tiwceAttack(out, gameState, (TwiceAttackUnit) unit, targetPlayerUnit);
						}
					}
				}
				else {
					//Game over
					return;
				}
			}
			else {
				AdjacentAttack.adjacentAttack(out, gameState, unit, targetPlayerUnit);
			}
			
		}
		
	}
}


