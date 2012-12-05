package cannon.shuffle.bullet;

import cannon.shuffle.CannonShuffle;
import cannon.shuffle.GameEntity;
import cannon.shuffle.explosion.Explosion;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;

public abstract class Bullet extends GameEntity{

	public Bullet(BodyType bodyType, Vector2 pos, float angle, World world, boolean shot_by_enemy) {
		super(bodyType, pos, angle, world);
		generalType = CannonShuffle.BULLET;
		is_enemy_attack = shot_by_enemy;
	}

	/**
	 * CrushableType is a way to define whether or not this Bullet is destroyed by another Bullet if they collide.
	 * The moment a collision between them happen, our game queries both crushableType values of both Bullets.
	 * They both get to be destroyed if their crushableTypes are the same, otherwise, only the Bullet with lower crushableType gets destroyed.
	 * Default: 5;
	 */
	public int crushableType = 5;

	/**
	 * Determines whether this bullet was destroyed or not.
	 * Default: false
	 */
	public boolean destroyed = false;
	
	/**
	 * The amount of damage this bullet inflicts once it hits something.
	 */
	public int damage = 0;
	
	/**
	 * Determines whether this bullet was shot by an enemy.
	 */
	public boolean is_enemy_attack = false;
	
	/**
	 * Returns the specific explosion type that this Bullet causes at the right position;
	 */
	public abstract Explosion getExplosion(boolean lerpWithTarget, Vector2 targetPosition, float factor);
	
	public void remove(){
		while ( !body.getFixtureList().isEmpty() ){
			body.destroyFixture(body.getFixtureList().get(0));
		}
	}
}