package cannon.shuffle.bullet;

import cannon.shuffle.Constants;
import cannon.shuffle.TextureWrapper;
import cannon.shuffle.Utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class AnimationWrapper extends TextureWrapper {

	final int BULLET_SPRITE_WIDTH_TOTAL;
	final int BULLET_SPRITE_HEIGHT_TOTAL;
	final int SPRITES_COLUMN;// (Constants.EXPLOSION_SPRITE_WIDTH_TOTAL)/Constants.EXPLOSION_SPRITE_WIDTH;
	final int SPRITES_ROW;
	public TextureWrapper[][] wrapper;
	static Texture sheet;
	int current_row = 0;
	int current_column= 0;
	
	public AnimationWrapper(String spriteSheet, Vector2 position, int BULLET_SPRITE_WIDTH, int BULLET_SPRITE_WIDTH_TOTAL, int BULLET_SPRITE_HEIGHT, int BULLET_SPRITE_HEIGHT_TOTAL) {
		super(spriteSheet, position);
		
		sheet = new Texture(Gdx.files.internal(spriteSheet));
		
		this.BULLET_SPRITE_WIDTH_TOTAL = BULLET_SPRITE_WIDTH_TOTAL;
		this.BULLET_SPRITE_HEIGHT_TOTAL = BULLET_SPRITE_HEIGHT_TOTAL;

		this.SPRITES_COLUMN = 3;
		this.SPRITES_ROW = 2;//BULLET_SPRITE_HEIGHT_TOTAL/BULLET_SPRITE_HEIGHT;
		
		Vector2 converted = new Vector2(Utils.convertToWorld(position.x), Utils.convertToWorld(position.y));
		
		TextureRegion[][] sheetregion= TextureRegion.split(sheet, BULLET_SPRITE_WIDTH, BULLET_SPRITE_HEIGHT);
		System.out.println(sheetregion.length);
		width=BULLET_SPRITE_WIDTH;
		height=BULLET_SPRITE_HEIGHT;
		originX=width/2;
		originY=height/2;
		
		wrapper = new TextureWrapper[SPRITES_ROW][SPRITES_COLUMN];
		for ( int i=0; i<SPRITES_ROW; i++ ){
			for ( int j=0; j<SPRITES_COLUMN; j++ ){
				wrapper[i][j] = new TextureWrapper(sheetregion[i][j], converted);
				wrapper[i][j].getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
			}
		}
	}

	@Override
	public TextureRegion getRegion(){
		return wrapper[0][0].getRegion();
	}

	@Override
	public void setPosition(float x, float y){
		for ( int i=0; i<SPRITES_ROW; i++ ){
			for ( int j=0; j<SPRITES_COLUMN; j++ ){
				wrapper[i][j].setPosition(x, y);
			}
		}		
	}
	
	@Override
	public void setPosition(Vector2 worldPosition) {
		setPosition(worldPosition.x, worldPosition.y);
	}
	
	@Override
	public void setRotation(float rotation){
		for ( int i=0; i<SPRITES_ROW; i++ ){
			for ( int j=0; j<SPRITES_COLUMN; j++ ){
				wrapper[i][j].setRotation(rotation);
			}
		}
	}
	
	int delay=1;
	@Override
	public boolean draw(SpriteBatch batch) {
//		if ( (current_row+1)*(current_column+1) >= SPRITES_ROW*SPRITES_COLUMN){
//			return false;
//		}
		wrapper[current_row][current_column].draw(batch);
		if(delay<0){
			current_column++;
			if(current_column%SPRITES_COLUMN==0){
				current_row = (current_row + 1)%SPRITES_ROW;
				current_column=0;
			}
			delay=1;
		}else
			delay--;

		return true;
	}
	
}
