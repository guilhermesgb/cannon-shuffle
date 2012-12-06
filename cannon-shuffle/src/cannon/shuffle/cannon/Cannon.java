package cannon.shuffle.cannon;

import cannon.shuffle.CannonShuffle;
import cannon.shuffle.Constants;
import cannon.shuffle.GameEntity;
import cannon.shuffle.TextureWrapper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Cannon extends GameEntity{

	Barrel barrel;
	
	public Cannon(World world, Vector2 pos){
		super(BodyType.KinematicBody, pos.add(new Vector2(0, (Constants.CANNON_CIRCLE_RADIUS+Constants.CANNON_RECT_HEIGHT)/2)), 0, world);
		hp = 1000;
		recoverable_hp = 1000;
		wrapper = new TextureWrapper(new TextureRegion(new Texture(Gdx.files.internal("cannon.png")), Constants.CANNON_CIRCLE_WIDTH, Constants.CANNON_CIRCLE_RADIUS + Constants.CANNON_RECT_HEIGHT), pos);
		createCannon(wrapper, pos, 0.5f, 0f, 0f);
		body.setUserData(this);
		wrapper.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		generalType = CannonShuffle.CANNON;
		specificType = CannonShuffle.CANNON;
		pos.sub(new Vector2(0, (Constants.CANNON_CIRCLE_RADIUS+Constants.CANNON_RECT_HEIGHT)/2));
		pos.add(new Vector2(0, Constants.CANNON_RECT_HEIGHT));
		barrel = new Barrel(world, pos);
	}
	
	private void createCannon(TextureWrapper texture, Vector2 pos, float density, float restitution, float angle) {

		CircleShape circleShape = new CircleShape();
		circleShape.setRadius(GameEntity.convertToBox(Constants.CANNON_CIRCLE_RADIUS));
		
		PolygonShape rectShape = new PolygonShape();
		rectShape.setAsBox(GameEntity.convertToBox(Constants.CANNON_RECT_WIDTH/2f), GameEntity.convertToBox(Constants.CANNON_RECT_HEIGHT/2f));
		
		FixtureDef fixtureDef=new FixtureDef();

		fixtureDef.density=density;
		fixtureDef.restitution=restitution;
		fixtureDef.friction=10f;
		
		fixtureDef.shape = circleShape;
		//circleShape.setPosition(pos);
		body.createFixture(fixtureDef);
		
		fixtureDef.shape=rectShape;
		body.createFixture(fixtureDef);

		rectShape.dispose();
		circleShape.dispose();
	}

	@Override
	public void draw(SpriteBatch sp){
		super.draw(sp);
		barrel.update(CannonShuffle.world, sp, CannonShuffle.cannon, CannonShuffle.bullets, CannonShuffle.camera);
	}

	@Override
	public void destroy(World world){
		super.destroy(world);
		barrel.destroy(world);
	}
	
	public void setLinearVelocity(Vector2 velocity) {
		body.setLinearVelocity(velocity);
		barrel.setLinearVelocity(velocity);
	}

}