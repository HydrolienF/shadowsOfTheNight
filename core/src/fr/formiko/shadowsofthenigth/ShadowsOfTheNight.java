package fr.formiko.shadowsofthenigth;

import fr.formiko.shadowsofthenigth.tools.Assets;
import fr.formiko.shadowsofthenigth.tools.Musics;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import box2dLight.Light;
import box2dLight.PointLight;
import box2dLight.RayHandler;

public class ShadowsOfTheNight extends ApplicationAdapter {
	public static Camera camera;
	public static Camera camera2;
	private ScreenViewport viewport;
	private SpriteBatch batch;
	public Stage stage1;
	private Stage stage2;
	private static Assets assets;
	private List<Shadow> shadows;
	public static World world;
	private Box2DDebugRenderer debugRenderer;
	private RayHandler rayHandler;
	private float ambiantLight = 0.2f;
	public static ShadowsOfTheNight game;
	private static List<Shadow> shadowsToRemove = new LinkedList<>();
	public static Chrono chrono;
	private Label chronoLabel;
	private Label deathLabel;
	private static LabelStyle labelStyle;
	private static LabelStyle labelStyle40;
	private Stage hud;
	private Stage menuStage;
	private int shadowsKilled;
	private int shadowsMissed;
	private boolean endGameNextFrame;
	private InputMultiplexer inputMultiplexer;
	private boolean playerIsShadow;
	public Bed bed;
	public int toysAtStart = 5;
	public int toysLeft;
	public BoyLight cl;
	public static float TIME_STEP = 1 / 60f;
	public static float PIXEL_PER_METER = 100f;
	private float lastPercentSpawnExtraShadow = 0;
	private float spawnEachXPercent = 0.0003f;


	public ShadowsOfTheNight() { game = this; }

	public void addProcessor(InputProcessor ip) { inputMultiplexer.addProcessor(ip); }
	public void removeProcessor(InputProcessor ip) { inputMultiplexer.removeProcessor(ip); }
	public boolean isPlayerShadow() { return playerIsShadow; }
	public boolean isPlayerBoy() { return !isPlayerShadow(); }

	@Override
	public void create() {
		Box2D.init();
		// debugRenderer = new Box2DDebugRenderer(); // @a
		camera = new OrthographicCamera();
		camera2 = new OrthographicCamera(Gdx.graphics.getWidth() / PIXEL_PER_METER, Gdx.graphics.getHeight() / PIXEL_PER_METER);
		camera2.position.set(camera2.viewportWidth / 2f, camera2.viewportHeight / 2f, 0);
		camera2.update();
		viewport = new ScreenViewport(camera);
		batch = new SpriteBatch();
		assets = new Assets();
		stage1 = null;
		stage2 = null;

		BitmapFont bmf = new BitmapFont(Gdx.files.internal("fonts/bloody.fnt"));
		labelStyle = new Label.LabelStyle(bmf, Color.WHITE);
		BitmapFont nbmf = new BitmapFont(Gdx.files.internal("fonts/bloody40.fnt"));
		labelStyle40 = new Label.LabelStyle(nbmf, Color.WHITE);

		// InputProcessor inputProcessor = new InputCore(this);
		inputMultiplexer = new InputMultiplexer();
		// inputMultiplexer.addProcessor(inputProcessor);
		Gdx.input.setInputProcessor(inputMultiplexer);

		// playerIsShadow = false; // TDOD swap to false
		displayMenu();
		// playGame1();
	}


