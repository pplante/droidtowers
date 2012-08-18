/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers;

import aurelienribon.tweenengine.Tween;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.happydroids.droidtowers.achievements.AchievementEngine;
import com.happydroids.droidtowers.actions.ActionManager;
import com.happydroids.droidtowers.actions.TimeDelayedAction;
import com.happydroids.droidtowers.audio.GameSoundController;
import com.happydroids.droidtowers.controllers.PathSearchManager;
import com.happydroids.droidtowers.entities.GameObject;
import com.happydroids.droidtowers.events.InGamePurchaseReceiver;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;
import com.happydroids.droidtowers.generators.NameGenerator;
import com.happydroids.droidtowers.gui.FontManager;
import com.happydroids.droidtowers.gui.WidgetAccessor;
import com.happydroids.droidtowers.input.*;
import com.happydroids.droidtowers.platform.Display;
import com.happydroids.droidtowers.scenes.ApplicationResumeScene;
import com.happydroids.droidtowers.scenes.LaunchUriScene;
import com.happydroids.droidtowers.scenes.MainMenuScene;
import com.happydroids.droidtowers.scenes.Scene;
import com.happydroids.droidtowers.scenes.components.SceneManager;
import com.happydroids.droidtowers.server.MovieServer;
import com.happydroids.droidtowers.tasks.MigrateExistingGamesTask;
import com.happydroids.droidtowers.tasks.SyncCloudGamesTask;
import com.happydroids.droidtowers.tween.GameObjectAccessor;
import com.happydroids.droidtowers.tween.TweenSystem;
import com.happydroids.droidtowers.types.*;
import com.happydroids.platform.Platform;
import com.happydroids.security.SecurePreferences;
import com.happydroids.utils.BackgroundTask;

import static com.badlogic.gdx.Application.ApplicationType.*;
import static com.happydroids.HappyDroidConsts.DEBUG;
import static com.happydroids.HappyDroidConsts.DISPLAY_DEBUG_INFO;

public class DroidTowersGame implements ApplicationListener, BackgroundTask.PostExecuteManager {
  private static final String TAG = DroidTowersGame.class.getSimpleName();

  private SpriteBatch spriteBatch;
  private static Stage rootUiStage;
  private SpriteBatch spriteBatchFBO;
  private FrameBuffer frameBuffer;
  private static GameSoundController soundController;
  private final Runnable afterInitRunnable;
  private final StringBuilder debugInfo;
  private float timeUntilDebugInfoUpdate;


  public DroidTowersGame(Runnable afterInitRunnable) {
    this.afterInitRunnable = afterInitRunnable;
    debugInfo = new StringBuilder();
    TowerGameService.instance();
  }

  public void create() {
    if (afterInitRunnable != null) {
      afterInitRunnable.run();
    }

    Thread.currentThread().setUncaughtExceptionHandler(Platform.uncaughtExceptionHandler);

    Gdx.app.error("lifecycle", "create");
    if (Gdx.app.getType().equals(Desktop)) {
      SecurePreferences displayPrefs = TowerGameService.instance().getPreferences();
      if (displayPrefs.contains("width") && displayPrefs.contains("height") && displayPrefs.contains("fullscreen")) {
        Gdx.graphics
                .setDisplayMode(displayPrefs.getInteger("width"), displayPrefs.getInteger("height"), displayPrefs.getBoolean("fullscreen"));
      } else {
        Gdx.graphics.setDisplayMode(960, 540, false);
      }
    }

    Display.setup();

    BackgroundTask.setPostExecuteManager(this);
    BackgroundTask.setUncaughtExceptionHandler(Platform.uncaughtExceptionHandler);

    TowerGameService.setInstance(new TowerGameService());

    if (Gdx.graphics.isGL20Available() && Gdx.app.getType().equals(Android)) {
      if (Display.isXHDPIMode()) {
//        float displayScalar = 0.75f;
//        frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, (int) (Display.getWidth() * displayScalar), (int) (Display.getHeight() * displayScalar), true);
//        spriteBatchFBO = new SpriteBatch();
      } else if (Display.isInCompatibilityMode()) {
        frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Display.getWidth(), Display.getHeight(), true);
        spriteBatchFBO = new SpriteBatch();
      }
    }

    if (DEBUG) {
      Gdx.app.error("DEBUG", "Debug mode is enabled!");
      Gdx.app.setLogLevel(Application.LOG_DEBUG);
    } else {
      Gdx.app.setLogLevel(Application.LOG_ERROR);
    }

    TowerAssetManager.assetManager();

    ActionManager.instance().addAction(new TimeDelayedAction(1f) {
      @Override
      public void run() {
        soundController = new GameSoundController();
        markToRemove();
      }
    });

    new MigrateExistingGamesTask().run();

    TowerGameService.instance().afterDeviceIdentification(new Runnable() {
      @Override
      public void run() {
        if (!TowerGameService.instance().isAuthenticated()) {
          return;
        }

        new SyncCloudGamesTask().run();
      }
    });

    Platform.getPurchaseManager().events().register(new InGamePurchaseReceiver());

    NameGenerator.initialize();
    RoomTypeFactory.instance();
    CommercialTypeFactory.instance();
    ServiceRoomTypeFactory.instance();
    ElevatorTypeFactory.instance();
    StairTypeFactory.instance();

    AchievementEngine.instance();
    Tween.setCombinedAttributesLimit(4);
    Tween.registerAccessor(CameraController.class, new CameraControllerAccessor());
    Tween.registerAccessor(GameObject.class, new GameObjectAccessor());
    Tween.registerAccessor(Actor.class, new WidgetAccessor());

    spriteBatch = new SpriteBatch();
    rootUiStage = new Stage(Display.getWidth(), Display.getHeight(), false, spriteBatch);

