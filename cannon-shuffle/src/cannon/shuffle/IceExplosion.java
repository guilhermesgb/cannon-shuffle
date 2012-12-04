package cannon.shuffle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class IceExplosion extends Explosion{

	final int SPRITES_COLUMN= (Constants.EXPLOSION_SPRITE_WIDTH_TOTAL)/Constants.EXPLOSION_SPRITE_WIDTH;
	final int SPRITES_ROW= Constants.EXPLOSION_SPRITE_HEIGHT_TOTAL/Constants.EXPLOSION_SPRITE_HEIGHT;
	private TextureWrapper[][] wrapper;
	static Texture sheet = new Texture(Gdx.files.internal("explosion_ice.png"));
	int current_row = 0;
	int current_column= 0;

	public IceExplosion(Vector2 position){
		Vector2 converted = new Vector2(GameEntity.convertToWorld(position.x), GameEntity.convertToWorld(position.y));

		wrapper = new TextureWrapper[SPRITES_ROW][SPRITES_COLUMN];
		
		TextureRegion[][] sheetregion= TextureRegion.split(sheet, Constants.EXPLOSION_SPRITE_WIDTH, Constants.EXPLOSION_SPRITE_HEIGHT);

		for ( int i=0; i<SPRITES_ROW; i++ ){
			for ( int j=0; j<SPRITES_COLUMN; j++ ){
				wrapper[i][j] = new TextureWrapper(sheetregion[i][j], converted);
				wrapper[i][j].getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
			}
		}	
	}
	int delay=1;
	public boolean draw(SpriteBatch batch) {
		if ( (current_row+1)*(current_column+1) >= SPRITES_ROW*SPRITES_COLUMN){
			return false;
		}
		wrapper[current_row][current_column].draw(batch);
		if(delay<0){
			current_column++;
			if(current_column%SPRITES_COLUMN==0){
				current_row++;
				current_column=0;
			}
			delay=1;
		}else
			delay--;

		return true;
	}

}