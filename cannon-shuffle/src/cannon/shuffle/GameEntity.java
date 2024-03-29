package cannon.shuffle;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

public abstract class GameEntity {

	public Body body;
	public TextureWrapper wrapper;
	public Vector2 worldPosition;
	
	public String generalType;
	public String specificType;

	public double hp = 1000;
	public double recoverable_hp = 1000;
	public float protection = 0.0f;

	public GameEntity(BodyDef.BodyType bodyType, Vector2 pos, float angle, World world) {
		if ( bodyType != null ){
			BodyDef bodyDef = new BodyDef();
			bodyDef.type = bodyType;
			bodyDef.position.set(Utils.convertToBox(pos.x), Utils.convertToBox(pos.y));
			bodyDef.angle=angle;
			body = world.createBody(bodyDef);
			worldPosition = new Vector2();
		}
	}

	public void update(){
		worldPosition.set(Utils.convertToWorld(body.getPosition().x),Utils.convertToWorld(body.getPosition().y));
		wrapper.setPosition(worldPosition);
		wrapper.setRotation(body.getAngle()*MathUtils.radiansToDegrees);
		if ( hp >= recoverable_hp ){
			recoverable_hp = hp;
		}
		else{
			recoverable_hp--;
		}
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
	
	public void destroy(World world) {
		for ( int i=0; i<body.getFixtureList().size(); i++ ){
			body.destroyFixture(body.getFixtureList().get(i));
		}
		if ( world != null ){
			world.destroyBody(body);
		}
	}
}