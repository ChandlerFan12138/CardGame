package structures.basic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;

/**
 * This is a representation of a Unit on the game board.
 * A unit has a unique id (this is used by the front-end.
 * Each unit has a current UnitAnimationType, e.g. move,
 * or attack. The position is the physical position on the
 * board. UnitAnimationSet contains the underlying information
 * about the animation frames, while ImageCorrection has
 * information for centering the unit on the tile. 
 * 
 * @author Dr. Richard McCreadie
 *
 *@author Shiyu, Zeyu, Yunsheng, Yunyi
 */
public class Unit {
	
	public static final int MAX_UNIT_ID = 999;

	@JsonIgnore
	protected static ObjectMapper mapper = new ObjectMapper(); // Jackson Java Object Serializer, is used to read java objects from a file
	
	int id;
	UnitAnimationType animation;
	Position position;
	UnitAnimationSet animations;
	ImageCorrection correction;
	
	String unitName;

	//judg if a unit can move and default true during the turn when it's been summoned
	public boolean moved = true;
	//judg if a unit can attack and default true during the turn when it's been summoned
	public boolean attacked = true;
	
	//attack
	int attack;
	int startAttack;
		
	//health
	int health;
	int startHealth;

	//judg if it has special abilities
	boolean canProvoke = false;
	boolean rangeAttack = false;//@Zeyu
	boolean flying = false;
	boolean attackTwice = false; //@ZY2543042
	protected boolean firstAttacked = false; // @ZY2543042

	//constructors
	public Unit() {}
	
	public Unit(int id, UnitAnimationSet animations, ImageCorrection correction) {
		super();
		this.id = id;
		this.animation = UnitAnimationType.idle;
		
		position = new Position(0,0,0,0);
		this.correction = correction;
		this.animations = animations;
		System.out.println("constructor1");
	}
	
	public Unit(int id, UnitAnimationSet animations, ImageCorrection correction, Tile currentTile) {
		super();
		this.id = id;
		this.animation = UnitAnimationType.idle;
		
		position = new Position(currentTile.getXpos(),currentTile.getYpos(),currentTile.getTilex(),currentTile.getTiley());
		this.correction = correction;
		this.animations = animations;
		System.out.println("constructor2");
	}
	
	public Unit(int id, UnitAnimationType animation, Position position, UnitAnimationSet animations,
			ImageCorrection correction) {
		super();
		this.id = id;
		this.animation = animation;
		this.position = position;
		this.animations = animations;
		this.correction = correction;
		System.out.println("constructor3");
	}
	
	//getters and setters
	public boolean isFlying() {
		return this.flying;
	}
	
	public boolean getCanProvoke() {
		return this.canProvoke;
	}

	public void setCanProvoke(boolean canProvoke) {
		//
		this.canProvoke = canProvoke;
	}
	
	public boolean canAttackTwice() {
		// @ZY2543042
		return this.attackTwice ;
	}
	
	public boolean isFirstAttacked() {
		// @ZY2543042
		return this.firstAttacked;
	}
	
	public void setFirstAttaced(boolean firstAttacked) {
		// @ZY2543042
		this.firstAttacked = firstAttacked ;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public UnitAnimationType getAnimation() {
		return animation;
	}
	public void setAnimation(UnitAnimationType animation) {
		this.animation = animation;
	}

	public ImageCorrection getCorrection() {
		return correction;
	}

	public void setCorrection(ImageCorrection correction) {
		this.correction = correction;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public UnitAnimationSet getAnimations() {
		return animations;
	}

	public void setAnimations(UnitAnimationSet animations) {
		this.animations = animations;
	}
	
	public int getAttack() {
		return attack;
	}
	public void setAttack(int attack) {
		this.attack = attack;
	}
	public void setAttack(ActorRef out, int attack) {
		this.attack = attack;
		BasicCommands.setUnitAttack(out, this, attack);
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
	}
	public void setStartAttack(int startAttack) {
		this.startAttack = startAttack;
	}
	public int getStartAttack() {
		return startAttack;
	}

	public int getHealth() {
		return health;
	}
	public void setHealth(int health) {
		this.health = health;
	}
	/*
	 * overload setHealth method
	 * set health and draw health
	 */
	public void setHealth(ActorRef out, int health) {
		this.health = health;
		BasicCommands.setUnitHealth(out, this, health);
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
	}

	/*
	 * overload setHealth method
	 * set health and draw health. if it's an avatar, set and draw player's health
	 */
	public void setHealth(ActorRef out, GameState gameState, int health) {
		BasicCommands.setUnitHealth(out, this, health);
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
		int tempHealth = this.health;
		this.health = health;
		if(this.id == 88) {
			gameState.AIPlayer.setAIPlayerHealth(out, health);
		}else if (this.id == 77) {
			gameState.Player1.setPlayerHealth(out, health);
			if(tempHealth > health) {
				//check Silverguard Knight's ability2
				Unit.silverguardKnightAbility1(out, gameState);
			}
		}
	}
	
	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	/*
	 * if a unit dead, delete it from its player's list and set its tiles attributes
	 */
	public void dead(ActorRef out, GameState gameState, int tilex, int tiley) {
		//set 0 health
		BasicCommands.setUnitHealth(out, this, 0);
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
		this.health = 0;
		//check it belongs which player and delete unit from unit list
		if(gameState.AIUnitList.contains(this)) {
			gameState.AIUnitList.remove(this);
		}else if(gameState.playerUnitList.contains(this)){
			gameState.playerUnitList.remove(this);
		}
		//unit dead animation
		BasicCommands.playUnitAnimation(out, this, UnitAnimationType.death);
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
		if(this.id == 77) {
			gameState.Player1.setPlayerHealth(out, 0);
			gameState.isPlayerTurn = false;
			gameState.isGameOver = true; //@ZY
			System.out.println("Player is lost!!! player's turn: "+gameState.isPlayerTurn);//@ZY
			
			//check Silverguard Knight's ability2
			Unit.silverguardKnightAbility1(out, gameState);
			
			BasicCommands.addPlayer1Notification(out, "AI win!", 10);
			try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();} 
		}else if(this.id == 88) {
			gameState.AIPlayer.setAIPlayerHealth(out, 0);
			gameState.isPlayerTurn = false;
			gameState.isGameOver = true; //@ZY
			System.out.println("AI is lost!!! player's turn: "+gameState.isPlayerTurn);//@ZY
			
			BasicCommands.addPlayer1Notification(out, "Player win!", 10);
			try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();} 
		}else if(this.id == 3||this.id == 10) {
			gameState.hasSilverguardKnight = false;
			System.out.println("silverguardKnight dead"+gameState.hasSilverguardKnight);
		}
		//delete the card
		BasicCommands.deleteUnit(out, this);
		gameState.boardTiles[tiley][tilex].setUnitId(999);
		gameState.boardTiles[tiley][tilex].hasUnit=false;
		if(this.id == 3||this.id == 10) {
			gameState.hasSilverguardKnight=false;
		}
	}

