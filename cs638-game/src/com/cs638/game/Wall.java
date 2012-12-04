package com.cs638.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

public class Wall extends GameEntity {

	TextureRegion region;
	
	public Wall(TextureRegion region, Vector2 pos, World world, int boxIndex, int collisionGroup, boolean floor) {
		super(BodyDef.BodyType.StaticBody, pos, world);

		int shapeType = 0; //POLYGONAL
		createBaseBoxObject(texture, pos, shapeType, 1f, .3f, 0f);
		body.setUserData(this);
	}

	public static Wall newWall(World world, Vector2 position, boolean floor) {
		TextureRegion region = new TextureRegion(new Texture(Gdx.files.internal("ground.png")), Constants.WALL_WIDTH, Constants.WALL_HEIGHT);
		return new Wall(region, position, world, 1, 1, floor);
	}

}