    Gdx.input.setInputProcessor(InputSystem.instance());
    InputSystem.instance().addInputProcessor(rootUiStage, 0);

    if (DEBUG) {
      InputSystem.instance().addInputProcessor(new DebugInputAdapter(), 1000);
    }

    InputSystem.instance().addInputProcessor(new QuitGameInputAdapter(), 1000000);

    Scene.setSpriteBatch(spriteBatch);

    Gdx.app.postRunnable(new Runnable() {
      @Override
      public void run() {
        if (Platform.protocolHandler != null && Platform.protocolHandler.hasUri()) {
          SceneManager.changeScene(LaunchUriScene.class, Platform.protocolHandler.consumeUri());
        } else {
          SceneManager.changeScene(MainMenuScene.class);
        }

        if (!Gdx.app.getType().equals(Applet)) {
          Platform.getConnectionMonitor().withConnection(new Runnable() {
            @Override
            public void run() {
              new RegisterDeviceTask().run();
            }
          });
        }
      }
    });
  }

  public void render() {
    Gdx.gl.glClearColor(0.48f, 0.729f, 0.870f, 1.0f);
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
    Gdx.gl.glEnable(GL10.GL_TEXTURE_2D);

    float deltaTime = Gdx.graphics.getDeltaTime();

    ActionManager.instance().update(deltaTime);
    InputSystem.instance().update(deltaTime);
    PathSearchManager.instance().update(deltaTime);
    TweenSystem.manager().update((int) (deltaTime * 1000 * SceneManager.activeScene().getTimeMultiplier()));
    if (soundController != null) {
      soundController.update(deltaTime);
    }

    SceneManager.activeScene().getCamera().update(true);
    spriteBatch.setProjectionMatrix(SceneManager.activeScene().getCamera().combined);

    if (frameBuffer != null) {
      frameBuffer.begin();
      Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
      SceneManager.activeScene().render(deltaTime);
      frameBuffer.end();

      spriteBatchFBO.begin();
      spriteBatchFBO.draw(frameBuffer.getColorBufferTexture(), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics
                                                                                                      .getHeight(), 0, 0, 1, 1);
      spriteBatchFBO.end();
    } else {
      SceneManager.activeScene().render(deltaTime);
    }

    SceneManager.activeScene().getStage().act(deltaTime);
    SceneManager.activeScene().getStage().draw();

    if (rootUiStage.getActors().size > 0) {
      rootUiStage.act(deltaTime);
      rootUiStage.draw();
    }

    //noinspection PointlessBooleanExpression
    if (DEBUG && DISPLAY_DEBUG_INFO) {
      Table.drawDebug(SceneManager.activeScene().getStage());
      Table.drawDebug(rootUiStage);

      float javaHeapInBytes = Gdx.app.getJavaHeap() / TowerConsts.ONE_MEGABYTE;
      float nativeHeapInBytes = Gdx.app.getNativeHeap() / TowerConsts.ONE_MEGABYTE;

      timeUntilDebugInfoUpdate -= deltaTime;
      if (timeUntilDebugInfoUpdate <= 0f) {
        timeUntilDebugInfoUpdate = 3f;
        debugInfo.delete(0, debugInfo.length());
        debugInfo.append("fps: ");
        debugInfo.append(Gdx.graphics.getFramesPerSecond());
        debugInfo.append("\nmem: (java ");
        debugInfo.append((int) javaHeapInBytes);
        debugInfo.append("Mb, heap: ");
        debugInfo.append((int) nativeHeapInBytes);
        debugInfo.append("Mb, gpu: ");
        debugInfo.append((int) TowerAssetManager.assetManager().getMemoryInMegabytes());
        debugInfo.append("Mb)");

        debugInfo.append(" psm: ");
        debugInfo.append(PathSearchManager.instance().queueLength());
      }
      spriteBatch.begin();
      BitmapFont font = FontManager.Roboto12.getFont();
      font.setColor(Color.BLACK);
      font.drawMultiLine(spriteBatch, debugInfo, 6, 35);
      font.setColor(Color.CYAN);
      font.drawMultiLine(spriteBatch, debugInfo, 5, 36);
      font.setColor(Color.WHITE);
      spriteBatch.end();
    }
  }

  public void resize(int width, int height) {
    Gdx.app.log("lifecycle", "resizing!");
    if (SceneManager.activeScene() != null) {
      SceneManager.activeScene().getCamera().viewportWidth = width;
      SceneManager.activeScene().getCamera().viewportHeight = height;
      SceneManager.activeScene().getSpriteBatch().getProjectionMatrix().setToOrtho2D(0, 0, width, height);
      Gdx.gl.glViewport(0, 0, width, height);
      spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
    }
  }


  public void pause() {
    Gdx.app.error("lifecycle", "pausing!");
    SceneManager.activeScene().pause();
  }

  public void resume() {
    Gdx.app.error("lifecycle", "resuming!");

    FontManager.resetAll();

    SceneManager.pushScene(ApplicationResumeScene.class);
  }

  public void dispose() {
    Gdx.app.error("lifecycle", "dispose");
    SceneManager.activeScene().dispose();

    rootUiStage = null;

    SceneManager.dispose();
    spriteBatch.dispose();
    Platform.getConnectionMonitor().dispose();
    PathSearchManager.instance().dispose();
    BackgroundTask.dispose();
    MovieServer.dispose();
    TowerAssetManager.dispose();
    FontManager.resetAll();

    Platform.dispose();
    System.exit(0);
  }

  public static Stage getRootUiStage() {
    return rootUiStage;
  }


  public static GameSoundController getSoundController() {
    return soundController;
  }

  public void postRunnable(Runnable runnable) {
    Gdx.app.postRunnable(runnable);
  }

}
