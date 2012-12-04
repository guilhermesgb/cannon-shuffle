package com.cs638.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class EnemyCloud extends Enemy {

	final static int MAX_CHARGING = 700;
	double lastFiring = 0;
	public double hp = 30;
	
	public EnemyCloud(TextureRegion region, World world, Vector2 pos,
			int boxIndex, int collisionGroup, float angle) {
		super(region, world, pos, boxIndex, collisionGroup, angle);
		body.setLinearVelocity(new Vector2(direction*Constants.ENEMY_SPEED,0));
		// TODO Auto-generated constructor stub
	}
	public static EnemyCloud newEnemy(World world, Vector2 pos, float angle){
		TextureRegion region= new TextureRegion(new Texture(Gdx.files.internal("enemy.png")), Constants.ENEMY_WIDTH, Constants.ENEMY_HEIGHT);
		return new EnemyCloud(region, world, pos, 1, 1, angle);
	}
	public void remove() {
		body.destroyFixture(body.getFixtureList().get(0));
	}

	public void move(World world, Cannon cannon, Array<EnemyBullet> enemy_bullets){
		//detect the x edges
		if(body.getPosition().x<=BaseBoxObject.convertToBox(-Constants.ENEMY_WIDTH)){
			direction*=-1;
			body.setLinearVelocity(new Vector2(direction*Constants.ENEMY_SPEED,0));
			body.setTransform((float)(BaseBoxObject.convertToBox(-Constants.ENEMY_WIDTH+1)), body.getPosition().y, body.getAngle());
		}
		if(body.getPosition().x>=BaseBoxObject.convertToBox(Constants.WORLD_WIDTH+Constants.ENEMY_WIDTH)){
			direction*=-1;
			body.setLinearVelocity(new Vector2(direction*Constants.ENEMY_SPEED,0));
			body.setTransform((float)(BaseBoxObject.convertToBox(Constants.WORLD_WIDTH+Constants.ENEMY_WIDTH-1)), body.getPosition().y, body.getAngle());
		}
		if(TimeUtils.millis()-lastFiring>0.07f*MAX_CHARGING){
			fire(world,cannon,enemy_bullets);
			lastFiring = ((double)TimeUtils.millis());
		}
		
	}
	@Override
	public void fire(World world, Cannon cannon, Array<EnemyBullet> enemy_bullets){
			Vector2 bullet_position = new Vector2(BaseBoxObject.convertToWorld(body.getPosition().x),BaseBoxObject.convertToWorld(body.getPosition().y));
			//bullet_position = bullet_position.add(new Vector2(0, -Constants.ENEMY_HEIGHT/2-Constants.BULLET_HEIGHT/2));
			Vector2 bullet_velocity = new Vector2(0.0f,-40.0f);
			EnemyBullet bullet = EnemyBullet.newBullet(world, bullet_position, 0,bullet_velocity);
			for(Fixture f:bullet.body.getFixtureList()){
				f.setSensor(true);
			}
			enemy_bullets.add(bullet);
			
		
	}
	public void update(World world, Cannon cannon, Array<EnemyBullet> enemy_bullets){
		super.update();
		move(world, cannon, enemy_bullets);
	}

}
