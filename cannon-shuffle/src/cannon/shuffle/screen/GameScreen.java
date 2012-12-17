package cannon.shuffle.screen;

import java.util.Iterator;

import cannon.shuffle.CollisionSystem;
import cannon.shuffle.Constants;
import cannon.shuffle.HealthBar;
import cannon.shuffle.Wall;
import cannon.shuffle.bullet.Bullet;
import cannon.shuffle.cannon.Cannon;
import cannon.shuffle.enemy.AdBlimp;
import cannon.shuffle.enemy.Enemy;
import cannon.shuffle.enemy.Pawn;
import cannon.shuffle.explosion.Explosion;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class GameScreen implements Screen{

	Game game;
	
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
	
	public static OrthographicCamera camera;
	private SpriteBatch batch;

	private static HealthBar healthBar;
	
	private Array<Wall> ground;
	private Array<Wall> leftWall;
	private Array<Wall> rightWall;

	public static Cannon cannon;
	public static Array<Bullet> bullets = new Array<Bullet>();
	public static Array<Explosion> explosions = new Array<Explosion>();
	public static Array<Enemy> enemies = new Array<Enemy>();

	public static Wall enemyBound;
	
	private int num_destroyed_enemies = 0;
	boolean cannon_destroyed = false;
	float p = 0.999f;
	float m = 1;
	
	public GameScreen(Game game){
		this.game = game;
		
		gravity = new Vector2(0.0f, -10.0f);
		doSleep = true;
		world = new World(gravity, doSleep);
		world.setContactListener(new CollisionSystem());
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
		camera.translate(new Vector3(0.0f, 0.0f, 0.0f));

		batch = new SpriteBatch();
		
		ground = new Array<Wall>();
		for ( int i=-2; i<=Constants.WORLD_WIDTH/Constants.WALL_WIDTH + 1; i++){
			ground.add(new Wall(world, new Vector2(i*(Constants.WALL_WIDTH), 0), false));
		}
		leftWall = new Array<Wall>();
		for ( int i=0; i<=(Constants.WORLD_HEIGHT/Constants.WALL_HEIGHT) + 30; i++){
			leftWall.add(new Wall(world, new Vector2((-3)*Constants.WALL_WIDTH, Constants.WALL_HEIGHT/2+Constants.WALL_HEIGHT * i), false));
		}
		rightWall = new Array<Wall>();
		for ( int i=0; i<=(Constants.WORLD_HEIGHT/Constants.WALL_HEIGHT) + 30; i++){
			rightWall.add(new Wall(world, new Vector2(Constants.WORLD_WIDTH + 2*Constants.WALL_WIDTH, Constants.WALL_HEIGHT/2+Constants.WALL_HEIGHT*i), false));
		}
		
		cannon = new Cannon(world, new Vector2((Constants.WORLD_WIDTH/2) + (Constants.CANNON_CIRCLE_WIDTH/2), (Constants.WALL_HEIGHT/2)));
		healthBar = new HealthBar(cannon, new Vector2(Constants.HEALTH_BAR_X, Constants.HEALTH_BAR_Y));
		
		enemyBound = new Wall(world, new Vector2((Constants.WORLD_WIDTH/2) + (Constants.CANNON_CIRCLE_WIDTH/2), cannon.getPosition().y+6*Constants.ENEMY_HEIGHT), true, 2*(Constants.WORLD_WIDTH+2*Constants.WALL_WIDTH), Constants.WALL_HEIGHT);
		bullets = new Array<Bullet>();

		enemies = new Array<Enemy>();
		explosions = new Array<Explosion>();
	}
	
	boolean ad_given = false;
	
	@Override
	public void render(float delta) {

		updatePhysics(Gdx.graphics.getDeltaTime());
		Gdx.gl.glClearColor(0.85f, 0.99f, 0.99f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		camera.update();

		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		if ( cannon.hp > 0 ){
			cannon.draw(batch);
		}
		else if (!cannon_destroyed){
			cannon.destroy(world);
			cannon_destroyed = true;
			System.out.println("Destroyed: "+num_destroyed_enemies+" pawns.");
		}
		cannon.update();
		
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

		enemyBound.draw(batch);
		
		Iterator<Enemy> itr_enemies = enemies.iterator();
		while ( itr_enemies.hasNext() ){
			Enemy e = itr_enemies.next();
			if( e.destroyed ){
				num_destroyed_enemies++;
				itr_enemies.remove();
				if ( e.getExplosion() != null ){
					explosions.add(e.getExplosion());
				}
				e.destroy(null);
			}
			e.draw(batch);
			e.update();
		}
		
		if (!ad_given){
			enemies.add(new AdBlimp(world, 
					new Vector2(Constants.WORLD_WIDTH+Constants.AD_BLIMP_WIDTH, (float) (Constants.WORLD_HEIGHT-Constants.AD_BLIMP_HEIGHT/2 )), 0));
			ad_given = true;
		}
		
		if ( Math.random() > p && enemies.size < m){
			enemies.add(new Pawn(world, 
					new Vector2(Constants.WORLD_WIDTH*(0.1f+4.0f/5.0f*(float)Math.random()), 
							(float) (Constants.WORLD_HEIGHT )), 0)); //*(Math.random()+2)
			m += 0.1;
		}
		p -= 0.00001;
		
		Iterator<Explosion> itr_explosion = explosions.iterator();
		while ( itr_explosion.hasNext() ){
			Explosion ex = itr_explosion.next();
			ex.draw(batch);
//			if (!(ex.draw(batch))){
//				itr_explosion.remove();
//			}
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
		
		healthBar.draw(batch);
		
		batch.end();

		if ( Gdx.input.isKeyPressed(Keys.ESCAPE)){
			System.exit(0);
		}
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
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
