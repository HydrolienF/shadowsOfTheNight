package fr.formiko.shadowsofthenigth;

import fr.formiko.shadowsofthenigth.tools.Assets;
import java.util.LinkedList;
import java.util.List;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class ShadowsOfTheNight extends ApplicationAdapter {
	private Camera camera;
	private ScreenViewport viewport;
	private SpriteBatch batch;
	private Stage stage1;
	private Stage stage2;
	private static Assets assets;
	private List<Shadow> shadows;

	@Override
	public void create() {
		camera = new OrthographicCamera();
		viewport = new ScreenViewport(camera);
		batch = new SpriteBatch();
		assets = new Assets();
		stage1 = null;
		stage2 = null;
		playGame1();
	}

	public void playGame1() {
		stage1 = new Stage(viewport, batch);
		stage1.addActor(new Image(new Texture("images/MainImage.png")));
		shadows = new LinkedList<>();
		for (int i = 0; i < 10; i++) {
			Shadow shadow = new Shadow();
			shadow.setScale(0.1f);
			shadow.setPosition(Gdx.graphics.getWidth() - 100, 100);
			shadows.add(shadow);
			stage1.addActor(shadow);
		}
	}

	@Override
	public void render() {
		ScreenUtils.clear(0, 0, 1, 1);
		if (stage1 != null) {
			stage1.act();
			stage1.draw();
		} else if (stage2 != null) {
			stage2.act();
			stage2.draw();
		}
	}

	@Override
	public void dispose() {
		batch.dispose();
		if (stage1 != null)
			stage1.dispose();
		if (stage2 != null)
			stage2.dispose();
	}

	public static Assets getAssets() { return assets; }
}
