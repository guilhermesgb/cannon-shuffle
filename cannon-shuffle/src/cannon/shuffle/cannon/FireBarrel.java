package cannon.shuffle.cannon;

import cannon.shuffle.Constants;
import cannon.shuffle.Entities;
import cannon.shuffle.TextureWrapper;
import cannon.shuffle.Utils;
import cannon.shuffle.bullet.Bullet;
import cannon.shuffle.bullet.FireBullet;
import cannon.shuffle.screen.GameScreen;

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

public class FireBarrel extends Barrel{

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

	public class FireBarrelPart extends BarrelPart{

		public FireBarrelPart(BodyType bodyType, Vector2 pos, float angle,
				World world) {
			super(bodyType, pos, angle, world);
			specificType = Entities.FIRE_BARREL_PART;
			protection = -0.2f;
		}
	}
	public class FireShieldPart extends ShieldPart{

		public FireShieldPart(BodyType bodyType, Vector2 pos, float angle,
				World world) {
			super(bodyType, pos, angle, world);
			specificType = Entities.FIRE_SHIELD_PART;
			protection =0.5f;
		}
	}

	public FireBarrel(World world, Vector2 pos){

		TextureRegion barrelRegion = new TextureRegion(new Texture(Gdx.files.internal("barrel.png")), Constants.BARREL_WIDTH, Constants.BARREL_HEIGHT);
		TextureRegion shieldRegion = new TextureRegion(new Texture(Gdx.files.internal("shield.png")), Constants.BARREL_WIDTH, Constants.BARREL_HEIGHT);

		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.KinematicBody;
		bodyDef.position.set(Utils.convertToBox(pos.x), Utils.convertToBox(pos.y));
		bodyDef.angle=angle;
		shield_body = world.createBody(bodyDef);
		barrel_body = world.createBody(bodyDef);
		barrelRegion.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		shieldRegion.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		wrapper_barrel = new TextureWrapper(new Sprite(barrelRegion.getTexture()), pos);
		wrapper_shield = new TextureWrapper(new Sprite(shieldRegion.getTexture()), pos);

		createBarrel(pos, 0.5f, 0f, 0f);
		barrel_part = new FireBarrelPart(null, null, -1, null);
		shield_part = new FireShieldPart(null, null, -1, null);
		barrel_body.setUserData(barrel_part);
		barrel_part.body = barrel_body;
		shield_body.setUserData(shield_part);
		shield_part.body = shield_body;
		touchPos = new Vector3();

	}

	private void createBarrel(Vector2 pos, float density, float restitution, float angle) {

		ChainShape chainShape = new ChainShape();
		int arcResolution=20;
		double theta=Math.PI/3;
		Vector2 positionArc[]=new Vector2[arcResolution];
		positionArc[0]=new Vector2(0,0);
		double nTheta=-theta;
		float radius=Utils.convertToBox(Constants.BARREL_CIRCLE_RADIUS);
		for(int i=1; i<arcResolution-1;i++,nTheta+=theta/(arcResolution-2)){
			positionArc[i]=new Vector2(radius*(float)Math.sin(nTheta/2),radius*(float)Math.cos(nTheta/2));
		}
		positionArc[arcResolution-1]=new Vector2(0,0);
		chainShape.createLoop(positionArc);
		PolygonShape rectShape1 = new PolygonShape();
		rectShape1.setAsBox(Utils.convertToBox(Constants.BARREL_RECT_WIDTH/2f), Utils.convertToBox(Constants.BARREL_RECT_HEIGHT/2f),
				new Vector2(0,Utils.convertToBox(Constants.BARREL_CIRCLE_RADIUS+Constants.BARREL_RECT_HEIGHT/2f)),0);
		PolygonShape rectShape2 = new PolygonShape();
		rectShape2.setAsBox(Utils.convertToBox(Constants.BARREL_RECT_WIDTH/2f), Utils.convertToBox(Constants.BARREL_RECT_HEIGHT/2f),
				new Vector2(0,Utils.convertToBox(Constants.BARREL_CIRCLE_RADIUS+Constants.BARREL_RECT_HEIGHT*3/2)),0);

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

		wrapper_barrel.setPosition(Utils.convertToWorld(barrel_body.getPosition().x),Utils.convertToWorld(barrel_body.getPosition().y));
		wrapper_barrel.setRotation(barrel_body.getAngle()*MathUtils.radiansToDegrees);
		wrapper_shield.setPosition(Utils.convertToWorld(shield_body.getPosition().x),Utils.convertToWorld(shield_body.getPosition().y));
		wrapper_shield.setRotation(shield_body.getAngle()*MathUtils.radiansToDegrees);

		float chargeRatio = 0f;

		if ( Gdx.input.isTouched() && !Utils.isTouchingCannon() && !GameScreen.cannon.blockShoot ){
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			if(preTouch){
				onTouchTime=TimeUtils.millis();
				preTouch=false;
			}
			postTouch=true;
			move(touchPos, cannon);
			chargeRatio = chargeRatio();
		}else if (!GameScreen.cannon.blockShoot){
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
		cannon_semicircle_center.x=Utils.convertToWorld(cannon_semicircle_center.x);
		cannon_semicircle_center.y=Utils.convertToWorld(cannon_semicircle_center.y);
		cannon_semicircle_center.y+=Constants.CANNON_CIRCLE_RADIUS;

		float da=target_angle-angle;
		if(da>resolution){
			angle+=resolution;
		}else if(da<-resolution){
			angle-=resolution;
		}else{
			if(has_shot){
				Vector2 bullet_pos=barrel_body.getPosition();

				bullet_pos.x=Utils.convertToWorld(bullet_pos.x);
				bullet_pos.y=Utils.convertToWorld(bullet_pos.y);

				float offset=5.0f+(Constants.BARREL_HEIGHT/2+Constants.BULLET_HEIGHT/2);
				bullet_pos.x+=offset*-Math.sin(angle);
				bullet_pos.y+=offset*Math.cos(angle);

				FireBullet bullet = new FireBullet(world, bullet_pos, angle, false);
				bullets.add(bullet);
				cannon_semicircle_center=new Vector2(Utils.convertToBox(cannon_semicircle_center.x),Utils.convertToBox(cannon_semicircle_center.y));
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
		cannon_semicircle_center.x=Utils.convertToWorld(cannon_semicircle_center.x);
		cannon_semicircle_center.y=Utils.convertToWorld(cannon_semicircle_center.y);
		cannon_semicircle_center.y+=Constants.CANNON_CIRCLE_RADIUS;
		target_angle=(float)(Math.PI/2+Math.atan2((double)cannon_semicircle_center.y-touchPos.y,(double)cannon_semicircle_center.x-touchPos.x));
	}

	public void shoot(){
		has_shot = true;
	}

	public void destroy(World world) {
		barrel_part.destroy(world);
		shield_part.destroy(world);
	}

	public void setLinearVelocity(Vector2 velocity) {
		barrel_body.setLinearVelocity(velocity);
		shield_body.setLinearVelocity(velocity);
	}

	public void setTransform(Vector2 pos, float angle) {
		barrel_body.setTransform(pos, angle);
		shield_body.setTransform(pos, angle);
	}

	public Vector2 getPosition() {
		return barrel_body.getPosition();
	}

	public float getAngle() {
		return barrel_body.getAngle();
	}

	@Override
	public String getSpecificType() {
		return barrel_part.specificType;
	}

	@Override
	public float getRotation() {
		return barrel_body.getAngle();
	}
}