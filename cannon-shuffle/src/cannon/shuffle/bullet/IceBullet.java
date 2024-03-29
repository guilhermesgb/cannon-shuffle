package cannon.shuffle.bullet;

import cannon.shuffle.Constants;
import cannon.shuffle.Entities;
import cannon.shuffle.TextureWrapper;
import cannon.shuffle.Utils;
import cannon.shuffle.explosion.Explosion;
import cannon.shuffle.explosion.IceExplosion;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class IceBullet extends Bullet{

	public IceBullet(Vector2 pos, World world, float angle, boolean shot_by_enemy){
		super(BodyType.DynamicBody, pos, angle, world, shot_by_enemy);

		wrapper = new TextureWrapper(new TextureRegion(new Texture(Gdx.files.internal("ice_bullet.png")), Constants.ICE_BULLET_WIDTH, Constants.ICE_BULLET_HEIGHT), pos);
		wrapper.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		PolygonShape bodyShape = new PolygonShape();

		float w=Utils.convertToBox(wrapper.getRegion().getRegionWidth()/2f);
		float h=Utils.convertToBox(wrapper.getRegion().getRegionHeight()/2f);
		bodyShape.setAsBox(w, h);

		FixtureDef fixtureDef=new FixtureDef();
		fixtureDef.density=0.5f;
		fixtureDef.restitution=0;
		fixtureDef.shape=bodyShape;
		fixtureDef.friction=10f;
		
		body.createFixture(fixtureDef);
		body.getFixtureList().get(0).setSensor(true);
		body.setTransform(body.getPosition(), angle);
		body.setGravityScale(0.01f);
		bodyShape.dispose();
		
		body.setUserData(this);
		
		specificType = Entities.ICE_BULLET;
		damage = 50;
		
		crushableType = 4;
	}

	public void update(){
		super.update();

		wrapper.setRotation(body.getAngle()*MathUtils.radiansToDegrees);
		double angle=Math.atan2(body.getLinearVelocity().y,body.getLinearVelocity().x)-Math.PI/2;
		body.setTransform(body.getPosition(), (float)angle);
	}

	@Override
	public Explosion getExplosion(boolean lerpWithTarget, Vector2 targetPosition, float factor) {
		if ( lerpWithTarget ){
			return new IceExplosion(this.getPosition().lerp(targetPosition, factor));
		}
		else{
			return new IceExplosion(this.getPosition());
		}
	}
	
}