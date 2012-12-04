package com.cs638.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Cannon extends GameEntity{

	public TextureWrapper wrapper;
	public double hp = 1000;
	public float protection = 0.0f;
	
	public Cannon(World world, Vector2 pos){
		super(BodyType.StaticBody, pos, 0, world);
		wrapper = new TextureWrapper(new TextureRegion(new Texture(Gdx.files.internal("cannon.png")), Constants.CANNON_CIRCLE_WIDTH, Constants.CANNON_CIRCLE_RADIUS + Constants.CANNON_RECT_HEIGHT), pos);
		createCannon(wrapper, pos, 0.5f, 0f, 0f);
		body.setUserData(this);
		wrapper.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		generalType = CannonShuffle.CANNON;
		specificType = CannonShuffle.CANNON;
	}
	
	private void createCannon(TextureWrapper texture, Vector2 pos, float density, float restitution, float angle) {

		CircleShape circleShape = new CircleShape();
		circleShape.setRadius(BaseBoxObject.convertToBox(Constants.CANNON_CIRCLE_RADIUS));
		
		PolygonShape rectShape = new PolygonShape();
		rectShape.setAsBox(BaseBoxObject.convertToBox(Constants.CANNON_RECT_WIDTH/2f), BaseBoxObject.convertToBox(Constants.CANNON_RECT_HEIGHT/2f));
		
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

}