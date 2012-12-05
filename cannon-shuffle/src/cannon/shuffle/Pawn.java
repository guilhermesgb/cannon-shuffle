package cannon.shuffle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.TimeUtils;

public class Pawn extends Enemy {

	//COMBAT
	float amplitude = (float) (Math.random()*2);
	float half_period = (float) (Math.random()*3);
	boolean avoidUp = false;
	boolean avoidDown = false;
	
	public Pawn(World world, Vector2 pos, float angle) {
		super(world, pos, angle);
		hp = 5;
		protection = 0.0f;

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
		
		generalType = CannonShuffle.ENEMY;
		specificType = CannonShuffle.PAWN;
		
		wrapper.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
	
	}

	@Override
	public void combat_action_1() {
		float x = direction*Constants.ENEMY_SPEED;
		float y = amplitude*Math.round(((Math.sin(half_period*body.getPosition().x)*Constants.ENEMY_SPEED)));
		if ( avoidUp && body.getPosition().y < convertToBox(Constants.WORLD_HEIGHT - 2*Constants.ENEMY_HEIGHT) ){
			avoidUp = false;
		}
		else if ( avoidDown && body.getPosition().y > convertToBox(CannonShuffle.cannon.getPosition().y + convertToBox(Constants.ENEMY_HEIGHT)*7 ) ){
			avoidDown = false;
		}
		else if ( body.getPosition().y > convertToBox(Constants.WORLD_HEIGHT) || avoidUp ){
			y = -1f;
			avoidUp = true;
		}
		else if ( body.getPosition().y < CannonShuffle.cannon.getPosition().y + convertToBox(Constants.ENEMY_HEIGHT)*10 || avoidDown ){
			y = 1f;
			avoidDown = true;
		}
		Vector2 velocity = new Vector2(x, y);
		body.setLinearVelocity(velocity);
		
		if(body.getPosition().x<=convertToBox(-Constants.ENEMY_WIDTH)){
			direction = 1;
		}
		if(body.getPosition().x>=convertToBox((Constants.ENEMY_WIDTH)+Constants.WORLD_WIDTH)){
			direction = -1;
		}
		if(TimeUtils.millis()-lastFiring> (Math.random()+2) * MAX_CHARGING){
			fire();
			lastFiring = (double)TimeUtils.millis();
		}		
	}

	@Override
	public void fire() {
		Vector2 bullet_position = new Vector2(convertToWorld(body.getPosition().x),convertToWorld(body.getPosition().y));
		bullet_position = bullet_position.add(new Vector2(0, -Constants.ENEMY_HEIGHT/2-Constants.BULLET_HEIGHT/2));
		Bullet bullet = new EnemyBullet(bullet_position, CannonShuffle.world, 0);
		float target_angle=(float)(Math.PI/2+Math.atan2((double)this.getPosition().y-CannonShuffle.cannon.getPosition().y,(double)this.getPosition().x-CannonShuffle.cannon.getPosition().x));
		bullet.body.setLinearVelocity(new Vector2((float)((-4f)*Math.sin(target_angle)), (float)((2f)*Math.cos(target_angle))));
		CannonShuffle.bullets.add(bullet);
	}

	@Override
	public void combat_action_2() {
		
	}
	
}