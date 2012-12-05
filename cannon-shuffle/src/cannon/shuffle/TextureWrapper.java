package cannon.shuffle;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;


public class TextureWrapper{

	public TextureRegion region;
	public int width;
	public int height;
	public Vector2 position;
	public float scaleX;
	public float scaleY;
	public float originX;
	public float originY;
	public float rotation;
	
	public TextureWrapper(TextureRegion region,Vector2 pos){

		this.position=pos;
		this.region=region;
		width=region.getRegionWidth();
		height=region.getRegionHeight();
		originX=width/2;
		originY=height/2;
		scaleX=1;
		scaleY=1;
	}
	
	public int getWidth(){

		return width;
	}
	
	public int getHeight(){

		return height;
	}

	public void setPosition(float x,float y){

		position.set(x,y);
	}

	public void setRotation(float r){

		rotation=r;
	}

	public void draw(SpriteBatch sp){

		sp.draw(region, position.x-width/2, position.y-height/2,
				originX, originY, width, height,
				scaleX, scaleY, rotation);
	}
	
	public void setPosition(Vector2 worldPosition) {

		position = worldPosition;
	}
	
	public TextureRegion getRegion(){
		return region;
	}
	
}