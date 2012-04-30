/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.droidtowers;

import aurelienribon.tweenengine.Tween;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.google.common.collect.Lists;
import com.happydroids.HappyDroidConsts;
import com.happydroids.droidtowers.achievements.AchievementEngine;
import com.happydroids.droidtowers.actions.ActionManager;
import com.happydroids.droidtowers.controllers.PathSearchManager;
import com.happydroids.droidtowers.entities.GameObject;
import com.happydroids.droidtowers.gamestate.server.TowerGameService;
import com.happydroids.droidtowers.gui.*;
import com.happydroids.droidtowers.input.CameraController;
import com.happydroids.droidtowers.input.CameraControllerAccessor;
import com.happydroids.droidtowers.input.InputCallback;
import com.happydroids.droidtowers.input.InputSystem;
import com.happydroids.droidtowers.platform.PlatformBrowserUtil;
import com.happydroids.droidtowers.platform.PlatformProtocolHandler;
import com.happydroids.droidtowers.scenes.LaunchUriScene;
import com.happydroids.droidtowers.scenes.MainMenuScene;
import com.happydroids.droidtowers.scenes.Scene;
import com.happydroids.droidtowers.scenes.SplashScene;
import com.happydroids.droidtowers.tween.GameObjectAccessor;
import com.happydroids.droidtowers.tween.TweenSystem;
import com.happydroids.droidtowers.types.*;
import com.happydroids.utils.BackgroundTask;

import java.net.URI;
import java.util.LinkedList;

public class TowerGame implements ApplicationListener, BackgroundTask.PostExecuteManager {
  private static final String TAG = TowerGame.class.getSimpleName();

  private static OrthographicCamera camera;
  private SpriteBatch spriteBatch;
  private BitmapFont menloBitmapFont;
  private static Scene activeScene;
  private static Stage rootUiStage;
  private static boolean audioEnabled;
  private static Thread.UncaughtExceptionHandler uncaughtExceptionHandler;
  private static PlatformBrowserUtil platformBrowserUtil;
  private static LinkedList<Scene> pausedScenes;
  private PlatformProtocolHandler protocolHandler;

  public TowerGame() {
    audioEnabled = true;
    pausedScenes = Lists.newLinkedList();
  }

  public static boolean isAudioEnabled() {
    return audioEnabled;
  }

  public static void setAudioEnabled(boolean audioEnabled) {
    TowerGame.audioEnabled = audioEnabled;
  }

  public void create() {
    Gdx.app.error("lifecycle", "create");

    BackgroundTask.setPostExecuteManager(this);

    TowerGameService.setInstance(new TowerGameService());
//    TowerGameService.instance().resetAuthentication();

    if (HappyDroidConsts.DEBUG) {
      Gdx.app.error("DEBUG", "Debug mode is enabled!");
      Gdx.app.setLogLevel(Application.LOG_DEBUG);
    } else {
      Gdx.app.setLogLevel(Application.LOG_ERROR);
    }

    Thread.currentThread().setUncaughtExceptionHandler(uncaughtExceptionHandler);

    Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

    TowerAssetManager.assetManager();

    new BackgroundTask() {
      @Override
      protected void execute() {
        TowerGameService.instance().registerDevice();
      }
    }.run();

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

    menloBitmapFont = new BitmapFont(Gdx.files.internal("fonts/menlo_14_bold_white.fnt"), false);
    camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    spriteBatch = new SpriteBatch(100);
    rootUiStage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false, spriteBatch);

    InputSystem.instance().setup(camera);
    Gdx.input.setInputProcessor(InputSystem.instance());
    InputSystem.instance().addInputProcessor(rootUiStage, 0);

    if (HappyDroidConsts.DEBUG) {
      InputSystem.instance().addInputProcessor(new DebugInputAdapter(), 1000);
    }

    InputSystem.instance().bind(new int[]{InputSystem.Keys.BACK, InputSystem.Keys.ESCAPE}, new InputCallback() {
      public boolean run(float timeDelta) {
        final boolean mainMenuIsActive = activeScene instanceof MainMenuScene;

        new Dialog(rootUiStage)
                .setTitle("Awe, don't leave me.")
                .setMessage("Are you sure you want to exit " + (mainMenuIsActive ? "the game?" : "to the Main Menu?"))
                .addButton(ResponseType.POSITIVE, "Yes", new OnClickCallback() {
                  @Override
                  public void onClick(Dialog dialog) {
                    dialog.dismiss();
                    if (mainMenuIsActive) {
                      Gdx.app.exit();
                    } else {
                      changeScene(MainMenuScene.class);
                    }
                  }
                })
                .addButton(ResponseType.NEGATIVE, "No way!", new OnClickCallback() {
                  @Override
                  public void onClick(Dialog dialog) {
                    dialog.dismiss();
                  }
                })
                .show();

        return true;
      }
    });

    final Skin skin = new Skin(Gdx.files.internal("default-skin.ui"));
    Scene.setGuiSkin(skin);
    Scene.setCamera(camera);
    Scene.setSpriteBatch(spriteBatch);


