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
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
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
	public static ShadowsOfTheNight game;
	private static List<Shadow> shadowsToRemove = new LinkedList<>();
	public static Chrono chrono;
	private Label chronoLabel;
	private Label deathLabel;
	private static LabelStyle labelStyle;
	private static LabelStyle normalLabelStyle;
	private Stage hud;
	private int shadowsKilled;
	private int shadowsMissed;
	private boolean endGameNextFrame;


	public ShadowsOfTheNight() { game = this; }

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

		BitmapFont bmf = new BitmapFont(Gdx.files.internal("fonts/bloody.fnt"));
		labelStyle = new Label.LabelStyle(bmf, Color.WHITE);
		BitmapFont nbmf = new BitmapFont(Gdx.files.internal("fonts/dominican.fnt"));
		normalLabelStyle = new Label.LabelStyle(nbmf, Color.WHITE);


		playGame1();
	}

	public void playGame1() {
		endGameNextFrame = false;
		if (stage2 != null) {
			stage2.dispose();
			stage2 = null;
		}
		shadowsKilled = 0;
		shadowsMissed = 0;
		stage1 = new Stage(viewport, batch);
		Image backgroundImage = new Image(new Texture("images/MainImage.png"));
		backgroundImage.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		stage1.addActor(backgroundImage);
		shadows = new LinkedList<>();
		for (int i = 0; i < 1; i++) {
			Shadow shadow = new Shadow();
			shadow.setScale(0.1f);
			shadow.setPosition(Gdx.graphics.getWidth() - 100 * getWidthRacio(), 100 * getHeightRacio());
			shadows.add(shadow);
			stage1.addActor(shadow);
			shadow.isPlayer = true;
		}
		stage1.addActor(new Obstacle("wall", 0, 0, Gdx.graphics.getWidth(), 10));
		// stage1.addActor(new Obstacle("wall", Gdx.graphics.getHeight() - 10, Gdx.graphics.getWidth(), 10, 0));
		stage1.addActor(new Obstacle("wall", 0, 0, 10, Gdx.graphics.getHeight()));
		stage1.addActor(new Obstacle("wall", Gdx.graphics.getWidth() - 10, 0, 10, Gdx.graphics.getHeight()));
		stage1.addActor(new Obstacle(600 * getWidthRacio(), Gdx.graphics.getHeight() - 170 * getHeightRacio(), 500 * getWidthRacio(),
				170 * getHeightRacio()));
		stage1.addActor(new Obstacle(650 * getWidthRacio(), 320 * getHeightRacio(), 150 * getWidthRacio(), 150 * getHeightRacio()));
		stage1.addActor(new Bed(0, 300 * getHeightRacio(), 300 * getWidthRacio(), 500 * getHeightRacio()));

		addLigth();
		world.setContactListener(new BedShadowContactListener());

		hud = new Stage(viewport, batch);
		int minOfGame = 10; // TODO swap to 5.
		chrono = new Chrono(minOfGame * 60 * 1000, 20, 7);
		chronoLabel = new Label(chrono.getCurrentHour(), labelStyle) {
			@Override
			public void act(float delta) {
				super.act(delta);
				setText(chrono.getCurrentHour());
				float light = (float) (0.1f + ambiantLight * (1 - Math.sqrt(chrono.getPercentElapsedTime())));
				if (chrono.getPercentElapsedTime() >= 1f) {
					light = 0.1f + ambiantLight;
					endGameNextFrame = true;
				}
				rayHandler.setAmbientLight(0.1f, 0.01f, 0.01f, light);
			}
		};
		chronoLabel.setPosition(Gdx.graphics.getWidth() - 250f, Gdx.graphics.getHeight() - 100f);
		hud.addActor(chronoLabel);
		deathLabel = new Label("0", labelStyle) {
			@Override
			public void act(float delta) {
				super.act(delta);
				setText(shadowsKilled);
			}
		};
		deathLabel.setPosition(0, Gdx.graphics.getHeight() - 100f);
		hud.addActor(deathLabel);

	}

	public void addLigth() {
		rayHandler = new RayHandler(world);
		rayHandler.setBlur(true);
		rayHandler.setBlurNum(3);

		rayHandler.setShadows(true);
		// pl.setStaticLight(false);
		// pl.setSoft(true);
		ConeLight cl = new ConeLight(rayHandler, 128, new Color(1, 250f / 255f, 204f / 255f, 0.8f), 10000, 0, 0, 0, 4);
		cl.setPosition(50 * getWidthRacio(), 500 * getHeightRacio());
		cl.setActive(true);
		cl.setSoft(true);
		// cl.setStaticLight(false);
		cl.setSoftnessLength(2f);
		// TODO react to light intersect with shadow
		// TODO shadow AI try to avoid light if it's to close from them.
	}

	public static float getWidthRacio() { return Gdx.graphics.getWidth() / 1920f; }

	public static float getHeightRacio() { return Gdx.graphics.getHeight() / 1080f; }

	@Override
	public void render() {
		// ScreenUtils.clear(0, 0, 1, 1);
		Gdx.gl.glClearColor(0f, 0f, 0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if (endGameNextFrame) {
			endGame();
			endGameNextFrame = false;
		}
		if (stage1 != null) {
			stage1.act();
			stage1.draw();
		}
		if (stage2 != null) {
			stage2.act();
			stage2.draw();
		}
		if (world != null) {
			world.step(1 / 60f, 6, 2);
			debugRenderer.render(world, camera.combined);
		}

		if (stage1 != null) {
			rayHandler.setCombinedMatrix(stage1.getCamera().combined, 0, 0, 1, 1);
			rayHandler.updateAndRender();
		}

		hud.act();
		hud.draw();

		for (Shadow shadow : shadowsToRemove) {
			shadow.remove();
			Gdx.app.log("BedShadowContactListener", "Shadow removed");
		}
		shadowsToRemove.clear();
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

	public void addShadowToRemove(Shadow shadow, boolean killed) {
		shadowsToRemove.add(shadow);
		if (killed) {
			shadowsKilled++;
			// TODO play death sound
		} else {
			shadowsMissed++;
			// if() there is an item to stole from bed, stole it
			// else lose game
			endGameNextFrame = true;
			// TODO play stole from bed sound
		}
	}

	public void endGame() {
		boolean win = chrono.isFinished();
		stage2 = new Stage(viewport, batch);
		Table table = new Table();
		table.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		stage2.addActor(table);
		chrono.stop();
		if (win) {
			// TODO play win sound
			table.add(new Label("You survived until sun rise!", labelStyle)).row();;
			table.add(new Label("Click anywhere to play again", labelStyle));
		} else {
			// TODO play loose sound
			// black screen
			stage1.dispose();
			stage1 = null;
			world.dispose();
			world = null;
			table.add(new Label("You survived until " + chrono.getCurrentHour(), labelStyle)).row();;
			table.add(new Label("Click anywhere to retry", labelStyle));
		}
		// TODO if user click anywhere it should restart the game

	}

}
