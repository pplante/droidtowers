package com.unhappyrobot;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.google.common.collect.Lists;
import com.unhappyrobot.controllers.AvatarLayer;
import com.unhappyrobot.entities.CloudLayer;
import com.unhappyrobot.entities.GameGrid;
import com.unhappyrobot.entities.GameGridRenderer;
import com.unhappyrobot.entities.GameLayer;
import com.unhappyrobot.gamestate.GameState;
import com.unhappyrobot.graphics.BackgroundLayer;
import com.unhappyrobot.gui.Dialog;
import com.unhappyrobot.gui.HeadsUpDisplay;
import com.unhappyrobot.gui.OnClickCallback;
import com.unhappyrobot.gui.ResponseType;
import com.unhappyrobot.input.InputCallback;
import com.unhappyrobot.input.InputSystem;
import com.unhappyrobot.types.CommercialTypeFactory;
import com.unhappyrobot.types.ElevatorTypeFactory;
import com.unhappyrobot.types.RoomTypeFactory;
import com.unhappyrobot.types.StairTypeFactory;
import com.unhappyrobot.utils.Random;

import java.util.List;

import static com.unhappyrobot.input.InputSystem.Keys;

public class TowerGame implements ApplicationListener {
  private static OrthographicCamera camera;
  private SpriteBatch spriteBatch;

  private static List<GameLayer> gameLayers;
  private Matrix4 matrix;

  private GameGrid gameGrid;
  private static TweenManager tweenManager;
  private Stage guiStage;
  private static GameGridRenderer gameGridRenderer;
  private GameState gameState;
  private boolean loadedSavedGame;
  private FileHandle gameSaveLocation;

  public static GameGridRenderer getGameGridRenderer() {
    return gameGridRenderer;
  }

  public void create() {
    Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

    RoomTypeFactory.getInstance();
    CommercialTypeFactory.getInstance();
    ElevatorTypeFactory.getInstance();
    StairTypeFactory.getInstance();

    Random.init();
    Tween.setPoolEnabled(true);

    tweenManager = new TweenManager();

    camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    spriteBatch = new SpriteBatch(100);
    guiStage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true, spriteBatch);

    GridPositionCache.instance();

    gameGrid = new GameGrid();
    gameGrid.setUnitSize(64, 64);
    gameGrid.setGridSize(50, 50);
    gameGrid.setGridColor(0.1f, 0.1f, 0.1f, 0.1f);

    gameState = new GameState(gameGrid);

    HeadsUpDisplay.getInstance().initialize(camera, gameGrid, guiStage, spriteBatch);

    BackgroundLayer groundLayer = new BackgroundLayer("backgrounds/ground.png");
    groundLayer.setSize(gameGrid.getWorldSize().x, 256f);
    groundLayer.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.ClampToEdge);

    BackgroundLayer skyLayer = new BackgroundLayer("backgrounds/bluesky.png");
    skyLayer.setPosition(0, 256);
    skyLayer.setSize(gameGrid.getWorldSize().x, gameGrid.getWorldSize().y - 256f);
    skyLayer.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.ClampToEdge);

    gameGridRenderer = gameGrid.getRenderer();
    AvatarLayer.initialize(gameGrid);

    GameLayer testLayer = new GameLayer();
    gameLayers = Lists.newArrayList();
    gameLayers.add(groundLayer);
    gameLayers.add(skyLayer);
    gameLayers.add(new CloudLayer(gameGrid.getWorldSize()));
    gameLayers.add(gameGridRenderer);
    gameLayers.add(gameGrid);
    gameLayers.add(AvatarLayer.instance());

//    BEGIN INPUT SETUP:
    InputSystem.getInstance().setup(camera, gameLayers);
    InputSystem.getInstance().addInputProcessor(guiStage, 10);
    Gdx.input.setInputProcessor(InputSystem.getInstance());

    InputSystem.getInstance().bind(Keys.G, new InputCallback() {
      public boolean run(float timeDelta) {
        gameGrid.getRenderer().toggleGridLines();

        return true;
      }
    });

    InputSystem.getInstance().bind(Keys.NUM_0, new InputCallback() {
      public boolean run(float timeDelta) {
        camera.zoom = 1f;

        return true;
      }
    });

    InputSystem.getInstance().bind(Keys.R, new InputCallback() {
      public boolean run(float timeDelta) {
        Texture.invalidateAllTextures(Gdx.app);

        return true;
      }
    });

    InputSystem.getInstance().bind(new int[]{Keys.BACK, Keys.ESCAPE}, new InputCallback() {
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

          exitDialog.onDismiss(new InputCallback() {
            public boolean run(float timeDelta) {
              exitDialog = null;
              return true;
            }
          });
        }

        return true;
      }
    });

    gameSaveLocation = Gdx.files.external(Gdx.app.getType().equals(Application.ApplicationType.Desktop) ? ".towergame/test.json" : "test.json");
    gameState.loadSavedGame(gameSaveLocation, camera);
  }

  public void render() {
    Gdx.gl.glClearColor(0.48f, 0.729f, 0.870f, 1.0f);
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
    Gdx.gl.glEnable(GL10.GL_BLEND);
    Gdx.gl.glEnable(GL10.GL_TEXTURE_2D);

    float deltaTime = Gdx.graphics.getDeltaTime();

    camera.update();
    InputSystem.getInstance().update(deltaTime);

    spriteBatch.setProjectionMatrix(camera.combined);

    updateGameObjects();

    for (GameLayer layer : gameLayers) {
      layer.render(spriteBatch, camera);
    }

    guiStage.draw();
    Table.drawDebug(guiStage);
  }

  private void updateGameObjects() {
    float deltaTime = Gdx.graphics.getDeltaTime();

    tweenManager.update();
    gameGrid.update(deltaTime);
    gameState.update(deltaTime, gameGrid);
    guiStage.act(deltaTime);

    for (GameLayer layer : gameLayers) {
      layer.update(deltaTime);
    }

    HeadsUpDisplay.getInstance().act(deltaTime);
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
    gameState.saveGame(gameSaveLocation, camera);
    System.exit(0);
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

  public static OrthographicCamera getCamera() {
    return camera;
  }
}
