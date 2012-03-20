package com.unhappyrobot;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.unhappyrobot.achievements.AchievementEngine;
import com.unhappyrobot.controllers.GameTips;
import com.unhappyrobot.controllers.PathSearchManager;
import com.unhappyrobot.gamestate.GameState;
import com.unhappyrobot.gamestate.server.HappyDroidService;
import com.unhappyrobot.input.DefaultKeybindings;
import com.unhappyrobot.input.InputSystem;
import com.unhappyrobot.scenes.GameScreen;
import com.unhappyrobot.scenes.SplashScreen;
import com.unhappyrobot.tween.TweenSystem;
import com.unhappyrobot.utils.AsyncTask;

public class TowerGame implements ApplicationListener {
  private static OrthographicCamera camera;
  private SpriteBatch spriteBatch;

  private Matrix4 matrix;

  private GameState gameState;
  private boolean loadedSavedGame;
  private FileHandle gameSaveLocation;
  private long nextGameStateSaveTime;
  private final String operatingSystemName;
  private final String operatingSystemVersion;
  private SplashScreen splashScreen;
  private GameScreen gameScreen;
  private BitmapFont menloBitmapFont;

  public TowerGame(String operatingSystemName, String operatingSystemVersion) {
    this.operatingSystemName = operatingSystemName;
    this.operatingSystemVersion = operatingSystemVersion;
  }


  public void create() {
    Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

    new AsyncTask() {
      @Override
      public void execute() {
        HappyDroidService.instance().setDeviceOSName(operatingSystemName);
        HappyDroidService.instance().setDeviceOSVersion(operatingSystemVersion);
        HappyDroidService.instance().registerDevice();
      }
    }.run();


    TweenSystem.getTweenManager();
    AchievementEngine.instance();
    GameTips.instance();

    menloBitmapFont = new BitmapFont(Gdx.files.internal("fonts/menlo_14_bold_white.fnt"), false);
    gameSaveLocation = Gdx.files.external(Gdx.app.getType().equals(Application.ApplicationType.Desktop) ? ".towergame/test.json" : "test.json");

    camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    spriteBatch = new SpriteBatch(100);
    gameScreen = new GameScreen(spriteBatch, camera, gameSaveLocation);

    splashScreen = new SplashScreen(spriteBatch);
    splashScreen.create();

//    BEGIN INPUT SETUP:
    InputSystem.instance().setup(camera, null);
    Gdx.input.setInputProcessor(InputSystem.instance());

    DefaultKeybindings.initialize(this);


    gameScreen.create();
  }

  public void render() {
    Gdx.gl.glClearColor(0.48f, 0.729f, 0.870f, 1.0f);
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
    Gdx.gl.glEnable(GL10.GL_BLEND);
    Gdx.gl.glEnable(GL10.GL_TEXTURE_2D);

    float deltaTime = Gdx.graphics.getDeltaTime();

    camera.update();
    InputSystem.instance().update(deltaTime);
    PathSearchManager.instance().update(deltaTime);
    TweenSystem.getTweenManager().update((int) (deltaTime * 1000));
    spriteBatch.setProjectionMatrix(camera.combined);

    gameScreen.render(deltaTime);
    gameScreen.getStage().draw();
//    splashScreen.render(deltaTime);

    float javaHeapInBytes = Gdx.app.getJavaHeap() / TowerConsts.ONE_MEGABYTE;
    float nativeHeapInBytes = Gdx.app.getNativeHeap() / TowerConsts.ONE_MEGABYTE;

    String infoText = String.format("fps: %02d, camera(%.1f, %.1f, %.1f)\nmem: (java %.1f Mb, native %.1f Mb)", Gdx.graphics.getFramesPerSecond(), camera.position.x, camera.position.y, camera.zoom, javaHeapInBytes, nativeHeapInBytes);
    spriteBatch.begin();
    menloBitmapFont.drawMultiLine(spriteBatch, infoText, 5, 35);
    spriteBatch.end();
  }


  public void resize(int width, int height) {
    Gdx.app.log("lifecycle", "resizing!");
    camera.viewportWidth = width;
    camera.viewportHeight = height;
    spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
    Gdx.gl.glViewport(0, 0, width, height);
  }

  public void pause() {
    Gdx.app.log("lifecycle", "pausing!");
    gameScreen.pause();
    System.exit(0);
  }

  public void resume() {
    Gdx.app.log("lifecycle", "resuming!");
  }

  public void dispose() {
    spriteBatch.dispose();
  }

}
