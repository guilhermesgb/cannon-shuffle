package cannon.shuffle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class EnemyBullet extends Bullet{

	public int damage = 5;
	
	public EnemyBullet(Vector2 pos, World world, float angle){
		super(BodyType.DynamicBody, pos, angle, world);

		wrapper = new TextureWrapper(new TextureRegion(new Texture(Gdx.files.internal("enemy_bullet.png")), Constants.ENEMY_BULLET_WIDTH, Constants.ENEMY_BULLET_HEIGHT), pos);
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
		body.setTransform(body.getPosition(), angle);
		bodyShape.dispose();
		
		body.setUserData(this);
		
		specificType = CannonShuffle.ENEMY_BULLET;
		
	}

	public void update(){
		super.update();
		body.setLinearVelocity(new Vector2(0,-4.0f));
	}

	@Override
	public Explosion getExplosion(boolean lerpWithTarget, Vector2 targetPosition, float factor) {
		if ( lerpWithTarget ){
			return new FireExplosion(this.getPosition().lerp(targetPosition, factor));
		}
		else{
			return new FireExplosion(this.getPosition());
		}
	}
	
}