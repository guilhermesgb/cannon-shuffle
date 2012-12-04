package com.cs638.game;

import java.util.Iterator;

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
	private World world;
	
	public void updatePhysics(float dt){

		accumulator+=dt;
	    while(accumulator>BOX_STEP){
	      world.step(BOX_STEP,BOX_VELOCITY_ITERATIONS,BOX_POSITION_ITERATIONS);
	      accumulator-=BOX_STEP;
	   }
	}
	
	private OrthographicCamera camera;
	private SpriteBatch batch;

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
	
	//Super Type
	public static final String BULLET = "Bullet";
	
	public static Array<CannonBullet> cannon_bullets = new Array<CannonBullet>();
	//General Type
	public static final String CANNON_BULLET = "CannonBullet";
	//Specific Types

	public static Array<EnemyBullet> enemy_bullets = new Array<EnemyBullet>();
	//General Type
	public static final String ENEMY_BULLET = "EnemyBullet";
	//Specific Types
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
			ground.add(Wall.newWall(world, new Vector2(i*(Constants.WALL_WIDTH), Constants.WALL_HEIGHT/2), true));
		}
		leftWall = new Array<Wall>();
		for ( int i=0; i<=(Constants.WORLD_HEIGHT/Constants.WALL_HEIGHT) + 30; i++){
			leftWall.add(Wall.newWall(world, new Vector2((-1)*Constants.WALL_WIDTH, Constants.WALL_HEIGHT/2+Constants.WALL_HEIGHT * i), false));
		}
		rightWall = new Array<Wall>();
		for ( int i=0; i<=(Constants.WORLD_HEIGHT/Constants.WALL_HEIGHT) + 30; i++){
			rightWall.add(Wall.newWall(world, new Vector2(Constants.WORLD_WIDTH + Constants.WALL_WIDTH, Constants.WALL_HEIGHT/2+Constants.WALL_HEIGHT*i), false));
		}

		cannon = Cannon.newCannon(world, new Vector2((Constants.WORLD_WIDTH/2) + (Constants.CANNON_CIRCLE_WIDTH/2), (Constants.WALL_HEIGHT)+(Constants.CANNON_CIRCLE_RADIUS+Constants.CANNON_RECT_HEIGHT)/2));
		barrel = Barrel.newBarrel(world, new Vector2((Constants.WORLD_WIDTH/2) + (Constants.CANNON_CIRCLE_WIDTH/2), Constants.CANNON_RECT_HEIGHT+(Constants.WALL_HEIGHT)));
		cannon_bullets = new Array<CannonBullet>();

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
		
		Iterator<CannonBullet> itr = cannon_bullets.iterator();
		while ( itr.hasNext() ){
			CannonBullet b = itr.next();
			if ( b.destroyed ){
				itr.remove();
				b.remove();
			}
			b.draw(batch);
			b.update();
		}
		
		Iterator<Enemy> itr_pawn = enemies.iterator();
		while ( itr_pawn.hasNext() ){
			Enemy e = itr_pawn.next();
			if( e.destroyed ){
				itr_pawn.remove();
				e.remove();
			}
			e.draw(batch);
			e.update();
		}
		
		Iterator<Explosion> itr_explosion = explosions.iterator();
		while ( itr_explosion.hasNext() ){
			Explosion ex = itr_explosion.next();
			if (!(ex.draw(batch))){
				itr_explosion.remove();
			}
		}

		barrel.update(world, batch, cannon, cannon_bullets, camera);
		batch.end();

		if ( Gdx.input.isKeyPressed(Keys.ESCAPE)){
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