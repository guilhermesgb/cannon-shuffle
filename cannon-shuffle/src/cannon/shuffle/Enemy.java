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

	public double hp = 5;
	
	boolean destroyed = false;
	boolean first_collision_happened = false;

	//just used for spinning enemy
	double theta=0;
	int direction=1;
	double rotation_speed=Math.PI/50;
	public float protection = 0.0f;
	
	final static int MAX_CHARGING = 700;
	double lastFiring = 0;
	
	//movement-related
	float amplitude = (float) (Math.random()*2);
	float half_period = (float) (Math.random()*3);
	boolean avoidUp = false;
	boolean avoidDown = false;
	
	public Enemy(World world, Vector2 pos, float angle){
		super(BodyDef.BodyType.KinematicBody, pos, angle, world);

		wrapper = new TextureWrapper(new TextureRegion(new Texture(Gdx.files.internal("pawn.png")), Constants.ENEMY_WIDTH, Constants.ENEMY_HEIGHT), pos);
		
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
		Vector2 velocity = new Vector2(direction*Constants.ENEMY_SPEED,(int)Math.sin(body.getPosition().x)*Constants.ENEMY_SPEED);
		body.setLinearVelocity(velocity);
//		just used for spinning enemy
		body.setAngularVelocity(0.5f);
		
		wrapper.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
	}

	public void remove() {
		body.destroyFixture(body.getFixtureList().get(0));
	}
	
	public void move(World world, Cannon cannon, Array<Bullet> enemy_bullets){
		//detect the x edges
		float x = direction*Constants.ENEMY_SPEED;
		float y = amplitude*Math.round(((Math.sin(half_period*body.getPosition().x)*Constants.ENEMY_SPEED)));
		if ( avoidUp && body.getPosition().y < convertToBox(Constants.WORLD_HEIGHT - 2*Constants.ENEMY_HEIGHT) ){
			avoidUp = false;
		}
		else if ( avoidDown && body.getPosition().y > convertToBox(cannon.getPosition().y + convertToBox(Constants.ENEMY_HEIGHT)*7 ) ){
			avoidDown = false;
		}
		else if ( body.getPosition().y > convertToBox(Constants.WORLD_HEIGHT) || avoidUp ){
			y = -1f;
			avoidUp = true;
		}
		else if ( body.getPosition().y < cannon.getPosition().y + convertToBox(Constants.ENEMY_HEIGHT)*5 || avoidDown ){
			y = 1f;
			avoidDown = true;
		}
		Vector2 velocity = new Vector2(x, y);
		body.setLinearVelocity(velocity);
		
		if(body.getPosition().x<=convertToBox(-Constants.ENEMY_WIDTH)){
			direction = 1;
		}
		if(body.getPosition().x>=convertToBox((Constants.ENEMY_WIDTH)+Constants.WORLD_WIDTH)){//-Constants.ENEMY_WIDTH)){
			direction = -1;
		}
		if(TimeUtils.millis()-lastFiring> (Math.random()+2) * MAX_CHARGING){
			fire(world,cannon,enemy_bullets);
			lastFiring = (double)TimeUtils.millis();
		}
		
	}
	public void fire(World world, Cannon cannon, Array<Bullet> bullets){
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