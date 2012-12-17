package cannon.shuffle.cannon;

import cannon.shuffle.Constants;
import cannon.shuffle.Entities;
import cannon.shuffle.GameEntity;
import cannon.shuffle.TextureWrapper;
import cannon.shuffle.Utils;
import cannon.shuffle.screen.GameScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class Cannon extends GameEntity{


	Barrel barrel;
	Array<Wheel> wheels;
	World world;

	public Cannon(World world, Vector2 pos){
		super(BodyType.KinematicBody, (new Vector2(pos.x, pos.y+(Constants.CANNON_CIRCLE_RADIUS+Constants.CANNON_RECT_HEIGHT)/2)), 0, world);
		hp = 1000;
		recoverable_hp = 1000;
		wrapper = new TextureWrapper(new TextureRegion(new Texture(Gdx.files.internal("cannon.png")), Constants.CANNON_CIRCLE_WIDTH, Constants.CANNON_CIRCLE_RADIUS + Constants.CANNON_RECT_HEIGHT), pos);
		createCannon(wrapper, pos, 0.5f, 0f, 0f);
		body.setUserData(this);
		wrapper.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		generalType = Entities.CANNON;
		specificType = Entities.CANNON;
		Vector2 pos_t = new Vector2(pos);
		pos_t.add(new Vector2(0, Constants.CANNON_RECT_HEIGHT));
		this.world = world;
		barrel = new LightningBarrel(world, pos_t);
		
		wheels = new Array<Wheel>();
		
		pos_t = new Vector2(pos);
		pos_t.add(new Vector2(-Constants.CANNON_WHEEL_DISTANCE, Constants.CANNON_RECT_HEIGHT/2-19f/2-.01f));
		wheels.add(new Wheel(world, pos_t));
		pos_t = new Vector2(pos);
		pos_t.add(new Vector2(Constants.CANNON_WHEEL_DISTANCE, Constants.CANNON_RECT_HEIGHT/2-19f/2-.01f));
		wheels.add(new Wheel(world, pos_t));
	}
	
	private void createCannon(TextureWrapper texture, Vector2 pos, float density, float restitution, float angle) {

		CircleShape circleShape = new CircleShape();
		circleShape.setRadius(Utils.convertToBox(Constants.CANNON_CIRCLE_RADIUS));
		
		PolygonShape rectShape = new PolygonShape();
		rectShape.setAsBox(Utils.convertToBox(Constants.CANNON_RECT_WIDTH/2f), Utils.convertToBox(Constants.CANNON_RECT_HEIGHT/2f));
		
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
		barrel.update(GameScreen.world, sp, GameScreen.cannon, GameScreen.bullets, GameScreen.camera);
		for (Wheel wheel : wheels){
			wheel.draw(sp);
		}
	}

	@Override
	public void destroy(World world){
		super.destroy(world);
		barrel.destroy(world);
	}
	
	public void setLinearVelocity(Vector2 velocity) {
		boolean blocked = false;
		Vector2 new_position = new Vector2();
		if ( Utils.convertToWorld(body.getPosition().x) > Constants.WORLD_WIDTH - Constants.CANNON_RECT_WIDTH/2 ){
			new_position = new Vector2(Utils.convertToBox(Constants.WORLD_WIDTH - Constants.CANNON_RECT_WIDTH/2 ),0);
			blocked = true;
		}
		else if ( Utils.convertToWorld(body.getPosition().x) < Constants.CANNON_RECT_WIDTH/2 ){
			new_position = new Vector2(Utils.convertToBox(Constants.CANNON_RECT_WIDTH/2 ),0);
			blocked = true;
		}
		if ( blocked ){
			body.setTransform(new Vector2(new_position.x, new_position.y + body.getPosition().y), body.getAngle());
			body.setLinearVelocity(new Vector2());
			barrel.setTransform(new Vector2(new_position.x, new_position.y + barrel.getPosition().y), barrel.getAngle());
			barrel.setLinearVelocity(new Vector2());
			Wheel wheel = wheels.get(0);
			wheel.body.setTransform(new Vector2(new_position.x+Utils.convertToBox(Constants.CANNON_WHEEL_DISTANCE), new_position.y + wheel.body.getPosition().y), wheel.body.getAngle());
			wheel.body.setLinearVelocity(new Vector2());
			wheel = wheels.get(1);
			wheel.body.setTransform(new Vector2(new_position.x-Utils.convertToBox(Constants.CANNON_WHEEL_DISTANCE), new_position.y + wheel.body.getPosition().y), wheel.body.getAngle());
			wheel.body.setLinearVelocity(new Vector2());
		}
		else{
			body.setLinearVelocity(velocity);
			barrel.setLinearVelocity(velocity);
			for (Wheel wheel : wheels){
				wheel.body.setLinearVelocity(velocity);
				wheel.body.setAngularVelocity(2*-velocity.x);
			}
		}
	}

	Vector3 touchInitial = new Vector3();
	Vector3 touchFinal = new Vector3();
	private boolean preTouch = true;
	private boolean postTouch = false;
	
	public boolean blockShoot = false;
	private float max_dt = 0;
	public void update(){
		super.update();

		if ( Gdx.input.isTouched() && ( Utils.isTouchingCannon() || preTouch == false ) ){
			Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
			GameScreen.camera.unproject(touchPos);
			if(preTouch){
				max_dt = 0;
				touchInitial = touchPos;
				preTouch=false;
				blockShoot = true;
			}
			touchFinal = touchPos;
//			if ( !Utils.isTouchingCannon(touchFinal) ){
				if(touchFinal.x>Constants.WORLD_WIDTH-Constants.CANNON_RECT_WIDTH/2)
					touchFinal.x=Constants.WORLD_WIDTH-Constants.CANNON_RECT_WIDTH/2;
				if(touchFinal.x<Constants.CANNON_RECT_WIDTH/2)
					touchFinal.x=Constants.CANNON_RECT_WIDTH/2;
				float dt = (touchFinal.x-Utils.convertToWorld(body.getPosition().x))/20.0f;
				dt=(float) ((dt>0)?(1/(1+Math.exp(-dt+3))-.047):(-1/(1+Math.exp(dt+3)))+.047);
				if(Math.abs(dt)>max_dt)
					max_dt=Math.abs(dt);
				setLinearVelocity(new Vector2(3*dt,body.getLinearVelocity().y));
//			}
			postTouch=true;
		}else{
			preTouch=true;
			if(postTouch){
				System.out.println(max_dt);
				postTouch=false;
				if ( max_dt<.9 ){
					//change weapon
					if (barrel.getSpecificType().equals("FireBarrelPart")){
						float rotation = barrel.getRotation();
						System.out.println("rotation: "+rotation);
						barrel.destroy(world);
						Vector2 pos = new Vector2(Utils.convertToWorld(body.getPosition().x), Utils.convertToWorld(body.getPosition().y));
						barrel = new LightningBarrel(world, pos);
//						barrel.setTransform(body.getPosition(), rotation);
					}
					else{
						float rotation = barrel.getRotation();
						System.out.println("rotation: "+rotation);
						barrel.destroy(world);
						Vector2 pos = new Vector2(Utils.convertToWorld(body.getPosition().x), Utils.convertToWorld(body.getPosition().y));
						barrel = new FireBarrel(world, pos);
//						barrel.setTransform(body.getPosition(), rotation);
					}
					blockShoot = false;
				}
				else{
					//move cannon
					System.out.println("move cannon");
					blockShoot = false;
				}
			}
			setLinearVelocity(new Vector2(body.getLinearVelocity().x*0.96f,body.getLinearVelocity().y));
		}
		
		for (Wheel wheel : wheels){
			wheel.update();
		}
	}
	
}