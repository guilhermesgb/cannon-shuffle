package com.cs638.game;

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

public class CannonBullet extends Bullet {

	boolean first_collision_happened = false;
	public int damage = 10;
	
	public CannonBullet(Vector2 pos, World world, float angle){
		super(BodyType.DynamicBody, pos, angle, world);

		wrapper = new TextureWrapper(new TextureRegion(new Texture(Gdx.files.internal("bullet.png")), Constants.BULLET_WIDTH, Constants.BULLET_HEIGHT), pos);
		wrapper.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
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
		
	}

	public void remove() {
		body.destroyFixture(body.getFixtureList().get(0));
	}

	public void update(){
		super.update();
		wrapper.setRotation(body.getAngle()*MathUtils.radiansToDegrees);
		
		if ( !first_collision_happened ){
			double angle=Math.atan2(body.getLinearVelocity().y,body.getLinearVelocity().x)-Math.PI/2;
				body.setTransform(body.getPosition(), (float)angle);
		}
	}

	@Override
	public Explosion getExplosion(boolean lerpWithTarget, Vector2 targetPosition, float factor) {
		if ( lerpWithTarget ){
			return new FireExplosion(this.getPosition().lerp(targetPosition, factor));
		}
		else{
			return new FireExplosion(this.getPosition());
		}
	}

}
