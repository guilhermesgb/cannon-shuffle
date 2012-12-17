package cannon.shuffle;

import cannon.shuffle.cannon.Cannon;
import cannon.shuffle.screen.GameScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;


public class Utils {

	private static final float WORLD_TO_BOX=0.01f;
	private static final float BOX_TO_WORLD=100f;
	
	public static float convertToBox(float x){
		return x*WORLD_TO_BOX;
	}
	
	public static float convertToWorld(float x){
		return x*BOX_TO_WORLD;
	}
	
	public static boolean isTouchingCannon() {
		Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
		GameScreen.camera.unproject(touchPos);
		return isTouchingCannon(touchPos);
	}

	public static boolean isTouchingCannon(Vector3 touchPos) {
		Cannon cannon = GameScreen.cannon;
		
		Vector2 origin = cannon.getPosition();
		origin.x = Utils.convertToWorld(origin.x);
		origin.y = Utils.convertToWorld(origin.y);
		origin.sub(new Vector2(cannon.wrapper.width/2, cannon.wrapper.height/2));
		Vector2 end = new Vector2(cannon.wrapper.width, cannon.wrapper.height);
		Rectangle rect = new Rectangle(origin.x, origin.y, end.x, end.y);
		return rect.contains(touchPos.x, touchPos.y);
	}

	
}
