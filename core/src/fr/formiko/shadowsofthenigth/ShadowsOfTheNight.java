package fr.formiko.shadowsofthenigth;

import fr.formiko.shadowsofthenigth.tools.Assets;
import java.util.LinkedList;
import java.util.List;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import box2dLight.ConeLight;
import box2dLight.RayHandler;

public class ShadowsOfTheNight extends ApplicationAdapter {
	public static Camera camera;
	private ScreenViewport viewport;
	private SpriteBatch batch;
	private Stage stage1;
	private Stage stage2;
	private static Assets assets;
	private List<Shadow> shadows;
	public static World world;
	private Box2DDebugRenderer debugRenderer;
	private RayHandler rayHandler;
	private float ambiantLight = 0.4f;

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

		rayHandler = new RayHandler(world);
		rayHandler.setAmbientLight(0.1f, 0.01f, 0.01f, ambiantLight);
		rayHandler.setBlur(true);
		rayHandler.setBlurNum(3);
		// rayHandler.useDiffuseLight(true);
		// rayHandler.setBlurNum(3);

		// PointLight pl = new PointLight(rayHandler, 128, new Color(0.2f, 1, 1, 1f), 10, -5, 2);
		// PointLight pl2 = new PointLight(rayHandler, 128, new Color(1, 0, 1, 1f), 10, 5, 2);
		// PointLight light = new PointLight(rayHandler, 32);
		// light.setActive(true);
		// light.setColor(Color.RED);
		// light.setDistance(400f);
		// light.setPosition(0, 0);

		rayHandler.setShadows(true);
		// pl.setStaticLight(false);
		// pl.setSoft(true);
		ConeLight cl = new ConeLight(rayHandler, 128, new Color(1, 250f / 255f, 204f / 255f, 0.8f), 10000, 0, 0, 0, 4);
		cl.setPosition(50, 500);
		cl.setActive(true);
		cl.setSoft(true);
		// cl.setStaticLight(false);
		cl.setSoftnessLength(2f);
		// TODO react to light intersect with shadow

		// cl.setSoft(true);
		// cl.setSoftnessLength(0.5f);

		// Add a light to the stage

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
		stage1.addActor(new Obstacle("wall", Gdx.graphics.getWidth(), 10, 0, 0));
		stage1.addActor(new Obstacle("wall", Gdx.graphics.getWidth(), 10, 0, Gdx.graphics.getHeight() - 10));
		stage1.addActor(new Obstacle("wall", 10, Gdx.graphics.getHeight(), 0, 0));
		stage1.addActor(new Obstacle("wall", 10, Gdx.graphics.getHeight(), Gdx.graphics.getWidth() - 10, 0));
		stage1.addActor(new Obstacle(500, 150, 600, Gdx.graphics.getHeight() - 150));
		stage1.addActor(new Obstacle(150, 150, 650, 320));
	}

	@Override
	public void render() {
		// ScreenUtils.clear(0, 0, 1, 1);
		Gdx.gl.glClearColor(0f, 0f, 0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if (stage1 != null) {
			stage1.act();
			stage1.draw();
		} else if (stage2 != null) {
			stage2.act();
			stage2.draw();
		}
		world.step(1 / 60f, 6, 2);
		debugRenderer.render(world, camera.combined);

		rayHandler.setCombinedMatrix(stage1.getCamera().combined, 0, 0, 1, 1);
		rayHandler.updateAndRender();
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
