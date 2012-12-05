package cannon.shuffle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Wall extends GameEntity {

	boolean is_floor = false;
	boolean is_invisible = false;
	
	public Wall(World world, Vector2 pos, boolean floor, boolean invisible, float width, float height){
		super(BodyDef.BodyType.StaticBody, pos, 0, world);
		
		is_floor = floor;
		is_invisible = invisible;

		PolygonShape bodyShape = new PolygonShape();

		float w=convertToBox(width);
		float h=convertToBox(height);
		bodyShape.setAsBox(w, h);

		FixtureDef fixtureDef=new FixtureDef();
		fixtureDef.density=0.5f;
		fixtureDef.restitution=0;
		fixtureDef.shape=bodyShape;
		fixtureDef.friction=10f;
		
		body.createFixture(fixtureDef);
		body.getFixtureList().get(0).setSensor(true);
		bodyShape.dispose();
		
		body.setUserData(this);
		
		generalType = CannonShuffle.WALL;
		specificType = CannonShuffle.WALL;
		
	}
	
	public Wall(World world, Vector2 pos, boolean floor, boolean invisible) {
		this(world, pos, floor, invisible, Constants.WALL_WIDTH, Constants.WALL_HEIGHT);

		wrapper = new TextureWrapper(new TextureRegion(new Texture(Gdx.files.internal("ground.png")), Constants.WALL_WIDTH, Constants.WALL_HEIGHT), pos);
		wrapper.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
	}

	@Override
	public void draw(SpriteBatch sp){
		if ( !is_invisible ){
			super.draw(sp);
		}
	}
	
}