package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.*;
import structures.basic.unit.action.*;
import utils.BasicObjectBuilders;

/**
 * IT project 2022 Card Game Team name: Tom Jerry and David Team 
 * Member: Yusheng
 * Fan 2660781f 
 * Zeyu Miao 2605917m 
 * Shiyu Ren 2518312r 
 * Yunyi Wang 2599297W
 * Yu Zeng 2543042z
 */

public class TileClicked implements EventProcessor {

	int preUnitx;
	int preUnity;
	boolean isMyUnitClicked = false;
	boolean lastTimeMyUnitClicked = false;
	Unit lastTimeClickedUnit = null;
	Tile previousTile = null;
	boolean isHighlight = false;

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

		int tilex = message.get("tilex").asInt();
		int tiley = message.get("tiley").asInt();

		// reset highlight @Shiyu
		if (previousTile != null) {
			if ((previousTile.getUnitId() <= 19 || previousTile.getUnitId() == 77) && isHighlight
					&& gameState.boardTiles[tiley][tilex].getCurrentTileTexture() == 0) {
				cancellAllTileTexture(gameState, out);
				isHighlight = false;
				if (gameState.boardTiles[tiley][tilex].getTilex() == previousTile.getTilex()
						&& gameState.boardTiles[tiley][tilex].getTiley() == previousTile.getTiley())
					return;
			}

		}

		gameState.isTileClicked = true;

