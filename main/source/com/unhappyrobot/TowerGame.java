package com.unhappyrobot;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.google.common.collect.Lists;
import com.unhappyrobot.entities.*;
import com.unhappyrobot.graphics.BackgroundLayer;
import com.unhappyrobot.gui.Dialog;
import com.unhappyrobot.gui.HeadsUpDisplay;
import com.unhappyrobot.gui.OnClickCallback;
import com.unhappyrobot.gui.ResponseType;
import com.unhappyrobot.input.Action;
import com.unhappyrobot.input.CameraController;
import com.unhappyrobot.input.InputSystem;
import com.unhappyrobot.utils.Random;

import java.util.List;

import static com.unhappyrobot.input.InputSystem.Keys;

public class TowerGame implements ApplicationListener {
  private OrthographicCamera camera;
  private SpriteBatch spriteBatch;
  private BitmapFont menloBitmapFont;

  private static List<GameLayer> layers;
  private Matrix4 matrix;

  private Matrix4 hudProjectionMatrix;
  private final GameGrid gameGrid = new GameGrid();
  private GameGridRenderer gameGridRenderer;
  private GestureDetector gestureDetector;
  private CameraController cameraController;
  private static TweenManager tweenManager;
  private static TowerGame instance;
  private GridObject mouseRat;
  private Skin uiSkin;
  private Button testButton;
  private Stage guiStage;
  private BitmapFont defaultBitmapFont;
  private Sprite particle;

  public void create() {
    instance = this;

    Random.init();
    Tween.setPoolEnabled(true);

    tweenManager = new TweenManager();

    camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    spriteBatch = new SpriteBatch(100);
    guiStage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

    Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

    layers = Lists.newArrayList();

    gameGrid.setUnitSize(64, 64);
    gameGrid.setGridSize(50, 50);
    gameGrid.setGridColor(0.1f, 0.1f, 0.1f, 0.1f);

    HeadsUpDisplay.getInstance().initialize(camera, gameGrid, guiStage, spriteBatch);

    BackgroundLayer groundLayer = new BackgroundLayer("backgrounds/ground.png");
    groundLayer.setSize(gameGrid.getWorldSize().x, 256f);
    groundLayer.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.ClampToEdge);
    layers.add(groundLayer);

    BackgroundLayer skyLayer = new BackgroundLayer("backgrounds/bluesky.png");
    skyLayer.setPosition(0, 256);
    skyLayer.setSize(gameGrid.getWorldSize().x, gameGrid.getWorldSize().y - 256f);
    skyLayer.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.ClampToEdge);
    layers.add(skyLayer);

    CloudLayer cloudLayer = new CloudLayer(gameGrid.getWorldSize());
    layers.add(cloudLayer);

    gameGridRenderer = gameGrid.getRenderer();
    layers.add(gameGridRenderer);

    InputSystem.getInstance().setup(camera, gameGrid);
    InputSystem.getInstance().addInputProcessor(guiStage, 10);
    Gdx.input.setInputProcessor(InputSystem.getInstance());

    InputSystem.getInstance().bind(Keys.G, new Action() {
      public boolean run(float timeDelta) {
        gameGridRenderer.toggleGridLines();

        return true;
      }
    });

    InputSystem.getInstance().bind(Keys.NUM_0, new Action() {
      public boolean run(float timeDelta) {
        camera.zoom = 1f;

        return true;
      }
    });

    InputSystem.getInstance().bind(new int[]{Keys.BACK, Keys.ESCAPE}, new Action() {
      private Dialog exitDialog;

      public boolean run(float timeDelta) {
        if (exitDialog != null) {
          exitDialog.dismiss();
        } else {
          exitDialog = new Dialog().setTitle("Awww, don't leave me.").setMessage("Are you sure you want to exit the game?").addButton(ResponseType.POSITIVE, "Yes", new OnClickCallback() {
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
          }).centerOnScreen().show();

          exitDialog.onDismiss(new Action() {
            public boolean run(float timeDelta) {
              exitDialog = null;
              return true;
            }
          });
        }
        return true;
      }
    });
  }

  public void render() {
    GL10 gl = Gdx.graphics.getGL10();
    gl.glClearColor(0.48f, 0.729f, 0.870f, 1.0f);
    gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
    gl.glEnable(GL10.GL_BLEND);
    gl.glEnable(GL10.GL_TEXTURE_2D);

    float deltaTime = Gdx.graphics.getDeltaTime();

    InputSystem.getInstance().update(deltaTime);

    camera.update();
    spriteBatch.setProjectionMatrix(camera.combined);

    update();

    gl.glColor4f(1, 1, 1, 1);
    for (GameLayer layer : layers) {
      layer.render(spriteBatch, camera);
    }

    guiStage.draw();
    Table.drawDebug(guiStage);
  }

  public void update() {
    float deltaTime = Gdx.graphics.getDeltaTime();

    tweenManager.update();
    gameGrid.update(deltaTime);
    guiStage.act(deltaTime);

    for (GameLayer layer : layers) {
      layer.update(deltaTime);
    }

    HeadsUpDisplay.getInstance().act(deltaTime);
  }

  public void resize(int width, int height) {
    Gdx.app.log("lifecycle", "resizing!");
    HeadsUpDisplay.getInstance().resize(spriteBatch, width, height);
    Gdx.gl.glViewport(0, 0, width, height);
  }

  public void pause() {
    Gdx.app.log("lifecycle", "pausing!");
  }

  public void resume() {
    Gdx.app.log("lifecycle", "resuming!");
  }

  public void dispose() {
    spriteBatch.dispose();
    guiStage.dispose();
  }

  public static TweenManager getTweenManager() {
    return tweenManager;
  }
}
