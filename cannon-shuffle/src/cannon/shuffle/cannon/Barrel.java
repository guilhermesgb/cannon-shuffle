package cannon.shuffle.cannon;

import cannon.shuffle.bullet.Bullet;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public abstract class Barrel {

	public abstract void update(World world, SpriteBatch sp, Cannon cannon,
			Array<Bullet> bullets, OrthographicCamera camera);

	public abstract void destroy(World world);

	public abstract Vector2 getPosition();

	public abstract float getAngle();

	public abstract void setLinearVelocity(Vector2 vector2);

	public abstract void setTransform(Vector2 vector2, float angle);

	public abstract String getSpecificType();

	public abstract float getRotation();
}
