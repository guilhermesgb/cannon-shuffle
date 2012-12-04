package cannon.shuffle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class Enemy extends GameEntity {

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
	
	public Enemy(TextureRegion region, World world, Vector2 pos, int boxIndex, int collisionGroup,float angle){
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
	public static Enemy newEnemy(World world, Vector2 pos, float angle){
		TextureRegion region= new TextureRegion(new Texture(Gdx.files.internal("enemy.png")), Constants.ENEMY_WIDTH, Constants.ENEMY_HEIGHT);
		return new Enemy(region, world, pos, 1, 1, angle);
	}
	public void remove() {
		body.destroyFixture(body.getFixtureList().get(0));
	}

	public void move(){
		//spin
		body.setLinearVelocity(new Vector2((float)(direction*Constants.ENEMY_SPEED*Math.cos(theta)),(float)(direction*Constants.ENEMY_SPEED*Math.sin(theta))));
		theta+=direction*rotation_speed;
		if(Math.abs(theta-direction*Math.PI/2)<0.01f)
			theta=-direction*Math.PI/2;
		//detect the x edges
		if(body.getPosition().x<=BaseBoxObject.convertToBox(Constants.ENEMY_WIDTH)){
			direction*=-1;
			body.setTransform((float)(BaseBoxObject.convertToBox(Constants.ENEMY_WIDTH+1)), body.getPosition().y, body.getAngle());
			System.out.println(body.getPosition().x);
		}
		if(body.getPosition().x>=BaseBoxObject.convertToBox(Constants.WORLD_WIDTH-Constants.ENEMY_WIDTH)){
			direction*=-1;
			body.setTransform((float)(BaseBoxObject.convertToBox(Constants.WORLD_WIDTH-Constants.ENEMY_WIDTH-1)), body.getPosition().y, body.getAngle());
			System.out.println(body.getPosition().x);
		}
		
	}
	public void fire(World world, Cannon cannon, Array<EnemyBullet> enemy_bullets){
		double cannon_enemy_relative_angle=-Math.PI/2+body.getPosition().sub(cannon.body.getPosition()).angle()*Math.PI/180;
		if(Math.abs(cannon_enemy_relative_angle-theta)<rotation_speed*4/5){
			System.out.println(theta);
			System.out.println("firing weapon!\n");
			cannon_enemy_relative_angle-=Math.PI/2;
			Vector2 bullet_position = new Vector2(BaseBoxObject.convertToWorld(body.getPosition().x),BaseBoxObject.convertToWorld(body.getPosition().y));
			bullet_position = bullet_position.add(new Vector2(2*Constants.ENEMY_WIDTH*(float)Math.cos(cannon_enemy_relative_angle),2*Constants.ENEMY_WIDTH*(float)Math.sin(cannon_enemy_relative_angle)));
			EnemyBullet bullet = EnemyBullet.newBullet(world, bullet_position, (float)cannon_enemy_relative_angle, new Vector2(0,-10));
			enemy_bullets.add(bullet);
			
		}
		
	}
	public void update(World world, Cannon cannon, Array<EnemyBullet> enemy_bullets){
		super.update();
		move();
		fire(world,cannon,enemy_bullets);
	}

}
