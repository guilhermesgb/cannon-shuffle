package cannon.shuffle.cannon;

import cannon.shuffle.Entities;
import cannon.shuffle.GameEntity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class ShieldPart extends GameEntity{

	public ShieldPart(BodyType bodyType, Vector2 pos, float angle,
			World world) {
		super(bodyType, pos, angle, world);
		generalType = Entities.SHIELD_PART;
		protection =0.5f;
	}
}