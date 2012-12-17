package cannon.shuffle;


import cannon.shuffle.screen.MainMenuScreen;

import com.badlogic.gdx.Game;

public class CannonShuffle extends Game{
	
	@Override
	public void create() {
		setScreen(new MainMenuScreen(this));
	}

	@Override
	public void render() {
		super.render();
	}

	@Override
	public void dispose() {
		super.dispose();
		getScreen().dispose();
	}

}