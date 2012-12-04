package com.cs638.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class Pawn extends GameEntityOld {

	int width;
	int height;
	public double hp = 30;
	
	boolean destroyed = false;
	boolean first_collision_happened = false;

	public TextureRegion region;
	
	//just used for spinning enemy
	double theta=0;
	int direction=1;
	double rotation_speed=Math.PI/50;
	public float protection = 0.0f;
	
	public Pawn(TextureRegion region, World world, Vector2 pos, int boxIndex, int collisionGroup,float angle){
		super(region, pos, world, boxIndex, collisionGroup, BodyDef.BodyType.KinematicBody);
		int shapeType = 0; //POLYGONAL
		createBaseBoxObject(texture, pos, shapeType, 0.5f, 0f, angle);
		body.setUserData(this);
		//just used for spinning enemy
		//body.setLinearVelocity(new Vector2(Constants.ENEMY_SPEED,0));
		//body.setAngularVelocity(8.0f);
		
		width = region.getRegionWidth();
		height = region.getRegionHeight();
		this.region = region;
		this.region.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
	}
	public static Pawn newPawn(World world, Vector2 pos, float angle){
		TextureRegion region= new TextureRegion(new Texture(Gdx.files.internal("pawn.png")), Constants.PAWN_WIDTH, Constants.PAWN_HEIGHT);
		return new Pawn(region, world, pos, 1, 1, angle);
	}
	public void remove() {
		body.destroyFixture(body.getFixtureList().get(0));
	}

	public void move(){

	}
	public void fire(World world, Cannon cannon, Array<EnemyBullet> enemy_bullets){

	}
	public void update(){
		super.update();
		move();
	}

}