	public void playGame1(boolean start) {
		lastPercentSpawnExtraShadow = 0;
		timeForNextMove = 0;
		if (menuStage != null && start) {
			menuStage.dispose();
			menuStage = null;
		}
		endGameNextFrame = false;
		world = new World(new Vector2(0, 0), true);
		if (stage2 != null && start) {
			stage2.dispose();
			removeProcessor(stage2);
			stage2 = null;
		}
		shadowsKilled = 0;
		shadowsMissed = 0;
		toysLeft = toysAtStart;
		stage1 = new Stage(viewport, batch);
		addProcessor(stage1);
		Image backgroundImage = new Image(new Texture("images/MainImage.png"));
		backgroundImage.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		stage1.addActor(backgroundImage);
		if (!start) {
			Image mom = new Image(new Texture("images/mom.png"));
			mom.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			stage1.addActor(mom);
		}
		shadows = new LinkedList<>();

		// walls
		if (start) {
			stage1.addActor(new Obstacle(0, 0, Gdx.graphics.getWidth(), 120 * getHeightRacio()));
			stage1.addActor(
					new Obstacle(0, Gdx.graphics.getHeight() - (120 * getHeightRacio()), Gdx.graphics.getWidth(), 120 * getHeightRacio()));
			stage1.addActor(new Obstacle(0, 0, 120 * getWidthRacio(), Gdx.graphics.getHeight()));
			stage1.addActor(
					new Obstacle(Gdx.graphics.getWidth() - (135 * getWidthRacio()), 0, 135 * getWidthRacio(), Gdx.graphics.getHeight()));
		}

		// meubles

		if (start) {
			stage1.addActor(new Obstacle(800 * getWidthRacio(), 120 * getHeightRacio(), 350 * getWidthRacio(), 125 * getHeightRacio()));
			stage1.addActor(new Obstacle(830 * getWidthRacio(), 780 * getHeightRacio(), 360 * getWidthRacio(), 200 * getHeightRacio()));
			stage1.addActor(new Obstacle(935 * getWidthRacio(), 680 * getHeightRacio(), 160 * getWidthRacio(), 120 * getHeightRacio()));
			stage1.addActor(new Obstacle(120 * getWidthRacio(), 780 * getHeightRacio(), 535 * getWidthRacio(), 300 * getHeightRacio()));
			bed = new Bed(50 * getWidthRacio(), 400 * getHeightRacio(), 450 * getWidthRacio(), 260 * getHeightRacio());
			stage1.addActor(bed);
		}

		addLigth(start);

		addToys();

		if (start) {
			world.setContactListener(new BedShadowContactListener());
			Musics.playGameMusic();

			spawnAShadow();
			addHud();
		}
	}
	public void playGame1() { playGame1(true); }

	private Stack<Image> toys = new Stack<>();
	public void addToys() {
		for (int i = 1; i < 6; i++) {
			Image toy = new Image(new Texture("images/toy" + i + ".png"));
			stage1.addActor(toy);
			toys.push(toy);
			toy.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		}
	}
	public void removeToy(int toyId) { toys.pop().remove(); }

