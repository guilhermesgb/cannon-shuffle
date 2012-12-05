package cannon.shuffle;

public abstract class Constants {

	public static final int WALL_WIDTH = 64;
	public static final int WALL_HEIGHT = 64;
	
	public static final int WORLD_WIDTH = 1280;
	public static final int WORLD_HEIGHT = 720;

	public static final int CANNON_CIRCLE_WIDTH = 128;
	public static final int CANNON_CIRCLE_RADIUS = 64;

	public static final int CANNON_RECT_WIDTH = 128;
	public static final int CANNON_RECT_HEIGHT = 64;
	
	public static final int EXPLOSION_SPRITE_HEIGHT=64;
	public static final int EXPLOSION_SPRITE_WIDTH=64;
	
	public static final int EXPLOSION_SPRITE_HEIGHT_TOTAL=128;
	public static final int EXPLOSION_SPRITE_WIDTH_TOTAL=256;
	
	public static final int BULLET_WIDTH = 16;
	public static final int BULLET_HEIGHT = 32;
	public static final int BULLET_SPEED = 4;
	public static final int MAX_BULLET_SPEED = 10;
	
	public static final int ENEMY_WIDTH = 32;
	public static final int ENEMY_HEIGHT = 32;
	public static final int ENEMY_SPEED = 2;
	
	public static final int BARREL_WIDTH = 64;
	public static final int BARREL_HEIGHT = 256;
	public static final float BARREL_RECT_WIDTH = 24;
	public static final float BARREL_CIRCLE_RADIUS = 66;
	public static final float BARREL_RECT_HEIGHT = (128-66)/2;

	public static final int ICE_BULLET_HEIGHT = 32;
	public static final int ICE_BULLET_WIDTH = 8;
	public static final int PAWN_WIDTH = 32;
	public static final int PAWN_HEIGHT = 32;
	
	public static final int HEALTH_BAR_WIDTH = 160;
	public static final int HEALTH_BAR_HEIGHT = 32;
	public static final float HEALTH_BAR_X = Constants.WORLD_WIDTH/24;
	public static final float HEALTH_BAR_Y = WORLD_HEIGHT - HEALTH_BAR_HEIGHT*2;
	
}