	/*
	*  combine setHealth method and dead method 
	*/
	public void setHealthAndDeath(ActorRef out, GameState gameState, int tilex, int tiley, int health) {
		if(health <= 0) {
			this.dead(out, gameState, tilex, tiley);
		}else {
			BasicCommands.setUnitHealth(out, this, health);
			try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
			int tempHealth = health;
			this.health = health;
			if(this.id == 88) {
				gameState.AIPlayer.setAIPlayerHealth(out, health);
				if(tempHealth > health) {
					
				}
			}else if (this.id == 77) {
				gameState.Player1.setPlayerHealth(out, health);
				if(tempHealth > health) {
					//check Silverguard Knight's ability2
					Unit.silverguardKnightAbility1(out, gameState);
				}
			}
		}
	}
	
	public void setStartHealth(int stratHealth) {
		this.startHealth = stratHealth;
	}
	
	public int getStartHealth() {
		return startHealth;
	}
	
	/**
	 * This command sets the position of the Unit to a specified
	 * tile.
	 * and set it's attributes: unitId and hasUnit
	 * @param tile
	 */
	@JsonIgnore
	public void setPositionByTile(Tile tile) {
		position = new Position(tile.getXpos(),tile.getYpos(),tile.getTilex(),tile.getTiley());
		tile.setUnitId(id);
		tile.setHasUnit(true);
	}

	/*
	 * overload the setPositionByTile 
	 * set its tile's parameter and 
	 * add the method of drawing the unit
	 */
	public void setPositionByTile(ActorRef out, Tile tile) {
		position = new Position(tile.getXpos(),tile.getYpos(),tile.getTilex(),tile.getTiley());
		tile.setUnitId(this.id);
		tile.setHasUnit(true);
		//draw the unit on the tile
		BasicCommands.drawUnit(out, this, tile);
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
	}
	
	public boolean isRangeAttack() {
		//@Zeyu
		return this.rangeAttack ;
	}
	
	
	/*
	 * Pureblade Enforcer's Ability when ai player use a spell card, it add +1
	 * attack and +1 health
	 */
	public static void purebladeEnforcerAbility(ActorRef out, GameState gameState) {
		for(Unit unit: gameState.playerUnitList) {
			if(unit.getId()==1||unit.getId()==13) {
				unit.setAttack(out, unit.getAttack()+1);
				unit.setHealth(out, unit.getHealth()+1);
				//notification
				BasicCommands.addPlayer1Notification(out, "Pureblade Enforcer's ability is triggered.", 2);
				try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
			}else {
				continue;
			}
		}
	}
	
	/*
	 * Silverguard Knight's ability when its avatar has been attacked, silverguard
	 * knight add +2 attack
	 */
	public static void silverguardKnightAbility1(ActorRef out, GameState gameState) {
		if(gameState.hasSilverguardKnight){
			for(Unit unit:gameState.playerUnitList) {
				if(unit.getId()==3||unit.getId() == 10) {
					unit.setAttack(out, unit.getAttack()+2);
					//notification
					BasicCommands.addPlayer1Notification(out, "Silverguard Knight's ability is triggered.", 2);
					try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
				}else {
					continue;
				}
				
			}
		}
		
	}
	
}
