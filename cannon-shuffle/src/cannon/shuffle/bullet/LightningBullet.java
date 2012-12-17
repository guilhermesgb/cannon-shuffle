package cannon.shuffle.bullet;

import cannon.shuffle.Constants;
import cannon.shuffle.Entities;
import cannon.shuffle.Utils;
import cannon.shuffle.explosion.Explosion;
import cannon.shuffle.explosion.LightningExplosion;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class LightningBullet extends Bullet {

	public LightningBullet(World world, Vector2 pos, float angle, boolean shot_by_enemy){
		super(BodyType.KinematicBody, pos, angle, world, shot_by_enemy);

		wrapper = new AnimationWrapper("lightning_bullet.png", pos, Constants.EXPLOSION_SPRITE_WIDTH, Constants.EXPLOSION_SPRITE_WIDTH_TOTAL, Constants.EXPLOSION_SPRITE_HEIGHT, Constants.EXPLOSION_SPRITE_HEIGHT_TOTAL);
		
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
		body.setTransform(body.getPosition(), angle);
		bodyShape.dispose();
		
		body.setUserData(this);
		body.getFixtureList().get(0).setSensor(true);
		
		specificType = Entities.LIGHTNING_BULLET;
		damage = .2f;
	}

	public void update(){
		super.update();
		wrapper.setRotation(body.getAngle()*MathUtils.radiansToDegrees);
	}

	@Override
	public Explosion getExplosion(boolean lerpWithTarget, Vector2 targetPosition, float factor) {
		if ( lerpWithTarget ){
			return new LightningExplosion(this.getPosition().lerp(targetPosition, factor));
		}
		else{
			return new LightningExplosion(this.getPosition());
		}
	}

}
