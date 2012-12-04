package cannon.shuffle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class Enemy extends GameEntity {

	public double hp = 30;
	
	boolean destroyed = false;
	boolean first_collision_happened = false;

	//just used for spinning enemy
	double theta=0;
	int direction=1;
	double rotation_speed=Math.PI/50;
	public float protection = 0.0f;
	
	final static int MAX_CHARGING = 700;
	double lastFiring = 0;
	
	public Enemy(World world, Vector2 pos, float angle){
		super(BodyDef.BodyType.KinematicBody, pos, angle, world);

		wrapper = new TextureWrapper(new TextureRegion(new Texture(Gdx.files.internal("enemy.png")), Constants.ENEMY_WIDTH, Constants.ENEMY_HEIGHT), pos);
		
		PolygonShape bodyShape = new PolygonShape();

		float w=convertToBox(wrapper.getRegion().getRegionWidth()/2f);
		float h=convertToBox(wrapper.getRegion().getRegionHeight()/2f);
		bodyShape.setAsBox(w, h);

		FixtureDef fixtureDef=new FixtureDef();
		fixtureDef.density=0.5f;
		fixtureDef.restitution=0;
		fixtureDef.shape=bodyShape;
		fixtureDef.friction=10f;
		
		body.createFixture(fixtureDef);
		body.setTransform(body.getPosition(), angle);
		bodyShape.dispose();
		
		body.setUserData(this);
		
		specificType = CannonShuffle.ENEMY;
		generalType = CannonShuffle.ENEMY;
//		just used for spinning enemy
//		body.setLinearVelocity(new Vector2(Constants.ENEMY_SPEED,0));
		body.setAngularVelocity(8.0f);
		
		wrapper.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
	}

	public void remove() {
		body.destroyFixture(body.getFixtureList().get(0));
	}

	public void move(World world, Cannon cannon, Array<Bullet> enemy_bullets){
		//detect the x edges
		if(body.getPosition().x<=convertToBox(Constants.ENEMY_WIDTH)){
			direction*=-1;
			body.setLinearVelocity(new Vector2(direction*Constants.ENEMY_SPEED,0));
			body.setTransform((float)(convertToBox(Constants.ENEMY_WIDTH+1)), body.getPosition().y, body.getAngle());
			System.out.println(body.getPosition().x);
		}
		if(body.getPosition().x>=convertToBox(Constants.WORLD_WIDTH-Constants.ENEMY_WIDTH)){
			direction*=-1;
			body.setLinearVelocity(new Vector2(direction*Constants.ENEMY_SPEED,0));
			body.setTransform((float)(convertToBox(Constants.WORLD_WIDTH-Constants.ENEMY_WIDTH-1)), body.getPosition().y, body.getAngle());
			System.out.println(body.getPosition().x);
		}
		if(TimeUtils.millis()-lastFiring>MAX_CHARGING){
			fire(world,cannon,enemy_bullets);
			lastFiring = (double)TimeUtils.millis();
		}
		
	}
	public void fire(World world, Cannon cannon, Array<Bullet> bullets){
			System.out.println(theta);
			System.out.println("firing weapon!\n");
			Vector2 bullet_position = new Vector2(convertToWorld(body.getPosition().x),convertToWorld(body.getPosition().y));
			bullet_position = bullet_position.add(new Vector2(0, -Constants.ENEMY_HEIGHT/2-Constants.BULLET_HEIGHT/2));
			Bullet bullet = new EnemyBullet(bullet_position, world, 0);
			bullets.add(bullet);
	}
	
	public void update(World world, Cannon cannon, Array<Bullet> bullets){
		super.update();
		move(world, cannon, bullets);
	}

}