	public void addHud() {
		hud = new Stage(viewport, batch);
		float minOfGame = 5f;
		// TODO do a x min long music named game.mp3 with all game music.
		chrono = new Chrono(((4 * 60) + 50) * 1000l, 20, 7);
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
				// light = 0.1f; // @a
				rayHandler.setAmbientLight(0.1f, 0.01f, 0.01f, light);

				// TODO respawn more shadow depending on the time
			}
		};
		chronoLabel.setPosition(Gdx.graphics.getWidth() - 250f, Gdx.graphics.getHeight() - 100f);
		hud.addActor(chronoLabel);
		deathLabel = new Label("0", labelStyle) {
			@Override
			public void act(float delta) {
				super.act(delta);
				setText(toysLeft + " toys left");
			}
		};
		deathLabel.setPosition(0, Gdx.graphics.getHeight() - 100f);
		hud.addActor(deathLabel);
	}

	public void spawnAShadow() {
		Shadow shadow = new Shadow((int)(500 * getWidthRacio()), playerIsShadow);
		shadow.setScale(0.1f * getWidthRacio());
		shadow.setPosition(Gdx.graphics.getWidth() - 200 * getWidthRacio(), MathUtils.random(200, 880) * getHeightRacio());
		shadows.add(shadow);
		stage1.addActor(shadow);
	}

	public void addLigth(boolean start) {
		rayHandler = new RayHandler(world);
		rayHandler.setBlur(true);
		rayHandler.setBlurNum(3);

		rayHandler.setShadows(true);
		// pl.setStaticLight(false);
		// pl.setSoft(true);
		if (start) {
			cl = new BoyLight(rayHandler);
			cl.setPosition(370 * getWidthRacio() / PIXEL_PER_METER, 480 * getHeightRacio() / PIXEL_PER_METER);
		} else {
			PointLight p1 = new PointLight(rayHandler, 128, new Color(1, 250f / 255f, 204f / 255f, 0.8f), 2000 / PIXEL_PER_METER, 0, 0);
			// p1.setPosition(Gdx.graphics.getWidth() - (200 * getWidthRacio()), 200 * getHeightRacio());
			p1.setPosition(1700 * getWidthRacio() / PIXEL_PER_METER, 200 * getHeightRacio() / PIXEL_PER_METER);
			// p1.setActive(true);
			PointLight p2 = new PointLight(rayHandler, 128, new Color(1, 250f / 255f, 204f / 255f, 0.8f), 500 / PIXEL_PER_METER, 0, 0);
			p2.setPosition(120 * getWidthRacio() / PIXEL_PER_METER, 350 * getHeightRacio() / PIXEL_PER_METER);

			rayHandler.setAmbientLight(0.1f, 0.01f, 0.01f, 0.2f);
		}
	}

	public static float getWidthRacio() { return Gdx.graphics.getWidth() / 1920f; }

	public static float getHeightRacio() { return Gdx.graphics.getHeight() / 1080f; }

	private float timeForNextMove;
	@Override
	public void render() {
		float delta = Gdx.graphics.getDeltaTime();
		// kill shadow with light
		for (Shadow shadow : shadows) {
			if (isShadowOnLight(shadow, cl)) {
				addShadowToRemove(shadow, true);
			}
		}
		if (cl != null) {
			cl.act();
		}

		// ScreenUtils.clear(0, 0, 1, 1);
		Gdx.gl.glClearColor(0f, 0f, 0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if (endGameNextFrame) {
			endGame();
			endGameNextFrame = false;
		}
		if (stage1 != null) {
			if (chrono != null && !playerIsShadow) {
				spawnExtraShadow();
			}
			stage1.act();
			stage1.draw();
		}
		if (stage2 != null) {
			stage2.act();
			stage2.draw();
		}
		if (world != null) {
			timeForNextMove += delta;
			while (timeForNextMove > ShadowsOfTheNight.TIME_STEP) {
				timeForNextMove -= ShadowsOfTheNight.TIME_STEP;
				world.step(TIME_STEP, 6, 2);
			}
			if (debugRenderer != null) {
				debugRenderer.render(world, camera2.combined);
			}
		}

		if (stage1 != null || menuStage != null) {
			rayHandler.setCombinedMatrix(camera2.combined, 0, 0, 1, 1);
			rayHandler.updateAndRender();
		}

		if (hud != null) {
			hud.act();
			hud.draw();
		}
		if (menuStage != null) {
			menuStage.act();
			menuStage.draw();
		}

		for (Shadow shadow : shadowsToRemove) {
			shadow.remove();
			shadows.remove(shadow);
			Gdx.app.log("BedShadowContactListener", "Shadow removed");
			if (shadow.isPlayer) { // || shadows.isEmpty()
				spawnAShadow();
			}
		}
		shadowsToRemove.clear();
	}

	public boolean isShadowOnLight(Shadow shadow, Light light) {
		// for the 8 point over the shadow body if ligth contains that point remove shadow. N, E, S, W, NE, SE, SW, NW
		for (Vector2 point : shadow.getHitPoints()) {
			if (light.contains(point.x, point.y)) {
				return true;
			}
		}
		return false;
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

	private long lastPlayLightOnghost = 0;
	public void addShadowToRemove(Shadow shadow, boolean killed) {
		shadowsToRemove.add(shadow);
		if (killed) {
			shadowsKilled++;
			if (lastPlayLightOnghost + 100 < System.currentTimeMillis()) {
				Musics.playSound("12 light_on_ghost");
				lastPlayLightOnghost = System.currentTimeMillis();
			}
		} else {
			shadowsMissed++;
			removeToy(toysLeft);
			toysLeft--;
			if (toysLeft <= 0) {
				endGameNextFrame = true;
			}
			Musics.playSound("toy stolen");
		}
	}

	public void endGame() {
		if (stage1 == null) {
			return;
		}
		Musics.play("16 death_sound");
		boolean boyWin = chrono.isFinished();
		stage2 = new Stage(viewport, batch);
		Table table = new Table();
		table.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		stage2.addActor(table);
		addProcessor(stage2);
		chrono.stop();
		String endText;
		String playAsShadowText;
		String playAsBoyText;
		if (boyWin) {
			if (playerIsShadow) {
				endText = "You didn't steal enough toys! " + toysLeft + " toys left";
			} else {
				endText = "You survived until sun rise!";
			}
		} else {
			if (playerIsShadow) {
				endText = "You managed to steal all toys and the flashlight from the boy!\n This family should leave your haunted house soon!";

			} else {
				endText = "You survived until " + chrono.getCurrentHour() + ".\n Mom was wrong, shadows eat children!";
			}
			// black screen
			stage1.dispose();
			removeProcessor(stage1);
			stage1 = null;
		}
		if (playerIsShadow) {
			playAsShadowText = "Click HERE to retry";
			playAsBoyText = "Click here to play as boy";
		} else {
			playAsShadowText = "Click HERE to play as shadow";
			playAsBoyText = "Click here to retry";
		}
		table.add(new Label(endText, labelStyle40)).row();
		Label playAsShadow = new Label(playAsShadowText, labelStyle40);
		playAsShadow.addListener(new ClickListener() {
			@Override
			public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
				playerIsShadow = true;
				playGame1();
			}
		});
		table.add(playAsShadow).row();
		Label playAsBoy = new Label(playAsBoyText, labelStyle40);
		playAsBoy.addListener(new ClickListener() {
			@Override
			public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
				playerIsShadow = false;
				playGame1();
			}
		});
		table.add(playAsBoy).row();
	}

	public void displayMenu() {
		Musics.play("01 Haunting Shadows (Menu)");
		Musics.setLooping(true);
		menuStage = new Stage(viewport, batch);

		Table table = new Table();
		table.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		table.setPosition(100 * getWidthRacio(), 0);
		menuStage.addActor(table);
		addProcessor(menuStage);
		table.add(new Label("Mom: Good night my little knight.", labelStyle40)).row();
		table.add(new Label("Boy: Mom I can't sleep, there is the same shadows that kill Lili.", labelStyle40)).row();
		table.add(new Label("Mom: Shadows don't eat children. Your sister is studying in Poland.", labelStyle40)).row();
		table.add(new Label("Mom: Don't worry, I checked under your bed.", labelStyle40)).row();
		table.add(new Label("Mom: If you see something just turn your light on for a minute.", labelStyle40)).row();
		table.add(new Label(" ", labelStyle40)).row();


		Label playAsShadow = new Label("Click HERE to play as the boy", labelStyle40);
		playAsShadow.addListener(new ClickListener() {
			@Override
			public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
				menuStage.clear();
				menuStage = null;
				playerIsShadow = false;
				Musics.playSound("09 flashlight_off");
				playGame1();
			}
		});
		table.add(playAsShadow).row();
		Label playAsBoy = new Label("Click HERE to play as a shadow", labelStyle40);
		playAsBoy.addListener(new ClickListener() {
			@Override
			public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
				menuStage.clear();
				menuStage = null;
				playerIsShadow = true;
				Musics.playSound("09 flashlight_off");
				playGame1();
			}
		});
		table.add(playAsBoy).row();
		table.add(new Label("(Use arrows to move)", labelStyle40)).row();

		playGame1(false);
	}

	private void spawnExtraShadow() {
		if (lastPercentSpawnExtraShadow + spawnEachXPercent < Math.pow(chrono.getPercentElapsedTime(), 2)) {
			System.out.println(
					"spawn extra shadow at " + chrono.getPercentElapsedTime() + " (" + Math.pow(chrono.getPercentElapsedTime(), 2) + ")");
			lastPercentSpawnExtraShadow += spawnEachXPercent;
			spawnAShadow();
		}
	}

}
