package cannon.shuffle;

import java.util.Iterator;

import cannon.shuffle.bullet.Bullet;
import cannon.shuffle.cannon.Barrel;
import cannon.shuffle.cannon.Cannon;
import cannon.shuffle.enemy.Enemy;
import cannon.shuffle.enemy.Pawn;
import cannon.shuffle.explosion.Explosion;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class CannonShuffle implements ApplicationListener{

	private static final float BOX_STEP=1/30f;
	private static final int  BOX_VELOCITY_ITERATIONS=8;
	private static final int BOX_POSITION_ITERATIONS=3;
	private float accumulator;

	private Vector2 gravity;
	private boolean doSleep;
	public static World world;
	
	public void updatePhysics(float dt){

		accumulator+=dt;
	    while(accumulator>BOX_STEP){
	      world.step(BOX_STEP,BOX_VELOCITY_ITERATIONS,BOX_POSITION_ITERATIONS);
	      accumulator-=BOX_STEP;
	   }
	}
	
	private OrthographicCamera camera;
	private SpriteBatch batch;

	private static HealthBar healthBar;
	
	private Array<Wall> ground;
	private Array<Wall> leftWall;
	private Array<Wall> rightWall;

	public static final String WALL = "Wall";
	
	public static Cannon cannon;
	public static final String CANNON = "Cannon";

	public static Barrel barrel;
	//General Type
	public static final String BARREL = "Barrel";
	//Specific Type
	public static final String BARREL_PART = "BarrelPart";
	public static final String SHIELD_PART = "ShieldPart";
	
	public static Array<Bullet> bullets = new Array<Bullet>();
	//General Type
	public static final String BULLET = "Bullet";
	//Specific Types
	public static final String CANNON_BULLET = "CannonBullet";
	public static final String ICE_BULLET = "IceBullet";
	
	public static Array<Explosion> explosions = new Array<Explosion>();
	//General Type
	public static final String EXPLOSION = "Explosion";
	//Specific Types
	public static final String FIRE_EXPLOSION = "FireExplosion";
	public static final String ICE_EXPLOSION = "IceExplosion";
	
	public static Array<Enemy> enemies = new Array<Enemy>();
	//General Type
	public static final String ENEMY = "Enemy";
	//Specific Types
	public static final String PAWN = "Pawn";

	private int num_destroyed_enemies = 0;
	
	@Override
	public void create() {

		gravity = new Vector2(0.0f, -10.0f);
		doSleep = true;
		world = new World(gravity, doSleep);
		world.setContactListener(new CollisionSystem());
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
		camera.translate(new Vector3(0.0f, 0.0f, 0.0f));

		batch = new SpriteBatch();
		
		ground = new Array<Wall>();
		for ( int i=0; i<=Constants.WORLD_WIDTH/Constants.WALL_WIDTH; i++){
			ground.add(new Wall(world, new Vector2(i*(Constants.WALL_WIDTH), Constants.WALL_HEIGHT/2), true));
		}
		leftWall = new Array<Wall>();
		for ( int i=0; i<=(Constants.WORLD_HEIGHT/Constants.WALL_HEIGHT) + 30; i++){
			leftWall.add(new Wall(world, new Vector2((-1)*Constants.WALL_WIDTH, Constants.WALL_HEIGHT/2+Constants.WALL_HEIGHT * i), false));
		}
		rightWall = new Array<Wall>();
		for ( int i=0; i<=(Constants.WORLD_HEIGHT/Constants.WALL_HEIGHT) + 30; i++){
			rightWall.add(new Wall(world, new Vector2(Constants.WORLD_WIDTH + Constants.WALL_WIDTH, Constants.WALL_HEIGHT/2+Constants.WALL_HEIGHT*i), false));
		}

		cannon = new Cannon(world, new Vector2((Constants.WORLD_WIDTH/2) + (Constants.CANNON_CIRCLE_WIDTH/2), (Constants.WALL_HEIGHT)+(Constants.CANNON_CIRCLE_RADIUS+Constants.CANNON_RECT_HEIGHT)/2));
		healthBar = new HealthBar(cannon, new Vector2(Constants.HEALTH_BAR_X, Constants.HEALTH_BAR_Y));
		
		barrel = new Barrel(world, new Vector2((Constants.WORLD_WIDTH/2) + (Constants.CANNON_CIRCLE_WIDTH/2), Constants.CANNON_RECT_HEIGHT+(Constants.WALL_HEIGHT)));
		bullets = new Array<Bullet>();

		enemies = new Array<Enemy>();
		explosions = new Array<Explosion>();
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void render() {

		updatePhysics(Gdx.graphics.getDeltaTime());
		Gdx.gl.glClearColor(0.85f, 0.99f, 0.99f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		camera.update();

		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		cannon.draw(batch);
		cannon.update();

		barrel.update(world, batch, cannon, bullets, camera);
		
		for ( Wall p : ground ){
			p.draw(batch);
			p.update();
		}
		for ( Wall p : leftWall ){
			p.draw(batch);
			p.update();
		}
		for ( Wall p : rightWall ){
			p.draw(batch);
			p.update();
		}
		
		Iterator<Bullet> itr = bullets.iterator();
		while ( itr.hasNext() ){
			Bullet b = itr.next();
			if ( b.destroyed ){
				itr.remove();
				b.remove();
			}
			b.draw(batch);
			b.update();
		}
		
		Iterator<Enemy> itr_enemies = enemies.iterator();
		while ( itr_enemies.hasNext() ){
			Enemy e = itr_enemies.next();
			if( e.destroyed ){
				num_destroyed_enemies++;
				itr_enemies.remove();
				e.remove();
			}
			e.draw(batch);
			e.update();
		}
		
		float p = 0.99f;
		if ( Math.random() > p && enemies.size < 5){
			enemies.add(new Pawn(world, new Vector2(Constants.WORLD_WIDTH*(0.1f+4.0f/5.0f*(float)Math.random()), (float) (Constants.WORLD_HEIGHT )), 0)); //*(Math.random()+2)
		}
		
		Iterator<Explosion> itr_explosion = explosions.iterator();
		while ( itr_explosion.hasNext() ){
			Explosion ex = itr_explosion.next();
			if (!(ex.draw(batch))){
				itr_explosion.remove();
			}
		}

		healthBar.draw(batch);
		
		batch.end();

		if ( Gdx.input.isKeyPressed(Keys.ESCAPE)){
			System.exit(0);
		}
		
		System.out.println("Cannon HP: "+cannon.hp+" - # destroyed enemies: "+num_destroyed_enemies );
		
		if ( cannon.hp < 0 ){
			System.exit(0);
		}

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

}