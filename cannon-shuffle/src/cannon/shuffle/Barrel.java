package cannon.shuffle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class Barrel{

	public Body barrel_body;
	public Body shield_body;
	public TextureWrapper wrapper_barrel;
	public TextureWrapper wrapper_shield;
	float angle = 0f;
	final float resolution=.1f;
	public boolean has_shot = false;
	float reloading = 0;
	float target_angle=0;

	Vector3 touchPos;
	boolean preTouch=true;
	boolean postTouch=false;
	float constantTime=.004f;
	long onTouchTime;
	long touchHoldTime;
	long maxTouchTime=(long) ((float)Constants.MAX_BULLET_SPEED/(Constants.BULLET_SPEED*constantTime));
	public BarrelPart barrel_part;
	public ShieldPart shield_part;

	class BarrelPart extends GameEntity{

		public BarrelPart(BodyType bodyType, Vector2 pos, float angle,
				World world) {
			super(bodyType, pos, angle, world);
			generalType = CannonShuffle.BARREL_PART;
			specificType = CannonShuffle.BARREL_PART;
			protection = -0.2f;
		}
	}
	class ShieldPart extends GameEntity{

		public ShieldPart(BodyType bodyType, Vector2 pos, float angle,
				World world) {
			super(bodyType, pos, angle, world);
			generalType = CannonShuffle.SHIELD_PART;
			specificType = CannonShuffle.SHIELD_PART;
			protection =0.5f;
		}
	}
	
	public Barrel(World world, Vector2 pos){
		
		TextureRegion barrelRegion = new TextureRegion(new Texture(Gdx.files.internal("barrel.png")), Constants.BARREL_WIDTH, Constants.BARREL_HEIGHT);
		TextureRegion shieldRegion = new TextureRegion(new Texture(Gdx.files.internal("shield.png")), Constants.BARREL_WIDTH, Constants.BARREL_HEIGHT);
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.KinematicBody;
		bodyDef.position.set(GameEntity.convertToBox(pos.x), GameEntity.convertToBox(pos.y));
		bodyDef.angle=angle;
		shield_body = world.createBody(bodyDef);
		barrel_body = world.createBody(bodyDef);
		barrelRegion.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		shieldRegion.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		wrapper_barrel = new TextureWrapper(new Sprite(barrelRegion.getTexture()), pos);
		wrapper_shield = new TextureWrapper(new Sprite(shieldRegion.getTexture()), pos);
		
		createBarrel(pos, 0.5f, 0f, 0f);
		barrel_part = new BarrelPart(null, null, -1, null);
		shield_part = new ShieldPart(null, null, -1, null);
		barrel_body.setUserData(barrel_part);
		shield_body.setUserData(shield_part);
		touchPos = new Vector3();
		
	}

	private void createBarrel(Vector2 pos, float density, float restitution, float angle) {

		ChainShape chainShape = new ChainShape();
		int arcResolution=20;
		double theta=Math.PI/3;
		Vector2 positionArc[]=new Vector2[arcResolution];
		positionArc[0]=new Vector2(0,0);
		double nTheta=-theta;
		float radius=GameEntity.convertToBox(Constants.BARREL_CIRCLE_RADIUS);
		for(int i=1; i<arcResolution-1;i++,nTheta+=theta/(arcResolution-2)){
			positionArc[i]=new Vector2(radius*(float)Math.sin(nTheta/2),radius*(float)Math.cos(nTheta/2));
		}
		positionArc[arcResolution-1]=new Vector2(0,0);
		chainShape.createLoop(positionArc);
		PolygonShape rectShape1 = new PolygonShape();
		rectShape1.setAsBox(GameEntity.convertToBox(Constants.BARREL_RECT_WIDTH/2f), GameEntity.convertToBox(Constants.BARREL_RECT_HEIGHT/2f),
				new Vector2(0,GameEntity.convertToBox(Constants.BARREL_CIRCLE_RADIUS+Constants.BARREL_RECT_HEIGHT/2f)),0);
		PolygonShape rectShape2 = new PolygonShape();
		rectShape2.setAsBox(GameEntity.convertToBox(Constants.BARREL_RECT_WIDTH/2f), GameEntity.convertToBox(Constants.BARREL_RECT_HEIGHT/2f),
			new Vector2(0,GameEntity.convertToBox(Constants.BARREL_CIRCLE_RADIUS+Constants.BARREL_RECT_HEIGHT*3/2)),0);
		
		FixtureDef fixtureDef=new FixtureDef();

		fixtureDef.density=density;
		fixtureDef.restitution=restitution;
		fixtureDef.friction=10f;
		
		fixtureDef.shape = chainShape;
		shield_body.createFixture(fixtureDef);
		
		fixtureDef.shape=rectShape1;
		barrel_body.createFixture(fixtureDef);

		fixtureDef.shape=rectShape2;
		barrel_body.createFixture(fixtureDef);
		
		rectShape1.dispose();
		rectShape2.dispose();
		chainShape.dispose();
	}
	
	public float chargeRatio(){
		float ratio = ((TimeUtils.millis()-onTouchTime) / (float)maxTouchTime);
		if(ratio>1)
			ratio=1;
		return ratio;
	}
	
	public void update(World world, SpriteBatch batch, Cannon cannon, Array<Bullet> bullets, OrthographicCamera camera){

		wrapper_barrel.setPosition(GameEntity.convertToWorld(barrel_body.getPosition().x),GameEntity.convertToWorld(barrel_body.getPosition().y));
		wrapper_barrel.setRotation(barrel_body.getAngle()*MathUtils.radiansToDegrees);
		wrapper_shield.setPosition(GameEntity.convertToWorld(shield_body.getPosition().x),GameEntity.convertToWorld(shield_body.getPosition().y));
		wrapper_shield.setRotation(shield_body.getAngle()*MathUtils.radiansToDegrees);
		
		float chargeRatio = 0f;
		
		if ( Gdx.input.isTouched() ){
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			if(preTouch){
				onTouchTime=TimeUtils.millis();
				preTouch=false;
			}
			postTouch=true;
			move(touchPos, cannon);
			chargeRatio = chargeRatio();
		}else{
			preTouch=true;
			if(postTouch){
				touchHoldTime=TimeUtils.millis()-onTouchTime;
				postTouch=false;
				if(touchHoldTime>50){
					shoot();
				}
			}
		}
		
		float power=touchHoldTime*constantTime;
		float barrelSizeRadians=(float)Math.PI/3+0.1f;
		if(target_angle>barrelSizeRadians&&target_angle<Math.PI)
			target_angle=barrelSizeRadians;
		if(target_angle<-barrelSizeRadians||target_angle>=Math.PI)
			target_angle=-barrelSizeRadians;
			
		Vector2 cannon_semicircle_center=cannon.body.getWorldCenter();
		cannon_semicircle_center.x=GameEntity.convertToWorld(cannon_semicircle_center.x);
		cannon_semicircle_center.y=GameEntity.convertToWorld(cannon_semicircle_center.y);
		cannon_semicircle_center.y+=Constants.CANNON_CIRCLE_RADIUS;
		
		float da=target_angle-angle;
		if(da>resolution){
			angle+=resolution;
		}else if(da<-resolution){
			angle-=resolution;
		}else{
			if(has_shot){
				Vector2 bullet_pos=barrel_body.getPosition();
				
				bullet_pos.x=GameEntity.convertToWorld(bullet_pos.x);
				bullet_pos.y=GameEntity.convertToWorld(bullet_pos.y);
				
				float offset=5.0f+(Constants.BARREL_HEIGHT/2+Constants.BULLET_HEIGHT/2);
				bullet_pos.x+=offset*-Math.sin(angle);
				bullet_pos.y+=offset*Math.cos(angle);
				
				CannonBullet bullet = new CannonBullet(world, bullet_pos, angle);
				bullets.add(bullet);
				cannon_semicircle_center=new Vector2(GameEntity.convertToBox(cannon_semicircle_center.x),GameEntity.convertToBox(cannon_semicircle_center.y));
				float speed = Constants.BULLET_SPEED*power > Constants.MAX_BULLET_SPEED ? Constants.MAX_BULLET_SPEED : Constants.BULLET_SPEED*power;
				Vector2 velocity=new Vector2((float)(speed*-Math.sin(angle)), (float)(speed*Math.cos(angle)));
				bullet.body.setLinearVelocity(velocity);
				has_shot = false;
			}
		}
		barrel_body.setTransform(barrel_body.getPosition(), angle);
		shield_body.setTransform(shield_body.getPosition(), angle);
		
		float boldness_barrel = (float) (.65f + chargeRatio*chargeRatio*chargeRatio /.35f);
		batch.setColor(new Color(boldness_barrel, boldness_barrel, boldness_barrel, 1));
		wrapper_barrel.draw(batch);
		batch.setColor(Color.WHITE);
		float boldness_shield = .65f;
		batch.setColor(new Color(boldness_shield, boldness_shield, boldness_shield, 1));
		wrapper_shield.draw(batch);
		batch.setColor(Color.WHITE);
			
	}

	public void move(Vector3 touchPos, Cannon cannon){

		Vector2 cannon_semicircle_center=cannon.body.getWorldCenter();
		cannon_semicircle_center.x=GameEntity.convertToWorld(cannon_semicircle_center.x);
		cannon_semicircle_center.y=GameEntity.convertToWorld(cannon_semicircle_center.y);
		cannon_semicircle_center.y+=Constants.CANNON_CIRCLE_RADIUS;
		target_angle=(float)(Math.PI/2+Math.atan2((double)cannon_semicircle_center.y-touchPos.y,(double)cannon_semicircle_center.x-touchPos.x));
	}
	
	public void shoot(){
		has_shot = true;
	}
	
}