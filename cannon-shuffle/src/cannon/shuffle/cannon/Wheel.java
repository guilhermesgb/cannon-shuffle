package cannon.shuffle.cannon;

import cannon.shuffle.GameEntity;
import cannon.shuffle.TextureWrapper;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Wheel extends GameEntity{

	public Wheel(World world, Vector2 pos) {
		super(BodyType.KinematicBody, pos, 0f, world);
		wrapper = new TextureWrapper(new TextureRegion(new Texture("cannon_wheels.png")), pos);
	}
	
}
