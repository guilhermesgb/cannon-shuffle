package cannon.shuffle;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public abstract class BaseBoxObject{
	
	public static final int POLY_OBJECT=0;
	public static final int CIRCLE_OBJECT=1;
	private static final float WORLD_TO_BOX=0.01f;
	private static final float BOX_TO_WORLD=100f;
	protected Body body;
	protected BoxUserData userData;
	protected Vector2 worldPosition;
	
	public BaseBoxObject(Vector2 pos, World world, int boxIndex, int collisionGroup, BodyDef.BodyType bodyType){
		userData=new BoxUserData(boxIndex, collisionGroup);
		worldPosition=new Vector2();
		createBody(world, pos, bodyType, 0);
		body.setUserData(userData);
	}

	public void createBaseBoxObject(TextureWrapper texture, Vector2 pos, int shapeType, float density, float restitution, float angle){

		if(shapeType==CIRCLE_OBJECT){
			makeCircleFixture(texture.getWidth()/2, density, restitution, pos, angle);
		}else{
			makeRectFixture(texture.getWidth(), texture.getHeight(), density, restitution, pos, angle);
		}		
	}
	
	public static float convertToBox(float x){
		return x*WORLD_TO_BOX;
	}
	
	public static float convertToWorld(float x){
		return x*BOX_TO_WORLD;
	}
	
	public void createBody(World world, Vector2 pos, BodyDef.BodyType bodyType, float angle){

		BodyDef bodyDef = new BodyDef();
		bodyDef.type = bodyType;
		bodyDef.position.set(convertToBox(pos.x), convertToBox(pos.y));
		bodyDef.angle=angle;
		body = world.createBody(bodyDef);
	}

	public void makeRectFixture(float width, float height, float density, float restitution, Vector2 pos, float angle){

		PolygonShape bodyShape = new PolygonShape();

		float w=convertToBox(width/2f);
		float h=convertToBox(height/2f);
		bodyShape.setAsBox(w, h);

		FixtureDef fixtureDef=new FixtureDef();
		fixtureDef.density=density;
		fixtureDef.restitution=restitution;
		fixtureDef.shape=bodyShape;
		fixtureDef.friction=10f;
		
		body.createFixture(fixtureDef);
		body.setTransform(body.getPosition(), angle);
		bodyShape.dispose();
	}
	
	public void makeCircleFixture(float radius, float density, float restitution, Vector2 pos, float angle){

		FixtureDef fixtureDef=new FixtureDef();
		fixtureDef.density=density;
		fixtureDef.restitution=restitution;
		fixtureDef.shape=new CircleShape();
		fixtureDef.shape.setRadius(convertToBox(radius));
		fixtureDef.friction=10f;

		body.createFixture(fixtureDef);
		fixtureDef.shape.dispose();
	}
	
	public void updateWorldPosition(){
		worldPosition.set(convertToWorld(body.getPosition().x),convertToWorld(body.getPosition().y));
	}
	
}