    changeScene(SplashScene.class, SplashSceneStates.PRELOAD_ONLY, new Runnable() {
      public void run() {
        if (protocolHandler.hasUri()) {
          URI launchUri = protocolHandler.consumeUri();
          changeScene(LaunchUriScene.class, launchUri);
        } else {
          changeScene(MainMenuScene.class);
        }
      }
    });
  }

  public void render() {
    Gdx.gl.glClearColor(0.48f, 0.729f, 0.870f, 1.0f);
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
    Gdx.gl.glEnable(GL10.GL_BLEND);
    Gdx.gl.glEnable(GL10.GL_TEXTURE_2D);

    float deltaTime = Gdx.graphics.getDeltaTime();

    camera.update();
    ActionManager.instance().update(deltaTime);
    InputSystem.instance().update(deltaTime);
    PathSearchManager.instance().update(deltaTime);
    TweenSystem.getTweenManager().update((int) (deltaTime * 1000 * activeScene.getTimeMultiplier()));

    spriteBatch.setProjectionMatrix(camera.combined);

    activeScene.render(deltaTime);
    activeScene.getStage().act(deltaTime);
    activeScene.getStage().draw();

    rootUiStage.act(deltaTime);
    rootUiStage.draw();

    if (HappyDroidConsts.DEBUG) {
      Table.drawDebug(activeScene.getStage());
      Table.drawDebug(rootUiStage);

      float javaHeapInBytes = Gdx.app.getJavaHeap() / TowerConsts.ONE_MEGABYTE;
      float nativeHeapInBytes = Gdx.app.getNativeHeap() / TowerConsts.ONE_MEGABYTE;

      String infoText = String.format("fps: %02d, camera(%.1f, %.1f, %.1f)\nmem: (java %.1f Mb, native %.1f Mb, gpu %.1f Mb)",
                                             Gdx.graphics.getFramesPerSecond(),
                                             camera.position.x,
                                             camera.position.y,
                                             camera.zoom,
                                             javaHeapInBytes,
                                             nativeHeapInBytes,
                                             TowerAssetManager.assetManager().getMemoryInMegabytes());
      spriteBatch.begin();
      menloBitmapFont.drawMultiLine(spriteBatch, infoText, 5, 35);
      spriteBatch.end();
    }
  }

  public void resize(int width, int height) {
    Gdx.app.log("lifecycle", "resizing!");
    camera.viewportWidth = width;
    camera.viewportHeight = height;
    spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
    activeScene.getSpriteBatch().getProjectionMatrix().setToOrtho2D(0, 0, width, height);
    Gdx.gl.glViewport(0, 0, width, height);
  }


  public void pause() {
    Gdx.app.error("lifecycle", "pausing!");
    activeScene.pause();
  }

  public void resume() {
    Gdx.app.error("lifecycle", "resuming!");

    pushScene(SplashScene.class, SplashSceneStates.FULL_LOAD);

    if (protocolHandler.hasUri()) {
      URI launchUri = protocolHandler.consumeUri();
      changeScene(LaunchUriScene.class, launchUri);
    }
  }

  public void dispose() {
    Gdx.app.error("lifecycle", "dispose");
    activeScene.dispose();

    activeScene = null;
    pausedScenes = null;
    rootUiStage = null;
    camera = null;
    uncaughtExceptionHandler = null;
    platformBrowserUtil = null;

    spriteBatch.dispose();
    BackgroundTask.dispose();
    TowerAssetManager.dispose();
    FontManager.resetAll();

    System.exit(0);
  }

  public static void changeScene(Class<? extends Scene> sceneClass, Object... args) {
    if (HappyDroidConsts.DEBUG) System.out.println("Switching scene to: " + sceneClass.getSimpleName());

    popScene();
    pushScene(sceneClass, args);
  }

  public static void pushScene(Class<? extends Scene> sceneClass, Object... args) {
    try {
      if (activeScene != null) {
        activeScene.pause();
        InputSystem.instance().removeInputProcessor(activeScene.getStage());
        pausedScenes.push(activeScene);
      }

      activeScene = sceneClass.newInstance();
      activeScene.create(args);
      activeScene.resume();
      InputSystem.instance().addInputProcessor(activeScene.getStage(), 10);
    } catch (InstantiationException e) {
      throw new RuntimeException(e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  public static void popScene() {
    if (activeScene != null) {
      InputSystem.instance().removeInputProcessor(activeScene.getStage());
      activeScene.pause();
      activeScene.dispose();

      activeScene = null;
    }

    if (!pausedScenes.isEmpty()) {
      activeScene = pausedScenes.pop();
      activeScene.resume();
      InputSystem.instance().addInputProcessor(activeScene.getStage(), 10);
    } else {
      Gdx.app.error(TAG, "popScene says there are no more scenes.");
    }
  }

  public static Scene getActiveScene() {
    return activeScene;
  }

  public static Stage getRootUiStage() {
    return rootUiStage;
  }


  public void setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
    TowerGame.uncaughtExceptionHandler = uncaughtExceptionHandler;
  }

  public static Thread.UncaughtExceptionHandler getUncaughtExceptionHandler() {
    return uncaughtExceptionHandler;
  }

  public void setPlatformBrowserUtil(PlatformBrowserUtil platformBrowserUtil) {
    TowerGame.platformBrowserUtil = platformBrowserUtil;
  }

  public static PlatformBrowserUtil getPlatformBrowserUtil() {
    return platformBrowserUtil;
  }

  public void setProtocolHandler(PlatformProtocolHandler protocolHandler) {
    this.protocolHandler = protocolHandler;
  }

  public void postRunnable(Runnable runnable) {
    Gdx.app.postRunnable(runnable);
  }
}