		// when it's human player's turn @Yunyi
		if (gameState.isPlayerTurn == true) {
			if (gameState.isCardClicked == true) {
				// if the clicked tile is highlighting, player can deploy a card on the tile.
				if (gameState.boardTiles[tiley][tilex].getCurrentTileTexture() == 1
						|| gameState.boardTiles[tiley][tilex].getCurrentTileTexture() == 2) {
					gameState.Player1.playCard(out, gameState, tilex, tiley);
					gameState.isCardClicked = false;
					gameState.isTileClicked = false;
					// if the clicked tile is not highlighting
				} else if (gameState.boardTiles[tiley][tilex].getCurrentTileTexture() == 0) {
					// the clicked card is canceled
					gameState.selectedCard = null;
					// delete its highlight
					cancelAllHandCardHighlight(gameState, out);
					gameState.isCardClicked = false;
				}
				// delete tile's highlight
				this.deHighlighting(out, gameState);
			}
			// if the tile has a unit. check if it can move or attack
			if ((gameState.boardTiles[tiley][tilex].getUnitId() <= 19
					|| gameState.boardTiles[tiley][tilex].getUnitId() == 77)) {
				isMyUnitClicked = true;
				lastTimeClickedUnit = gameState.playerUnitList.get(searchWhichOurUnit(gameState, tiley, tilex));
				previousTile = gameState.boardTiles[tiley][tilex];
			}

			// cancel card highlight @Shiyu
			cancelAllHandCardHighlight(gameState, out);

			// show all highlight info @Shiyu
			if (gameState.isPlayerTurn && gameState.boardTiles[tiley][tilex].isHasUnit()
					&& (gameState.boardTiles[tiley][tilex].getUnitId() <= 19
							|| gameState.boardTiles[tiley][tilex].getUnitId() == 77)
					&& gameState.playerUnitList.get(searchWhichOurUnit(gameState, tiley, tilex)).attacked == false
					&& isMyUnitClicked) {
				this.lastTimeMyUnitClicked = true;// record the last time clicked player's unit. @YuZeng
				// whether there are provoke unit around @Shiyu
				boolean provokeFlag = false;
				provokeFlag = haveProvoke(gameState, tiley, tilex, out);

				// can range attack?
				boolean rangeAttackFlag = false;
				rangeAttackFlag = haveRangeAttack(gameState, tiley, tilex, out);

				// can range move?
				boolean rangeMoveFlag = false;
				rangeMoveFlag = haveRangeMove(gameState, tiley, tilex, out);

				// if it can range attack and no provoked
				if (rangeAttackFlag == true && provokeFlag == false) {
					this.preUnitx = tilex;
					this.preUnity = tiley;

					if (gameState.playerUnitList.get(searchWhichOurUnit(gameState, tiley, tilex)).moved == false) {
						findTexture1(gameState, tiley, tilex, out);
					}

					for (int i = 0; i < 5; i++) {
						for (int j = 0; j < 9; j++) {
							if ((gameState.boardTiles[i][j].getUnitId() >= 20
									&& gameState.boardTiles[i][j].getUnitId() <= 39)
									|| gameState.boardTiles[i][j].getUnitId() == 88) {
								gameState.boardTiles[i][j].setCurrentTileTexture(2);
								BasicCommands.drawTile(out, gameState.boardTiles[i][j], 2);
								try {
									Thread.sleep(10);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}

//						    //if it can range move and no provoked
//						    if(provokeFlag == false && rangeMoveFlag == true) {
//						    	
//						    	for(int i = 0;i < 5;i++) {
//									for(int j = 0; j < 9; j++) {
//										if(gameState.boardTiles[i][j].isHasUnit() == false) {
//											gameState.boardTiles[i][j].setCurrentTileTexture(1);
//											BasicCommands.drawTile(out, gameState.boardTiles[i][j], 1);
//											try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
//										}
//										if((gameState.boardTiles[i][j].getUnitId() >= 20 && gameState.boardTiles[i][j].getUnitId()<=39)||gameState.boardTiles[i][j].getUnitId()==88) {
//											gameState.boardTiles[i][j].setCurrentTileTexture(2);
//											BasicCommands.drawTile(out, gameState.boardTiles[i][j], 2);
//											try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
//										}
//									}
//								}
//						    }

				// normal unit and not provoked
				if (provokeFlag == false && rangeAttackFlag == false) {
					System.out.println("highlight");
					this.preUnitx = tilex;
					this.preUnity = tiley;
					clickMyUnit(gameState, tiley, tilex, out);
					isHighlight = true;
				}

				// if it is provoked, only attack available.
				if (provokeFlag == true) {

					hightlightProvokeUnit(gameState, tiley, tilex, out);
				}

				isHighlight = true;

			}

			// Attack Trigger @YuZeng
			if (gameState.isPlayerTurn// This is player's turn
					&& gameState.boardTiles[tiley][tilex].isHasUnit() // Target tile has a unit
					&& gameState.boardTiles[tiley][tilex].getCurrentTileTexture() == 2 // Target tile is highlighted in
																						// Red
					&& lastTimeMyUnitClicked // Last event was clicking my unit
					&& !lastTimeClickedUnit.attacked // My unit has not attacked
			) {
				System.out.println("Attack Triggered (" + tilex + "," + tiley + ")");
				cancellAllTileTexture(gameState, out);
				Unit attacker = lastTimeClickedUnit;
				Unit defender = gameState.AIUnitList.get(searchWhichAIUnit(gameState, tiley, tilex));
				//for certain kind of attack would be determined inside Attack method
				Attack.attack(out, gameState, attacker, defender);

			}

			// movement @Shiyu
			System.out.println("isMyUnitClicked" + isMyUnitClicked);
			System.out.println(gameState.boardTiles[tiley][tilex].getCurrentTileTexture() == 1);

			System.out.println("preUnity: " + preUnity);
			System.out.println("preUnitx: " + preUnitx);
			if (isMyUnitClicked && gameState.boardTiles[tiley][tilex].getCurrentTileTexture() == 1
					&& gameState.playerUnitList.get(searchWhichOurUnit(gameState, preUnity, preUnitx)).moved == false) {
				tile2Tile(out, gameState, preUnitx, preUnity, tilex, tiley);
				cancellAllTileTexture(gameState, out);
				gameState.playerUnitList.get(searchWhichOurUnit(gameState, tiley, tilex)).moved = true;
				preUnitx = 999;
				preUnity = 999;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				isMyUnitClicked = false;
			}

			// if it has moved, just show red tiles (can attack) @Shiyu
			if (isMyUnitClicked && gameState.isPlayerTurn && gameState.boardTiles[tiley][tilex].isHasUnit()
					&& (gameState.boardTiles[tiley][tilex].getUnitId() <= 19
							|| gameState.boardTiles[tiley][tilex].getUnitId() == 77)
					&& gameState.playerUnitList.get(searchWhichOurUnit(gameState, tiley, tilex)).moved == true
					&& gameState.playerUnitList.get(searchWhichOurUnit(gameState, tiley, tilex)).attacked == false) {
				boolean isProvoked = false;
				isProvoked = haveProvoke(gameState, tiley, tilex, out);
				if (!isProvoked) {
					findTexture2(gameState, tiley, tilex, out);
					isHighlight = true;
				} else {
					hightlightProvokeUnit(gameState, tiley, tilex, out);
					isHighlight = true;
				}
			}
		} else {// when it's ai player's turn
			; // do nothing
		}

		// record this tile, which can be used by next clicked event @Shiyu
		if (gameState.isPlayerTurn) {
			previousTile = gameState.boardTiles[tiley][tilex];
		}

	}

	// cancel tiles highlight
	public void deHighlighting(ActorRef out, GameState gameState) {
		for (int i = 0; i < gameState.playerHandCard.size(); i++) {
			BasicCommands.drawCard(out, gameState.playerHandCard.get(i), i + 1, 0); // Firstly, set all cards mode 0
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 9; j++) {
				if (gameState.boardTiles[i][j].getCurrentTileTexture() != 0) {
					gameState.boardTiles[i][j].setCurrentTileTexture(0);
					BasicCommands.drawTile(out, gameState.boardTiles[i][j], 0);
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	// check whether any enemies in Ring 1 @Shiyu
	public void findTexture2(GameState gameState, int tiley, int tilex, ActorRef out) {
		// top left
		if (tiley - 1 >= 0 && tilex - 1 >= 0) {
			if ((gameState.boardTiles[tiley - 1][tilex - 1].getUnitId() >= 20
					&& gameState.boardTiles[tiley - 1][tilex - 1].getUnitId() <= 39)
					|| gameState.boardTiles[tiley - 1][tilex - 1].getUnitId() == 88) {
				BasicCommands.drawTile(out, gameState.boardTiles[tiley - 1][tilex - 1], 2);
				gameState.boardTiles[tiley - 1][tilex - 1].setCurrentTileTexture(2);
			}
		}
		// top
		if (tiley - 1 >= 0) {
			if ((gameState.boardTiles[tiley - 1][tilex].getUnitId() >= 20
					&& gameState.boardTiles[tiley - 1][tilex].getUnitId() <= 39)
					|| gameState.boardTiles[tiley - 1][tilex].getUnitId() == 88) {
				BasicCommands.drawTile(out, gameState.boardTiles[tiley - 1][tilex], 2);
				gameState.boardTiles[tiley - 1][tilex].setCurrentTileTexture(2);
			}
		}
		// top right
		if (tiley - 1 >= 0 && tilex + 1 <= 8) {
			if ((gameState.boardTiles[tiley - 1][tilex + 1].getUnitId() >= 20
					&& gameState.boardTiles[tiley - 1][tilex + 1].getUnitId() <= 39)
					|| gameState.boardTiles[tiley - 1][tilex + 1].getUnitId() == 88) {
				BasicCommands.drawTile(out, gameState.boardTiles[tiley - 1][tilex + 1], 2);
				gameState.boardTiles[tiley - 1][tilex + 1].setCurrentTileTexture(2);
			}
		}
		// left
		if (tilex - 1 >= 0) {
			if ((gameState.boardTiles[tiley][tilex - 1].getUnitId() >= 20
					&& gameState.boardTiles[tiley][tilex - 1].getUnitId() <= 39)
					|| gameState.boardTiles[tiley][tilex - 1].getUnitId() == 88) {
				BasicCommands.drawTile(out, gameState.boardTiles[tiley][tilex - 1], 2);
				gameState.boardTiles[tiley][tilex - 1].setCurrentTileTexture(2);
			}
		}
		// right
		if (tilex + 1 <= 8) {
			if ((gameState.boardTiles[tiley][tilex + 1].getUnitId() >= 20
					&& gameState.boardTiles[tiley][tilex + 1].getUnitId() <= 39)
					|| gameState.boardTiles[tiley][tilex + 1].getUnitId() == 88) {
				BasicCommands.drawTile(out, gameState.boardTiles[tiley][tilex + 1], 2);
				gameState.boardTiles[tiley][tilex + 1].setCurrentTileTexture(2);
			}
		}
		// bottom right
		if (tilex - 1 >= 0 && tiley + 1 <= 4) {
			if ((gameState.boardTiles[tiley + 1][tilex - 1].getUnitId() >= 20
					&& gameState.boardTiles[tiley + 1][tilex - 1].getUnitId() <= 39)
					|| gameState.boardTiles[tiley + 1][tilex - 1].getUnitId() == 88) {
				BasicCommands.drawTile(out, gameState.boardTiles[tiley + 1][tilex - 1], 2);
				gameState.boardTiles[tiley + 1][tilex - 1].setCurrentTileTexture(2);
			}
		}
		// bottom
		if (tiley + 1 <= 4) {
			if ((gameState.boardTiles[tiley + 1][tilex].getUnitId() >= 20
					&& gameState.boardTiles[tiley + 1][tilex].getUnitId() <= 39)
					|| gameState.boardTiles[tiley + 1][tilex].getUnitId() == 88) {
				BasicCommands.drawTile(out, gameState.boardTiles[tiley + 1][tilex], 2);
				gameState.boardTiles[tiley + 1][tilex].setCurrentTileTexture(2);
			}
		}
		// bottom right
		if (tiley + 1 <= 4 && tilex + 1 <= 8) {
			if ((gameState.boardTiles[tiley + 1][tilex + 1].getUnitId() >= 20
					&& gameState.boardTiles[tiley + 1][tilex + 1].getUnitId() <= 39)
					|| gameState.boardTiles[tiley + 1][tilex + 1].getUnitId() == 88) {
				BasicCommands.drawTile(out, gameState.boardTiles[tiley + 1][tilex + 1], 2);
				gameState.boardTiles[tiley + 1][tilex + 1].setCurrentTileTexture(2);
			}
		}
	}

	// high movement range
	public void findTexture1(GameState gameState, int tiley, int tilex, ActorRef out) {

		if (tiley != 4) {
			// bottom
			// bottom first tile
			if (!gameState.boardTiles[tiley + 1][tilex].isHasUnit()) {
				BasicCommands.drawTile(out, gameState.boardTiles[tiley + 1][tilex], 1);
				gameState.boardTiles[tiley + 1][tilex].setCurrentTileTexture(1);
			}
			// bottom second tile
			if (tiley != 3
					&& (gameState.boardTiles[tiley + 1][tilex].getUnitId() == 999
							|| gameState.boardTiles[tiley + 1][tilex].getUnitId() <= 19
							|| gameState.boardTiles[tiley + 1][tilex].getUnitId() == 77)
					&& !gameState.boardTiles[tiley + 2][tilex].isHasUnit()) {
				BasicCommands.drawTile(out, gameState.boardTiles[tiley + 2][tilex], 1);
				gameState.boardTiles[tiley + 2][tilex].setCurrentTileTexture(1);
			}
		}

		if (tiley != 0) {
			// top 
			// top first
			if (!gameState.boardTiles[tiley - 1][tilex].isHasUnit()) {
				BasicCommands.drawTile(out, gameState.boardTiles[tiley - 1][tilex], 1);
				gameState.boardTiles[tiley - 1][tilex].setCurrentTileTexture(1);
			}
			// top second
			if (tiley != 1
					&& (gameState.boardTiles[tiley - 1][tilex].getUnitId() == 999
							|| gameState.boardTiles[tiley - 1][tilex].getUnitId() <= 19
							|| gameState.boardTiles[tiley - 1][tilex].getUnitId() == 77)
					&& !gameState.boardTiles[tiley - 2][tilex].isHasUnit()) {
				BasicCommands.drawTile(out, gameState.boardTiles[tiley - 2][tilex], 1);
				gameState.boardTiles[tiley - 2][tilex].setCurrentTileTexture(1);
			}
		}

		if (tilex != 0) {
			if (!gameState.boardTiles[tiley][tilex - 1].isHasUnit()) {
				BasicCommands.drawTile(out, gameState.boardTiles[tiley][tilex - 1], 1);
				gameState.boardTiles[tiley][tilex - 1].setCurrentTileTexture(1);
			}
			if (tilex != 1
					&& (gameState.boardTiles[tiley][tilex - 1].getUnitId() == 999
							|| gameState.boardTiles[tiley][tilex - 1].getUnitId() <= 19
							|| gameState.boardTiles[tiley][tilex - 1].getUnitId() == 77)
					&& !gameState.boardTiles[tiley][tilex - 2].isHasUnit()) {
				BasicCommands.drawTile(out, gameState.boardTiles[tiley][tilex - 2], 1);
				gameState.boardTiles[tiley][tilex - 2].setCurrentTileTexture(1);
			}
		}

		if (tilex != 8) {
			if (!gameState.boardTiles[tiley][tilex + 1].isHasUnit()) {
				BasicCommands.drawTile(out, gameState.boardTiles[tiley][tilex + 1], 1);
				gameState.boardTiles[tiley][tilex + 1].setCurrentTileTexture(1);
			}
			if (tilex != 7
					&& (gameState.boardTiles[tiley][tilex + 1].getUnitId() == 999
							|| gameState.boardTiles[tiley][tilex + 1].getUnitId() <= 19
							|| gameState.boardTiles[tiley][tilex + 1].getUnitId() == 77)
					&& !gameState.boardTiles[tiley][tilex + 2].isHasUnit()) {
				BasicCommands.drawTile(out, gameState.boardTiles[tiley][tilex + 2], 1);
				gameState.boardTiles[tiley][tilex + 2].setCurrentTileTexture(1);
			}
		}

		if (tiley != 0 && tilex != 8 && !gameState.boardTiles[tiley - 1][tilex + 1].isHasUnit()) {
			// top right
			BasicCommands.drawTile(out, gameState.boardTiles[tiley - 1][tilex + 1], 1);
			gameState.boardTiles[tiley - 1][tilex + 1].setCurrentTileTexture(1);
		}

		if (tiley != 4 && tilex != 8 && !gameState.boardTiles[tiley + 1][tilex + 1].isHasUnit()) {
			// bottom right
			BasicCommands.drawTile(out, gameState.boardTiles[tiley + 1][tilex + 1], 1);
			gameState.boardTiles[tiley + 1][tilex + 1].setCurrentTileTexture(1);
		}

		if (tiley != 0 && tilex != 0 && !gameState.boardTiles[tiley - 1][tilex - 1].isHasUnit()) {
			// top left
			BasicCommands.drawTile(out, gameState.boardTiles[tiley - 1][tilex - 1], 1);
			gameState.boardTiles[tiley - 1][tilex - 1].setCurrentTileTexture(1);
		}

		if (tilex != 0 && tiley != 4 && !gameState.boardTiles[tiley + 1][tilex - 1].isHasUnit()) {
			// bottom left
			BasicCommands.drawTile(out, gameState.boardTiles[tiley + 1][tilex - 1], 1);
			gameState.boardTiles[tiley + 1][tilex - 1].setCurrentTileTexture(1);
		}
	}

	// cancel all hand card highlight
	public void cancelAllHandCardHighlight(GameState gameState, ActorRef out) {
		for (int i = 0; i < gameState.playerHandCard.size(); i++) {
			BasicCommands.drawCard(out, gameState.playerHandCard.get(i), i + 1, 0);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	// check whether any enemies in Ring 2 @Shiyu
	public void findTexture22(GameState gameState, int tiley, int tilex, ActorRef out) {

		if (tiley - 2 >= 0) {
			// top 1
			if (tilex - 2 >= 0					
					&& gameState.boardTiles[tiley - 1][tilex - 1].getCurrentTileTexture() == 1) {			
				if ((gameState.boardTiles[tiley - 2][tilex - 2].getUnitId() >= 20
						&& gameState.boardTiles[tiley - 2][tilex - 2].getUnitId() <= 39)
						|| gameState.boardTiles[tiley - 2][tilex - 2].getUnitId() == 88) {
					BasicCommands.drawTile(out, gameState.boardTiles[tiley - 2][tilex - 2], 2);
					gameState.boardTiles[tiley - 2][tilex - 2].setCurrentTileTexture(2);
				}
			}
			// top 2
			if (tilex - 1 >= 0 && (gameState.boardTiles[tiley - 1][tilex - 1].getCurrentTileTexture() == 1
					|| gameState.boardTiles[tiley - 1][tilex].getCurrentTileTexture() == 1
					|| gameState.boardTiles[tiley - 2][tilex].getCurrentTileTexture() == 1)) {
				if ((gameState.boardTiles[tiley - 2][tilex - 1].getUnitId() >= 20
						&& gameState.boardTiles[tiley - 2][tilex - 1].getUnitId() <= 39)
						|| gameState.boardTiles[tiley - 2][tilex - 1].getUnitId() == 88) {
					BasicCommands.drawTile(out, gameState.boardTiles[tiley - 2][tilex - 1], 2);
					gameState.boardTiles[tiley - 2][tilex - 1].setCurrentTileTexture(2);
				}
			}
			// top 3
			if ((tilex - 1 >= 0 && gameState.boardTiles[tiley - 1][tilex - 1].getCurrentTileTexture() == 1)
					|| gameState.boardTiles[tiley - 1][tilex].getCurrentTileTexture() == 1
					|| (tilex + 1 <= 8 && gameState.boardTiles[tiley - 1][tilex + 1].getCurrentTileTexture() == 1)) {
				if ((gameState.boardTiles[tiley - 2][tilex].getUnitId() >= 20
						&& gameState.boardTiles[tiley - 2][tilex].getUnitId() <= 39)
						|| gameState.boardTiles[tiley - 2][tilex].getUnitId() == 88) {
					BasicCommands.drawTile(out, gameState.boardTiles[tiley - 2][tilex], 2);
					gameState.boardTiles[tiley - 2][tilex].setCurrentTileTexture(2);
				}
			}
			// top 4
			if (tilex + 1 <= 8 && (gameState.boardTiles[tiley - 1][tilex].getCurrentTileTexture() == 1
					|| gameState.boardTiles[tiley - 1][tilex + 1].getCurrentTileTexture() == 1
					|| gameState.boardTiles[tiley - 2][tilex].getCurrentTileTexture() == 1)) {
				if ((gameState.boardTiles[tiley - 2][tilex + 1].getUnitId() >= 20
						&& gameState.boardTiles[tiley - 2][tilex + 1].getUnitId() <= 39)
						|| gameState.boardTiles[tiley - 2][tilex + 1].getUnitId() == 88) {
					BasicCommands.drawTile(out, gameState.boardTiles[tiley - 2][tilex + 1], 2);
					gameState.boardTiles[tiley - 2][tilex + 1].setCurrentTileTexture(2);
				}
			}
			// top 5
			if (tilex + 2 <= 8 && gameState.boardTiles[tiley - 1][tilex + 1].getCurrentTileTexture() == 1) {
				if ((gameState.boardTiles[tiley - 2][tilex + 2].getUnitId() >= 20
						&& gameState.boardTiles[tiley - 2][tilex + 2].getUnitId() <= 39)
						|| gameState.boardTiles[tiley - 2][tilex + 2].getUnitId() == 88) {
					BasicCommands.drawTile(out, gameState.boardTiles[tiley - 2][tilex + 2], 2);
					gameState.boardTiles[tiley - 2][tilex + 2].setCurrentTileTexture(2);
				}
			}
		}

		// left
		if (tilex - 2 >= 0) {
			// left 2
			if (tiley - 1 >= 0 && (gameState.boardTiles[tiley - 1][tilex - 1].getCurrentTileTexture() == 1
					|| gameState.boardTiles[tiley][tilex - 1].getCurrentTileTexture() == 1
					|| gameState.boardTiles[tiley][tilex - 2].getCurrentTileTexture() == 1)) {
				if ((gameState.boardTiles[tiley - 1][tilex - 2].getUnitId() >= 20
						&& gameState.boardTiles[tiley - 1][tilex - 2].getUnitId() <= 39)
						|| gameState.boardTiles[tiley - 1][tilex - 2].getUnitId() == 88) {
					BasicCommands.drawTile(out, gameState.boardTiles[tiley - 1][tilex - 2], 2);
					gameState.boardTiles[tiley - 1][tilex - 2].setCurrentTileTexture(2);
				}
			}

			// left 3
			if ((tiley - 1 >= 0 && gameState.boardTiles[tiley - 1][tilex - 1].getCurrentTileTexture() == 1
					|| gameState.boardTiles[tiley][tilex - 1].getCurrentTileTexture() == 1
					|| (tiley + 1 <= 4 && gameState.boardTiles[tiley + 1][tilex - 1].getCurrentTileTexture() == 1))

			) {
				if ((gameState.boardTiles[tiley][tilex - 2].getUnitId() >= 20
						&& gameState.boardTiles[tiley][tilex - 2].getUnitId() <= 39)
						|| gameState.boardTiles[tiley][tilex - 2].getUnitId() == 88) {
					BasicCommands.drawTile(out, gameState.boardTiles[tiley][tilex - 2], 2);
					gameState.boardTiles[tiley][tilex - 2].setCurrentTileTexture(2);
				}
			}

			// left 4
			if (tiley + 1 <= 4 && (gameState.boardTiles[tiley][tilex - 1].getCurrentTileTexture() == 1
					|| gameState.boardTiles[tiley + 1][tilex - 1].getCurrentTileTexture() == 1
					|| gameState.boardTiles[tiley][tilex - 2].getCurrentTileTexture() == 1)) {
				if ((gameState.boardTiles[tiley + 1][tilex - 2].getUnitId() >= 20
						&& gameState.boardTiles[tiley + 1][tilex - 2].getUnitId() <= 39)
						|| gameState.boardTiles[tiley + 1][tilex - 2].getUnitId() == 88) {
					BasicCommands.drawTile(out, gameState.boardTiles[tiley + 1][tilex - 2], 2);
					gameState.boardTiles[tiley + 1][tilex - 2].setCurrentTileTexture(2);
				}
			}

			// left 5
			if (tiley + 2 <= 4 && gameState.boardTiles[tiley + 1][tilex - 1].getCurrentTileTexture() == 1) {
				if ((gameState.boardTiles[tiley + 2][tilex - 2].getUnitId() >= 20
						&& gameState.boardTiles[tiley + 2][tilex - 2].getUnitId() <= 39)
						|| gameState.boardTiles[tiley + 2][tilex - 2].getUnitId() == 88) {
					BasicCommands.drawTile(out, gameState.boardTiles[tiley + 2][tilex - 2], 2);
					gameState.boardTiles[tiley + 2][tilex - 2].setCurrentTileTexture(2);
				}
			}
		}

		// right
		if (tilex + 2 <= 8) {
			// right 2
			if (tiley - 1 >= 0 && (gameState.boardTiles[tiley - 1][tilex + 1].getCurrentTileTexture() == 1
					|| gameState.boardTiles[tiley][tilex + 1].getCurrentTileTexture() == 1
					|| gameState.boardTiles[tiley][tilex + 2].getCurrentTileTexture() == 1)) {
				if ((gameState.boardTiles[tiley - 1][tilex + 2].getUnitId() >= 20
						&& gameState.boardTiles[tiley - 1][tilex + 2].getUnitId() <= 39)
						|| gameState.boardTiles[tiley - 1][tilex + 2].getUnitId() == 88) {
					BasicCommands.drawTile(out, gameState.boardTiles[tiley - 1][tilex + 2], 2);
					gameState.boardTiles[tiley - 1][tilex + 2].setCurrentTileTexture(2);
				}
			}
			// right 3
			if ((tiley - 1 >= 0 && gameState.boardTiles[tiley - 1][tilex + 1].getCurrentTileTexture() == 1)
					|| gameState.boardTiles[tiley][tilex + 1].getCurrentTileTexture() == 1
					|| (tiley + 1 <= 4 && gameState.boardTiles[tiley + 1][tilex + 1].getCurrentTileTexture() == 1)) {
				if ((gameState.boardTiles[tiley][tilex + 2].getUnitId() >= 20
						&& gameState.boardTiles[tiley][tilex + 2].getUnitId() <= 39)
						|| gameState.boardTiles[tiley][tilex + 2].getUnitId() == 88) {
					BasicCommands.drawTile(out, gameState.boardTiles[tiley][tilex + 2], 2);
					gameState.boardTiles[tiley][tilex + 2].setCurrentTileTexture(2);
				}
			}

			// right 4
			if (tiley + 1 <= 4 && (gameState.boardTiles[tiley + 1][tilex + 1].getCurrentTileTexture() == 1
					|| gameState.boardTiles[tiley][tilex + 1].getCurrentTileTexture() == 1
					|| gameState.boardTiles[tiley][tilex + 2].getCurrentTileTexture() == 1)) {
				if ((gameState.boardTiles[tiley + 1][tilex + 2].getUnitId() >= 20
						&& gameState.boardTiles[tiley + 1][tilex + 2].getUnitId() <= 39)
						|| gameState.boardTiles[tiley + 1][tilex + 2].getUnitId() == 88) {
					BasicCommands.drawTile(out, gameState.boardTiles[tiley + 1][tilex + 2], 2);
					gameState.boardTiles[tiley + 1][tilex + 2].setCurrentTileTexture(2);
				}
			}

			// right 5
			if (tiley + 2 <= 4 && gameState.boardTiles[tiley + 1][tilex + 1].getCurrentTileTexture() == 1) {
				if ((gameState.boardTiles[tiley + 2][tilex + 2].getUnitId() >= 20
						&& gameState.boardTiles[tiley + 2][tilex + 2].getUnitId() <= 39)
						|| gameState.boardTiles[tiley + 2][tilex + 2].getUnitId() == 88) {
					BasicCommands.drawTile(out, gameState.boardTiles[tiley + 2][tilex + 2], 2);
					gameState.boardTiles[tiley + 2][tilex + 2].setCurrentTileTexture(2);
				}
			}
		}

		// bottom
		if (tiley + 2 <= 4) {
			// bottom 2
			if (tilex - 1 >= 0 && (gameState.boardTiles[tiley + 1][tilex - 1].getCurrentTileTexture() == 1
					|| gameState.boardTiles[tiley + 1][tilex].getCurrentTileTexture() == 1
					|| gameState.boardTiles[tiley + 2][tilex].getCurrentTileTexture() == 1)) {
				if ((gameState.boardTiles[tiley + 2][tilex - 1].getUnitId() >= 20
						&& gameState.boardTiles[tiley + 2][tilex - 1].getUnitId() <= 39)
						|| gameState.boardTiles[tiley + 2][tilex - 1].getUnitId() == 88) {
					BasicCommands.drawTile(out, gameState.boardTiles[tiley + 2][tilex - 1], 2);
					gameState.boardTiles[tiley + 2][tilex - 1].setCurrentTileTexture(2);
				}
			}
			// bottom 3
			if ((tilex - 1 >= 0 && gameState.boardTiles[tiley + 1][tilex - 1].getCurrentTileTexture() == 1)
					|| gameState.boardTiles[tiley + 1][tilex].getCurrentTileTexture() == 1
					|| (tilex + 1 <= 8 && gameState.boardTiles[tiley + 1][tilex + 1].getCurrentTileTexture() == 1)) {
				if ((gameState.boardTiles[tiley + 2][tilex].getUnitId() >= 20
						&& gameState.boardTiles[tiley + 2][tilex].getUnitId() <= 39)
						|| gameState.boardTiles[tiley + 2][tilex].getUnitId() == 88) {
					BasicCommands.drawTile(out, gameState.boardTiles[tiley + 2][tilex], 2);
					gameState.boardTiles[tiley + 2][tilex].setCurrentTileTexture(2);
				}
			}
			// bottom 4
			if (tilex + 1 <= 8 && (gameState.boardTiles[tiley + 1][tilex].getCurrentTileTexture() == 1
					|| gameState.boardTiles[tiley + 1][tilex + 1].getCurrentTileTexture() == 1
					|| gameState.boardTiles[tiley + 2][tilex].getCurrentTileTexture() == 1)) {
				if ((gameState.boardTiles[tiley + 2][tilex + 1].getUnitId() >= 20
						&& gameState.boardTiles[tiley + 2][tilex + 1].getUnitId() <= 39)
						|| gameState.boardTiles[tiley + 2][tilex + 1].getUnitId() == 88) {
					BasicCommands.drawTile(out, gameState.boardTiles[tiley + 2][tilex + 1], 2);
					gameState.boardTiles[tiley + 2][tilex + 1].setCurrentTileTexture(2);
				}
			}
		}
	}

	//check whether any enemies in Ring 3 @Shiyu
	public void findTexture23(GameState gameState, int tiley, int tilex, ActorRef out) {
		//left
		if (tilex - 3 >= 0) {
			if (gameState.boardTiles[tiley][tilex - 2].getCurrentTileTexture() == 1) {
				
				if (tiley - 1 >= 0) {
					if ((gameState.boardTiles[tiley - 1][tilex - 3].getUnitId() >= 20
							&& gameState.boardTiles[tiley - 1][tilex - 3].getUnitId() <= 39)
							|| gameState.boardTiles[tiley - 1][tilex - 3].getUnitId() == 88) {
						BasicCommands.drawTile(out, gameState.boardTiles[tiley - 1][tilex - 3], 2);
						gameState.boardTiles[tiley - 1][tilex - 3].setCurrentTileTexture(2);
					}
				}

				if ((gameState.boardTiles[tiley][tilex - 3].getUnitId() >= 20
						&& gameState.boardTiles[tiley][tilex - 3].getUnitId() <= 39)
						|| gameState.boardTiles[tiley][tilex - 3].getUnitId() == 88) {
					BasicCommands.drawTile(out, gameState.boardTiles[tiley][tilex - 3], 2);
					gameState.boardTiles[tiley][tilex - 3].setCurrentTileTexture(2);
				}
				if (tiley + 1 <= 4) {
					if ((gameState.boardTiles[tiley + 1][tilex - 3].getUnitId() >= 20
							&& gameState.boardTiles[tiley + 1][tilex - 3].getUnitId() <= 39)
							|| gameState.boardTiles[tiley + 1][tilex - 3].getUnitId() == 88) {
						BasicCommands.drawTile(out, gameState.boardTiles[tiley + 1][tilex - 3], 2);
						gameState.boardTiles[tiley + 1][tilex - 3].setCurrentTileTexture(2);
					}
				}
			}
		}
		//right
		if (tilex + 3 <= 8) {
			if (gameState.boardTiles[tiley][tilex + 2].getCurrentTileTexture() == 1) {
				//
				if (tiley - 1 >= 0) {
					if ((gameState.boardTiles[tiley - 1][tilex + 3].getUnitId() >= 20
							&& gameState.boardTiles[tiley - 1][tilex + 3].getUnitId() <= 39)
							|| gameState.boardTiles[tiley - 1][tilex + 3].getUnitId() == 88) {
						BasicCommands.drawTile(out, gameState.boardTiles[tiley - 1][tilex + 3], 2);
						gameState.boardTiles[tiley - 1][tilex + 3].setCurrentTileTexture(2);
					}
				}

				if ((gameState.boardTiles[tiley][tilex + 3].getUnitId() >= 20
						&& gameState.boardTiles[tiley][tilex + 3].getUnitId() <= 39)
						|| gameState.boardTiles[tiley][tilex + 3].getUnitId() == 88) {
					BasicCommands.drawTile(out, gameState.boardTiles[tiley][tilex + 3], 2);
					gameState.boardTiles[tiley][tilex + 3].setCurrentTileTexture(2);
				}
				if (tiley + 1 <= 4) {
					if ((gameState.boardTiles[tiley + 1][tilex + 3].getUnitId() >= 20
							&& gameState.boardTiles[tiley + 1][tilex + 3].getUnitId() <= 39)
							|| gameState.boardTiles[tiley + 1][tilex + 3].getUnitId() == 88) {
						BasicCommands.drawTile(out, gameState.boardTiles[tiley + 1][tilex + 3], 2);
						gameState.boardTiles[tiley + 1][tilex + 3].setCurrentTileTexture(2);
					}
				}
			}
		}
		// top
		if (tiley - 3 >= 0) {
			if (gameState.boardTiles[tiley - 2][tilex].getCurrentTileTexture() == 1) {
				//
				if (tilex - 1 >= 0) {
					if ((gameState.boardTiles[tiley - 3][tilex - 1].getUnitId() >= 20
							&& gameState.boardTiles[tiley - 3][tilex - 1].getUnitId() <= 39)
							|| gameState.boardTiles[tiley - 3][tilex - 1].getUnitId() == 88) {
						BasicCommands.drawTile(out, gameState.boardTiles[tiley - 3][tilex - 1], 2);
						gameState.boardTiles[tiley - 3][tilex - 1].setCurrentTileTexture(2);
					}
				}

				if ((gameState.boardTiles[tiley - 3][tilex].getUnitId() >= 20
						&& gameState.boardTiles[tiley - 3][tilex].getUnitId() <= 39)
						|| gameState.boardTiles[tiley - 3][tilex].getUnitId() == 88) {
					BasicCommands.drawTile(out, gameState.boardTiles[tiley - 3][tilex], 2);
					gameState.boardTiles[tiley - 3][tilex].setCurrentTileTexture(2);
				}
				if (tilex + 1 <= 8) {
					if ((gameState.boardTiles[tiley - 3][tilex + 1].getUnitId() >= 20
							&& gameState.boardTiles[tiley - 3][tilex + 1].getUnitId() <= 39)
							|| gameState.boardTiles[tiley - 3][tilex + 1].getUnitId() == 88) {
						BasicCommands.drawTile(out, gameState.boardTiles[tiley - 3][tilex + 1], 2);
						gameState.boardTiles[tiley - 3][tilex + 1].setCurrentTileTexture(2);
					}
				}
			}
		}
		// bottom
		if (tiley + 3 <= 4) {
			if (gameState.boardTiles[tiley + 2][tilex].getCurrentTileTexture() == 1) {
				//
				if (tilex - 1 >= 0) {
					if ((gameState.boardTiles[tiley + 3][tilex - 1].getUnitId() >= 20
							&& gameState.boardTiles[tiley + 3][tilex - 1].getUnitId() <= 39)
							|| gameState.boardTiles[tiley + 3][tilex - 1].getUnitId() == 88) {
						BasicCommands.drawTile(out, gameState.boardTiles[tiley + 3][tilex - 1], 2);
						gameState.boardTiles[tiley + 3][tilex - 1].setCurrentTileTexture(2);
					}
				}
				if ((gameState.boardTiles[tiley + 3][tilex].getUnitId() >= 20
						&& gameState.boardTiles[tiley + 3][tilex].getUnitId() <= 39)
						|| gameState.boardTiles[tiley + 3][tilex].getUnitId() == 88) {
					BasicCommands.drawTile(out, gameState.boardTiles[tiley + 3][tilex], 2);
					gameState.boardTiles[tiley + 3][tilex].setCurrentTileTexture(2);
				}
				if (tilex + 1 <= 8) {
					if ((gameState.boardTiles[tiley + 3][tilex + 1].getUnitId() >= 20
							&& gameState.boardTiles[tiley + 3][tilex + 1].getUnitId() <= 39)
							|| gameState.boardTiles[tiley + 3][tilex + 1].getUnitId() == 88) {
						BasicCommands.drawTile(out, gameState.boardTiles[tiley + 3][tilex + 1], 2);
						gameState.boardTiles[tiley + 3][tilex + 1].setCurrentTileTexture(2);
					}
				}
			}
		}
	}
	
	//show all highlight info @Shiyu
	public void clickMyUnit(GameState gameState, int tiley, int tilex, ActorRef out) {

		boolean isProvoked = false;

		if (gameState.playerUnitList.get(searchWhichOurUnit(gameState, tiley, tilex)).moved != true) {
			findTexture1(gameState, tiley, tilex, out);
		}
		try {
			Thread.sleep(5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		isProvoked = haveProvoke(gameState, tiley, tilex, out);

		if (gameState.playerUnitList.get(searchWhichOurUnit(gameState, tiley, tilex)).attacked != true && !isProvoked) {
			findTexture2(gameState, tiley, tilex, out);
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			tileBugFix2(gameState, tiley, tilex, out);
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			findTexture22(gameState, tiley, tilex, out);
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			findTexture23(gameState, tiley, tilex, out);
			// Bug fix @Shiyu
			tileBugFix1(gameState, tiley, tilex, out);
			tileBugFix2(gameState, tiley, tilex, out);
		}

		if (isProvoked) {
			hightlightProvokeUnit(gameState, tiley, tilex, out);
		}
		try {
			Thread.sleep(5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	// cancel all tiles highlight @Shiyu
	public void cancellAllTileTexture(GameState gameState, ActorRef out) {
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 9; j++) {
				if (gameState.boardTiles[i][j].getCurrentTileTexture() == 1
						|| gameState.boardTiles[i][j].getCurrentTileTexture() == 2) {
					gameState.boardTiles[i][j].setCurrentTileTexture(0);
					BasicCommands.drawTile(out, gameState.boardTiles[i][j], 0);
				}
			}
		}
	}

	// movement @Shiyu
	public void tile2Tile(ActorRef out, GameState gameState, int preUnitx, int preUnity, int tilex, int tiley) {
		Unit u = gameState.playerUnitList.get(searchWhichOurUnit(gameState, preUnity, preUnitx));
		int id = u.getId();

		if (Math.abs(tilex - preUnitx) == 1 && Math.abs(tiley - preUnity) == 1) {
			// different move animation @YuZeng
			int tilexDirectionHelper = tilex; // x = x of destination
			int tileyDirectionHelper = preUnity; // y = y of start point

			if (gameState.boardTiles[tileyDirectionHelper][tilexDirectionHelper].isHasUnit()
					&& (gameState.boardTiles[tileyDirectionHelper][tilexDirectionHelper].getUnitId() > 19
							&& gameState.boardTiles[tileyDirectionHelper][tilexDirectionHelper].getUnitId() != 77)) {
				// y first
				System.out.println("y first");
				BasicCommands.moveUnitToTile(out, u, gameState.boardTiles[tiley][tilex], true);
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				// x first
				System.out.println("x first");
				BasicCommands.moveUnitToTile(out, u, gameState.boardTiles[tiley][tilex]);
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} else {
			BasicCommands.moveUnitToTile(out, u, gameState.boardTiles[tiley][tilex]);
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// transport info from tile to tile @Shiyu
		gameState.boardTiles[tiley][tilex].setUnitId(id);
		gameState.boardTiles[tiley][tilex].setHasUnit(true);

		Tile tmp = BasicObjectBuilders.loadTile(preUnitx, preUnity);
		gameState.boardTiles[preUnity][preUnitx] = tmp;

		gameState.playerUnitList.get(searchWhichOurUnit(gameState, tiley, tilex))
				.setPositionByTile(gameState.boardTiles[tiley][tilex]);
	}

	//search the AI unit index in our unit list @Shiyu
	public int searchWhichOurUnit(GameState gameState, int tiley, int tilex) {
		int ourUnitId = gameState.boardTiles[tiley][tilex].getUnitId();
		for (Unit u : gameState.playerUnitList) {
			if (u.getId() == ourUnitId) {
				return gameState.playerUnitList.indexOf(u);
			} else {
				continue;
			}
		}
		return 999;
	}
	
	//search the AI unit index in AI unit list @Shiyu
	public int searchWhichAIUnit(GameState gameState, int tiley, int tilex) {
		int ourUnitId = gameState.boardTiles[tiley][tilex].getUnitId();
		for (Unit u : gameState.AIUnitList) {
			if (u.getId() == ourUnitId) {
				return gameState.AIUnitList.indexOf(u);
			} else {
				continue;
			}
		}
		return 999;
	}

	//bug fix 1 @Shiyu
	public void tileBugFix1(GameState gameState, int tiley, int tilex, ActorRef out) {
		// top right x2
		if (tiley - 2 >= 0 && tilex + 2 <= 8) {
			if (gameState.boardTiles[tiley - 1][tilex + 1].isHasUnit()) {
				//System.out.println("hello1");
				gameState.boardTiles[tiley - 2][tilex + 2].setCurrentTileTexture(0);
				BasicCommands.drawTile(out, gameState.boardTiles[tiley - 2][tilex + 2], 0);
			}
		}

		// bottom right x2 highlight cancel
		if (tiley + 2 <= 4 && tilex + 2 <= 8) {

			if (gameState.boardTiles[tiley + 1][tilex + 1].isHasUnit()) {
				//System.out.println("hello2");
				gameState.boardTiles[tiley + 2][tilex + 2].setCurrentTileTexture(0);
				BasicCommands.drawTile(out, gameState.boardTiles[tiley + 2][tilex + 2], 0);
			}
		}

		// top left x2 highlight cancel
		if (tiley - 2 >= 0 && tilex - 2 >= 0) {
			if (gameState.boardTiles[tiley - 1][tilex - 1].isHasUnit()) {
				//System.out.println("hello3");
				gameState.boardTiles[tiley - 2][tilex - 2].setCurrentTileTexture(0);
				BasicCommands.drawTile(out, gameState.boardTiles[tiley - 2][tilex - 2], 0);
			}
		}

		// bottom left x2 highlight cancel
		if (tiley + 2 <= 4 && tilex - 2 >= 0) {

			if (gameState.boardTiles[tiley + 1][tilex - 1].isHasUnit()) {
				System.out.println("hello4");
				gameState.boardTiles[tiley + 2][tilex - 2].setCurrentTileTexture(0);
				BasicCommands.drawTile(out, gameState.boardTiles[tiley + 2][tilex - 2], 0);
			}
		}

		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	//but fix 2 @Shiyu
	public void tileBugFix2(GameState gameState, int tiley, int tilex, ActorRef out) {
		// System.out.println("hello5");
		// top right x2
		if (tiley - 2 >= 0 && tilex + 2 <= 8) {
			//System.out.println("hello6");
			if (gameState.boardTiles[tiley - 1][tilex].getCurrentTileTexture() == 2
					&& gameState.boardTiles[tiley][tilex + 1].getCurrentTileTexture() == 2) {
				//System.out.println("hello7");
				BasicCommands.drawTile(out, gameState.boardTiles[tiley - 2][tilex + 2], 0);
				gameState.boardTiles[tiley - 2][tilex + 2].setCurrentTileTexture(0);
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		// top left x2
		if (tiley - 2 >= 0 && tilex - 2 >= 0) {
			// System.out.println("hello8");
			if (gameState.boardTiles[tiley - 1][tilex].getCurrentTileTexture() == 2
					&& gameState.boardTiles[tiley][tilex - 1].getCurrentTileTexture() == 2) {
				//System.out.println("hello9");
				BasicCommands.drawTile(out, gameState.boardTiles[tiley - 2][tilex - 2], 0);
				gameState.boardTiles[tiley - 2][tilex - 2].setCurrentTileTexture(0);
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		// bottom right x2
		if (tiley + 2 <= 4 && tilex + 2 <= 8) {
			//System.out.println("hello10");
			if (gameState.boardTiles[tiley + 1][tilex].getCurrentTileTexture() == 2
					&& gameState.boardTiles[tiley][tilex + 1].getCurrentTileTexture() == 2) {
				//System.out.println("hello11");
				BasicCommands.drawTile(out, gameState.boardTiles[tiley + 2][tilex + 2], 0);
				gameState.boardTiles[tiley + 2][tilex + 2].setCurrentTileTexture(0);
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		// bottom left x2
		if (tiley + 2 <= 4 && tilex - 2 >= 0) {
			// System.out.println("hello12");
			if (gameState.boardTiles[tiley + 1][tilex].getCurrentTileTexture() == 2
					&& gameState.boardTiles[tiley][tilex - 1].getCurrentTileTexture() == 2) {
				//System.out.println("hello13");
				BasicCommands.drawTile(out, gameState.boardTiles[tiley + 2][tilex - 2], 0);
				gameState.boardTiles[tiley + 2][tilex - 2].setCurrentTileTexture(0);
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		// top right x2
		if (tiley - 1 >= 0 && tilex + 1 <= 8) {
			//System.out.println("hello14");
			if (gameState.boardTiles[tiley - 1][tilex].getCurrentTileTexture() == 2
					&& gameState.boardTiles[tiley][tilex + 1].getCurrentTileTexture() == 2
					&& gameState.boardTiles[tiley - 1][tilex + 1].getCurrentTileTexture() == 1) {
				//System.out.println("hello15");
				BasicCommands.drawTile(out, gameState.boardTiles[tiley - 1][tilex + 1], 0);
				gameState.boardTiles[tiley - 1][tilex + 1].setCurrentTileTexture(0);
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		// top left
		if (tiley - 1 >= 0 && tilex - 1 >= 0) {
			//System.out.println("hello16");
			if (gameState.boardTiles[tiley - 1][tilex].getCurrentTileTexture() == 2
					&& gameState.boardTiles[tiley][tilex - 1].getCurrentTileTexture() == 2
					&& gameState.boardTiles[tiley - 1][tilex - 1].getCurrentTileTexture() == 1) {
				//System.out.println("hello17");
				BasicCommands.drawTile(out, gameState.boardTiles[tiley - 1][tilex - 1], 0);
				gameState.boardTiles[tiley - 1][tilex - 1].setCurrentTileTexture(0);
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		// bottom left
		if (tiley + 1 <= 4 && tilex - 1 >= 0) {
			//System.out.println("hello18");
			if (gameState.boardTiles[tiley + 1][tilex].getCurrentTileTexture() == 2
					&& gameState.boardTiles[tiley][tilex - 1].getCurrentTileTexture() == 2
					&& gameState.boardTiles[tiley + 1][tilex - 1].getCurrentTileTexture() == 1) {
				//System.out.println("hello19");
				BasicCommands.drawTile(out, gameState.boardTiles[tiley + 1][tilex - 1], 0);
				gameState.boardTiles[tiley + 1][tilex - 1].setCurrentTileTexture(0);
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		// bottom right
		if (tiley + 1 <= 4 && tilex + 1 <= 8) {
			//System.out.println("hello20");
			if (gameState.boardTiles[tiley + 1][tilex].getCurrentTileTexture() == 2
					&& gameState.boardTiles[tiley][tilex + 1].getCurrentTileTexture() == 2
					&& gameState.boardTiles[tiley + 1][tilex + 1].getCurrentTileTexture() == 1) {
				//System.out.println("hello21");
				BasicCommands.drawTile(out, gameState.boardTiles[tiley + 1][tilex + 1], 0);
				gameState.boardTiles[tiley + 1][tilex + 1].setCurrentTileTexture(0);
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	//find provoke unit around my unit @Shiyu
	public boolean haveProvoke(GameState gameState, int tiley, int tilex, ActorRef out) {
		// top left
		if (tiley - 1 >= 0 && tilex - 1 >= 0) {
			if (gameState.boardTiles[tiley - 1][tilex - 1].getUnitId() >= 20
					&& gameState.boardTiles[tiley - 1][tilex - 1].getUnitId() <= 39) {
				if (gameState.AIUnitList.get(searchWhichAIUnit(gameState, tiley - 1, tilex - 1)).getCanProvoke()) {
					return true;
				}
			}
		}

		// left
		if (tilex - 1 >= 0) {
			if (gameState.boardTiles[tiley][tilex - 1].getUnitId() >= 20
					&& gameState.boardTiles[tiley][tilex - 1].getUnitId() <= 39) {
				if (gameState.AIUnitList.get(searchWhichAIUnit(gameState, tiley, tilex - 1)).getCanProvoke()) {
					return true;
				}
			}
		}

		// bottom left
		if (tiley + 1 <= 4 && tilex - 1 >= 0) {
			if (gameState.boardTiles[tiley + 1][tilex - 1].getUnitId() >= 20
					&& gameState.boardTiles[tiley + 1][tilex - 1].getUnitId() <= 39) {
				if (gameState.AIUnitList.get(searchWhichAIUnit(gameState, tiley + 1, tilex - 1)).getCanProvoke()) {
					return true;
				}
			}
		}

		// bottom
		if (tiley + 1 <= 4) {
			if (gameState.boardTiles[tiley + 1][tilex].getUnitId() >= 20
					&& gameState.boardTiles[tiley + 1][tilex].getUnitId() <= 39) {
				if (gameState.AIUnitList.get(searchWhichAIUnit(gameState, tiley + 1, tilex)).getCanProvoke()) {
					return true;
				}
			}
		}

		// bottom right
		if (tiley + 1 <= 4 && tilex + 1 <= 8) {
			if (gameState.boardTiles[tiley + 1][tilex + 1].getUnitId() >= 20
					&& gameState.boardTiles[tiley + 1][tilex + 1].getUnitId() <= 39) {
				if (gameState.AIUnitList.get(searchWhichAIUnit(gameState, tiley + 1, tilex + 1)).getCanProvoke()) {
					return true;
				}
			}
		}

		// right
		if (tilex + 1 <= 8) {
			if (gameState.boardTiles[tiley][tilex + 1].getUnitId() >= 20
					&& gameState.boardTiles[tiley][tilex + 1].getUnitId() <= 39) {
				if (gameState.AIUnitList.get(searchWhichAIUnit(gameState, tiley, tilex + 1)).getCanProvoke()) {
					return true;
				}
			}
		}

		// top right
		if (tiley - 1 >= 0 && tilex + 1 <= 8) {
			if (gameState.boardTiles[tiley - 1][tilex + 1].getUnitId() >= 20
					&& gameState.boardTiles[tiley - 1][tilex + 1].getUnitId() <= 39) {
				if (gameState.AIUnitList.get(searchWhichAIUnit(gameState, tiley - 1, tilex + 1)).getCanProvoke()) {
					return true;
				}
			}
		}

		// top
		if (tiley - 1 >= 0) {
			if (gameState.boardTiles[tiley - 1][tilex].getUnitId() >= 20
					&& gameState.boardTiles[tiley - 1][tilex].getUnitId() <= 39) {
				if (gameState.AIUnitList.get(searchWhichAIUnit(gameState, tiley - 1, tilex)).getCanProvoke()) {
					return true;
				}
			}
		}

		return false;

	}

	public void hightlightProvokeUnit(GameState gameState, int tiley, int tilex, ActorRef out) {

		// top left
		if (tiley - 1 >= 0 && tilex - 1 >= 0) {
			if (gameState.boardTiles[tiley - 1][tilex - 1].getUnitId() >= 20
					&& gameState.boardTiles[tiley - 1][tilex - 1].getUnitId() <= 39) {
				if (gameState.AIUnitList.get(searchWhichAIUnit(gameState, tiley - 1, tilex - 1)).getCanProvoke()) {
					gameState.boardTiles[tiley - 1][tilex - 1].setCurrentTileTexture(2);
					BasicCommands.drawTile(out, gameState.boardTiles[tiley - 1][tilex - 1], 2);
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}

		// left
		if (tilex - 1 >= 0) {
			if (gameState.boardTiles[tiley][tilex - 1].getUnitId() >= 20
					&& gameState.boardTiles[tiley][tilex - 1].getUnitId() <= 39) {
				if (gameState.AIUnitList.get(searchWhichAIUnit(gameState, tiley, tilex - 1)).getCanProvoke()) {
					gameState.boardTiles[tiley][tilex - 1].setCurrentTileTexture(2);
					BasicCommands.drawTile(out, gameState.boardTiles[tiley][tilex - 1], 2);
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}

		// bottom left
		if (tiley + 1 <= 4 && tilex - 1 >= 0) {
			if (gameState.boardTiles[tiley + 1][tilex - 1].getUnitId() >= 20
					&& gameState.boardTiles[tiley + 1][tilex - 1].getUnitId() <= 39) {
				if (gameState.AIUnitList.get(searchWhichAIUnit(gameState, tiley + 1, tilex - 1)).getCanProvoke()) {
					gameState.boardTiles[tiley + 1][tilex - 1].setCurrentTileTexture(2);
					BasicCommands.drawTile(out, gameState.boardTiles[tiley + 1][tilex - 1], 2);
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}

		// bottom
		if (tiley + 1 <= 4) {
			if (gameState.boardTiles[tiley + 1][tilex].getUnitId() >= 20
					&& gameState.boardTiles[tiley + 1][tilex].getUnitId() <= 39) {
				if (gameState.AIUnitList.get(searchWhichAIUnit(gameState, tiley + 1, tilex)).getCanProvoke()) {
					gameState.boardTiles[tiley + 1][tilex].setCurrentTileTexture(2);
					BasicCommands.drawTile(out, gameState.boardTiles[tiley + 1][tilex], 2);
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}

		// bottom right
		if (tiley + 1 <= 4 && tilex + 1 <= 8) {
			if (gameState.boardTiles[tiley + 1][tilex + 1].getUnitId() >= 20
					&& gameState.boardTiles[tiley + 1][tilex + 1].getUnitId() <= 39) {
				if (gameState.AIUnitList.get(searchWhichAIUnit(gameState, tiley + 1, tilex + 1)).getCanProvoke()) {
					gameState.boardTiles[tiley + 1][tilex + 1].setCurrentTileTexture(2);
					BasicCommands.drawTile(out, gameState.boardTiles[tiley + 1][tilex + 1], 2);
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}

		// right
		if (tilex + 1 <= 8) {
			if (gameState.boardTiles[tiley][tilex + 1].getUnitId() >= 20
					&& gameState.boardTiles[tiley][tilex + 1].getUnitId() <= 39) {
				if (gameState.AIUnitList.get(searchWhichAIUnit(gameState, tiley, tilex + 1)).getCanProvoke()) {
					gameState.boardTiles[tiley][tilex + 1].setCurrentTileTexture(2);
					BasicCommands.drawTile(out, gameState.boardTiles[tiley][tilex + 1], 2);
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}

		// top right
		if (tiley - 1 >= 0 && tilex + 1 <= 8) {
			if (gameState.boardTiles[tiley - 1][tilex + 1].getUnitId() >= 20
					&& gameState.boardTiles[tiley - 1][tilex + 1].getUnitId() <= 39) {
				if (gameState.AIUnitList.get(searchWhichAIUnit(gameState, tiley - 1, tilex + 1)).getCanProvoke()) {
					gameState.boardTiles[tiley - 1][tilex + 1].setCurrentTileTexture(2);
					BasicCommands.drawTile(out, gameState.boardTiles[tiley - 1][tilex + 1], 2);
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}

		// top
		if (tiley - 1 >= 0) {
			if (gameState.boardTiles[tiley - 1][tilex].getUnitId() >= 20
					&& gameState.boardTiles[tiley - 1][tilex].getUnitId() <= 39) {
				if (gameState.AIUnitList.get(searchWhichAIUnit(gameState, tiley - 1, tilex)).getCanProvoke()) {
					gameState.boardTiles[tiley - 1][tilex].setCurrentTileTexture(2);
					BasicCommands.drawTile(out, gameState.boardTiles[tiley - 1][tilex], 2);
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public boolean haveRangeAttack(GameState gameState, int tiley, int tilex, ActorRef out) {
		int uid = gameState.boardTiles[tiley][tilex].getUnitId();
		if (uid <= 19 || uid == 77) {
			if (gameState.playerUnitList.get(searchWhichOurUnit(gameState, tiley, tilex)).isRangeAttack() == true) {
				return true;
			}
		}
		return false;
	}

	public boolean haveRangeMove(GameState gameState, int tiley, int tilex, ActorRef out) {
		int uid = gameState.boardTiles[tiley][tilex].getUnitId();
		if (uid <= 19 || uid == 77) {
			if (gameState.playerUnitList.get(searchWhichOurUnit(gameState, tiley, tilex)).isFlying() == true) {
				return true;
			}
		}
		return false;
	}
}
