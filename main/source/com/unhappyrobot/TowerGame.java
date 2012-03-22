package com.unhappyrobot;

import aurelienribon.tweenengine.Tween;
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
import com.unhappyrobot.achievements.AchievementEngine;
import com.unhappyrobot.actions.ActionManager;
import com.unhappyrobot.controllers.PathSearchManager;
import com.unhappyrobot.entities.GameObject;
import com.unhappyrobot.gamestate.server.HappyDroidService;
import com.unhappyrobot.gui.Dialog;
import com.unhappyrobot.gui.OnClickCallback;
import com.unhappyrobot.gui.ResponseType;
import com.unhappyrobot.gui.WidgetAccessor;
import com.unhappyrobot.input.CameraController;
import com.unhappyrobot.input.CameraControllerAccessor;
import com.unhappyrobot.input.InputCallback;
import com.unhappyrobot.input.InputSystem;
import com.unhappyrobot.scenes.Scene;
import com.unhappyrobot.scenes.SplashScene;
import com.unhappyrobot.tween.GameObjectAccessor;
import com.unhappyrobot.tween.TweenSystem;
import com.unhappyrobot.utils.AsyncTask;

public class TowerGame implements ApplicationListener {
  private static OrthographicCamera camera;
  private SpriteBatch spriteBatch;
  private final String operatingSystemName;
  private final String operatingSystemVersion;
  private BitmapFont menloBitmapFont;
  private static Scene activeScene;
  private Stage rootUiStage;

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


    AchievementEngine.instance();
    Tween.setCombinedAttributesLimit(4);
    Tween.registerAccessor(CameraController.class, new CameraControllerAccessor());
    Tween.registerAccessor(GameObject.class, new GameObjectAccessor());
    Tween.registerAccessor(Actor.class, new WidgetAccessor());

    menloBitmapFont = new BitmapFont(Gdx.files.internal("fonts/menlo_14_bold_white.fnt"), false);
    camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    spriteBatch = new SpriteBatch(100);
    rootUiStage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true, spriteBatch);

    InputSystem.instance().setup(camera);
    Gdx.input.setInputProcessor(InputSystem.instance());
    InputSystem.instance().addInputProcessor(rootUiStage, 0);

    InputSystem.instance().bind(new int[]{InputSystem.Keys.BACK, InputSystem.Keys.ESCAPE}, new InputCallback() {
      public boolean run(float timeDelta) {
        new Dialog(rootUiStage).setTitle("Awww, don't leave me.").setMessage("Are you sure you want to exit the game?").addButton(ResponseType.POSITIVE, "Yes", new OnClickCallback() {
          @Override
          public void onClick(Dialog dialog) {
            dialog.dismiss();
            Gdx.app.exit();
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

    Scene.setGuiSkin(new Skin(Gdx.files.internal("default-skin.ui"), Gdx.files.internal("default-skin.png")));
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

    Table.drawDebug(activeScene.getStage());
    Table.drawDebug(rootUiStage);

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
    rootUiStage.setViewport(width, height, rootUiStage.isStretched());
    activeScene.getStage().setViewport(width, height, activeScene.getStage().isStretched());
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
    spriteBatch.dispose();
  }

  public static void changeScene(Class<? extends Scene> sceneClass) {
    try {
      if (activeScene != null) {
        InputSystem.instance().removeInputProcessor(activeScene.getStage());
        activeScene.pause();
      }

      System.out.println("Switching scene to: " + sceneClass.getSimpleName());
      activeScene = sceneClass.newInstance();
      activeScene.create();
      activeScene.resume();
      InputSystem.instance().addInputProcessor(activeScene.getStage(), 10);
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  public static Scene getActiveScene() {
    return activeScene;
  }
}
