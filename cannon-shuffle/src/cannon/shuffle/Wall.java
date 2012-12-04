package cannon.shuffle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Wall extends GameEntity {

	TextureRegion region;
	boolean is_floor = false;
	
	public Wall(World world, Vector2 pos, boolean floor) {
		super(BodyDef.BodyType.StaticBody, pos, 0, world);

		is_floor = floor;
		
		wrapper = new TextureWrapper(new TextureRegion(new Texture(Gdx.files.internal("ground.png")), Constants.WALL_WIDTH, Constants.WALL_HEIGHT), pos);
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
		bodyShape.dispose();
		
		body.setUserData(this);
	}

}