package cannon.shuffle;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class HealthBar {

	private TextureWrapper healthBar;
	private TextureWrapper healthGauge;
	private int gaugeWidth;
	private TextureWrapper recoverableHealthGauge;
	private int recoverableGaugeWidth;
	private double maxHPValue;
	private double lastHPValue;
	private double lastRecoverableHPValue;
	private GameEntity tracked;
	private Vector2 position;
	
	public HealthBar(GameEntity toBeTracked, Vector2 pos){
		position = pos;
		healthBar = new TextureWrapper(new TextureRegion(new Texture("health_bar.png"), Constants.HEALTH_BAR_WIDTH, Constants.HEALTH_BAR_HEIGHT), pos){
			public void draw(SpriteBatch sp){

				sp.draw(region, position.x, position.y-height/2,
						originX, originY, width, height,
						scaleX, scaleY, rotation);
			}
		};
		gaugeWidth = Constants.HEALTH_BAR_WIDTH;
		healthGauge = new TextureWrapper(new TextureRegion(new Texture("health_gauge_good.png"), gaugeWidth, Constants.HEALTH_BAR_HEIGHT), pos){
			public void draw(SpriteBatch sp){
				
				sp.draw(region, position.x, position.y-height/2,
						originX, originY, width, height,
						scaleX, scaleY, rotation);
			}
		};
		recoverableGaugeWidth = Constants.HEALTH_BAR_WIDTH;
		recoverableHealthGauge = new TextureWrapper(new TextureRegion(new Texture("recoverable_health_gauge_good.png"), recoverableGaugeWidth, Constants.HEALTH_BAR_HEIGHT), pos){
			public void draw(SpriteBatch sp){
				
				sp.draw(region, position.x, position.y-height/2,
						originX, originY, width, height,
						scaleX, scaleY, rotation);
			}
		};
		tracked = toBeTracked;
		maxHPValue = tracked.hp;
		lastHPValue = tracked.hp;
		lastRecoverableHPValue = tracked.recoverable_hp;
	}
	
	public void draw(SpriteBatch batch){
		if ( !(tracked.recoverable_hp == lastRecoverableHPValue) ){
			if ( tracked.recoverable_hp <= 0 ){
				recoverableGaugeWidth = 0;
			}
			else {
				recoverableGaugeWidth = (int) Math.round(((tracked.recoverable_hp / maxHPValue) * Constants.HEALTH_BAR_WIDTH));
			}
			String gauge_name = "recoverable_health_gauge";
			if ( (tracked.hp / maxHPValue) <= .25 ){
				gauge_name += "_bad.png";
			}
			else if ( (tracked.hp / maxHPValue) <= .6 ){
				gauge_name += "_medium.png";
			}
			else{
				gauge_name += "_good.png";
			}
			recoverableHealthGauge = new TextureWrapper(new TextureRegion(new Texture(gauge_name), recoverableGaugeWidth, Constants.HEALTH_BAR_HEIGHT), position){
			public void draw(SpriteBatch sp){
				
				sp.draw(region, position.x, position.y-height/2,
						originX, originY, width, height,
						scaleX, scaleY, rotation);
				}
			};
		}
		recoverableHealthGauge.draw(batch);
		lastRecoverableHPValue = tracked.recoverable_hp;
		if ( tracked.hp == lastHPValue ){
			healthGauge.draw(batch);
			healthBar.draw(batch);
			return;
		}
		else if ( tracked.hp <= 0 ){
			gaugeWidth = 1;
		}
		else{
			gaugeWidth = (int) Math.round(((tracked.hp / maxHPValue) * Constants.HEALTH_BAR_WIDTH));
		}
		String gauge_name = "health_gauge";
		if ( (tracked.hp / maxHPValue) <= .25 ){
			gauge_name += "_bad.png";
		}
		else if ( (tracked.hp / maxHPValue) <= .6 ){
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
		lastHPValue = tracked.hp;
	}
	
}
