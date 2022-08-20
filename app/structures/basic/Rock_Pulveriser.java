package structures.basic;

public class Rock_Pulveriser extends Unit {
	
	boolean canProvoke = true;
	
	public Rock_Pulveriser() {}
	
	public Rock_Pulveriser(int id, UnitAnimationSet animations, ImageCorrection correction) {
		super();
		this.id = id;
		this.animation = UnitAnimationType.idle;
		
		position = new Position(0,0,0,0);
		this.correction = correction;
		this.animations = animations;
		System.out.println("constructor1");
	}
	
	public Rock_Pulveriser(int id, UnitAnimationSet animations, ImageCorrection correction, Tile currentTile) {
		super();
		this.id = id;
		this.animation = UnitAnimationType.idle;
		
		position = new Position(currentTile.getXpos(),currentTile.getYpos(),currentTile.getTilex(),currentTile.getTiley());
		this.correction = correction;
		this.animations = animations;
		System.out.println("constructor2");
	}
	
	public Rock_Pulveriser(int id, UnitAnimationType animation, Position position, UnitAnimationSet animations,
			ImageCorrection correction) {
		super();
		this.id = id;
		this.animation = animation;
		this.position = position;
		this.animations = animations;
		this.correction = correction;
		System.out.println("constructor3");
	}
}
