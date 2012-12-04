package cannon.shuffle;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

public abstract class GameEntity {

	protected Body body;
	protected TextureWrapper wrapper;
	protected Vector2 worldPosition;
	
	public String generalType;
	public String specificType;

	private static final float WORLD_TO_BOX=0.01f;
	private static final float BOX_TO_WORLD=100f;
	

	public GameEntity(BodyDef.BodyType bodyType, Vector2 pos, float angle, World world) {
		if ( bodyType != null ){
			BodyDef bodyDef = new BodyDef();
			bodyDef.type = bodyType;
			bodyDef.position.set(convertToBox(pos.x), convertToBox(pos.y));
			bodyDef.angle=angle;
			body = world.createBody(bodyDef);
			worldPosition = new Vector2();
		}
	}

	public static float convertToBox(float x){
		return x*WORLD_TO_BOX;
	}
	
	public static float convertToWorld(float x){
		return x*BOX_TO_WORLD;
	}
	
	public void update(){
		worldPosition.set(convertToWorld(body.getPosition().x),convertToWorld(body.getPosition().y));
		wrapper.setPosition(worldPosition);
		wrapper.setRotation(body.getAngle()*MathUtils.radiansToDegrees);
	}

	public void draw(SpriteBatch sp){
		wrapper.draw(sp);
	}
	
	public Vector2 getPosition(){
		return body.getPosition();
	}
	
	public boolean hasType(String type){
		return type.equals(generalType) || type.equals(specificType); 
	}
}