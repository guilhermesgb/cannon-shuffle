package cannon.shuffle.enemy;

import cannon.shuffle.Constants;
import cannon.shuffle.Entities;
import cannon.shuffle.TextureWrapper;
import cannon.shuffle.Utils;
import cannon.shuffle.explosion.Explosion;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class AdBlimp extends Enemy {

	public AdBlimp(World world, Vector2 pos, float angle) {
		super(world, pos, angle);
		hp = 5;
		protection = 0.0f;

		wrapper = new TextureWrapper(new TextureRegion(new Texture(Gdx.files.internal("blimp.png")), Constants.AD_BLIMP_WIDTH, Constants.AD_BLIMP_HEIGHT), pos);
		
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
		body.setAngularVelocity(0.2f);
		bodyShape.dispose();
		body.setUserData(this);
		generalType = Entities.ENEMY;
		specificType = Entities.AD_BLIMP;
		
		body.getFixtureList().get(0).setSensor(true);
		wrapper.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);

	}
	
	@Override
	public void arrive(){
		body.setTransform(body.getPosition(), 0);
		Vector2 velocity = new Vector2(-Constants.AD_BLIMP_SPEED, .28f);
		body.setLinearVelocity(velocity);
	}

	@Override
	public void combat_action_1() {

	}

	@Override
	public void fire() {
	}

	@Override
	public void combat_action_2() {
		
	}

	@Override
	public Explosion getExplosion() {
		// TODO Auto-generated method stub
		return null;
	}
	
}