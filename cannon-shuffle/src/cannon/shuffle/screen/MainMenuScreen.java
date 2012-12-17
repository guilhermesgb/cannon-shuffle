package cannon.shuffle.screen;

import cannon.shuffle.Constants;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GLCommon;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;

public class MainMenuScreen implements Screen{
	Game game;
	OrthographicCamera camera;
	SpriteBatch batch;
	Vector3 touchPoint;
	Rectangle playBounds;
	
	public MainMenuScreen(Game game){
		this.game = game;
		this.camera = new OrthographicCamera(320, 480);
		this.camera.position.set(320/2, 480/2, 0);
		this.batch = new SpriteBatch();
		this.touchPoint = new Vector3();
		this.playBounds = new Rectangle(125, 100, 70, 64);
	}

	TextureRegion logo = new TextureRegion(new Texture("CSlogo.png"), 512, 256);
	TextureRegion start = new TextureRegion(new Texture("start.png"), 512, 256);
	TextureRegion startOnTouch = new TextureRegion(new Texture("start_on_touch.png"), 512, 256);
	TextureRegion background = new TextureRegion(new Texture("background.png"), 1024, 1024);
	TextureRegion company = new TextureRegion(new Texture("LGlogo.png"), 512, 256);

	long startClicked = -1;
	
	@Override
	public void render(float delta) {
		if ( startClicked != -1 && TimeUtils.millis() - startClicked > 1000){
			game.setScreen(new GameScreen(game));
			return;
		}
		GLCommon gl = Gdx.gl;
		gl.glClearColor(1, 0, 0, 1);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		camera.update();
		batch.setProjectionMatrix(camera.combined);

		batch.enableBlending();
		batch.begin();
		batch.draw(background, -20, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
		batch.draw(logo, 95, 300, 140, 128);

		if ( startClicked == -1 ){
			batch.draw(start, 125, 100, 70, 64);
		}
		else{
			batch.draw(startOnTouch, 125, 100, 70, 64);			
		}
		batch.end();
		
		if (Gdx.input.justTouched()) {
			camera.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));

			if (playBounds.contains(touchPoint.x, touchPoint.y)) {
				startClicked = TimeUtils.millis();
			}
		}

	}
	
	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}
