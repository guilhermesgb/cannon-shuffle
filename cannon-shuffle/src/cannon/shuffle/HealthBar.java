package cannon.shuffle;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class HealthBar {

	private TextureWrapper healthBar;
	private TextureWrapper healthGauge;
	private int gaugeWidth;
	private double lastValue;
	private double maxValue;
	private GameEntity tracked;
	private Vector2 position;
	
	public HealthBar(GameEntity toBeTracked, Vector2 pos){
		healthBar = new TextureWrapper(new TextureRegion(new Texture("health_bar.png"), Constants.HEALTH_BAR_WIDTH, Constants.HEALTH_BAR_HEIGHT), pos){
			public void draw(SpriteBatch sp){

				sp.draw(region, position.x, position.y-height/2,
						originX, originY, width, height,
						scaleX, scaleY, rotation);
			}
		};
		gaugeWidth = Constants.HEALTH_BAR_WIDTH;
		tracked = toBeTracked;
		maxValue = tracked.hp;
		position = pos;
	}
	
	public void draw(SpriteBatch batch){
		if ( tracked.hp == lastValue ){
			healthGauge.draw(batch);
			healthBar.draw(batch);
			return;
		}
		else if ( tracked.hp <= 0 ){
			gaugeWidth = 1;
		}
		else{
			gaugeWidth = (int) Math.round(((tracked.hp / maxValue) * Constants.HEALTH_BAR_WIDTH));
		}
		String gauge_name = "health_gauge";
		if ( (tracked.hp / maxValue) <= .25 ){
			gauge_name += "_bad.png";
		}
		else if ( (tracked.hp / maxValue) <= .6 ){
			gauge_name += "_medium.png";
		}
		else{
			gauge_name += "_good.png";
		}
		healthGauge = new TextureWrapper(new TextureRegion(new Texture(gauge_name), gaugeWidth, Constants.HEALTH_BAR_HEIGHT), position){
			public void draw(SpriteBatch sp){
				
				sp.draw(region, position.x, position.y-height/2,
						originX, originY, width, height,
						scaleX, scaleY, rotation);
			}
		};
		healthGauge.draw(batch);
		healthBar.draw(batch);
	}
	
}
