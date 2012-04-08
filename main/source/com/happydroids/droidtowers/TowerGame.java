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
import com.happydroids.droidtowers.scenes.MainMenuScene;
import com.happydroids.droidtowers.scenes.Scene;
import com.happydroids.droidtowers.scenes.SplashScene;
import com.happydroids.droidtowers.tween.GameObjectAccessor;
import com.happydroids.droidtowers.tween.TweenSystem;
import com.happydroids.utils.BackgroundTask;

public class TowerGame implements ApplicationListener {
  private static OrthographicCamera camera;
  private SpriteBatch spriteBatch;
  private BitmapFont menloBitmapFont;
  private static Scene activeScene;
  private static Stage rootUiStage;
  private static boolean audioEnabled;
  private static Thread.UncaughtExceptionHandler uncaughtExceptionHandler;
  private static PlatformBrowserUtil platformBrowserUtil;

  public TowerGame() {
    audioEnabled = true;
  }

  public static boolean isAudioEnabled() {
    return audioEnabled;
  }

  public static void setAudioEnabled(boolean audioEnabled) {
    TowerGame.audioEnabled = audioEnabled;
  }

  public void create() {
    TowerGameService.setInstance(new TowerGameService());

    if (HappyDroidConsts.DEBUG) {
      Gdx.app.error("DEBUG", "Debug mode is enabled!");
      Gdx.app.setLogLevel(Application.LOG_DEBUG);
    } else {
      Gdx.app.setLogLevel(Application.LOG_ERROR);
    }

    Gdx.app.postRunnable(new Runnable() {
      public void run() {
        Thread.currentThread().setUncaughtExceptionHandler(uncaughtExceptionHandler);
      }
    });

    Thread.currentThread().setUncaughtExceptionHandler(uncaughtExceptionHandler);
    Thread.currentThread().setPriority(Thread.NORM_PRIORITY);

    TowerAssetManager.assetManager();

    BackgroundTask.setUncaughtExceptionHandler(uncaughtExceptionHandler);

    new BackgroundTask() {
      @Override
      public void execute() {
        ((TowerGameService) TowerGameService.instance()).registerDevice();
      }
    }.run();


    AchievementEngine.instance();
    Tween.setCombinedAttributesLimit(4);
    Tween.registerAccessor(CameraController.class, new CameraControllerAccessor());
    Tween.registerAccessor(GameObject.class, new GameObjectAccessor());
    Tween.registerAccessor(Actor.class, new WidgetAccessor());

    menloBitmapFont = new BitmapFont(Gdx.files.internal("fonts/menlo_14_bold_white.fnt"), false);
    camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    spriteBatch = new SpriteBatch(100);
    rootUiStage = new Stage(800, 480, true, spriteBatch);

    InputSystem.instance().setup(camera);
    Gdx.input.setInputProcessor(InputSystem.instance());
    InputSystem.instance().addInputProcessor(rootUiStage, 0);

    if (HappyDroidConsts.DEBUG) {
      InputSystem.instance().addInputProcessor(new DebugInputAdapter(), 1000);
    }

    InputSystem.instance().bind(new int[]{InputSystem.Keys.BACK, InputSystem.Keys.ESCAPE}, new InputCallback() {
      public boolean run(float timeDelta) {
        new Dialog(rootUiStage).setTitle("Awe, don't leave me.").setMessage("Are you sure you want to exit the game?").addButton(ResponseType.POSITIVE, "Yes", new OnClickCallback() {
          @Override
          public void onClick(Dialog dialog) {
            dialog.dismiss();
            if (activeScene instanceof MainMenuScene) {
              Gdx.app.exit();
            } else {
              changeScene(MainMenuScene.class);
            }
          }
        }).addButton(ResponseType.NEGATIVE, "No way!", new OnClickCallback() {
          @Override
          public void onClick(Dialog dialog) {
            dialog.dismiss();
          }
        }).show();

        return true;
      }
    });

    final Skin skin = new Skin(Gdx.files.internal("default-skin.ui"));
    Scene.setGuiSkin(skin);
    Scene.setCamera(camera);
    Scene.setSpriteBatch(spriteBatch);

    changeScene(SplashScene.class);
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

      String infoText = String.format("fps: %02d, camera(%.1f, %.1f, %.1f)\nmem: (java %.1f Mb, native %.1f Mb)", Gdx.graphics.getFramesPerSecond(), camera.position.x, camera.position.y, camera.zoom, javaHeapInBytes, nativeHeapInBytes);
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
    Gdx.gl.glViewport(0, 0, width, height);
  }


  public void pause() {
    Gdx.app.log("lifecycle", "pausing!");
    activeScene.pause();
  }

  public void resume() {
    Gdx.app.log("lifecycle", "resuming!");
    activeScene.resume();
  }

  public void dispose() {
    activeScene.dispose();
    spriteBatch.dispose();
    for (LabelStyle labelStyle : LabelStyle.values()) {
      labelStyle.reset();
    }
    BackgroundTask.dispose();
    TowerAssetManager.reset();
  }

  public static void changeScene(Class<? extends Scene> sceneClass, Object... args) {
    try {
      if (activeScene != null) {
        InputSystem.instance().removeInputProcessor(activeScene.getStage());
        activeScene.pause();
        activeScene.dispose();
      }

      System.out.println("Switching scene to: " + sceneClass.getSimpleName());
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
}
