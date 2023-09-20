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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
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
	public static World world;
	private Box2DDebugRenderer debugRenderer;

	@Override
	public void create() {
		Box2D.init();
		world = new World(new Vector2(0, 0), true);
		debugRenderer = new Box2DDebugRenderer(); // @a
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
		for (int i = 0; i < 1; i++) {
			Shadow shadow = new Shadow();
			shadow.setScale(0.1f);
			shadow.setPosition(Gdx.graphics.getWidth() - 100, 100);
			shadows.add(shadow);
			stage1.addActor(shadow);
			shadow.isPlayer = true;
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
		world.step(1 / 60f, 6, 2);
		debugRenderer.render(world, camera.combined);
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
